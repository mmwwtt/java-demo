package com.lititi.exams.web.controller;

import com.lititi.exams.commons2.object.CommonResultObject;
import com.lititi.exams.web.dto.MomentAddDto;
import com.lititi.exams.web.dto.MomentDto;
import com.lititi.exams.web.entity.Moment;
import com.lititi.exams.web.service.MomentService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/test/moment")
public class MomentController extends BaseController{

    @Autowired
    private MomentService momentService;

    private static final Integer PAGE_SIZE = 10;

    /**
     * 分页获取动态信息
     * @param userId
     * @param pageNum
     * @return
     */
    @GetMapping("/getPage")
    public CommonResultObject getPage(@RequestParam("userId") Long userId, @RequestParam("pageNum") Integer pageNum) {
        // 从上下文中获取userId
        // userId =

        CommonResultObject resultObject = new CommonResultObject();
        List<MomentDto>  momentDtoList = momentService.getPage(userId, pageNum, PAGE_SIZE);
        return resultObject.addObject("context", momentDtoList);
    }

    /**
     * 新增动态
     * @param momentAddDto
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/add")
    public CommonResultObject addMoment(@RequestBody @Validated MomentAddDto momentAddDto) throws InvocationTargetException,
            IllegalAccessException {
        // 从上下文中获取userId
        // momentAddDto.setUserId();

        Moment moment = new Moment();
        BeanUtils.copyProperties(moment,momentAddDto);
        momentService.addMoment(moment);

        CommonResultObject resultObject = new CommonResultObject();
        return resultObject.addObject("context", moment);
    }

    @PostMapping("/addMomentToCache")
    public CommonResultObject addMomentToCache() throws ExecutionException, InterruptedException {
        momentService.getMomentByDays();
        CommonResultObject resultObject = new CommonResultObject();
        return resultObject.addObject("context", "success");
    }
}
