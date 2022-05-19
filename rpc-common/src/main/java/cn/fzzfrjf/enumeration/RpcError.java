package cn.fzzfrjf.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RpcError {

    SERVICE_NOT_REGISTERED("服务未能成功注册"),
    SERVICE_NOT_FOUND("未能找到服务");

    private String message;
}
