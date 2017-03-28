package com.aurea.caonb.egizatullin;

import com.aurea.caonb.egizatullin.controllers.impls.RepositoryController;
import com.aurea.caonb.egizatullin.data.RepositoryDao;
import com.aurea.caonb.egizatullin.processing.ProcessingService;
import com.aurea.caonb.egizatullin.und.UndService;
import com.aurea.caonb.egizatullin.utils.github.GithubService;
import com.aurea.caonb.egizatullin.utils.servlet.SimpleCORSFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Predicates;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackageClasses = {
    RepositoryDao.class,
    RepositoryController.class,
    SimpleCORSFilter.class,
    ProcessingService.class,
    GithubService.class,
    UndService.class
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    @Primary
    public class CustomObjectMapper extends ObjectMapper {
        public CustomObjectMapper() {
            setSerializationInclusion(JsonInclude.Include.NON_NULL);
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            enable(SerializationFeature.INDENT_OUTPUT);
        }
    }

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            .select()
            .paths(Predicates.not(PathSelectors.regex("/error.*")))
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Springfox petstore API")
            .description("description")
            .contact(new Contact("Eduard Gizatullin", "www.aurea.com", "eduard.gizatullin@aurea.com"))
            .license("Apache License Version 2.0")
            .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
            .version("1.0")
            .build();
    }

    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory(
        @Value("${app.http.port}") final int port,
        @Value("${app.http.threadPool.maxThreads}") final int maxThreads,
        @Value("${app.http.threadPool.minThreads}") final int minThreads,
        @Value("${app.http.threadPool.idleTimeout}") final int idleTimeout) {
        final JettyEmbeddedServletContainerFactory f =  new JettyEmbeddedServletContainerFactory(port);
        f.addServerCustomizers((JettyServerCustomizer) server -> {
            // Tweak the connection pool used by Jetty to handle incoming HTTP connections
            final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
            threadPool.setMaxThreads(maxThreads);
            threadPool.setMinThreads(minThreads);
            threadPool.setIdleTimeout(idleTimeout);
        });
        return f;
    }
}