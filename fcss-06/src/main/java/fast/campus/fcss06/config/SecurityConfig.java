package fast.campus.fcss06.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * HTTP Basic 인증 방식과 양식 기반 로그인 설정 클래스
 *
 * <p>
 * <strong>[HTTP Basic 인증 방식]</strong>
 * </p>
 * <ul>
 *   <li>HTTP Basic 인증은 간단하여 예제와 데모에서 효과적으로 활용할 수 있음.</li>
 *   <li>SecurityFilterChain 설정에서 HttpSecurity의 httpBasic() 메서드를 활용하여 구성.</li>
 *   <li>기본 설정은 Customizer.withDefaults()를 사용.</li>
 *   <li>realm 영역 설정이 필요할 경우 httpBasic()의 특정 속성 활용 가능:
 *     <ul>
 *       <li>예: 인증 요청 실패 시 특정 영역 이름(Basic realm="REALM_1")을 반환.</li>
 *       <li>CustomEntryPoint를 활용하여 사용자 지정 인증 실패 응답 설정 가능.</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>
 * <strong>[양식 기반 로그인]</strong>
 * </p>
 * <ul>
 *   <li>일반적인 웹 애플리케이션에서 사용자가 아이디와 비밀번호를 입력하는 로그인 화면 제공.</li>
 *   <li>인증되지 않은 사용자 요청 시:
 *     <ul>
 *       <li>로그인 여부 확인 후 로그인이 안 된 경우 로그인 양식 화면으로 리다이렉트.</li>
 *       <li>로그인 상태라면 요청 URL로 허용.</li>
 *     </ul>
 *   </li>
 *   <li>SecurityFilterChain 설정에서 formLogin() 메서드를 활용하여 양식 기반 로그인 구성.</li>
 *   <li>로그인 성공 시 특정 URL (예: /home)로 리다이렉트하려면 defaultSuccessUrl() 활용 가능.</li>
 *   <li>HTTP Basic 인증 방식과 formLogin()은 함께 사용 가능.</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomAuthenticationSuccessHandler successHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    // 기본 HTTP Basic 설정은 Customizer.withDefaults()를 사용
//    httpSecurity.httpBasic(Customizer.withDefaults());

    // 인증 영역(realm) 설정 예제:
    // - 인증 실패 시 401 Unauthorized 응답과 함께 Basic realm="REALM_1" 설정 확인 가능
    // - CustomEntryPoint를 authenticationEntryPoint에 설정하여 사용자 지정 인증 실패 처리가 가능
//    httpSecurity.httpBasic(c -> c.realmName("REALM_1")
//        .authenticationEntryPoint(new CustomEntryPoint()));
//    httpSecurity.authorizeHttpRequests(a -> a.anyRequest().authenticated());

//    httpSecurity.formLogin(c -> c.defaultSuccessUrl("/home", true));

    // 양식 기반 로그인 설정:
    // - SecurityFilterChain에서 formLogin()을 사용하여 구성
    // - formLogin()과 httpBasic()을 함께 사용할 수도 있음
    httpSecurity.formLogin(c -> c.successHandler(successHandler));

    // HTTP Basic 인증 활성화 (기본 설정)
    httpSecurity.httpBasic(Customizer.withDefaults());

    httpSecurity.authorizeHttpRequests(a -> a.anyRequest().authenticated());

    return httpSecurity.build();
  }
}
