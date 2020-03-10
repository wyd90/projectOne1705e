package com.realtime.dao;

import com.realtime.bean.ResBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class DemoDao {

    @Autowired
    private RedisTemplate redisTemplate;

    public List<ResBean> getDatas(){
        Set<String> keys = redisTemplate.opsForHash().keys("usershop");
        List<ResBean> resBeans = new ArrayList<>();
        for(String key : keys) {
            System.out.println(key);
            Object accessNum = redisTemplate.opsForHash().get("usershop", key);
            ResBean resBean = new ResBean();
            resBean.setName(key);
            resBean.setValue(((Integer) accessNum).longValue());
            System.out.println(resBean.toString());
            resBeans.add(resBean);
        }
        return resBeans;
    }
}
