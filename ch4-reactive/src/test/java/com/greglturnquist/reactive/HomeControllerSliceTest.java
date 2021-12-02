package com.greglturnquist.reactive;

import com.greglturnquist.reactive.repository.CartRepository;
import com.greglturnquist.reactive.repository.ItemRepository;
import com.greglturnquist.reactive.service.CartService;
import com.greglturnquist.reactive.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * HomeController에 국한된 스프링 웹플럭스 슬라이스 테스트
 */
@WebFluxTest(HomeController.class)
@Slf4j
public class HomeControllerSliceTest {

    @Autowired
    private WebTestClient client;

    // 가짜 객체를 이용해서 테스트 대상에 집중할 수 있게만듬
    @MockBean
    InventoryService inventoryService;

    @Test
    void homePage() {
        when(inventoryService.getInventory()).thenReturn(Flux.just(
                new Item("id1", "name1", "desc1", 1.99),
                new Item("id2", "name2", "desc2", 3.99)
        ));
        when(inventoryService.getCart("My Cart"))
                .thenReturn(Mono.just(new Cart("My Cart")));

        client.get().uri("/").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(exchangeResult -> {
                    log.info("========== Assertions =========");
                    log.info(exchangeResult.getResponseBody());
                    assertThat(exchangeResult.getResponseBody()).contains("\"/add/id1\"");
                    assertThat(exchangeResult.getResponseBody()).contains("\"/add/id2\"");
                });

    }
}
