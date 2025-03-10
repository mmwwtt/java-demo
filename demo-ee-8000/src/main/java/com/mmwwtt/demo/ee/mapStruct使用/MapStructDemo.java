package com.mmwwtt.demo.ee.mapStruct使用;

import com.mmwwtt.demo.common.dto.BaseInfoDTO;
import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.response.ApiResponse;
import com.mmwwtt.demo.common.util.CommonUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/service/demo/ee/mapStruct")
public class MapStructDemo {
    BaseInfoConverter baseInfoConverter = BaseInfoConverter.INSTANCE;
    @PostMapping("/mapStructDemo")
    public ApiResponse<Void> mapStructDemo() {
        BaseInfoDTO baseInfoDTO = BaseInfoDTO.getPresetSingle1();
        BaseInfo baseInfo = baseInfoConverter.converter(baseInfoDTO);

        List<BaseInfo> list = baseInfoConverter.converter(Collections.singletonList(baseInfoDTO));
        CommonUtils.println(baseInfo, list);
        return ApiResponse.success();
    }
}
