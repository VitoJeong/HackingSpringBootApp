package com.greglturnquist.reactive.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    /**
     * POJO 객체를 Json 문자열로 변환하고 문자열을 바이트 배열로 변환해주는 라이브러리
     * 자바 객체를 AMQP 네트워크 전송할 수 있드록 변환
     *
     * @return
     */
    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
