package com.greglturnquist.ch2reactive.repository;

import com.greglturnquist.ch2reactive.Item;
import org.springframework.data.repository.CrudRepository;

/**
 * 블로킹 리포지토리
 * 블로킹 코드를 실제 운영환경에서는 사용해서는 안 된다!
 */
public interface BlockingItemRepository extends CrudRepository<Item, String> {
}
