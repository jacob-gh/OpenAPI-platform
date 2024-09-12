package com.jacoe.openapi.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jacoe.openapi.backend.common.ErrorCode;
import com.jacoe.openapi.backend.exception.BusinessException;
import com.jacoe.openapi.backend.service.UserService;
import com.jacoe.openapi.common.model.entity.User;
import com.jacoe.openapi.common.service.InnerUserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserService userService;

    @Override
    public User getInvokeUser(String accessKey) {
        if (accessKey == null || accessKey.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("accesskey", accessKey);
        return userService.getOne(userQueryWrapper);


    }
}
