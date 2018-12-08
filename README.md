# 动态生成HTML并生成PDF和图片

## 说明

参考自https://github.com/petterobam/my-html2file，修正其不能在linux内正常使用的问题，
并使用Apache PdfBox将生成出来的PDF文件转化为PNG图片，以解决用wkhtmltoimage生成出来的图片过大的问题。
即通过转PDF省去一步图片压缩的任务，而PDF又正好是业务需要的。使用字符串拼接，帮助用户拼接wkhtmltopdf需要
的参数指令，避免不必要的麻烦。

具体的参数指令说明见```src/main/resources/static/description.html```这是一份以```wkhtmltopdf
--helpdoc```的方式导出的说明文档。

## 接口

- POST：/getCert
- Request Parameter:
    ```json
    
    {
          "userId": "spencercjh",
          "certificate": {
                "0": "经链证算法检验数据对象为安全，未经篡改",
                "1": "y3dP63zF2BYyRBfYBg6ECLUlGkV6usPC",
                "2": "XXX链证平台",
                "3": "上链证书",
                "4": "MD5+SHA512",
                "5": "基于wkhtmltopdf和PDFBOX的Html转PDF图片工具.git",
                "6": "cX4MpCXmY5nauwHaZx6WDbXYNDcCU9f9gUjIgkrKaBysviaCgpekvE4Gw7Sf8GV6",
                "7": "2018年12月9日",
                "8": "gK4KYq1iBMbEbai0NnZXjmbV2aqUxQXvLGoWrsBYne5KLMLzQ16hHLfB5aW0GCa2",
                "9": "GVZESwAmsfrR18nbi5vHnX4hqHIfLFXn"
          },
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
- Response:
    ```json
      {
        "pdfPath": "/output/20181209/pdf/d6c5f7b8f4d44f009422a8c49621659f.pdf",
        "imagePath": "/output/20181209/image/dce4774314f44b95a6a19ff9df268076.png"
      }
    ```
- 说明：返回2个加了主机信息就可以访问到的URI

    
