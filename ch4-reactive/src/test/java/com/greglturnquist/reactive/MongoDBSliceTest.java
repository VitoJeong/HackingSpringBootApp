package com.greglturnquist.reactive;

import com.greglturnquist.reactive.repository.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

// 스프링데이터 몽고 DB 테스트 관련기능 활성화 - @ExtendWith({SpringExtension.class}) 가 포함
@DataMongoTest
public class MongoDBSliceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void itemRepositorySaveItems() {
        // 테스트 데이터 정의
        String sampleId = "item1";
        String sampleName = "TV tray";
        String sampleDescription = "Alf TV tray";
        Double samplePrice = 19.25;

        Item sampleItem =
                new Item(sampleId, sampleName, sampleDescription, samplePrice);

        itemRepository.save(sampleItem)
                .as(StepVerifier::create)
                .expectNextMatches(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getName()).isEqualTo(sampleName);
                    assertThat(item.getDescription()).isEqualTo(sampleDescription);
                    assertThat(item.getPrice()).isEqualTo(samplePrice);

                    return true;
                })
                .verifyComplete();
    }
}
