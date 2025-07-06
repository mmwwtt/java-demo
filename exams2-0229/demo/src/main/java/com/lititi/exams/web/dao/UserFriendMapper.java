package com.lititi.exams.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface User friend mapper.
 */
@Mapper
public interface UserFriendMapper {
    /**
     * 根据用户id找朋友id
     */
    List<Long> getUserFriendId(@Param("userId") Long userId);
}
