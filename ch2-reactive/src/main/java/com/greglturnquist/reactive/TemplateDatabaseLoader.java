package com.greglturnquist.reactive;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

/**
 * 블로킹 리포지토리를 사용하지 않고 블로킹 방식으로 데이터를 로딩하기 위한 클래스
 */
@Component
public class TemplateDatabaseLoader {
    
    /**
     * @Bean: 메서드가 반환하는 객체가 빈으로 등록되게 해주는 어노테이션
     * CommandLineRunner: 애플리케이션이 시작된 후, 자동으로 실행되는 스프링 부트 컴포넌트
     *                  여러개의 객체를 조율해서 순서에 따라실행되도록 작성해서는 안 된다.
     */
    @Bean
    CommandLineRunner initialize(MongoOperations mongo) {
        // MongoOperations: 애플리케이션과 MongoDB의 결합도를 낮춰주는 인터페이스
        //                  이 인터페이스를 통해 계약과 세부 구현내용을 분리할 수 있다.

        return args -> {
            mongo.save(new Item("Alf alarm clock", 19.99));
            mongo.save(new Item("Smurf TV tray", 24.99));
        };
    }
}
