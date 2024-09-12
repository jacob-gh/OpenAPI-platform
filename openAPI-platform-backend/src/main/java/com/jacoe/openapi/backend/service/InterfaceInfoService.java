package com.jacoe.openapi.backend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jacoe.openapi.common.model.entity.InterfaceInfo;

/**
* @author jacoe
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-06-21 15:26:08
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
