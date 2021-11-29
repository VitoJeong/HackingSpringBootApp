package com.greglturnquist.reactive.service;

import com.greglturnquist.reactive.Cart;
import com.greglturnquist.reactive.CartItem;
import com.greglturnquist.reactive.Item;
import com.greglturnquist.reactive.repository.CartRepository;
import com.greglturnquist.reactive.repository.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class InventoryServiceTest {

    /**
     * 테스트 대상 클래스인 InventoryService 클래스에 주입되는 협력자 클래스
     * 테스트 대상이 아니므로 가짜 객체를 만들어서 테스트에 사용
     * 
     * @MockBean 어노테이션 : Mockito를 사용해서 가짜 객체를 만들고 스프링에 빈으로 등록
     */
    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private CartRepository cartRepository;

    InventoryService inventoryService;


    /**
     * 모든 테스트 메소드 전에 실행되는 준비 내용이 담긴 메서드
     * 테스트 준비
     */
    @BeforeEach
    void setUp() {
        // itemRepository = mock(ItemRepository.class);
        // cartRepository = mock(CartRepository.class);

        // 테스트 데이터 정의
        String sampleId = "item1";
        String sampleName = "TV tray";
        String sampleDescription = "Alf TV tray";
        Double samplePrice = 19.25;

        Item sampleItem = new Item(sampleId, sampleName, sampleDescription, samplePrice);
        CartItem sampleCartItem = new CartItem(sampleItem);
        Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

        // 협력자와의 상호작용 정의
        when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
        when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

        inventoryService = new InventoryService(itemRepository, cartRepository);
    }

    /**
     * top-level 방식: 리액터 기반 함수를 최상위에서 호출
     * 테스트 메서드 결괏값을 StepVerifier로 흘려보낸다.
     */
    @Test
    void addItemToCart_cartIsEmpty() {
        String sampleId = "item1";
        String sampleName = "TV tray";
        String sampleDescription = "Alf TV tray";
        Double samplePrice = 19.25;
        String sampleCartId = "My Cart";

        inventoryService.addItemToCart(sampleCartId, sampleId)
                // 구독을 하고 값을 확인할 수 있게 해줌(리액터 타입 핸들러 생성)
                .as(StepVerifier::create)
                // 결과 검증(boolean 타입 반환)
                .expectNextMatches(cart -> {
                    // 장바구니에 한 종류의 상품이 하나만 들어있음
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
                            .containsExactlyInAnyOrder(1);

                    // setUp()에서 정의한 데이터와 일치하는지 검증
                    assertThat(cart.getCartItems()).extracting(CartItem::getItem)
                            .containsExactly(new Item(sampleId, sampleName, sampleDescription, samplePrice));

                    return true;
                })
                // 리액티브 스트림의 complete 시그널이 발생하고 리액터 플로우가 완료됨을 검증
                .verifyComplete();
    }

    /**
     * 리액터 플로우를 테스트하는 다른 방식(top-level 테스트가 읽기쉬움)
     */
    @Test
    void addItemToCart_cartIsEmpty_alternativeWay() {
        String sampleId = "item1";
        String sampleName = "TV tray";
        String sampleDescription = "Alf TV tray";
        Double samplePrice = 19.25;
        String sampleCartId = "My Cart";

        StepVerifier.create(
                inventoryService.addItemToCart(sampleCartId, sampleId))
                .expectNextMatches(cart -> {
                    // 장바구니에 한 종류의 상품이 하나만 들어있음
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
                            .containsExactlyInAnyOrder(1);

                    // setUp()에서 정의한 데이터와 일치하는지 검증
                    assertThat(cart.getCartItems()).extracting(CartItem::getItem)
                            .containsExactly(new Item(sampleId, sampleName, sampleDescription, samplePrice));

                    return true;
                })
                .verifyComplete();
        
    }
}