package com.greglturnquist.reactive;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    /**
     * InMemoryHttpTraceRepository
     * 
     * 애플리케이션을 누가 어떻게 호출하는지 쉽게 볼 수 있도록하는 기능을 제공하는
     * HttpTraceRepository의 대표적인 구현체(Memory 기반)
     * 
     * @return 
     */
    @Bean
    HttpTraceRepository traceRepository() {
        return new InMemoryHttpTraceRepository();
    }
}
