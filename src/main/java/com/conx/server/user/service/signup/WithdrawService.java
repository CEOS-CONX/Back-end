package com.conx.server.user.service.signup;

import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.domain.User;
import com.conx.server.user.service.common.UserFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WithdrawService {

    private final UserFinder userFinder;

    public WithdrawService(UserFinder userFinder) {
        this.userFinder = userFinder;
    }

    @Transactional
    public void userWithdraw(CustomUserDetails customUserDetails){
        String email = customUserDetails.getUserEmail();

        User user = userFinder.findByEmail(email);
        user.withdrawUser();
    }
}
