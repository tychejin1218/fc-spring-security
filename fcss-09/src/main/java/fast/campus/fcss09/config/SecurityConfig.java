package fast.campus.fcss09.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
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

        httpSecurity.authorizeHttpRequests(c -> {
            c.requestMatchers("/api/v1/hello").hasRole("ADMIN");
            c.requestMatchers("/api/v1/hi").hasRole("MANAGER");
            c.anyRequest().permitAll();
        });

        return httpSecurity.build();
    }
}
