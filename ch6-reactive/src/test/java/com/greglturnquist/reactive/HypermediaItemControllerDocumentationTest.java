package com.greglturnquist.reactive;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.*;

import com.greglturnquist.reactive.repository.ItemRepository;
import com.greglturnquist.reactive.service.InventoryService;
import org.junit.jupiter.api.DisplayName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = HypermediaItemController.class)
@AutoConfigureRestDocs
public class HypermediaItemControllerDocumentationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    InventoryService service;

    @MockBean
    ItemRepository repository;


    @Test
    @DisplayName("아이템 하나를 상세 조회")
    void findOneItem() {
        when(repository.findById("item-1")).thenReturn(Mono.just( //
                new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)));

        this.webTestClient.get().uri("/hypermedia/items/item-1") //
                .exchange() //
                .expectStatus().isOk() //
                .expectBody() //
                .consumeWith(document("findOne-hypermedia", preprocessResponse(prettyPrint()), //
                        links( // <1>
                                linkWithRel("self").description("이 `Item`에 대한 공식 링크"), // <2>
                                linkWithRel("item").description("`Item` 목록 링크")))); // <3>
    }


    @Test
    void findProfile() {
        this.webTestClient.get().uri("/hypermedia/items/profile") //
                .exchange() //
                .expectStatus().isOk() //
                .expectBody() //
                .consumeWith(document("profile", //
                        preprocessResponse(prettyPrint())));
    }

}