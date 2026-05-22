package com.conx.server.user.service;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.User;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.repository.CompanyRepository;
import com.conx.server.user.repository.CrewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFinder {

    private final CrewRepository crewRepository;
    private final CompanyRepository companyRepository;

    public User findByEmail(String email) {
        return companyRepository.findByEmail(email)
                .map(user -> (User) user)
                .orElseGet(() ->
                        crewRepository.findByEmail(email)
                                .orElseThrow(() ->
                                        new CustomException(ErrorCode.USER_NOT_FOUND)
                                )
                );
    }

    public boolean existPendingUserByEmail(String email) {
        if(companyRepository.existsByEmailAndStatus(email, UserStatus.PENDING)){
            return true;
        } else return crewRepository.existsByEmailAndStatus(email, UserStatus.PENDING);
    }

    public boolean informationIsFilled(User user){
        if (user instanceof Crew crew){
            return crew.getAdvantages() == null;
        } else if (user instanceof Company company){
            return company.getCompanyName() == null;
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
