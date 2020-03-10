package com.bawei.datatransmission.source;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MyTailDirSource extends AbstractSource implements Configurable, PollableSource{


    private String posFilePath;
    private String filePath;
    private File posFile;
    private RandomAccessFile raf;

    public Status process() throws EventDeliveryException {
        Status status = null;
        try {
            String line = raf.readLine();
            if(!StringUtils.isEmpty(line)){

                Event event = new SimpleEvent();
                event.setBody(line.getBytes());
//                发送event到channel
                getChannelProcessor().processEvent(event);
//               更新偏移量
                long offset = raf.getFilePointer();
                FileUtils.writeStringToFile(posFile, offset+"");
            }
            status = Status.READY;
        } catch (IOException e) {
            e.printStackTrace();
            status = Status.BACKOFF;
        }
        return status;
    }
    public long getBackOffSleepIncrement() {
        return 0;
    }

    public long getMaxBackOffSleepInterval() {
        return 0;
    }

//    读取配置文件
    public void configure(Context context) {
//        获取位置文件信息
        posFilePath = context.getString("posFilePath");
        filePath = context.getString("filePath");

    }



//    读取偏移量文件，设置偏移量，打开被监听的文件
    @Override
    public synchronized void start() {

        try {
            posFile = new File(posFilePath);
//            如果配置文件不存在就创建一个
             if(!posFile.exists()){

                posFile.createNewFile();
             }

            Long offset = 0L;
//           获取文件中存储的偏移量
            String offsetStr = FileUtils.readFileToString(posFile);
            if(!StringUtils.isEmpty(offsetStr)){

                offset = Long.valueOf(offsetStr);
            }

//          创建随机读取的文件类
            raf = new RandomAccessFile(filePath, "r");
//      将偏移量调整到偏移量文件存储的位置
            raf.seek(offset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.start();
    }

    @Override
    public synchronized void stop() {

        try {
//            关闭监听文件输入流
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.stop();

    }
}
