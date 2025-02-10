package fast.campus.fcss14.auth;

import fast.campus.fcss14.repository.TokenJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Repository;

/**
 * CustomCsrfTokenRepository 클래스는 {@link CsrfTokenRepository} 인터페이스를 구현하여 사용자 정의 방식으로 CSRF 토큰을 관리
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>CSRF 토큰 생성: {@link #generateToken(HttpServletRequest)}</li>
 *   <li>CSRF 토큰 저장: {@link #saveToken(CsrfToken, HttpServletRequest, HttpServletResponse)}</li>
 *   <li>CSRF 토큰 로드: {@link #loadToken(HttpServletRequest)}</li>
 * </ul>
 */
@Repository
@RequiredArgsConstructor
public class CustomCsrfTokenRepository implements CsrfTokenRepository {

  private final TokenJpaRepository tokenJpaRepository;

  /**
   * HTTP 요청을 기반으로 CSRF 토큰을 생성합니다.
   *
   * @param request HTTP 요청 객체
   * @return {@link CsrfToken} 기본 CSRF 토큰 객체
   */
  @Override
  public CsrfToken generateToken(HttpServletRequest request) {
    String uuid = UUID.randomUUID().toString();
    return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", uuid);
  }

  /**
   * 생성된 CSRF 토큰을 데이터베이스에 저장하거나 업데이트
   *
   * <p>동작:</p>
   * <ul>
   *   <li>요청 헤더에서 클라이언트 식별자를 추출합니다.</li>
   *   <li>해당 식별자를 바탕으로 데이터베이스에서 기존 토큰을 조회합니다.</li>
   *   <li>토큰이 존재하면, 새로 생성된 토큰 값으로 업데이트합니다.</li>
   *   <li>토큰이 존재하지 않으면, 새로운 토큰 레코드를 생성합니다.</li>
   * </ul>
   *
   * @param csrfToken 생성된 CSRF 토큰 객체
   * @param request HTTP 요청 객체
   * @param response HTTP 응답 객체
   */
  @Override
  public void saveToken(CsrfToken csrfToken, HttpServletRequest request,
      HttpServletResponse response) {
    String identifier = request.getHeader("X-IDENTIFIER");

    // 클라이언트 ID 를 기반으로 데이터베이스에서 토큰값을 찾음
    Optional<Token> existingToken = tokenJpaRepository.findTokenByIdentifier(identifier);

    // 클라이언트 ID 가 존재하면 새로 생성된 토큰값으로 업데이트
    if (existingToken.isPresent()) {
      Token token = existingToken.get();
      token.updateToken(csrfToken.getToken());
    } else { // 존재하지 않으면 새로운 레코드 생성
      Token token = new Token(identifier, csrfToken.getToken());
      tokenJpaRepository.save(token);
    }
  }

  /**
   * 요청 헤더에서 클라이언트 ID를 추출하여 데이터베이스에 저장된 CSRF 토큰을 로드
   *
   * @param request HTTP 요청 객체
   * @return {@link CsrfToken} 데이터베이스에서 조회된 토큰 객체, 없을 경우 {@code null}
   */
  @Override
  public CsrfToken loadToken(HttpServletRequest request) {

    String identifier = request.getHeader("X-IDENTIFIER");
    Optional<Token> existingToken = tokenJpaRepository.findTokenByIdentifier(identifier);

    if (existingToken.isPresent()) {
      Token token = existingToken.get();
      return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", token.getToken());
    }

    return null;
  }
}
