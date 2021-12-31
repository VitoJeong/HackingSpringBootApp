package com.greglturnquist.reactive.commerce;

import com.greglturnquist.reactive.commerce.item.Item;
import com.greglturnquist.reactive.commerce.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class RSocketService {

    private final ItemRepository itemRepository;

    // 누구든지 구독을 통해서 스트림 트래픽을 받아갈 수 있도록 제공
    private final Sinks.Many<Item> itemSink;

    public RSocketService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        this.itemSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    /**
     * 요청 응답 R소켓 익스체인지 처리
     * newItems.request-response로 지정된 R소켓 메시지를 라우팅
     *
     * @param item
     * @return : 리액터 타입으로 반환
     */
    @MessageMapping("newItems.request-response")
    public Mono<Item> processNewItemsViaRSocketRequestResponse(Item item) {
        // 몽고DB에 저장
        return itemRepository.save(item)
                // 저장된 Item 객체를 FluxProcessor로 보냄
                .doOnNext(itemSink::tryEmitNext);
    }

    /**
     * 요청 스트림 R소켓 익스체인지 처리
     * newItems.request-stream로 지정된 R소켓 메시지를 라우팅
     *
     * @return : 여러 개를 리액터 타입으로 반환
     */
    @MessageMapping("newItems.request-stream")
    public Flux<Item> findItemsViaRSocketRequestStream() {
        // 메시지가 들어오면 Item 목록을 조회한 후 Flux에 담아 반환
        return itemRepository.findAll()
                // Item 객체를 FluxProcessor로 보냄
                .doOnNext(itemSink::tryEmitNext);
    }

    /**
     * 실행 후 망각 R소켓 익스체인지 처리
     * newItems.fire-and-forget로 지정된 R소켓 메시지를 라우팅
     *
     * @return : 반환할 데이터가 없는 리액티브 타입
     */
    @MessageMapping("newItems.fire-and-forget")
    public Mono<Void> processNewItemsViaRSocketFireAndForget(Item item) {
        return itemRepository.save(item)
                .doOnNext(itemSink::tryEmitNext)
                // 데이터를 사용하지 않고 버림
                .then();

        // Mono<Void> : 리액티브 스트림 프로그래밍 규격을 준수하는 가장 기본적인 반환 타입
    }

    /**
     * R소켓 익스체인지 채널 모니터링
     * 
     * @return 
     */
    @MessageMapping("newItems.monitor")
    public Flux<Item> monitorNewItems() {
        // FluxProcessor를 반환(입수, 저장, 발행된 Item객체가 들어있다.)
        // -> 담겨있는 Item 객체들의 복사본을 받게된다.
        return itemSink.asFlux();
    }
}
