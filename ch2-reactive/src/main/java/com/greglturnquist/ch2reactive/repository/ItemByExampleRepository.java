package com.greglturnquist.ch2reactive.repository;

import com.greglturnquist.ch2reactive.Item;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

/**
 * Example 쿼리를 사용할 수 있는 ReactiveQueryByExampleExecutor를 상속받은 리포지토리
 */
public interface ItemByExampleRepository extends ReactiveQueryByExampleExecutor<Item> {


}
