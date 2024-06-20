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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    void charge() throws InterruptedException {
        // given
        long id = 1L;
        long amount = 10L;

        // when
        int threadCount = 10;
        // 병렬 작업 처리
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        // 일정 개수의 스레드가 끝난 후 다음 쓰레드가 실행될 수 있도록 대기시켜주는 역할
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    RestAssured
                            .given().log().all()
                            .body(amount)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when().patch(PATH + "/" + id + "/charge")
                            .then().log().all().extract();
                } finally {
                    latch.countDown(); // 스레드가 끝날 때 마다 카운트 감소
                }
            });
        }
        latch.await(); // 대기 상태 해제

        // then
        UserPoint userPoint = pointService.findPointById(id);
        assertThat(userPoint.point()).isEqualTo(amount * threadCount);
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
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    RestAssured
                            .given().log().all()
                            .body(amount)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when().patch(PATH + "/" + id + "/use")
                            .then().log().all().extract();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        UserPoint userPoint = pointService.findPointById(id);
        assertThat(userPoint.point()).isZero();
    }

    @Test
    @DisplayName("포인트 충전과 사용을 동시에 호출한다.")
    void concurrencyPointChargeAndUse() throws InterruptedException {
        // given
        long id = 1L;
        long amount = 10L;

        // when
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(20);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    RestAssured
                            .given().log().all()
                            .body(amount)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when().patch(PATH + "/" + id + "/charge")
                            .then().log().all().extract();
                } finally {
                    latch.countDown();
                }
            });
        }
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    RestAssured
                            .given().log().all()
                            .body(amount)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when().patch(PATH + "/" + id + "/use")
                            .then().log().all().extract();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        UserPoint userPoint = pointService.findPointById(id);
        assertThat(userPoint.point()).isZero();
    }
}
