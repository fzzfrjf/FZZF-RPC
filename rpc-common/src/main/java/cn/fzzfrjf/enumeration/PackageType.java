package cn.fzzfrjf.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PackageType {
    REQUEST_PACKAGE(0),
    RESPONSE_PACKAGE(1);

    private int code;
}
