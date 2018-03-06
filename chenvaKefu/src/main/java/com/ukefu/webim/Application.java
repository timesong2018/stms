package com.ukefu.webim;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import com.lmax.disruptor.dsl.Disruptor;
import com.ukefu.core.UKDataContext;
import com.ukefu.util.event.UserHistoryEvent;
import com.ukefu.webim.config.web.StartedEventListener;
import com.ukefu.webim.util.disruptor.UserEventHandler;
import com.ukefu.webim.util.disruptor.UserHistoryEventFactory;

@EnableAutoConfiguration
@SpringBootApplication
@EnableJpaRepositories("com.ukefu.webim.service.repository")
@EnableAsync
public class Application {
	
    @Bean   
    public MultipartConfigElement multipartConfigElement() {   
            MultipartConfigFactory factory = new MultipartConfigFactory();  
            factory.setMaxFileSize("50MB"); //KB,MB  
            factory.setMaxRequestSize("100MB");   
            return factory.createMultipartConfig();   
    }   
      
    @SuppressWarnings({ "unchecked", "deprecation" })
	@Bean(name="disruptor")   
    public Disruptor<UserHistoryEvent> disruptor() {   
    	 Executor executor = Executors.newCachedThreadPool();
    	 UserHistoryEventFactory factory = new UserHistoryEventFactory();
    	 Disruptor<UserHistoryEvent> disruptor = new Disruptor<UserHistoryEvent>(factory, 1024, executor);
    	 disruptor.handleEventsWith(new UserEventHandler());
    	 disruptor.start();
         return disruptor;   
    }  
    
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                ErrorPage error = new ErrorPage("/error.html");
                container.addErrorPages(error);
            }
        };
    }
    
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Application.class) ;
		springApplication.addListeners(new StartedEventListener());
		UKDataContext.setApplicationContext(springApplication.run(args));
	}
	
}
