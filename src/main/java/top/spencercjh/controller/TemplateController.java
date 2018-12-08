package top.spencercjh.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import top.spencercjh.Html2PdfService;
import top.spencercjh.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author spencercjh
 */
@Controller
public class TemplateController {

    private final Html2PdfService html2PdfService;

    @Autowired
    public TemplateController(Html2PdfService html2PdfService) {
        this.html2PdfService = html2PdfService;
    }

    private static String fillDataToHtml(String userId, String templateFilePath, List<String> data) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        File temFile = new File(templateFilePath);
        if (!temFile.exists()) {
            throw new IOException();
        }
        BufferedReader br = Files.newBufferedReader(Paths.get(templateFilePath));
        List<String> list = br.lines().collect(Collectors.toList());
        if (list.isEmpty()) {
            throw new IOException();
        }
        int i = 0;
        for (String str : list) {
            if (str.indexOf("$DATA$") > 0) {
                str = str.replace("$DATA$", data.get(i++));
            }
            stringBuilder.append(str).append("\n");
        }
        String writerStr = new String(stringBuilder.toString().getBytes(), StandardCharsets.UTF_8);
        String htmlOutPath = "output/" + BaseUtils.getDateStr("yyyyMMdd") + "/html/cert_" + userId + ".html";
        htmlOutPath = PathUtils.getClassRootPath(htmlOutPath);
        if(!OsInfo.isWindows()) {
            htmlOutPath = "/" + htmlOutPath;
        }
        FilesUtils.checkFolderAndCreate(Objects.requireNonNull(htmlOutPath));
        Files.write(Paths.get(Objects.requireNonNull(htmlOutPath)), writerStr.getBytes());
        return htmlOutPath;
    }

    @PostMapping("/getCert")
    @ResponseBody
    public Map<String, String> getCert(@RequestParam("data") String data) throws IOException {
        JSONObject jsonObject = JSON.parseObject(data);
        String userId = jsonObject.getString("userId");
        String sign0 = jsonObject.getString("sign0");
        String sign1 = jsonObject.getString("sign1");
        String sign2 = jsonObject.getString("sign2");
        String sign3 = jsonObject.getString("sign3");
        String sign4 = jsonObject.getString("sign4");
        String sign5 = jsonObject.getString("sign5");
        String sign6 = jsonObject.getString("sign6");
        String sign7 = jsonObject.getString("sign7");
        String sign8 = jsonObject.getString("sign8");
        String sign9 = jsonObject.getString("sign9");
        List<String> dataList = new ArrayList<>(16);
        dataList.add(sign0);
        dataList.add(sign1);
        dataList.add(sign2);
        dataList.add(sign3);
        dataList.add(sign4);
        dataList.add(sign5);
        dataList.add(sign6);
        dataList.add(sign7);
        dataList.add(sign8);
        dataList.add(sign9);
        String htmlInputPath = PathUtils.getClassRootPath("/static/template.html");
        if(!OsInfo.isWindows()){
            htmlInputPath = "/" + htmlInputPath;
        }
        System.out.println("##########################" + htmlInputPath);

        String htmlOutPath = fillDataToHtml(userId, htmlInputPath, dataList);
        Map<String, String> result = new HashMap<>(2);
        try {
            String originPdfPath = html2PdfService.excute(htmlOutPath);
            result.put("pdfPath", originPdfPath);
            result.put("imagePath",Pdf2PngUtils.pdf2png(PathUtils.getClassRootPath(originPdfPath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
