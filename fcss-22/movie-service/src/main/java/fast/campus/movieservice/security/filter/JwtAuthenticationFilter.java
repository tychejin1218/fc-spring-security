package fast.campus.movieservice.security.filter;

import fast.campus.movieservice.security.UsernamePasswordAuthentication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 인증을 처리하는 필터 클래스.
 *
 * <p>이 필터는 요청의 헤더에서 JWT를 추출하고, JWT의 유효성을 검증합니다. 검증된 JWT로부터 사용자 정보를 읽어
 * 해당 사용자 정보를 Spring Security의 {@link SecurityContextHolder}에 저장하여 인증 처리를 수행합니다.</p>
 *
 * <p>이 필터는 모든 요청에 대해 동작하며, `/login` 경로는 필터에서 제외됩니다.</p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Value("${jwt.signing-key}")
  private String signingKey;

  /**
   * JWT 인증 처리를 수행
   *
   * <p>이 메서드는 요청 헤더에서 JWT 토큰을 추출한 뒤, 토큰의 서명을 검증하고,
   * 토큰에 포함된 클레임에서 사용자 정보를 추출합니다. 추출된 사용자 정보를 기반으로 인증 객체를 생성하고 Spring Security의
   * {@link SecurityContextHolder}에 설정합니다.</p>
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

    // 요청 헤더에서 JWT 추출
    String jwt = request.getHeader("Authorization");

    // 서명을 검증하기 위한 SecretKey 생성
    SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));

    // JWT 파싱 및 클레임 추출
    Claims payload = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(jwt)
        .getPayload();

    String username = String.valueOf(payload.get("username"));

    GrantedAuthority authority = new SimpleGrantedAuthority("user");

    UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication(username,
        null, List.of(authority));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    ;

    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getServletPath().equals("/login");
  }
}
