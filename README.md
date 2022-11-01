一个开黑啦Bot工程
--

该工程全部基于Spring Boot WebFlux，欢迎参考，GPLv3协议请谨慎引用。多年前的旧项目，近期突然想起来想重启而已。

`0. khl-framework`

提取出的半成品框架

`1. message-transport-server`

测试项目，用来进行回声实验

运行时请记得在application.properties里写入如下配置：

```properties
kook.client-id=<KOOK应用中心里的对应参数>
kook.client-secret=<KOOK应用中心里的对应参数>
kook.token=<KOOK应用中心里对应机器人的参数>
kook.command-prefix=<命令前缀，比如transport，机器人就会对/transport进行转发>
```

`2. 不存在的`

用来打通mc游戏内聊天频道
