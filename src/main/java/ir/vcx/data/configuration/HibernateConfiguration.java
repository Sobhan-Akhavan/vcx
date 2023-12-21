package ir.vcx.data.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Sobhan on 11/16/2023 - VCX
 */
@Configuration
@EnableTransactionManagement
public class HibernateConfiguration {

    @Value("${spring.datasource.hikari.driver-class-name}")
    private String DRIVER_CLASS_NAME;
    @Value("${spring.datasource.hikari.jdbc-url}")
    private String JDBC_URL;
    @Value("${spring.datasource.hikari.username}")
    private String USERNAME;
    @Value("${spring.datasource.hikari.password}")
    private String PASSWORD;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String DDL_AUTO;
    @Value("${spring.jpa.hibernate.dialect}")
    private String DIALECT;
    @Value("${spring.jpa.hibernate.naming.physical-strategy}")
    private String PHYSICAL_STRATEGY;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("ir.vcx.data.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setJdbcUrl(JDBC_URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", DDL_AUTO);
        hibernateProperties.setProperty("hibernate.dialect", DIALECT);
        hibernateProperties.setProperty("hibernate.physical_naming_strategy", PHYSICAL_STRATEGY);

        return hibernateProperties;
    }
}
