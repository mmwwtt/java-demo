package com.mmwwtt.demo.mybatis.demo;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mybatis/base-info")
@Slf4j
public class DemoController {


    @Resource
    private BaseInfoService baseInfoService;

    @PostMapping("/getByQuery")
    public List<BaseInfo> getByQuery(@RequestBody BaseInfo baseInfo) {
        return this.baseInfoService.getByQuery(baseInfo);
    }

    @PostMapping("/getPageByQuery")
    public PageInfo<BaseInfo> getPageByQuery(@RequestBody BaseInfo baseInfo,
                                             @RequestParam("pageNum") int pageNum,
                                             @RequestParam("pageSize") int pageSize) {
        return baseInfoService.getPageByQuery(baseInfo, pageNum, pageSize);
    }
    @PostMapping("/add")
    public BaseInfo add(@RequestBody BaseInfo baseInfo) {
        return baseInfoService.add(baseInfo);
    }
    @PostMapping("/addAll")
    public List<BaseInfo>  addAll(@RequestBody List<BaseInfo> list) {
        return baseInfoService.addAll(list);
    }
    @PostMapping("/update")
    public BaseInfo update(@RequestBody BaseInfo baseInfo) {
        return baseInfoService.update(baseInfo);
    }
    @PostMapping("/updateAll")
    public List<BaseInfo>  updateAll(@RequestBody List<BaseInfo> list) {
        return baseInfoService.updateAll(list);
    }

}
