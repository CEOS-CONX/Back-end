package com.conx.server.global.common;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Operation(
            summary = "서버 준비 상태 확인",
            description = "인증 없이 서버가 HTTP 요청에 응답 가능한지 확인하며 200 OK와 문자열 \"OK\"를 반환합니다. DB나 Redis 등 외부 의존성 상태는 확인하지 않습니다."
    )
    @GetMapping("/ready")
    public ResponseEntity<String> ready() {
        return ResponseEntity.ok("OK");
    }
}
