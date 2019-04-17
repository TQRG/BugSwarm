package sic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableScheduling
public class App extends WebMvcConfigurerAdapter {
    
    @Bean    
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/*/login");
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}