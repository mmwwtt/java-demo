package com.lititi.exams.web.service;

import com.lititi.exams.web.dto.MomentDto;
import com.lititi.exams.web.entity.Moment;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * The interface Moment service.
 */
public interface MomentService {

    /**
     * 分页查询动态
     *
     * @param userId   the user id
     * @param pageNum  the page num
     * @param pageSize the page size
     * @return page
     */
    List<MomentDto> getPage(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 新增动态
     *
     * @param moment the moment
     * @return moment
     */
    Moment addMoment(Moment moment);

    /**
     * 同步近10天的动态
     *
     * @return moment
     */

    Boolean getMomentByDays() throws ExecutionException, InterruptedException;
}
