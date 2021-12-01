package com.greglturnquist.reactive;

import com.greglturnquist.reactive.repository.CartRepository;
import com.greglturnquist.reactive.repository.ItemRepository;
import com.greglturnquist.reactive.service.BlockingInventoryService;
import com.greglturnquist.reactive.service.InventoryService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class})
public class BlockHoundIntegrationTest {

    BlockingInventoryService inventoryService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {

        // 테스트 데이터 정의
        String sampleId = "item1";
        String sampleName = "TV tray";
        String sampleDescription = "Alf TV tray";
        Double samplePrice = 19.25;

        Item sampleItem = new Item(sampleId, sampleName, sampleDescription, samplePrice);
        CartItem sampleCartItem = new CartItem(sampleItem);
        Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

        // 협력자와의 상호작용 정의
        // Mono.hide() 테스트를 정확하게 수행하기 위해 식별성 기준 최적화를 방지
        // Mono.empty()가 MonoEmpty 객체를 반환하면 리액터에서 인스턴스를 감지하고 최적화한다.(블로킹 호출을 삭제)
        when(cartRepository.findById(anyString())).thenReturn(Mono.<Cart> empty().hide());
        when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

        inventoryService = new BlockingInventoryService(itemRepository, cartRepository);
    }

    @Test
    void blockHoundShouldTrapBlockingCall() {
        // 후속 작업이 리액터 스레드안에서 실행
        Mono.delay(Duration.ofSeconds(1))
                .flatMap(tick -> inventoryService.addItemToCart("My cart", "item1"))
                // Mono를 리액터 StepVerifier로 전환
                .as(StepVerifier::create)
                // 발생한 예외를 검증
                .verifyErrorSatisfies(throwable -> {
                    Assertions.assertThat(throwable).hasMessageContaining(
                            "block()/blockFirst()/blockLast() are blocking"
                    );
                });
    }
}
