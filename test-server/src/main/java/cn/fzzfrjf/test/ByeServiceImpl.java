package cn.fzzfrjf.test;

import cn.fzzfrjf.annotation.Service;
import cn.fzzfrjf.entity.ByeService;
import cn.fzzfrjf.entity.RpcObject;


@Service
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(RpcObject object) {
        return "(" + object.getMessage() + ")，bye！";
    }
}
