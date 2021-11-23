package com.greglturnquist.reactive;

import lombok.Data;

/**
 * 구매 상품
 * Item in a Cart
 */
@Data
public class CartItem {

    // 상품
    private Item item;
    // 구매 수량
    private int quantity;

    private CartItem() {

    }

    public CartItem(Item item) {
        this.item = item;
        this.quantity = 1;
    }

    public void increment() {
        this.quantity++;
    }
}
