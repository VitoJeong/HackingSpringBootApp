package com.greglturnquist.reactive.service;

import com.greglturnquist.reactive.Cart;
import com.greglturnquist.reactive.CartItem;
import com.greglturnquist.reactive.repository.CartRepository;
import com.greglturnquist.reactive.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class InventoryService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    public Mono<Cart> addItemToCart(String cartId, String itemId) {
        return cartRepository.findById(cartId)
                    .log("foundCart")
                    .defaultIfEmpty(new Cart(cartId))
                    .log("emptyCart")
                    .flatMap(cart -> cart.getCartItems().stream()
                            .filter(cartItem -> cartItem.getItem()
                                .getId().equals(itemId))
                    .findAny()
                    .map(cartItem -> {
                        cartItem.increment();
                        return Mono.just(cart).log("addedCartItem");
                    })
                    .orElseGet(() -> {
                            return itemRepository.findById(itemId)
                                    .log("fetchedItem")
                                    .map(CartItem::new) //
                                    .log("cartItem") //
                                    .map(cartItem -> {
                                            cart.getCartItems().add(cartItem);
                                            return cart;
                                    }).log("newCartItem");
                    }))
                .log("cartWithAnotherItem") //
                .flatMap(cartRepository::save) //
                .log("savedCart");
    }

}
