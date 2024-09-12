package com.jacoe.openapi.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jacoe.openapi.backend.common.ErrorCode;
import com.jacoe.openapi.backend.exception.BusinessException;
import com.jacoe.openapi.backend.service.InterfaceInfoService;
import com.jacoe.openapi.common.model.entity.InterfaceInfo;
import com.jacoe.openapi.common.service.InnerInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method, List<String> params) {

        int[] arr = {1, 2, 3, 4, 5};



        if(StringUtils.isAnyBlank(url,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> interfaceInfoQueryWrapper = new QueryWrapper<>();
        interfaceInfoQueryWrapper.eq("url", url);
        interfaceInfoQueryWrapper.eq("method", method);
        //interfaceInfoQueryWrapper.eq("params", params);
        return interfaceInfoService.getOne(interfaceInfoQueryWrapper);
    }
}
