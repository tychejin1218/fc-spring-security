package fast.campus.fcss15.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
        .authorizeHttpRequests(c -> c.anyRequest().permitAll()) // 인증 없이 허용
        .cors(c -> {
          CorsConfigurationSource source = request -> {
            CorsConfiguration config = new CorsConfiguration();
            /*config.setAllowedOrigins(
                List.of("http://localhost:8080")
            );
            config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE")
            );*/
            config.setAllowedOrigins(List.of("*")); // 모든 출처 허용
            config.setAllowedMethods(List.of("*")); // 모든 HTTP 메서드 허용
            return config;
          };
          c.configurationSource(source);
        });

    return httpSecurity.build();
  }
}
