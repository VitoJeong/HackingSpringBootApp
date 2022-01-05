package com.greglturnquist.reactive;

import com.greglturnquist.reactive.commerce.item.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

import static io.rsocket.metadata.WellKnownMimeType.MESSAGE_RSOCKET_ROUTING;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;
import static org.springframework.http.MediaType.parseMediaType;

@RestController
@Slf4j
public class RSocketController {

    // RSocketRequester 리액터로 감싸, 새 클라이언트가 구독할 때마다 호출됨
    private final Mono<RSocketRequester> requester;

    public RSocketController(RSocketRequester.Builder builder) {
        // 자동설정된 RSocketRequester.Builder 빈을 주입할 수 있다. 
        this.requester = builder
                // 데이터미디어 타입 설정
                .dataMimeType(APPLICATION_JSON)
                // 메타데이터 설정(라우팅 정보 등)
                .metadataMimeType(parseMediaType(MESSAGE_RSOCKET_ROUTING.toString()))
                // R소켓 서버 연결(7000번 포트)
                .connectTcp("localhost", 7000)
                // 메시지 처리 실패시 재시도 설정(5번)
                .retry(5)
                // 요청 Mono를 핫 소스로 전환
                .cache();
    }

    /**
     * 요청 - 응답 방식 R소켓에서 새 Item 추가 전송
     * 
     * @param item 
     * @return
     */
    @PostMapping("/items/request-response")
    Mono<ResponseEntity<?>> addNewItemUsingRSocketRequestResponse(@RequestBody Item item) {
        log.info("request!!");
        return this.requester
                .flatMap(rSocketRequester -> rSocketRequester
                        // 라우팅
                        .route("newItems.request-response")
                        // 객체를 data() 메서드로 전달
                        .data(item)
                        // 응답을 원한다는 신호(Item 타입)
                        .retrieveMono(Item.class))
                // ResponseEntity 반환
                .map(savedItem -> ResponseEntity.created(
                        URI.create("/items/request-response")).body(savedItem));
    }

    @GetMapping(value = "/items/request-stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    Flux<Item> findItemsUsingRSocketRequestStream() {
        return this.requester
                .flatMapMany(rSocketRequester -> rSocketRequester
                        .route("newItems.request-stream")
                        .retrieveFlux(Item.class)
                        .delayElements(Duration.ofSeconds(1)));
    }

    @PostMapping("/items/fire-and-forget")
    Mono<ResponseEntity<?>> addNewItemUsingRSocketFireAndForget(@RequestBody Item item) {
        return this.requester
                .flatMap(rSocketRequester -> rSocketRequester
                        .route("newItems.fire-and-forget")
                        .data(item)
                        .send())
                .then(
                        Mono.just(
                                ResponseEntity.created(
                                        URI.create("/items/fire-and-forget")).build()));
    }

    @GetMapping(value = "/items", produces = TEXT_EVENT_STREAM_VALUE) 
    Flux<Item> liveUpdates() {
        return this.requester
                .flatMapMany(rSocketRequester -> rSocketRequester
                        .route("newItems.monitor")
                        .retrieveFlux(Item.class));
    }

}