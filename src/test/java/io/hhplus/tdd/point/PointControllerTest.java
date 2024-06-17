package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PointService pointService;

    private static final String PATH = "/point";

    @Test
    @DisplayName("포인트를 조회한다.")
    void point() throws Exception {
        // given
        long id = 1L;

        // when
        ResultActions result = mockMvc.perform(get(PATH + "/" + id));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(0))
                .andExpect(jsonPath("$.updateMillis").value(0))
        ;
    }

    @Test
    @DisplayName("포인트 내역을 조회한다.")
    void history() throws Exception {
        // given
        long id = 1L;

        // when
        ResultActions result = mockMvc.perform(get(PATH + "/" + id + "/histories"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
        ;
    }

    @Test
    @DisplayName("포인트를 충전한다.")
    void charge() throws Exception {
        // given
        long id = 1L;
        long amount = 1000L;

        // when
        ResultActions result = mockMvc.perform(patch(PATH + "/" + id + "/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(amount))
        );

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(amount))
                .andExpect(jsonPath("$.updateMillis").value(0))
        ;
    }

    @Test
    @DisplayName("포인트를 사용한다.")
    void use() throws Exception {
        // given
        long id = 1L;
        long amount = 100L;

        // when
        ResultActions result = mockMvc.perform(patch(PATH + "/" + id + "/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(amount))
        );

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(900))
                .andExpect(jsonPath("$.updateMillis").value(0))
        ;
    }
}