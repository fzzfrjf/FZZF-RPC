package cn.fzzfrjf.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class RpcObject implements Serializable {
    private int id;
    private String message;
}
