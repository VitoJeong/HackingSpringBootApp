package com.greglturnquist.reactive;

import com.greglturnquist.reactive.commerce.item.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@Slf4j
public class AmqpItemController {

    private final AmqpTemplate template;

    @PostMapping("/items")
    Mono<ResponseEntity<?>> addNewItemUsingSpringAmqp(@RequestBody Mono<Item> item) {
        return item
                // boundedElastic 이라는 별도의 스레드로 실행
                // -> AmqpTemplate은 블로킹 API를 호출하기 때문
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(content -> {
                    return Mono
                            .fromCallable(() -> {
                                // exchange, routing-key, body(Item 데이터)
                                template.convertAndSend(
                                        "hacking-spring-boot", "new-items-spring-amqp", content);

                                // URI를 lcation 헤더에 담아 201 상태코드와 반환
                                return ResponseEntity.created(URI.create("/items")).build();
                            });
                });
    }

    // 리액터 플로우에서 스케줄러를 변경하는 두가지 방법
    // publishOn(): 호출되는 시점 이후로 지정한 스케줄러 사용. 여러번 바꿀 수 있음
    // subscribeOn(): 플로우 전 단계에 걸치는 스케줄러 지정.
}
