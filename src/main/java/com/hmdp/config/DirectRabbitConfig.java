package com.hmdp.config;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import javax.annotation.Resource;


@Configuration
public class DirectRabbitConfig {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Queue createDirectQueue(){
        return new Queue("saveOrder",true);
    }

    @Bean
    public DirectExchange createDirectExchange(){
        return new DirectExchange("seckill:order",true,false);
    }

    @Bean
    public Binding bindingQueue(){
        return BindingBuilder.bind(createDirectQueue()).to(createDirectExchange()).with("save");
    }
}
