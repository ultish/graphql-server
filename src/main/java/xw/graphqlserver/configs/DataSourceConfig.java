package xw.graphqlserver.configs;

import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    /**
     * Customise the physical naming strategy to use for Hibernate.
     *
     * @return
     */
    @Bean
    public PhysicalNamingStrategy physical() {
        return new CamelCasePhysicalNamingStrategy();
    }

    //    @Autowired
    //    private EntityManagerFactory entityManagerFactory;
    //
    //    @Bean
    //    public SessionFactory getSessionFactory() {
    //        if (entityManagerFactory.unwrap(SessionFactory.class) == null) {
    //            throw new NullPointerException("factory is not a hibernate " +
    //                "factory");
    //        }
    //        return entityManagerFactory.unwrap(SessionFactory.class);
    //    }
    //
    //    @Bean
    //    public DataSource dataSource() {
    //        DriverManagerDataSource dataSource = new
    //        DriverManagerDataSource();
    //        dataSource.setDriverClassName("org.postgresql.Driver");
    //        dataSource.setUsername("postgres");
    //        dataSource.setPassword("password");
    //        dataSource.setUrl(
    //            "jdbc:postgresql://localhost:5432/jikanganai");
    //        return dataSource;
    //    }

    //    @Bean
    //    @Autowired
    //    public HibernateTransactionManager transactionManager(
    //        SessionFactory sessionFactory
    //    ) {
    //
    //        HibernateTransactionManager txManager
    //            = new HibernateTransactionManager();
    //        txManager.setSessionFactory(sessionFactory);
    //
    //        return txManager;
    //    }
}

