package com.greglturnquist.ch2reactive;

import lombok.Data;
import lombok.NoArgsConstructor;

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
}
