package com.greglturnquist.reactive;

import org.springframework.stereotype.Component;

@Component
public class RepositoryDatabaseLoader {

    /**
     * @Bean: 메서드가 반환하는 객체가 빈으로 등록되게 해주는 어노테이션
     * CommandLineRunner: 애플리케이션이 시작된 후, 자동으로 실행되는 스프링 부트 컴포넌트
     *                  여러개의 객체를 조율해서 순서에 따라실행되도록 작성해서는 안 된다.
     */
    // @Bean
    // CommandLineRunner initialize(BlockingItemRepository repository) {
    //     // CommandLineRunner의 구현체 생성
    //     return args -> {
    //         // 블로킹으로 동작하는
    //         repository.save(new Item("Alf alarm clock", 19.99));
    //         repository.save(new Item("Smurf TV tray", 24.99));
    //     };
    // }
}
