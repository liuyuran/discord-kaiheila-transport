一个开黑啦Bot工程
--

该工程全部基于Spring Boot WebFlux，欢迎参考，GPLv3协议请谨慎引用。

`0. khl-framework`

由1拆解而来，尝试实现一个通用框架简化后续开发，目前没有使用说明，进度合并在1里。

原理就是websocket接消息，使用gson自定义序列化/反序列化请求体发请求，不过都是基于webflux那一套异步框架。

`1. message-transport-server`

初衷是将Discord和开黑啦消息互通，然后将游戏内频道引入Discord，后来决定把第二个功能拆出来单独做，泛用性还稍微强一点。

进度：

- [x] 实现websocket协议的基础保活
- [X] 实现开黑啦侧机器人读取消息逻辑
- [X] 实现开黑啦消息单条发送逻辑
- [ ] 实现消息队列，防止被封
- [ ] 实现开黑啦消息批量发送逻辑
- [ ] 实现Discord侧机器人读取消息逻辑
- [ ] 实现discord消息单条发送逻辑
- [ ] 实现Discord消息批量发送逻辑
- [ ] 真机部署并上线

`2. minecraft-message-transport`

预计要做一个1.16.5的mod，不过还没开坑，看我能不能坚持写完0和1
