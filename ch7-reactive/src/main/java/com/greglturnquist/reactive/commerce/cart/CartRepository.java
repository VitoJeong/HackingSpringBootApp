package com.greglturnquist.reactive.commerce.cart;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * Cart용 리액티브 데이터 리포지토리
 */
public interface CartRepository extends ReactiveCrudRepository<Cart, String> {
}
