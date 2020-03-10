package com.realtime.bean;

public class ResBean {
    private String name;
    private Long value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ResBean{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
