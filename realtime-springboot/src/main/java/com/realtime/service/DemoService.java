package com.realtime.service;

import com.realtime.bean.ResBean;
import com.realtime.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;

    public List<ResBean> getResBeans() {
        return demoDao.getDatas();
    }
}
