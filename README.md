## projectOne1705e
#### 项目介绍:
projectOne1705e是一个基于java、scala语言开发的项目，它是一个电商项目，根据用户购买的商品信息做实时指标，构造用户画像。
#### 上手指南：
以下指南将介绍项目安装要求，主要功能特点以及基本架构。
#### 安装要求：
Apache Spark  
Apache Kafka  
Apache HBase  
Apache Hive  
Apache Sqoop  
Apache Flume  
Hadoop Distributed File System(HDFS)  
Redis  
MySQL
#### 功能特点：
实时更新用户购买记录，根据用户购买的商品信息，分析用户信息。
#### 基本架构：
**datatransmission——数据层：** 收集数据，过滤数据将数据存入kafka消息队列中。  
**projectrealtime——实时层：** 读取kafka消息，将数据做成实时etl，计算实时指标存入redis，数据流实时存入HDFS。  
**projectbatch——离线层：** 获取数据，根据数据用sparkcore做用户画像，将结果存入HBase。  
**projecttest——测试层：** 测试项目，整合模块。
  
*github地址：* https://github.com/wyd90/projectOne1705e.git
