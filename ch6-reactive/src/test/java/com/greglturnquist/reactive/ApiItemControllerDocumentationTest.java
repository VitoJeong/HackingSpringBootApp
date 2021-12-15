package com.greglturnquist.reactive;

import com.greglturnquist.reactive.repository.ItemRepository;
import com.greglturnquist.reactive.service.InventoryService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@WebFluxTest(controllers = ApiItemController.class)
@AutoConfigureRestDocs
public class ApiItemControllerDocumentationTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private ItemRepository repository;

    @Test
    @DisplayName("모든 상품의 목록 조회")
    void findingAllItems() {
        when(repository.findAll()).thenReturn( 
                Flux.just(new Item("item-1", "Alf alarm clock", 
                        "nothing I really need", 19.99)));

        // GET(/items) 요청을 테스트
        client.get().uri("/items")
                .exchange()
                .expectStatus().isOk() 
                .expectBody() 
                // document()
                // 첫번째 인자: 디렉토리 생성
                // 두번째 인ㄴ자 요청결과로 반환되는 JSON 문자열 출력
                .consumeWith(document("findAll", preprocessResponse(prettyPrint()))); // <2>
    }

    @Test
    @DisplayName("새로운 상품 등록")
    void postNewItem() {
        when(repository.save(any())).thenReturn( //
                Mono.just(new Item("1", "Alf alarm clock", "nothing important", 19.99)));

        // POST(/items) 요청을 테스트
        client.post().uri("/items")
                .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(document("post-new-item", preprocessResponse(prettyPrint())));
    }

}
