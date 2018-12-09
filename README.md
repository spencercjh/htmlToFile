# 动态生成HTML并生成PDF和图片

## 项目说明

参考自https://github.com/petterobam/my-html2file，修正其不能在linux内正常使用的问题（原项目的LinuxPath并不正确），
并使用Apache PdfBox将生成出来的PDF文件转化为PNG图片，以解决用wkhtmltoimage生成出来的图片过大的问题。
即通过转PDF省去一步图片压缩的任务，而PDF又正好是业务需要的。使用字符串拼接，帮助用户拼接wkhtmltopdf需要
的参数指令，避免不必要的麻烦。

## 工程说明

```/html2file``` 工程输出文件目录

```/html2file/plugin``` wkhtmltox的位置，pdf就是靠调用它生成出来的

```/html2file/html /html2file/image /html2file/pdf ```生成的3种文件存放目录

```description.html```是一份以```wkhtmltopdf--helpdoc```的方式导出的说明文档。

```src/main/java/top/spencercjh/controller#getCert(String data)```具体写了我需要的业务逻辑

```MyService MyServiceImpl```我的业务逻辑：将前端传入的数据替换掉template.html中的内容

```top.spencercjh.config.WebConfig```中配置了filter，允许用户能够访问生成出来的资源。

## 接口说明

- POST：/getCert
- Request Parameter:
    ```json
    {
          "userId": "spencercjh",
              "certificate": [
                "经链证算法检验数据对象为安全，未经篡改",
                "y3dP63zF2BYyRBfYBg6ECLUlGkV6usPC",
                "XXX链证平台",
                "上链证书",
                "MD5+SHA512",
                "基于wkhtmltopdf和PDFBOX的Html转PDF图片工具.git",
                "cX4MpCXmY5nauwHaZx6WDbXYNDcCU9f9gUjIgkrKaBysviaCgpekvE4Gw7Sf8GV6",
                "2018年12月9日",
                "gK4KYq1iBMbEbai0NnZXjmbV2aqUxQXvLGoWrsBYne5KLMLzQ16hHLfB5aW0GCa2",
                "GVZESwAmsfrR18nbi5vHnX4hqHIfLFXn"
              ],
          "option": {
                "margin-bottom": "0",
                "margin-left": "0",
                "margin-right": "0",
                "margin-top": "0",
                "page-height": "125",
                "page-width": "88"
          }
    }
   
- 说明：certificate对应template.html上的10个需要替换的字段，option是要拼接到指令中的参数Key和Value，
单位为毫米mm。

    margin-bottom:设置页面底部边距;
    
    margin-left:设置页面左边距（默认为10mm）;
    
    margin-right:设置页面右边距（默认为10mm）;
    
    margin-top:	设置页面上边距;
    
    page-height:页面高度;
    
    page-width:页面宽度;
    
_读了description.html后，如有需要其他参数请发issue_

- Response:
    ```json
     {
         "pdfPath": "D:/test/html2file/pdf/20181209/7bd88f09857042b8bed411c8604a44f4.pdf",
         "imagePath": "D:/test/html2file/image/20181209/3d8671f74e4342519e272244b91be3bf.png"
     }
    ```
- 说明：返回2个加了主机信息就可以访问到的URI，比如在本地运行的话访问图片的URL就应该是
```localhost:8090/image/dce4774314f44b95a6a19ff9df268076.png```，pdf同理。

## 部署说明

**已经在所有的业务流程中区分了Windows/Linux，请用户务必去修改对应的目录。**
    
若没有SSL需要配置，删除或注释```application.properties```中的

    
        server.ssl.key-store-password=1523355275441
        server.ssl.key-store=1523355275441.pfx
        server.ssl.key-store-type=PKCS12
    
    
**_否则将不能正常运行。_**

若需要配置，修改上述字段，并将秘钥文件放入相应位置

### Windows平台部署

将项目中html2file文件夹移到你想要的位置，修改```application.properties```中的以下配置信息

    
        windows-template-path=D:/test/html2file/template.html
        windows-output-html-path=D:/test/html2file/html/
        windows-output-image-path=D:/test/html2file/image/
        windows-output-pdf-path=D:/test/html2file/pdf/
        windows-plugin-path=D:/test/html2file/plugin/window/wkhtmltopdf/bin/wkhtmltopdf
    
Windows操作系统默认GB2312编码，而我们的开发工作都使用UTF-8，我已经很努力地去解决这个问题了。

如果你直接在IDEA里运行SpringBoot项目，我可以保证全程UTF-8并使生成结果无乱码。

如果你使用```java -jar html2Pdf-2.0-SNAPSHOT.jar```在Windows上部署Jar包，那么生成出来的东西仍然会有乱码，我无力解决
，知道的请联系我。

### Linux平台部署

将项目中html2file文件夹移到你想要的位置，修改```application.properties```中的以下配置信息


        linux-template-path=/root/html2file/template.html
        linux-output-html-path=/root/html2file/html/
        linux-output-image-path=/root/html2file/image/
        linux-output-pdf-path=/root/html2file/pdf/
        linux-plugin-path=/root/html2file/plugin/linux/wkhtmltox/bin/wkhtmltopdf

一般情况下你是不可能直接成功运行的 [参考链接](https://www.jianshu.com/p/cc2958636d74)
- 环境变量:
```vi /etc/profile```,在最后加上
```export PATH=你的html2file目录/plugin/linux/wkhtmltox/bin:$PATH```，保存退出后执行
```source /etc/profile```

- 安装需要的图形库:
    ```
         apt-get/yum install libXrender*
         apt-get/yum install libfontconfig*
         apt-get/yum install libXext*    
    ```
    
- 配置服务器中文字体 [参考链接](https://www.linuxidc.com/Linux/2016-09/135548.htm)

    安装字体库```yum -y install fontconfig```
    
    在```/usr/shared/```中要能看到```fonts```和```fontconfig```
    
    在/fonts中```mkdir chinese```
    
    将一些中文字体上传进去；
    
    修改chinese目录的权限：```chmod -R 755 /usr/share/fonts/chinese```
    
    安装ttmkfdir来搜索目录中所有的字体信息，并汇总生成fonts.scale文件，输入命令：```yum -y install ttmkfdir```
    
    执行```ttmkfdir -e /usr/share/X11/fonts/encodings/encodings.dir```
    
    修改字体配置文件，```vi /etc/fonts/fonts.conf```
    
    在```Fort directory list```部分中添加```<dir>/usr/share/fonts/chines</dir>```，保存退出
    
    刷新内存字体缓存```fc-cache```
    
    执行```fc-list```，能看到中文字和中文字体
    

你可以选择将本Spring Boot项目以Jar包或者tomcat+War包的形式部署在Linux服务器中，这里介绍Jar包方式——
[参考链接](https://blog.csdn.net/m0_37063257/article/details/78300877)
```
   nohup java -jar html2Pdf-2.0-SNAPSHOT.jar > log.txt & 
```
即能部署成功，并生成log日志文件
