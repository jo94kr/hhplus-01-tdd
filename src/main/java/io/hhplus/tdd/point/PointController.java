package io.hhplus.tdd.point;

import io.hhplus.tdd.point.dto.FindPointHistoryApiResDto;
import io.hhplus.tdd.point.dto.FindUserPointApiResDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public FindUserPointApiResDto point(
            @PathVariable(name = "id") long id
    ) {
        log.info("id: {}", id);
        return FindUserPointApiResDto.of(pointService.findPointById(id));
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<FindPointHistoryApiResDto> history(
            @PathVariable(name = "id") long id
    ) {
        log.info("id: {}", id);
        return pointService.findAllPointById(id).stream()
                .map(FindPointHistoryApiResDto::new)
                .toList();
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("/{id}/charge")
    public FindUserPointApiResDto charge(
            @PathVariable(name = "id") long id,
            @RequestBody long amount
    ) {
        log.info("id: {}, amount: {}", id, amount);
        return FindUserPointApiResDto.of(pointService.charge(id, amount));
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public FindUserPointApiResDto use(
            @PathVariable(name = "id") long id,
            @RequestBody long amount
    ) {
        log.info("id: {}, amount: {}", id, amount);
        return FindUserPointApiResDto.of(pointService.use(id, amount));
    }
}
