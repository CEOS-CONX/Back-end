package com.conx.server.user.service;

import com.conx.server.global.apiResponse.ApiResponse;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.mailSender.EmailDTO;
import com.conx.server.global.mailSender.MailSender;
import com.conx.server.user.dto.emailKey.CheckingVerificationKeyRequestDTO;
import com.conx.server.user.domain.types.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SendingVerificationNumberService {

    private final MailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserFinder userFinder;

    @Value("${google.user}")
    private String sender;

    /**
     * 이메일은 전송하는 DTO 객체를 생성하는 역할을 담당합니다.
     *
     * @param email 받는 이메일 주소
     * @param key 인증번호
     */
    private EmailDTO setEmailDTO(String email, String key){
        String text = "안녕하세요, CONX 로그인 인증번호입니다.\n인증번호는 " + key + "입니다.";
        String subject = "CONX 로그인 인증번호 알림";

        return EmailDTO.create(
                sender, email, text, subject
        );
    }

    /**
     * 6자리 인증코드를 만든 후 이메일로 전송하는 역할을 합니다.
     * 인증번호는 5분 간 유효합니다.
     *
     * @param email 인증번호를 받을 사람의 이메일주소
     */
    @Transactional(readOnly = true)
    public ApiResponse<?> sendCorrectKey(String email){
        if (!userFinder.existPendingUserByEmail(email)){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Random random = new Random();

        String code = String.valueOf(100000 + random.nextInt(900000));
        EmailDTO emailDTO = setEmailDTO(email, code);
        mailSender.sendMail(emailDTO);

        redisTemplate.opsForValue().set(
                "email:"+email,
                code,
                Duration.ofMinutes(5)
        );

        return ApiResponse.ofEmpty();
    }

    /**
     * 사용자가 입력한 인증번호를 검증합니다.
     * 인증번호가 일치한 경우 정상적으로 값이 반환되며, 인증요청 정보가 레디스에서 삭제됩니다.
     * 이메일 인증은 30분 간 유효하며, 이 이후에는 다시 이메일인증을 거쳐야합니다.
     *
     * @param req 이메일 + 인증정보
     */
    public ApiResponse<?> checkCorrectKey(CheckingVerificationKeyRequestDTO req){
        String code = redisTemplate.opsForValue().get("email:"+req.email());

        if (code == null) {
            throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
        }

        if (!String.valueOf(req.code()).equals(code)){
            throw new CustomException(ErrorCode.CODE_UNMATCHED);
        }

        redisTemplate.opsForValue().set(
                "verified:" + req.email(),
                "true",
                Duration.ofMinutes(30)
        );

        redisTemplate.delete("email:" + req.email());
        return ApiResponse.ofEmpty();
    }

    /**
     * 사용자가 입력한 이메일 인증을 거친 사용자인기 확인합니다.
     * 확인된 경우 레디스에서 해당 사용자의 인증정보를 제거합니다.
     * @param email 사용자 이메일
     */
    public void checkVerification(String email){

        String verified = redisTemplate.opsForValue().get("verified:" + email);
        System.out.println(">>> " + verified);
        if (verified == null){
            throw new CustomException(ErrorCode.USER_UNVERIFIED);
        }
        redisTemplate.delete("verified:"+email);
    }
}
