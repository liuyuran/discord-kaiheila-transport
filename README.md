一个开黑啦Bot工程
--

该工程全部基于Spring Boot WebFlux，欢迎参考，GPLv3协议请谨慎引用。

`1. message-transport-server`

初衷是将Discord和开黑啦消息互通，然后将游戏内频道引入Discord，后来决定把第二个功能拆出来单独做，泛用性还稍微强一点。

进度：

- [x] 实现websocket协议的基础保活
- [ ] 实现开黑啦侧机器人读取消息逻辑
- [ ] 实现消息队列，防止被封
- [ ] 实现开黑啦消息批量发送逻辑
- [ ] 实现Discord侧机器人读取消息逻辑
- [ ] 实现Discord消息批量发送逻辑
- [ ] 真机部署并上线

`2. minecraft-message-transport`

预计要做一个1.16.5的mod，不过还没开坑，看我能不能坚持写完
