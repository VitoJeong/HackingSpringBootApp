package com.greglturnquist.reactive.commerce.item;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


/**
 * Item용 리액티브 데이터 리포지토리
 * 첫번째 제네릭 파라미터: 리포지토리가 저장하고 조회할 타입
 * 두번째 제네릭 파라미터: 데이터의 식별자의 타입
 */
public interface ItemRepository extends ReactiveCrudRepository<Item, String>, ReactiveQueryByExampleExecutor<Item> {


    /**
     * 검색어로 상품 목록을 조회(대소문자 구분없이 상품명에 부분일치)
     * @param partialName 
     * @return
     */
    Flux<Item> findByNameContainingIgnoreCase(String partialName);

    // search by description
    Flux<Item> findByDescriptionContainingIgnoreCase(String partialName);

    // search by name AND description
    Flux<Item> findByNameContainingAndDescriptionContainingAllIgnoreCase(String partialName, String partialDesc);

    // search by name OR description
    Flux<Item> findByNameContainingOrDescriptionContainingAllIgnoreCase(String partialName, String partialDesc);

    // /**
    //  * 직접 작성한 쿼리문을 사용
    //  * @param name
    //  * @param age
    //  * @return
    //  */
    // @Query("{'name': ?0, 'age': ?1}")
    // Flux<Item> findItemsForCustomerMonthlyReport(String name, int age);
    //
    //
    // /**
    //  * @Query: 개발자가 직접 명시한 쿼리문을 사용하는 어노테이션
    //  * @return
    //  */
    // @Query(sort = "{'age': -1}")
    // Flux<Item> findSortedStuffForWeeklyReport();
}
