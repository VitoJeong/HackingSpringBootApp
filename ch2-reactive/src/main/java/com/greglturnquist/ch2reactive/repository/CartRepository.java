package com.greglturnquist.ch2reactive.repository;

import com.greglturnquist.ch2reactive.Cart;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * Cart용 리액티브 데이터 리포지토리
 */
public interface CartRepository extends ReactiveCrudRepository<Cart, String> {
}
