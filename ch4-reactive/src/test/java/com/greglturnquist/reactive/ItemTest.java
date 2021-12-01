package com.greglturnquist.reactive;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class ItemTest {

    @Test
    void itemBasicsShouldWork() {

        String sampleId = "item1";
        String sampleName = "TV tray";
        String sampleDescription = "Alf TV tray";
        Double samplePrice = 19.25;

        Item sample = new Item(sampleId, sampleName, sampleDescription, samplePrice);

        assertThat(sample.getId()).isEqualTo(sampleId);
        assertThat(sample.getName()).isEqualTo(sampleName);
        assertThat(sample.getDescription()).isEqualTo(sampleDescription);
        assertThat(sample.getPrice()).isEqualTo(samplePrice);

        Item secondSample = new Item(sampleId, sampleName, sampleDescription, samplePrice);

        assertThat(sample).isEqualTo(secondSample);
    }

}