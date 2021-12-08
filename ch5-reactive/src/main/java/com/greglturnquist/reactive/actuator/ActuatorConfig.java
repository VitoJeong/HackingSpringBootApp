package com.greglturnquist.reactive.actuator;

import org.bson.Document;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * Actuator 관련된 빈을 등록하기 위한 설정 클래스
 */
@Configuration
public class ActuatorConfig {

    /**
     * InMemoryHttpTraceRepository
     * 
     * 애플리케이션을 누가 어떻게 호출하는지 쉽게 볼 수 있도록하는 기능을 제공하는
     * HttpTraceRepository의 대표적인 구현체(Memory 기반)
     * 
     * @return 
     */
    @Bean
    HttpTraceRepository traceRepository(HttpTraceWrapperRepository repository) {
        return new SpringDataHttpTraceRepository(repository);
    }

    /**
     * MongoDB Document를 HttpTraceWrapper 로 변환하는 정적컨버터
     */
    static Converter<Document, HttpTraceWrapper> CONVERTER =
            new Converter<Document, HttpTraceWrapper>() { 

                /**
                 * HttpTrace의 생성자로 접근해  HttpTraceWrapper 객체를 생성해 반환
                 *
                 * @param document
                 * @return
                 */
                @Override
                public HttpTraceWrapper convert(Document document) {
                    Document httpTrace = document.get("httpTrace", Document.class);
                    Document request = httpTrace.get("request", Document.class);
                    Document response = httpTrace.get("response", Document.class);


                    return new HttpTraceWrapper(new HttpTrace( //
                            new HttpTrace.Request( //
                                    request.getString("method"), //
                                    URI.create(request.getString("uri")), //
                                    request.get("headers", Map.class), //
                                    null),
                            new HttpTrace.Response( //
                                    response.getInteger("status"), //
                                    response.get("headers", Map.class)),
                            httpTrace.getDate("timestamp").toInstant(), //
                            null, //
                            null, //
                            httpTrace.getLong("timeTaken")));
                }

            };

    /**
     * 스프링 컨버터로 등록
     * 
     * @param context 
     * @return
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoMappingContext context) {

        MappingMongoConverter mappingConverter = //
                new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context); // <1>

        // MongoCustomConversions 객체를 mappingConverter에 설정
        mappingConverter.setCustomConversions( // <2>
                new MongoCustomConversions(Collections.singletonList(CONVERTER))); // <3>

        return mappingConverter;
    }
}
