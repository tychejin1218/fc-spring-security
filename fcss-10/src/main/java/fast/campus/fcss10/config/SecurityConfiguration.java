package fast.campus.fcss10.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

  @Bean
  public UserDetailsService userDetailsService() {
    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

    UserDetails danny = User.withUsername("danny.kim")
        .password("12345")
        .roles("ADMIN")
        .build();

    UserDetails steve = User.withUsername("steve.kim")
        .password("12345")
        .roles("MANAGER")
        .build();

    manager.createUser(danny);
    manager.createUser(steve);

    return manager;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.httpBasic(Customizer.withDefaults());

    // 엔드포인트의 PathVariable 에 숫자만 받도록 하기 위해서는?
    // @Valid 를 사용해도 되지만..
    // 스프링 시큐리티를 적극 활용한다면?
    // 문자가 하나라도 포함되어 있으면 401 Unauthorized, 숫자만 있으면 200 OK 와 함께 해당 숫자 반환
    /*httpSecurity.authorizeHttpRequests(c -> c.requestMatchers("/product/{code:^[0-9]*$}")
        .permitAll());
    httpSecurity.authorizeHttpRequests(c -> c.anyRequest().denyAll());*/

    // HTTP 방식에 따라 다른 권한 적용
    // GET /api/v1/hello 에는 인증을 요구
    // 인증이 없으면 401 Unauthorized, 인증이 있으면 200 OK
    httpSecurity.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, "/api/v1/hello")
        .authenticated());

    // POST /api/v1/hello 에는 인증 없이 허용
    // 인증이 있어도 없어도 200 OK
    httpSecurity.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, "/api/v1/hello")
        .permitAll());

    // /api/v1/hello/** 식은 접두사 /api/v1/hello 가 붙은 모든 경로를 의미함
    httpSecurity.authorizeHttpRequests(c -> c.requestMatchers("/api/v1/hello/**")
        .authenticated());

    // 나머지 요청은 모두 거부함
    httpSecurity.authorizeHttpRequests(c -> c.anyRequest().denyAll());

    httpSecurity.csrf(AbstractHttpConfigurer::disable);
    
    return httpSecurity.build();
  }
}
