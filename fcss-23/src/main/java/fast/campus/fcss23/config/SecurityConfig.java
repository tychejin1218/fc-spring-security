package fast.campus.fcss23.config;

import java.time.LocalTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  /**
   * ReactiveUserDetailsService 설정
   * <p>
   * - MapReactiveUserDetailsService를 반환하며, 해당 방식은 인메모리 저장소처럼 메모리에 사용자 정보를 저장함.
   * </p>
   *
   * <p>
   * ### 커스텀 인증 흐름
   * </p>
   * <p>
   * 리액티브 앱에서 커스텀 인증 논리를 구현하려면 `ReactiveAuthenticationManager` 인터페이스를 구현해야 함.
   * </p>
   *
   * <ul>
   *   <li>리액티브와 비-리액티브 아키텍처의 차이는 크지 않음</li>
   *   <li>HTTP 요청을 가로채서 인증 처리 흐름 실행</li>
   *   <li>인증 처리를 `ReactiveAuthenticationManager`에 위임</li>
   *   <li>인증 관리자는 `ReactiveAuthenticationManager` 인터페이스를 구현해야 함</li>
   *   <li>구체적인 사용자 상세 정보는 `ReactiveUserDetailsService`로부터 조회</li>
   * </ul>
   *
   * @return ReactiveUserDetailsService 인스턴스를 반환
   */
  @Bean
  public ReactiveUserDetailsService reactiveUserDetailsService() {
    UserDetails user = User.withUsername("danny.kim")
        .password("12345")
        .roles("ADMIN")
        .build();
    return new MapReactiveUserDetailsService(user);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(a -> a.pathMatchers(HttpMethod.GET, "/hello").authenticated());
        http.authorizeExchange(a -> a.anyExchange().permitAll());
//    http.authorizeExchange(a -> a.anyExchange().access(this::getAuthorizationDecisionMono));
//    http.httpBasic(Customizer.withDefaults());
    return http.build();
  }

  private Mono<AuthorizationDecision> getAuthorizationDecisionMono(Mono<Authentication> a,
      AuthorizationContext ac) {
    String path = getRequestPath(ac);

    if (path.equals("/hello")) {
      boolean isEvenMinute = LocalTime.now().getMinute() % 2 == 0;
      return Mono.just(new AuthorizationDecision(isEvenMinute));
    }

    return Mono.just(new AuthorizationDecision(false));
  }

  private String getRequestPath(AuthorizationContext ac) {
    return ac.getExchange()
        .getRequest()
        .getPath()
        .toString();
  }
}
