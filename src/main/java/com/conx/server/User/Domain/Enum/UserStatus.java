package com.conx.server.User.Domain.Enum;

public enum UserStatus {
    PENDING, ACTIVE, WITHDRAW
    //PENDING: 기본 이메일, 비밀번호만 입력하고 아직 제대로 확인안된 상태
    //ACTIVE: 가입이 완료된 활동 중인 사용자
    //WITHDRAW: 탈퇴한 사용자
}
