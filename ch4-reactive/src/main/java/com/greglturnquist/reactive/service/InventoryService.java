package com.greglturnquist.reactive.service;

import com.greglturnquist.reactive.Cart;
import com.greglturnquist.reactive.CartItem;
import com.greglturnquist.reactive.Item;
import com.greglturnquist.reactive.repository.CartRepository;
import com.greglturnquist.reactive.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class InventoryService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    public Mono<Cart> getCart(String cartId) {
        return this.cartRepository.findById(cartId);
    }

    public Flux<Item> getInventory() {
        return this.itemRepository.findAll();
    }

    public Mono<Item> saveItem(Item newItem) {
        return this.itemRepository.save(newItem);
    }

    public Mono<Void> deleteItem(String id) {
        return this.itemRepository.deleteById(id);
    }

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

    public Mono<Cart> removeOneFromCart(String cartId, String itemId) {
        return this.cartRepository.findById(cartId)
                .defaultIfEmpty(new Cart(cartId))
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                        .findAny()
                        .map(cartItem -> {
                            cartItem.decrement();
                            return Mono.just(cart);
                        }) //
                        .orElse(Mono.empty()))
                .map(cart -> new Cart(cart.getId(), cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getQuantity() > 0)
                        .collect(Collectors.toList())))
                .flatMap(cart -> this.cartRepository.save(cart));
    }
}
