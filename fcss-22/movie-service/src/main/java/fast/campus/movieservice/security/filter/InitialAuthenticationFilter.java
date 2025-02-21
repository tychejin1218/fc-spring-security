package fast.campus.movieservice.security.filter;

import fast.campus.movieservice.security.OtpAuthentication;
import fast.campus.movieservice.security.OtpAuthenticationProvider;
import fast.campus.movieservice.security.UsernamePasswordAuthentication;
import fast.campus.movieservice.security.UsernamePasswordAuthenticationProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class InitialAuthenticationFilter extends OncePerRequestFilter {

  private final OtpAuthenticationProvider otpAuthenticationProvider;
  private final UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

  @Value("${jwt.signing-key}")
  private String jwtKey;

  /**
   * 초기 인증 로직을 처리
   *
   * <p>헤더에서 사용자 이름, 비밀번호, OTP 정보를 확인하며,
   * OTP가 제공되지 않은 경우 사용자 이름/비밀번호 인증을 수행합니다. OTP가 제공된 경우 JWT 토큰을 생성하여 응답에 포함시킵니다.</p>
   *
   * @param request     클라이언트의 HTTP 요청 객체
   * @param response    서버의 HTTP 응답 객체
   * @param filterChain 필터 체인
   * @throws ServletException 필터 처리 중 발생할 수 있는 예외
   * @throws IOException      입력/출력 처리 중 발생할 수 있는 예외
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // 헤더로부터 사용자 이름, 비밀번호, OTP 정보를 가져오기
    String username = request.getHeader("username");
    String password = request.getHeader("password");
    String otp = request.getHeader("otp");

    // OTP가 없는 경우: 사용자 이름과 비밀번호로 인증
    if (StringUtils.isBlank(otp)) {
      // Username/Password 인증 객체 생성
      UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication(username,
          password);
      // 인증 수행
      usernamePasswordAuthenticationProvider.authenticate(authentication);
    } else {
      // OTP가 제공된 경우: OTP 인증 처리
      Authentication authentication = new OtpAuthentication(username, otp);
      otpAuthenticationProvider.authenticate(authentication);

      // JWT 생성
      SecretKey secretKey = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));

      String jwt = Jwts.builder()
          .claim("username", username)
          .signWith(secretKey)
          .compact();

      // JWT를 응답 헤더에 추가
      response.setHeader("Authorization", jwt);
    }

    // 다음 필터로 요청 전달
    filterChain.doFilter(request, response);
  }

  /**
   * 필터를 실행하지 않아야 하는 HTTP 요청을 결정
   *
   * <p>이 필터는 `/login` 엔드포인트에 대해서만 실행됩니다.
   * 다른 경로의 요청은 이 필터를 건너뜁니다.</p>
   *
   * @param request 클라이언트의 HTTP 요청 객체
   * @return 필터를 실행하지 않아야 한다면 true 반환
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // "/login" 엔드포인트가 아닌 경우, 필터 실행 제외
    return !request.getServletPath().equals("/login");
  }
}
