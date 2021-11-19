package com.greglturnquist.ch2reactive.repository;

import com.greglturnquist.ch2reactive.Item;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


/**
 * Item용 리액티브 데이터 리포지토리
 * 첫번째 제네릭 파라미터: 리포지토리가 저장하고 조회할 타입
 * 두번째 제네릭 파라미터: 데이터의 식별자의 타입
 */
public interface ItemRepository extends ReactiveCrudRepository<Item, String> {
}
