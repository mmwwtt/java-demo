package com.mmwwtt.demo.ee.mapStruct;

import com.mmwwtt.demo.common.dto.BaseInfoDTO;
import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.util.CommonUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MapStructTest {

    @Test
    public void test() {
        BaseInfo baseInfo = BaseInfo.getInstance();
        BaseInfoDTO baseInfoDTO = BaseInfoConverter.INSTANCE.converterToDTO(baseInfo);

        List<BaseInfo> baseInfoList = BaseInfo.getPresetList();
        List<BaseInfoDTO> baseInfoDTOlist = BaseInfoConverter.INSTANCE.converterToDTO(baseInfoList);
        CommonUtils.println(baseInfoDTO, baseInfoDTOlist);
    }
}
