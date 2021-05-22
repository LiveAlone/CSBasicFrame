## 项目模块划分

项目模型分层

1. protocol 通讯协议层
2. transport 数据通讯层
3. serialization 通讯数据序列化方式

* 模块划分

    1. bio 阻塞模型 transport 提供提供转换方式
    2. serialization 提供服务序列化传输方式
    3. protocol 提供协议驱动方式
    4. empty 空集合, 提供数据通讯方式
  
