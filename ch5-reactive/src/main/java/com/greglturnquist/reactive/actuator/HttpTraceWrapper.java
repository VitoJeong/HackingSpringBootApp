package com.greglturnquist.reactive.actuator;

import lombok.Getter;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.data.annotation.Id;

/**
 * HttpTrace 객체를 MongoDB에 저장하기 위한 래퍼 클래스
 */
@Getter
public class HttpTraceWrapper {

    @Id
    private String id;

    private HttpTrace httpTrace;

    public HttpTraceWrapper(HttpTrace httpTrace) {
        this.httpTrace = httpTrace;
    }
}
