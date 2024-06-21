package io.hhplus.tdd.point;

import io.hhplus.tdd.IntegratedTest;
import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.UserPoint;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class PointControllerIntegratedTest extends IntegratedTest {

    @Autowired
    PointService pointService;

    private static final String PATH = "/point";

    @Test
    @DisplayName("포인트를 조회한다.")
    void point() {
        // given
        long id = 1L;

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .when().get(PATH + "/" + id)
                .then().log().all().extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("포인트 내역을 조회한다.")
    void history() {
        // given
        long id = 1L;
        pointService.charge(id, 20L);
        pointService.use(id, 10L);
        pointService.charge(id, 10L);

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .when().get(PATH + "/" + id + "/histories")
                .then().log().all().extract();

        // then
        List<PointHistory> pointHistoryList = pointService.findAllPointById(id);
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(pointHistoryList).hasSize(3);
    }

    @Test
    @DisplayName("동시에 10포인트씩 10번 충전한다.")
    void charge() {
        // given
        long id = 1L;
        long amount = 10L;

        // when
        int cnt = 10;
        CompletableFuture<?>[] futureArray = new CompletableFuture[cnt];
        for (int i = 0; i < 10; i++) {
            futureArray[i] = CompletableFuture.runAsync(() ->
                    RestAssured
                    .given().log().all()
                    .body(amount)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().patch(PATH + "/" + id + "/charge")
                    .then().log().all().extract());
        }
        CompletableFuture.allOf(futureArray).join();

        // then
        UserPoint userPoint = pointService.findPointById(id);
        assertThat(userPoint.point()).isEqualTo(amount * 10);
    }

    @Test
    @DisplayName("동시에 10포인트씩 10번 사용한다.")
    void use() throws Exception {
        // given
        long id = 1L;
        long amount = 10L;
        // 100 포인트 충전
        pointService.charge(id, 100L);

        // when
        int cnt = 10;
        CompletableFuture<?>[] futureArray = new CompletableFuture[cnt];
        for (int i = 0; i < 10; i++) {
            futureArray[i] = CompletableFuture.runAsync(() -> {
                RestAssured
                        .given().log().all()
                        .body(amount)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().patch(PATH + "/" + id + "/use")
                        .then().log().all().extract();
            });
        }
        CompletableFuture.allOf(futureArray).join();

        // then
        UserPoint userPoint = pointService.findPointById(id);
        assertThat(userPoint.point()).isZero();
    }

    @Test
    @DisplayName("동시성 테스트 - 동시에 포인트 충전/차감 한다.")
    void concurrencyPointChargeAndUse2() {
        // given
        long id = 1L;
        long 보유포인트 = 100000L;
        long 사용포인트 = 10000L;
        long 충전포인트 = 10000L;
        long 사용포인트2 = 100000L;
        pointService.charge(id, 보유포인트);

        // when
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() ->
                        RestAssured
                                .given().log().all()
                                .body(사용포인트)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .when().patch(PATH + "/" + id + "/use")
                                .then().log().all().extract()),
                CompletableFuture.runAsync(() ->
                        RestAssured
                                .given().log().all()
                                .body(충전포인트)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .when().patch(PATH + "/" + id + "/charge")
                                .then().log().all().extract()),
                CompletableFuture.runAsync(() ->
                        RestAssured
                                .given().log().all()
                                .body(사용포인트2)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .when().patch(PATH + "/" + id + "/use")
                                .then().log().all().extract())
        ).join();

        // then
        UserPoint userPoint = pointService.findPointById(1);
        assertThat(userPoint.point()).isEqualTo(보유포인트 - 사용포인트 + 충전포인트 - 사용포인트2);
    }
}
