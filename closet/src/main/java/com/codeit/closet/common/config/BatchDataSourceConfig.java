package com.codeit.closet.common.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class BatchDataSourceConfig {

  @Primary
  @Bean(name = "batchDataSource")
  @ConfigurationProperties(prefix = "spring.batch.datasource")
  public DataSource batchDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Primary
  @Bean(name = "batchEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean batchEntityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("batchDataSource") DataSource batchDataSource
  ) {
    return builder
        .dataSource(batchDataSource)
        .packages("com.codeit.closet.common.entity")
        .build();
  }

  @Primary
  @Bean(name = "batchTransactionManager")
  public PlatformTransactionManager batchTransactionManager(
      final @Qualifier("batchEntityManagerFactory") EntityManagerFactory batchEntityManagerFactory
  ) {
    return new JpaTransactionManager(batchEntityManagerFactory);
  }
}
