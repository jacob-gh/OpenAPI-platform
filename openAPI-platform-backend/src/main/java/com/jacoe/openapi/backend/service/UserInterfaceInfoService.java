package com.jacoe.openapi.backend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jacoe.openapi.common.model.entity.UserInterfaceInfo;

/**
* @author jacoe
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-06-27 17:26:53
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);
}
