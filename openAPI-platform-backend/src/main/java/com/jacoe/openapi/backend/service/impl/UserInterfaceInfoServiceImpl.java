package com.jacoe.openapi.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.jacoe.openapi.backend.common.ErrorCode;
import com.jacoe.openapi.backend.exception.BusinessException;
import com.jacoe.openapi.backend.service.UserInterfaceInfoService;
import com.jacoe.openapi.backend.mapper.UserInterfaceInfoMapper;
import com.jacoe.openapi.common.model.entity.UserInterfaceInfo;
import org.springframework.stereotype.Service;

/**
* @author jacoe
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2024-06-27 17:26:53
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if(userInterfaceInfo==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "调用次数已耗尽");
        }

    }
}




