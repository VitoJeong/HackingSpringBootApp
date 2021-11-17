package com.greglturnquist.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class KitchenService {

    /**
     * 요리 스트림 생성
     * 250ms 간격으로 계속 요리를 제공한다.
     *
     * generate(): 동기 방식으로 한 번에 1개의 데이터를 생성할 때 사용하는 함수
     *              Subscriber로부터 요청이 왔을 때 신호를 생성
     *
     * SynchronousSink: Flux의 핸들, 원소를 동적으로 발행
     *
     * @return
     */
    public Flux<Dish> getDishes() {
        return Flux.<Dish>generate(sink -> {
            Dish dish = randomDish();
            // if (dish.getDescription().equals("Sesame chicken")) {
            //     log.info("Generator sink complete.");
            //     sink.complete();
            // }
            if (dish.getDescription().equals("Burnt meat pie")) {
                log.info("dish:" + dish.getDescription());
                sink.error(new IllegalStateException("Sorry. This is burnt.."));
            }
            sink.next(dish);
        }).delayElements(Duration.ofMillis(250));
    }

    /**
     * 요리 무작위 선택
     *
     * @return
     */
    private Dish randomDish() {
        return menu.get(picker.nextInt(menu.size()));
    }

    private List<Dish> menu = Arrays.asList(
            new Dish("Sesame chicken"),
            new Dish("Lo mein noodels"),
            // new Dish("Burnt meat pie"),
            new Dish("Sweet & sour beef"));

    private Random picker = new Random();
}
