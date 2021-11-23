package com.greglturnquist.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thymeleaf.TemplateEngine;
import reactor.blockhound.BlockHound;

// @SpringBootApplication
public class ApplicationBlockHound {
    public static void main(String[] args) {
        // 블록하운드 등록
        // 자바 에이전트 API로 블로킹 메소드를 검출하, 블로킹 메소드를 허용하는지 검사
        BlockHound.install();

        // run 앞에 위치하여 바이트 코드 조작
        SpringApplication.run(ApplicationBlockHound.class, args);
    }

    // java.io.FileInputStream#readBytes
}
