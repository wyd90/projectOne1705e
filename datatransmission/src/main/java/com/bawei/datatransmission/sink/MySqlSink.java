package com.bawei.datatransmission.sink;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlSink extends AbstractSink implements Configurable{

    private String url;
    private String username;
    private String password;
    private Connection conn;
    private Statement stat;

    public Status process() throws EventDeliveryException {
        Status status = null;

        Channel ch = getChannel();
        Transaction tran = ch.getTransaction();
        tran.begin();
        try {
            Event event = ch.take();
            if (event !=null){
                byte[] body = event.getBody();
                String message = new String(body, "UTF-8");
                String[] arr = message.split(",");
//               u001,手机,iPhone11,5500,1,2020-02-10
                 String user =arr[0];
                 String phone =arr[1];
                 String phonetype =arr[2];
                 Double price =Double.valueOf(arr[3]);
                 Integer counts = Integer.valueOf(arr[4]);
                 String time = arr[5];
                 stat.execute("insert into t_datas(t_user,phone,phonetype,price,counts,time) value('"+user+","+phone+","+phonetype+","+price+","+counts+","+time+"')" );
                 status = Status.READY;
            }else{
                status = Status.BACKOFF;
            }
            tran.commit();

        }catch (Throwable t){
            tran.rollback();
            status = Status.READY;
            if (t instanceof Error){
                throw (Error) t;
            }
        }finally {
            tran.close();
        }
        return status;
    }

    public void configure(Context context) {

        url = context.getString("url");
        username = context.getString("username");
        password = context.getString("password");

    }

    @Override
    public synchronized void start() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
            stat = conn.createStatement();

        }catch (ClassNotFoundException e){

            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.start();
    }

    @Override
    public synchronized void stop() {

        try {
            if(stat != null) {
                stat.close();
            }
            if(conn != null){
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.stop();
    }
}
