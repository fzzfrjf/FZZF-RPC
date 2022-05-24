package cn.fzzfrjf.service;

import cn.fzzfrjf.entity.ByeService;
import cn.fzzfrjf.entity.RpcObject;

public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(RpcObject object) {
        return "(" + object.getMessage() + ")，bye！";
    }
}
