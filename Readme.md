#  RocT

RocT是一个基于Java Netty实现的可用于内网穿透的代理工具，支持TCP协议，同时具有https代理服务器的功能。

RocT包含RocTWAN和RocTIntranet,RocTWAN运行在带有公网IP的服务器上，RocTIntranet运行在没有公网IP的机器上

## RocT的启动

### RocTWAN的启动

1. 打开RocTWAN.jar文件，输入serverPort和password。这个端口号是RocTIntranet连接RocTWAN的端口号，并非对外网提供访问的端口号。

2. 如图所示

   ​									 ![avatar](https://github.com/Roc-T/RocT/blob/master/pic/1.PNG)

   1.  打开成功后RocTWAN如图所示![avatar](https://github.com/Roc-T/RocT/blob/master/pic/2.PNG)

## RocTIntranet的启动

1. 在没有公网IP的机器上打开RocTIntranet，如图所示

   ​							 ![avatar](https://github.com/Roc-T/RocT/blob/master/pic/3.PNG)

2. 输入相应的信息

   + ` Server Address ` :  RocTWAN的网络地址
   + `Server Port`： RocTWAN的端口 
   + `Password`：RocTWAN的端口的密码

3. 成功连接后界面如下

    ![avatar](https://github.com/Roc-T/RocT/blob/master/pic/4.PNG)

4. 可以通过bind页面绑定被代理的服务器

    								

   ![avatar](https://github.com/Roc-T/RocT/blob/master/pic/5.PNG)

   相关参数如下：

   + `Proxy Address`: 被代理的应用网络地址 
   + `Proxy Port:` 被代理的应用端口号 
   + `Remote Port`: RocTWAN对外访问该应用的端口 

   

   **代理成功后可以通过选中表格中相应的项，点击`Delete`按钮进行取消代理**

5. 可以设置https代理服务器端口

    ![avatar](https://github.com/Roc-T/RocT/blob/master/pic/6.PNG)