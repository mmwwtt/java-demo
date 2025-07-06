package com.lititi.exams.commons2.service;

import com.lititi.exams.commons2.exception.LttException;
import com.lititi.exams.commons2.utils.NumberGenerator;

public interface BaseService {

    default Long generateNumber() {
        Long existId = null;
        Long number = null;
        for(int i = 0;i < 2;i++){
            number = NumberGenerator.generateRandomNumber();
            existId = selectIdByNumber(number);
            if(existId == null){
                break;
            }
        }
        if(existId != null){
            throw new LttException("生成number失败");
        }
        return number;
    }

    Long selectIdByNumber(long number);
}
