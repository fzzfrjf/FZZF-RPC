package cn.fzzfrjf.service;

import cn.fzzfrjf.entity.HelloService;
import cn.fzzfrjf.entity.RpcObject;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(RpcObject object) {
        return "这是id为：" + object.getId() + "发送的：" + object.getMessage();
    }
}
