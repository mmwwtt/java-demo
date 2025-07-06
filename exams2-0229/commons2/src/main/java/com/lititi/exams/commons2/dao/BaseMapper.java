package com.lititi.exams.commons2.dao;

import com.lititi.exams.commons2.annotation.Master;

public interface BaseMapper {

    @Master
    Long selectIdByNumber(long number);

}
