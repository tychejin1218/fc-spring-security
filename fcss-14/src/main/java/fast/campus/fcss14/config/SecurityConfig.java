package fast.campus.fcss14.config;

import fast.campus.fcss14.auth.CsrfTokenLogger;
import fast.campus.fcss14.auth.CustomCsrfTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final CsrfTokenLogger csrfTokenLogger;
  // CustomCsrfTokenRepository 를 주입받음, csrf 의 csrfTokenRepository 에 customCsrfTokenRepository 를 등록
  private final CustomCsrfTokenRepository customCsrfTokenRepository;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    httpSecurity.httpBasic(Customizer.withDefaults());

    // 특정 경로에만 CSRF 보호를 적용하고 싶으면, csrf() 의 ignoringRequestMatchers 를 활용하여 제외하고 싶은 경로를 입력
//    httpSecurity.csrf(c -> c.ignoringRequestMatchers("/api/v1/hi"));

    httpSecurity.csrf(c -> {
      c.csrfTokenRepository(customCsrfTokenRepository);
      c.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler());
    });
    httpSecurity.addFilterAfter(csrfTokenLogger, CsrfFilter.class);
    httpSecurity.authorizeHttpRequests(c -> c.anyRequest().permitAll());
    return httpSecurity.build();
  }
}
