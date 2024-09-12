package com.jacoe.openapi.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jacoe.openapi.backend.common.ErrorCode;
import com.jacoe.openapi.backend.exception.BusinessException;
import com.jacoe.openapi.backend.service.InterfaceInfoService;
import com.jacoe.openapi.backend.mapper.InterfaceInfoMapper;
import com.jacoe.openapi.common.model.entity.InterfaceInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author jacoe
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2024-06-21 15:26:08
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if(interfaceInfo==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (add) {
            if (StringUtils.isBlank(interfaceInfo.getName())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }


    }
}




