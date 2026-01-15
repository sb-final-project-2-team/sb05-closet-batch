package com.codeit.closet.common.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.codeit.closet.module.weather.repository",
    entityManagerFactoryRef = "businessManagerFactory",
    transactionManagerRef = "businessTransactionManager"
)
public class BusinessDataSourceConfig {

  @Bean(name = "businessDataSource")
  @ConfigurationProperties(prefix = "spring.business.datasource")
  public DataSource businessDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "businessManagerFactory")
  public LocalContainerEntityManagerFactoryBean businessManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("businessDataSource") DataSource businessDataSource) {
    return builder
        .dataSource(businessDataSource)
        .packages("com.codeit.closet.common.entity")
        .build();
  }

  @Bean(name = "businessTransactionManager")
  public PlatformTransactionManager businessTransactionManager(
      @Qualifier("businessManagerFactory") EntityManagerFactory businessManagerFactory) {
    return new JpaTransactionManager(businessManagerFactory);
  }
}
