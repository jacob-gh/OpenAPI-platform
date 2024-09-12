package com.jacoe.openapi.common.service;

import com.jacoe.openapi.common.model.entity.InterfaceInfo;

import java.util.List;

/**
* @author jacoe
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-06-21 15:26:08
*/
public interface InnerInterfaceInfoService  {
    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    InterfaceInfo getInterfaceInfo(String path, String method, List<String> params);


}
