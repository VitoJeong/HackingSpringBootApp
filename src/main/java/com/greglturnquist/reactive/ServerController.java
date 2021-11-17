package com.greglturnquist.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class ServerController {

    private final KitchenService kitchen;


    /**
     * 반환되는 미디어 타입 text/event-stream
     *  -> 클라이언트가 스트림을 쉽게 consume(소비)할 수 있다.
     *
     * Flux: Spring Reactor Publisher 중 하나.
     *       0개 이상의 next 신호를 발생할 수 있고 complete나 error 신호를 발생하거나 발생하지 않을 수 있다.
     *
     * @return
     */
    @GetMapping(value = "/server", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Dish> serveDishes() {
        return this.kitchen.getDishes();
    }

    /**
     * deliver() 함수를 통해서 delivered 값이 설정된 요리 반환
     * 
     * @return 
     */
    @GetMapping(value = "/served-dishes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Dish> deliverDishes() {
        return this.kitchen.getDishes()
                .map(dish -> Dish.deliver(dish));
    }

}
