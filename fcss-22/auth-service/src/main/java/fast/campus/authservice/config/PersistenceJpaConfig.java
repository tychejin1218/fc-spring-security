package fast.campus.authservice.config;

import fast.campus.authservice.entity.EntityModule;
import fast.campus.authservice.repository.RepositoryModule;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * PersistenceJpaConfig는 JPA 관련 설정을 정의한 Spring Configuration 클래스입니다.
 *
 * <p>이 클래스는 데이터 소스(DataSource), 트랜잭션 관리자(TransactionManager),
 * 트랜잭션 템플릿(TransactionTemplate) 등의 Bean을 정의합니다.</p>
 *
 * <p><strong>주요 설정:</strong></p>
 * <p>1. <code>@EntityScan(basePackageClasses = {EntityModule.class})</code>:<br>
 * EntityModule.class가 속한 패키지를 기준으로 모든 JPA 엔티티(@Entity, @Embeddable, @MappedSuperclass)를 스캔합니다.</p>
 *
 * <p>2. <code>@EnableJpaRepositories(basePackageClasses = {RepositoryModule.class})</code>:<br>
 * RepositoryModule.class가 속한 패키지를 기준으로 모든 JPA 리포지토리(JpaRepository를 상속한 클래스)를 스캔하고 Spring 컨텍스트에
 * 등록합니다.</p>
 */
@Configuration
@EntityScan(basePackageClasses = {EntityModule.class})
@EnableJpaRepositories(basePackageClasses = {RepositoryModule.class})
public class PersistenceJpaConfig {

  /**
   * 데이터 소스를 설정하고 반환하는 Bean
   * <p>spring.datasource.hikari 접두사에 해당하는 설정 속성을 기반으로 데이터 소스를 생성합니다.</p>
   *
   * @return 설정된 DataSource
   */
  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.hikari")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  /**
   * JPA 기반의 트랜잭션 관리자를 정의하는 Bean
   *
   * @return JpaTransactionManager 트랜잭션 관리자
   */
  @Bean
  public PlatformTransactionManager transactionManager() {
    return new JpaTransactionManager();
  }

  /**
   * 쓰기 작업에 사용할 트랜잭션 템플릿을 설정하고 반환하는 Bean
   * <p>이는 읽기 전용 설정을 false로 설정합니다.</p>
   *
   * @param transactionManager 트랜잭션 관리자
   * @return 쓰기 전용 TransactionTemplate
   */
  @Bean
  public TransactionTemplate writeTransactionOperations(
      PlatformTransactionManager transactionManager) {
    var transactionTemplate = new TransactionTemplate(transactionManager);
    transactionTemplate.setReadOnly(false);
    return transactionTemplate;
  }

  /**
   * 읽기 작업에 사용할 트랜잭션 템플릿을 설정하고 반환하는 Bean
   * <p>이는 읽기 전용 설정을 true로 설정합니다.</p>
   *
   * @param transactionManager 트랜잭션 관리자
   * @return 읽기 전용 TransactionTemplate
   */
  @Bean
  public TransactionTemplate readTransactionOperations(
      PlatformTransactionManager transactionManager) {
    var transactionTemplate = new TransactionTemplate(transactionManager);
    transactionTemplate.setReadOnly(true);
    return transactionTemplate;
  }
}
