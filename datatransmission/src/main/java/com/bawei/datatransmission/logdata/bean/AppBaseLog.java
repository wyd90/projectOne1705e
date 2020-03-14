package com.bawei.datatransmission.logdata.bean;

import java.io.Serializable;
/**
 * AppBaseLog
 */
public class AppBaseLog implements Serializable {
    private String user;           //用户名
    private String shoptype;               //商品类别
    private String shopname;            //商品名称
    private Integer price;            //单价
    private Integer count;          //数量
    private String time;          //购买时间
    private String ip;            //ip

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getShoptype() {
        return shoptype;
    }

    public void setShoptype(String shoptype) {
        this.shoptype = shoptype;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
