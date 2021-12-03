package com.greglturnquist.reactive.repository;

import com.greglturnquist.reactive.Cart;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * Cart용 리액티브 데이터 리포지토리
 */
public interface CartRepository extends ReactiveCrudRepository<Cart, String> {
}
