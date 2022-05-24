package cn.fzzfrjf.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerCode {
    PROTOBUF(0);

    private int code;
}
