package com.greglturnquist.ch2reactive;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * 장바구니
 * Cart
 */
@Data
public class Cart {

    // 장바구니 식별자
    @Id
    private String id;
    // 장바구니에 담긴 상품 목록
    private List<CartItem> cartItems;

    private Cart() {

    }

    public Cart(String id) {
        this(id, new ArrayList<>());
    }

    public Cart(String id, List<CartItem> cartItems) {
        this.id = id;
        this.cartItems = cartItems;
    }
}
