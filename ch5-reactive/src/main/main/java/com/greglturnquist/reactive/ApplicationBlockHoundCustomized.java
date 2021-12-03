package com.greglturnquist.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thymeleaf.TemplateEngine;
import reactor.blockhound.BlockHound;

// @SpringBootApplication
public class ApplicationBlockHoundCustomized {
    public static void main(String[] args) {
        // 블록하운드 등록
        // 자바 에이전트 API로 블로킹 메소드를 검출하, 블로킹 메소드를 허용하는지 검사
        // BlockHound.install();

        // 블로킹메서드를 호출하는 메서드를 허용리스트에 추가
        BlockHound.builder()
                .allowBlockingCallsInside(
                        TemplateEngine.class.getCanonicalName(), "process")
                .install();

        // run 앞에 위치하여 바이트 코드 조작
        SpringApplication.run(ApplicationBlockHoundCustomized.class, args);
    }


    // java.io.FileInputStream#readBytes
    // readBytes() 호출이 블로킹 코드 => TemplateEngine.process 에서 호출(템플릿 파일 처리)
}
