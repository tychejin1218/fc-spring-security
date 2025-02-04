package fast.campus.fcss11.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RequestValidationFilter 의 흐름
 * 1. 인입된 요청 헤더에 Request-Id 가 존재하는지 확인
 * 2. 만약 존재하면 체인의 다음 필터로 전달
 * 3. 만약 존재하지 않으면 HTTP 400 Bad Request 응답을 반환
 */
public class RequestValidationFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    Filter.super.init(filterConfig);
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    // 헤더에 Request-Id 가 존재하는지 확인한다.
    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

    String requestId = httpServletRequest.getHeader("Request-Id");

    // 없으면 응답으로 400 Bad Request 를 반환한다.
    if (requestId == null || requestId.isBlank()) {
      httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // 존재하면 체인의 다음 필터로 전달한다.
    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void destroy() {
    Filter.super.destroy();
  }
}
