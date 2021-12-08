package com.greglturnquist.reactive.actuator;

import org.springframework.data.repository.Repository;

import java.util.stream.Stream;

/**
 * HttpTraceWrapper 객체를 MongoDB에 저장하기 위한 스프링데이터 Repository
 */
public interface HttpTraceWrapperRepository extends Repository<HttpTraceWrapper, String> {

    // 자바 Stream을 반환
    Stream<HttpTraceWrapper> findAll();

    void save(HttpTraceWrapper trace);

}
