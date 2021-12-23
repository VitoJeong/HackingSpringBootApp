package com.greglturnquist.reactive.commerce.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/**
 * RabbitMQ의 컨슈머
 *
 * @RabbitListener 어노테이션을 이용하는게 가장 직관적이며, 유연하고 편리하다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SpringAmqpItemService {

    private final ItemRepository itemRepository;

    // AMQP 메시지 리스너로 등록되어 메시지를 소비할 수 있다.
    @RabbitListener(
            ackMode = "MANUAL",
            // 큐를 exchange에 바인딩하는 방법을 지정
            bindings = @QueueBinding(
                    // 지속성없는 임의의 큐를 생성(익명 Queue)
                    value = @Queue,
                    // 이 큐와 연결될 exchange를 지정(속성값으로 지정된 익스체인지를 큐와 연결)
                    exchange = @Exchange("hacking-spring-boot"),
                    // 라우팅 키를 지정
                    key = "new-items-spring-amqp"))
    public Mono<Void> processNewItemsViaSpringAmqp(Item item) {
        log.info("Consuming ==> " + item);
        // 반환 타입이 Mono이므로 then()을 호출해 저장이 완료될 때까지 기다린다.
        // 스프링 AMQP는 리액터 타입도 처리할 수 있으므로 구독도 스프링 AMQP 에게 위임할 수 있다.
        return itemRepository.save(item).then();
    }

    // * 익명 큐를 사용하는 이유
    // 하나의 큐에 있는 메시지는 하나의 컨슈머에 의해서만 소비될 수 있다.
    // 그러나, 동일한 라우팅 키를 사용하는 하나의 익스체인지에 2개의 컨슈머가 연결돼있지만 각각 다른 큐를 사용한다면,
    // 하나의 메시지가 다른 큐에 복제되므로 메시지 프로듀서 쪽을 변경하지 않고도 모든 컨슈머가 메시지 소비를 할 수있다.
}
