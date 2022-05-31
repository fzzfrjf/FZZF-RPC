package cn.fzzfrjf.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class RpcRequest implements Serializable {

    private Boolean heartbeatMessage;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;


    public void setHeartbeatMessage(Boolean heartbeatMessage) {
        this.heartbeatMessage = heartbeatMessage;
    }
}
