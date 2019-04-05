package sic;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import sic.vista.swing.GUI_LogIn;

@SpringBootApplication
@EnableTransactionManagement
public class App {

    private Properties getAdditionalProperties() {
        Properties properties = new Properties();
        //properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql", "true");        
        properties.setProperty("hibernate.connection.CharSet", "utf8");
        properties.setProperty("hibernate.connection.characterEncoding", "utf8");
        properties.setProperty("hibernate.connection.useUnicode", "true");
        properties.setProperty("hibernate.id.new_generator_mappings", "false");
        properties.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        return properties;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean getEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(this.getDataSource());
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setPackagesToScan("sic.modelo");
        em.setJpaProperties(this.getAdditionalProperties());
        return em;
    }

    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://" + System.getenv("SIC_HOST") + ":3306/sic");
        dataSource.setUsername(System.getenv("SIC_USERNAME"));
        dataSource.setPassword(System.getenv("SIC_PASSWORD"));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager getTransactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor getExceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public AppContextProvider getApplicationContextProvider() {
        return new AppContextProvider();
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
        System.out.println("Spring Context inicializado con ID: " + context.getId());
        GUI_LogIn gui_LogIn = new GUI_LogIn();
        gui_LogIn.setVisible(true);
        SpringApplication.run(App.class, args);
    }
}
