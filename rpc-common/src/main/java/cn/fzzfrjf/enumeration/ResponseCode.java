package cn.fzzfrjf.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200),
    FAILURE(500);

    private int code;
}
