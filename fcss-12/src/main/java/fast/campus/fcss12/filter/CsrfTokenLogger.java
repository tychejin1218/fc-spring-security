package fast.campus.fcss12.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CsrfTokenLogger implements Filter {

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    CsrfToken csrf = (CsrfToken) servletRequest.getAttribute("_csrf");
    log.info("csrf token={}", csrf.getToken());
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
