package com.jacob.opanapi.gateway;

import com.jacob.opanapi.clientsdk.utils.SignUtils;
import com.jacoe.openapi.common.model.entity.InterfaceInfo;
import com.jacoe.openapi.common.model.entity.User;
import com.jacoe.openapi.common.service.InnerInterfaceInfoService;
import com.jacoe.openapi.common.service.InnerUserInterfaceInfoService;
import com.jacoe.openapi.common.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class RequestLogGlobalFilter implements GlobalFilter, Ordered {
    private static final List<String> IP_WHITE_LIST= Arrays.asList("127.0.0.1");
    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //打印日志
        String uri = request.getURI().toString();
        String method = request.getMethodValue();
        String remoteAddress = request.getRemoteAddress().getHostString();
        MultiValueMap<String, String> params = request.getQueryParams();
        String paramsString = params.toString();


        log.info("请求uri：{}",uri);
        log.info("请求方法：{}",method);
        log.info("请求源地址：{}",remoteAddress);


        log.info("请求参数：{}", params);
        //访问控制（黑白名单）
        if(!IP_WHITE_LIST.contains(remoteAddress)){
            return noHandleAuth(response);
        }
        //校验签名
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String body = headers.getFirst("body");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        User invokeUser = innerUserService.getInvokeUser(accessKey);
        if(invokeUser==null){
            return noHandleAuth(response);
        }
        //todo 随机数应保存在服务端（数据库或缓存），校验时需检查当前nonce是否已保存，如果已经保存过了，说明该请求是非法请求
        //nonce+时间戳主要是为了防重放
        if(nonce.length()!=4){
            return noHandleAuth(response);
        }
        long curTime = System.currentTimeMillis() / 1000;
        final long ONE_MINUTES=1*60L;
        if(curTime-Long.parseLong(timestamp)>ONE_MINUTES){
            return noHandleAuth(response);
        }
        String invokeSign = SignUtils.genSign(body, invokeUser.getSecretKey());
        if(!invokeSign.equals(sign)){
            return noHandleAuth(response);
        }
        //校验调用的接口是否存在
        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(uri, method, null);
        if(interfaceInfo==null){
            return noHandleAuth(response);
        }
        return handleResponse(exchange,chain,interfaceInfo.getId(),invokeUser.getId());

    }


    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                             innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }



    //确定过滤器的执行顺序
    @Override
    public int getOrder() {
        return -1;
    }


    public Mono<Void> noHandleAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }



}
