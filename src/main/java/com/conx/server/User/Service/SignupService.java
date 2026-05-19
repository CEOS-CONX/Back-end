package com.conx.server.User.Service;

import com.conx.server.Global.ApiResponse.ApiResponse;
import com.conx.server.User.DTO.SignupRequest.SignupRequestDTO;
import com.conx.server.User.DTO.SignupRequest.UpdateCompanyUserDTO;
import com.conx.server.User.DTO.SignupRequest.UpdateCrewUserDTO;
import com.conx.server.User.DTO.UserRole;
import com.conx.server.User.Domain.*;
import com.conx.server.User.Domain.Enum.CrewType;
import com.conx.server.User.Domain.Enum.Industry;
import com.conx.server.User.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class SignupService {

    private final PasswordEncoder encoder;
    private final SendingVerificationNumberService sendingVerificationNumberService;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CrewRepository crewRepository;

    /**
     * 사용자의 이메일과 비밀번호를 입력받아, 임시 User 객체를 생성합니다.
     *
     * @param req 사용자 이메일 및 비밀번호
     */
    @Transactional
    public ApiResponse<?> setUserWithEmailAndPW(SignupRequestDTO req){

        if(userRepository.existsByEmail(req.email())) {
            //TODO: ErrorCode 정하고 CustomException 반환하기
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        String password = encoder.encode(req.password());

        User user = User.create(
                req.email(), password, req.options().personalInformation(), req.options().sendingPromoteMessage()
        );

        userRepository.save(user);
        return ApiResponse.ofEmpty();
    }

    /**
     * 이메일 검증이 끝난 임시상태의 사용자에 추가정보를 넣어 상태를 활성화상태로 바꿉니다.
     * 만약 이메일 검증이 되지 않았거나, 임시상태가 아니거나, 이메일 인증 후 30분이 경과하였다면 오류가 발생합니다.
     *
     * @param req 식별용 이메일 및 추가정보
     */
    @Transactional
    public ApiResponse<?> updateCompany(UpdateCompanyUserDTO req){

        if (req.industry().equals(Industry.ETC) && req.customIndustry()==null){
            //TODO: CustomException
            throw new RuntimeException("기타 정보를 모두 작성해주세요");
        }

        User user = userRepository.findByEmail(req.email()).orElseThrow(
            //TODO: CustomException
            () -> new RuntimeException("사용자 정보가 없습니다.")
        );

        user.updateUser(UserRole.COMPANY);
        Company company = Company.create(user, req.brandName(), req.industry(),
                req.customIndustry(), req.managerName(), req.job());

        companyRepository.save(company);

        sendingVerificationNumberService.checkVerification(req.email());
        return ApiResponse.ofEmpty();
    }

    /**
     * 이메일 검증이 끝난 임시상태의 사용자에 추가정보를 넣어 상태를 활성화상태로 바꿉니다.
     * 만약 이메일 검증이 되지 않았거나, 임시상태가 아니거나, 이메일 인증 후 30분이 경과하였다면 오류가 발생합니다.
     *
     * @param req 식별용 이메일 및 추가정보
     */
    @Transactional
    public ApiResponse<?> updateCrew(UpdateCrewUserDTO req){

        if (req.crewType().equals(CrewType.ETC) && req.customCrewType()==null){
            //TODO: CustomException
            throw new RuntimeException("기타 정보를 모두 작성해주세요");
        }

        User user = userRepository.findByEmail(req.email()).orElseThrow(
                //TODO: CustomException
                () -> new RuntimeException("사용자 정보가 없습니다.")
        );

        user.updateUser(UserRole.CREW);
        Crew crew = Crew.create(user, req.crewType(), req.customCrewType(),
                req.managerName(), req.job());

        crewRepository.save(crew);

        sendingVerificationNumberService.checkVerification(req.email());
        return ApiResponse.ofEmpty();
    }
}