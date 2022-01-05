package com.greglturnquist.reactive.commerce.cart;

import com.greglturnquist.reactive.commerce.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/**
 * 장바구니 관리 서비스
 */
@RequiredArgsConstructor
@Service
public class CartService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    public Mono<Cart> addToCart(String cartId, String id) {
        // 상품을 장바구니에 추가한다.
        return cartRepository.findById(cartId)
                // 장바구니를 찾아서 없으면 새로 생성해서 반환
                .defaultIfEmpty(new Cart("My Cart"))
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem()
                                .getId().equals(id))
                        // 새로 담은 상품과 동일한 상품이 이미 있는지 확인
                        .findAny()
                        // 이미 같은 상품이 장바구니에 있다면
                        .map(cartItem -> {
                            // 상품의 수량만 증가시켜 장바구니를 Mono에 담아 반환
                            cartItem.increment();
                            return Mono.just(cart);
                        })
                        .orElseGet(() -> {
                            // 새로 담은 상품이 장바구니에 담겨 있지 않다면
                            return itemRepository.findById(id)
                                    // 상품을 조회 한 후 수량을 1로 지정한 후
                                    .map(CartItem::new)
                                    .map(cartItem -> {
                                        // CartItem을 장바구니에 추가한 후 장바구니에 담아 반환
                                        cart.getCartItems().add(cartItem);
                                        return cart;
                                    });
                        }))
                // 업데이트된 장바구니를 DB에 저장
                .flatMap(cartRepository::save);
    }

}
