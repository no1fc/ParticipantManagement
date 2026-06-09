package com.jobmoa.app.CounselMain.biz.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 기존 평문 비밀번호를 BCrypt 해시로 일괄 변환하는 일회성 마이그레이션 러너.
 *
 * application.properties에 password.migration.enabled=true 설정 시 실행.
 * 마이그레이션 완료 후 해당 설정을 제거하거나 false로 변경.
 */
@Slf4j
@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${password.migration.enabled:false}")
    private boolean migrationEnabled;

    /**
     * 애플리케이션 기동 시 평문 비밀번호를 BCrypt 해시로 일괄 변환한다.
     *
     * <p>{@code password.migration.enabled} 속성이 {@code false}이면 즉시 종료한다.
     * 이미 BCrypt 해시({@code $2a$} 접두사)인 행은 건너뛴다.
     *
     * @param args 커맨드 라인 인자 (사용하지 않음)
     */
    @Override
    public void run(String... args) {
        if (!migrationEnabled) {
            log.info("비밀번호 마이그레이션 비활성화 상태 (password.migration.enabled=false)");
            return;
        }

        log.info("===== 비밀번호 BCrypt 마이그레이션 시작 =====");

        List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT 전담자번호, 비밀번호 FROM J_참여자관리_로그인정보"
        );

        int migrated = 0;
        int skipped = 0;

        for (Map<String, Object> user : users) {
            int pk = (int) user.get("전담자번호");
            String password = (String) user.get("비밀번호");

            if (password == null || password.startsWith("$2a$")) {
                skipped++;
                continue;
            }

            String hashed = passwordEncoder.encode(password);
            jdbcTemplate.update(
                    "UPDATE J_참여자관리_로그인정보 SET 비밀번호 = ? WHERE 전담자번호 = ?",
                    hashed, pk
            );
            migrated++;
        }

        log.info("===== 비밀번호 마이그레이션 완료: {}건 변환, {}건 스킵 =====", migrated, skipped);
    }
}
