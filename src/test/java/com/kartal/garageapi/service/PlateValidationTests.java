package com.kartal.garageapi.service;

import com.kartal.garageapi.util.PlateFunctions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PlateValidationTests {

    @Test
    void plateUpperCaseValidationTest() {
        String plate = "16-JGR-42";
        assertThat(PlateFunctions.isValidPlate(plate)).isTrue();
    }

    @Test
    void plateLowerCaseValidationTest() {
        String plate = "16-jgr-42";
        assertThat(PlateFunctions.isValidPlate(plate)).isTrue();
    }

    @Test
    void plateFormatValidationTests() {
        assertThat(PlateFunctions.isValidPlate("16jgr42")).isFalse();
        assertThat(PlateFunctions.isValidPlate("16213SD24378")).isFalse();
        assertThat(PlateFunctions.isValidPlate("1JGR42")).isFalse();
        assertThat(PlateFunctions.isValidPlate("16JGR4")).isFalse();
    }

}
