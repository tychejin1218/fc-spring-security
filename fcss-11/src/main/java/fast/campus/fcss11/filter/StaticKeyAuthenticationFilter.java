package fast.campus.fcss11.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 정적 키 인증을 관리하기 위해 StaticKeyAuthenticationFilter 클래스를 생성함
 */
@Component
public class StaticKeyAuthenticationFilter implements Filter {

  @Value("${authorization.key}")
  private String authorizationKey;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    String authentication = request.getHeader("Authorization");

    if (authorizationKey.equals(authentication)) {
      filterChain.doFilter(servletRequest, servletResponse);
      return;
    }

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
