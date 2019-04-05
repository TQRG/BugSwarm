package sic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sic.vista.swing.GUI_LogIn;

@SpringBootApplication
public class App {
    
    @Bean
    public AppContextProvider getApplicationContextProvider() {
        return new AppContextProvider();
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        
        System.setProperty("java.awt.headless", "false");
        GUI_LogIn gui_LogIn = new GUI_LogIn();
        gui_LogIn.setVisible(true);
        
    }
}