package com.jacoe.openapi.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jacoe.openapi.backend.common.ErrorCode;
import com.jacoe.openapi.backend.exception.BusinessException;
import com.jacoe.openapi.backend.service.UserInterfaceInfoService;
import com.jacoe.openapi.common.model.entity.UserInterfaceInfo;
import com.jacoe.openapi.common.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    /**
     * 更新调用次数
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if(interfaceInfoId<=0 ||userId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
        userInterfaceInfoQueryWrapper.eq("userId", userId);
        userInterfaceInfoQueryWrapper.eq("interfaceInfoId", interfaceInfoId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(userInterfaceInfoQueryWrapper);
        if(userInterfaceInfo==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新调用次数失败");
        }
        if(userInterfaceInfo.getLeftNum()<=0){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"该接口调用次数已耗尽");
        }
        userInterfaceInfo.setLeftNum(userInterfaceInfo.getLeftNum()-1);
        userInterfaceInfo.setTotalNum(userInterfaceInfo.getTotalNum()+1);
        return userInterfaceInfoService.updateById(userInterfaceInfo);



    }
}
