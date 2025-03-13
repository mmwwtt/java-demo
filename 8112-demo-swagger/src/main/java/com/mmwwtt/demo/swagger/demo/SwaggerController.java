package com.mmwwtt.demo.swagger.demo;

import com.mmwwtt.demo.common.dto.BaseInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * http://localhost:8112/swagger-ui.html
 */
@RestController
@RequestMapping("/swagger")
@Tag(name = "基本信息管理", description = "基本信息相关操作")
public class SwaggerController {
    @GetMapping("/baseInfo/queryAll")
    @Operation(summary = "查询所有基本信息", description = "查询所有基本信息")
    public List<BaseInfoDTO> queryAll() {
        return BaseInfoDTO.getPresetList();
    }

    @PostMapping("/baseInfo/save")
    @Operation(summary = "保存基本信息", description = "保存基本信息")
    public BaseInfoDTO save(@Parameter(description = "基本信息实体", example = "")
                            @RequestBody BaseInfoSwagger baseInfo) {
        return BaseInfoDTO.getInstance();
    }

    @GetMapping("/baseInfo/queryById/{id}")
    @Operation(summary = "根据ID查询基本信息", description = "根据ID查询基本信息",
            responses = {
                    @ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "404", description = "用户不存在")
            })

    public BaseInfoDTO queryById(@Parameter(description = "基本信息ID", example = "1")
                                 @PathVariable Long baseInfoId) {
        return BaseInfoDTO.getInstance();
    }

    @DeleteMapping("/baseInfo/delete/{id}")
    @Operation(summary = "根据ID删除基本信息", description = "根据ID删除基本信息")
    public Boolean delete(@Parameter(description = "基本信息ID", example = "1")
                          @PathVariable Long baseInfoId) {
        return true;
    }
}
