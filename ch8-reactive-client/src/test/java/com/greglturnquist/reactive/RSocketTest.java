package com.greglturnquist.reactive;

import com.greglturnquist.reactive.commerce.item.Item;
import com.greglturnquist.reactive.commerce.item.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient // webTestClient 인스턴스 사용가능
public class RSocketTest {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    ItemRepository repository;

    @Test
    void verifyRemoteOperationsThroughRSocketRequestResponse() throws InterruptedException {

        // 데이터 초기화
        this.repository.deleteAll() 
                .as(StepVerifier::create) 
                .verifyComplete();

        // 새로운 아이템 생성
        // RSocketController에 POST 요청
        this.webTestClient.post().uri("/items/request-response")
                // request body 설정
                .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99))
                // 요청 수행
                .exchange()
                // 201 검증
                .expectStatus().isCreated()
                .expectBody(Item.class)
                .value(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getName()).isEqualTo("Alf alarm clock");
                    assertThat(item.getDescription()).isEqualTo("nothing important");
                    assertThat(item.getPrice()).isEqualTo(19.99);
                });

        // 스레드 일시 정지
        Thread.sleep(500); // <4>

        // 요청한 아이템이 몽고DB에 저장됐는지 검증
        repository.findAll()
                .as(StepVerifier::create) 
                .expectNextMatches(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getName()).isEqualTo("Alf alarm clock");
                    assertThat(item.getDescription()).isEqualTo("nothing important");
                    assertThat(item.getPrice()).isEqualTo(19.99);
                    return true;
                }) 
                .verifyComplete();
    }

}