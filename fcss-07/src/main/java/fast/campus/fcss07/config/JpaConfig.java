package fast.campus.fcss07.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JPA 설정 파일 클래스
 * <p>
 * 데이터베이스와 연결하기 위해 DataSource 빈(Bean)을 생성하고 설정
 * </p>
 */
@Configuration
public class JpaConfig {

  /**
   * HikariCP DataSource를 생성
   * <p>
   * `spring.datasource.hikari` 접두어로 시작하는 설정 값을 가져와 DataSource를 생성
   * <p>
   *
   * @return HikariCP 기반의 DataSource 객체
   */
  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.hikari")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }
}
