package fast.campus.fcss04.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * DataSource 를 파라미터로 주입받음
 * - JdbcUserDetailsManager 를 반환하도록 설정
 */
@Configuration
public class SecurityConfig {

    /**
     * JdbcUserDetailsManager 이용
     *
     * 데이터베이스에서 사용자를 관리하는 경우에는 JdbcUserDetailsManager 를 활용할 수 있음
     * - 데이터베이스는 MySQL 을 사용
     * - 사용자를 위한 users 테이블과 권한 관리를 위한 authorities 테이블을 생성
     * - users 테이블에는 사용자 ID, 암호, 활성화 여부를 저장하는 3개의 필드가 존재함
     * - authorities 테이블에는 사용자 ID 와 그 사용자에게 부여된 권한을 나타내는 authority 필드가 존재함
     * 스프링부트에서는 schema.sql과 data.sql 파일을 통해 초기 테이블을 설정할 수 있음
     */
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();

        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder(2, 2, 2, 2, 2));

        return new DelegatingPasswordEncoder("bcrypt", encoders);
    }
}
