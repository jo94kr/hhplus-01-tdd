package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String PATH = "/point";

    @Test
    @DisplayName("포인트를 조회한다.")
    void point() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("포인트 내역을 조회한다.")
    void history() {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("포인트를 충전한다.")
    void charge() {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("포인트를 사용한다.")
    void use() {
        // given

        // when

        // then

    }
}
