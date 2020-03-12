package com.my.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

//为了让hdfs中的碎片文件减少  /order1705e/2020/3/12/16 /order1705eres/2020/3/12/16
public class MergeFile {
    public static void main(String[] args) throws IOException, InterruptedException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create("hdfs://hadoop1:8020"), conf, "root");

        List<Path> paths = new ArrayList<>();

        //找出源文件夹中的所有数据文件
        //listFile(new Path("/order1705e/2020/3/12/16"), fs, paths);
        listFile(new Path(args[0]), fs, paths);

        int i =0;
        //把所有有效数据文件移动到一个中间目录中
        for(Path path : paths) {
            if(!path.toString().contains("_SUCCESS")) {

                //FileUtil.copy(fs, path, fs, new Path("/order1705eres/2020/3/12/16"+"/mid/"+i),true, conf);
                FileUtil.copy(fs, path, fs, new Path(args[1]+i),true, conf);

                i++;
            }
        }

        //合并中间目录中的所有文件
        //FileUtil.copyMerge(fs,new Path("/order1705eres/2020/3/12/16"+"/mid"),fs, new Path("/order1705eres/2020/3/12/16"+"/data.dat"), true,conf,null);
        FileUtil.copyMerge(fs,new Path(args[1]+"/mid"),fs, new Path(args[1]+"/data.dat"), true,conf,null);

        //删除源文件夹
        //fs.delete(new Path("/order1705e/2020/3/12/16"),true);
        fs.delete(new Path(args[0]),true);
    }

    //遍历出目录下所有文件和所有子目录下的文件
    public static void listFile(Path path, FileSystem fs, List<Path> paths) throws IOException {
        FileStatus[] list = fs.listStatus(path);
        for(FileStatus fileStatus : list) {
            if(fileStatus.isDirectory()) {
                listFile(fileStatus.getPath(),fs,paths);
            } else {
                paths.add(fileStatus.getPath());
            }
        }
    }
}
