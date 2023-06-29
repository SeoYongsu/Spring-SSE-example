package com.example.webfluxsseexample.config;



import com.example.webfluxsseexample.controller.NotificationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterConfig {
    @Bean
    RouterFunction<ServerResponse> sseEndPoint(NotificationHandler handler){
        return RouterFunctions.route(
                GET("/connect/{username}"), handler::connect
        ).andRoute(
                POST("/push/{username}"),handler::push
        );
    }

}
