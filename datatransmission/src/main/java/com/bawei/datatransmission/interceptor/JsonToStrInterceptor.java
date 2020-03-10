package com.bawei.datatransmission.interceptor;

import com.alibaba.fastjson.JSONObject;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class JsonToStrInterceptor implements Interceptor {

    private String[] fields;
    private String[] fieldTypes;

    private JsonToStrInterceptor(String schema, String types){

        fields = schema.split(",");
        fieldTypes = types.split(",");
    }

//初始化执行一次
    public void initialize() {

    }
//    拦截一条event消息
    public Event intercept(Event event) {
//        取出消息体
        byte[] body = event.getBody();
//        取出消息头
        Map<String, String> headers = event.getHeaders();
//        设置消息头 u001,手机,iPhone11,5500,1,2020-02-10 16777472
        headers.put("u001","手机 iPhone11,5500,1,2020-02-10,16777472");
//        把消息头set回event
        event.setHeaders(headers);

//        处理消息体，把消息体从json转化成字符串
        StringBuffer strb = new StringBuffer("");

        try {
            String jsonStr = new String(body,"UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            int i =0;
            for (String field : fields){
                String type = fieldTypes[i];
                if ("String".equals(type)){
                    strb.append(jsonObject.getString(field) + ",");
                }else if("Long".equals(type)){
                    strb.append(Long.getLong(field)+",");
                }else if("Int".equals(type)){
                    strb.append(jsonObject.getLong(field) + ",");

                }
                i++;
            }

            String eventBodyStr = strb.toString();
            eventBodyStr = eventBodyStr.substring(0, eventBodyStr.length() - 1);
        }catch (UnsupportedEncodingException e){

            e.printStackTrace();
        }

        return event;
    }

    public List<Event> intercept(List<Event> events) {
        for(Event event : events){
//            循环调用处理单个消息的方法
            intercept(event);
        }
        return events;
    }

    public void close() {

    }

    public static class Builder implements Interceptor.Builder{

        private String schema;
        private String types;
        public Interceptor build() {
            return new JsonToStrInterceptor(schema,types);
        }

        public void configure(Context context) {

            schema = context.getString("schema");
            types = context.getString("types");
        }
    }
}
