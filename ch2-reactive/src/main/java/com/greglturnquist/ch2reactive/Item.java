package com.greglturnquist.ch2reactive;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * 판매상품
 * Inventory Item
 */
@Data
public class Item {

    // 상품 일련번호
    // @Id: 해당필드를 ObjectId 필드로 지정
    @Id
    private String id;
    // 설명
    private String name;
    // 가격
    private double price;

    private Item() {}

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }
}
