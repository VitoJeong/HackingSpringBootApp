package com.greglturnquist.reactive.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 스프링데이터 Repository를 사용하는 HttpTraceRepository 구현 클래스
 */
@RequiredArgsConstructor
public class SpringDataHttpTraceRepository implements HttpTraceRepository {

    private final HttpTraceWrapperRepository repository;


    /**
     * HttpTraceWrapper 스트림을 받아 HttpTrace 객체를 리스트에 담아 반환
     * @return 
     */
    @Override
    public List<HttpTrace> findAll() {
        return repository.findAll()
                .map(HttpTraceWrapper::getHttpTrace)
                .collect(Collectors.toList());
    }

    /**
     * HttpTraceWrapper를 새로 생성한 후 MongoDB에 저장
     * 
     * @param trace 
     */
    @Override
    public void add(HttpTrace trace) {
        repository.save(new HttpTraceWrapper(trace));
    }

}
