package com.conx.server.global;

import com.conx.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthController {

    private final DataSource dataSource;

    /**
     * CD 이후 서버가 정상적으로 동작하는지 파악하기 위한 엔드포인트입니다.
     */
    @GetMapping("/ready")
    public ResponseEntity<Map<String, String>> healthChecking() {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(2)) {
                return downResponse();
            }
        } catch (Exception exception) {
            return downResponse();
        }

        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    private ResponseEntity<Map<String, String>> downResponse() {
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(Map.of("status", "DOWN"));
    }
}