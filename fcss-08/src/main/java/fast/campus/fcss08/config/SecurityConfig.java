package fast.campus.fcss08.config;

import java.util.Collection;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
public class SecurityConfig {

  @Bean
  public UserDetailsService userDetailsService() {

    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

    // 두 명의 사용자를 생성함
    //- 한 명은 READ 권한을, 나머지 한 명은 WRITE 권한을 가짐
    /*UserDetails danny = User.withUsername("danny.kim")
        .password("12345")
        .authorities("READ")
        .build();
    UserDetails steve = User.withUsername("steve.kim")
        .password("23456")
        .authorities("WRITE")
        .build();*/

    // 역할과 권한
    // ADMIN 과 MANAGER 역할을 부여
    /*UserDetails danny = User.withUsername("danny.kim")
        .password("12345")
        .roles("ADMIN")
        .build();
    UserDetails steve = User.withUsername("steve.kim")
        .password("23456")
        .roles("MANAGER")
        .build();*/

    // 역할과 권한
    // ROLE_ prefix 를 사용하여 역할을 선언할 수도 있음
    UserDetails danny = User.withUsername("danny.kim")
        .password("12345")
        .authorities("ROLE_ADMIN")

        // roles() 에 ROLE_ prefix 를 사용하면 예외가 발생함
        // Caused by: java.lang.IllegalArgumentException: ROLE_ADMIN cannot start with ROLE_ (it is automatically added)
//        .roles("ROLE_ADMIN")
        .build();
    UserDetails steve = User.withUsername("steve.kim")
        .password("23456")
        .authorities("ROLE_MANAGER")
        .build();

    manager.createUser(danny);
    manager.createUser(steve);

    return manager;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // PasswordEncoder 는 평문으로 동작하는 NoOpPasswordEncoder 를 활용 (UserDetailsService 를 직접 정의했기 때문에 필요함)
    return NoOpPasswordEncoder.getInstance();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    httpSecurity.httpBasic(Customizer.withDefaults());

    // authorizeHttpRequests 설정
    // 모든 요청을 허용하도록 설정
//    httpSecurity.authorizeHttpRequests(c -> c.anyRequest().permitAll());

    // WRITE 권한이 있는 사용자만 허용
//    httpSecurity.authorizeHttpRequests(c -> c.anyRequest().hasAuthority("WRITE"));

    // hasAnyAuthority 활용
    // READ 또는 WRITE 권한이 있으면 허용
    // hasAnyAuthority 는 list 형식의 권한을 받을 수 있음
//    httpSecurity.authorizeHttpRequests(c -> c.anyRequest()
//        .hasAnyAuthority("READ", "WRITE"));

    // access 활용
    // Custom 하게 설정하여 사용할 수 있음
    // WRITE 권한이 있는 사용자만 허용
//    httpSecurity.authorizeHttpRequests(c -> c.anyRequest().access(customAuthManager()));

    // MANAGER 역할이 있으면 허용
//    httpSecurity.authorizeHttpRequests(
//        c -> c.anyRequest().hasRole("MANAGER"));

    // 모든 요청 거부

    // denyAll 을 활용하여 접근 제한
    // denyAll 을 활용하면 모든 요청을 기본적으로 거부하게 됨
    // - 특정 요청만 허용하고 싶을 때 활용할 수 있음
    // - .com 으로 끝나는 이메일에 대해서만 요청을 받고 나머지 요청에 대해서는 거부하고 싶을 때
    httpSecurity.authorizeHttpRequests(c -> c.anyRequest().denyAll());

    return httpSecurity.build();
  }

  private AuthorizationManager<RequestAuthorizationContext> customAuthManager() {
    return new AuthorizationManager<RequestAuthorizationContext>() {
      @Override
      public AuthorizationDecision check(Supplier<Authentication> authentication,
          RequestAuthorizationContext object) {
        Authentication auth = authentication.get();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        boolean granted = authorities.stream().anyMatch(each -> each.getAuthority().equals("READ"));
        return new AuthorizationDecision(granted);
      }
    };
  }
}
