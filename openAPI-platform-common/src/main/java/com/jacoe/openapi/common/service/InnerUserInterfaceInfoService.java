package com.jacoe.openapi.common.service;


/**
* @author jacoe
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
*
*/
public interface InnerUserInterfaceInfoService  {
    /**
     * 接口调用次数统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

}
