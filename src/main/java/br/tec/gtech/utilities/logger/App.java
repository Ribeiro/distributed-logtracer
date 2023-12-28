package br.tec.gtech.utilities.logger;

import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import br.tec.gtech.utilities.filter.LogFilter;
//import br.tec.gtech.utilities.filter.RequestResponseLoggingFilter;

@SpringBootApplication
//@EnableAutoConfiguration
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
	public LogFilter auditFilter(){
		return  new LogFilter();
	}
    

    /* 
    @Bean
	public RequestResponseLoggingFilter auditFilter(){
		return  new RequestResponseLoggingFilter();
	}
    */
}