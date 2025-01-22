package fast.campus.fcss23.config;

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

import java.time.LocalTime;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
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
//        http.authorizeExchange(a -> a.pathMatchers(HttpMethod.GET, "/hello").authenticated());
//        http.authorizeExchange(a -> a.anyExchange().permitAll());
        http.authorizeExchange(a -> a.anyExchange().access(this::getAuthorizationDecisionMono));
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    private Mono<AuthorizationDecision> getAuthorizationDecisionMono(Mono<Authentication> a, AuthorizationContext ac) {
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
