package com.lititi.exams.web.dao;

import com.lititi.exams.web.dto.MomentDto;
import com.lititi.exams.web.entity.Moment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Moment mapper.
 */
@Mapper
public interface MomentMapper {
    /**
     * 根据动态id返回图片路径列表
     */
    List<String> getImgByMomentId(@Param(("momentId")) Long momentId);

    /**
     * 根据朋友id列表 返回朋友圈动态
     */
    List<MomentDto> getByQuery(@Param("userFriendList") List<Long> userFriendList);

    /**
     * 新增动态
     */
    void addMoment(@Param("moment") Moment moment);

    /**
     * 批量新增动态图片
     */
    void batchAddMomentImg(@Param("momentId") Long momentId, @Param("imgPathList") List<String> imgPathList);

    List<MomentDto> getMomentByDays(@Param("dayNum") Integer dayNum);
    Integer countMomentByDays(@Param("dayNum") Integer dayNum);
}
