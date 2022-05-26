package cn.fzzfrjf.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RpcError {

    SERVICE_NOT_REGISTERED("服务未能成功注册"),
    SERVICE_NOT_FOUND("未能找到服务"),
    UNKNOWN_PROTOCOL("不识别的协议包"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包"),
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务器失败"),
    COMPLETABLE_ERROR("异步调用中出现错误"),
    CONNECT_REGISTRATION_FAILURE("连接到注册中心失败"),
    REGISTERED_SERVICE_FAILURE("将服务注册到注册中心失败");

    private String message;
}
