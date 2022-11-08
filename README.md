一个开黑啦Bot工程
--

该工程全部基于Spring Boot WebFlux，欢迎参考，GPLv3协议请谨慎引用。多年前的旧项目，近期突然想起来想重启而已。

`0. kook-framework`

提取出的半成品框架

`1. message-transport-server`

测试项目，用来进行回声实验

运行时请记得在application.properties里写入如下配置：

```properties
kook.client-id=<KOOK应用中心里的对应参数>
kook.client-secret=<KOOK应用中心里的对应参数>
kook.token=<KOOK应用中心里对应机器人的参数>
kook.command-prefix=<命令前缀，比如transport，机器人就会对/transport进行转发>
discord.token=<Discord应用中心BOT选项卡里的token>
discord.with-local-proxy=<如果需要在墙内开代理，记得设定为true，然后修改代码里的代理配置，否则为false>
```

`2. 不存在的`

用来打通mc游戏内聊天频道

特别提醒
--
这类软件在国内属灰色地带，以当下的舆论环境，很可能会因为R18甚至R18G内容的明文传输而导致封号、炸服务器甚至被请喝茶。

要知道KOOK和Discord不同，并不是开了NSFW标签就能免责的。

所以个人建议使用者一定不要把命令改得太好用，必要时加入足够高的知识门槛，如果一定要开放使用，务必做好审核工作。

我自己是只在自己能绝对控制的封闭环境里使用，人怂得很，出事与我无关。
