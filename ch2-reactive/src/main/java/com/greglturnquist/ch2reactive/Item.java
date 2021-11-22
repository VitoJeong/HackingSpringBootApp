package com.greglturnquist.ch2reactive;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;

import java.util.Date;

/**
 * 판매상품
 * Inventory Item
 */
@Data
@AllArgsConstructor
public class Item {

    // 상품 일련번호
    // @Id: 해당필드를 ObjectId 필드로 지정
    @Id
    private String id;
    // 설명
    private String name;
    private String description;
    private double price;
    private String distributorRegion;
    private Date releaseDate;
    private int availableUnits;
    private Point location;
    private boolean active;

    private Item() {}

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Item(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
