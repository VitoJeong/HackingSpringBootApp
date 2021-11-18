package com.greglturnquist.ch1reactive;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {

    /**
     * 문자열을 리액티브 컨테이너인 Mono에 담아서 반환한다.
     *
     * Mono: Spring Reactor Publisher 중 하나.
     *       0 또는 1개의  next 신호를 발생할 수 있고 complete나 error 신호를 발생하거나 발생하지 않을 수 있다.
     *
     * @return
     */
    @GetMapping// 기본값인 "/" 요청 처리
    Mono<String> home() {
        // home이라는 문자열을 Mono.just()로 감싸서 반환한다.
        // Thymeleaf 기본 설정
        // prefix: classpath:/templates
        // suffix: .html
        return Mono.just("home");
    }
}
