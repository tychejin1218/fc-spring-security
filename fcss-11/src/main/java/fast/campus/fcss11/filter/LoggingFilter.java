package fast.campus.fcss11.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * 로깅을 하는 로깅필터 클래스를 생성함 마찬가지로 로직을 수행하고 나면 다음 필터로 전달
 */
@Slf4j
public class LoggingFilter implements Filter {

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    String header = httpServletRequest.getHeader("Request-Id");
    log.info("Request-Id={}", header);
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
