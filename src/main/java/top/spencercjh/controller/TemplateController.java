package top.spencercjh.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.spencercjh.Html2PdfService;
import top.spencercjh.utils.*;

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
        if (!OsInfo.isWindows()) {
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
        Map<String, Object> certificate = jsonObject.getJSONObject("certificate");
        List<String> dataList = new ArrayList<>(certificate.size());
        Map<String, Object> option = jsonObject.getJSONObject("option");
        for (Map.Entry<String, Object> entry : certificate.entrySet()) {
            dataList.add((String) entry.getValue());
        }
        String htmlInputPath = PathUtils.getClassRootPath("/static/template.html");
        if (!OsInfo.isWindows()) {
            htmlInputPath = "/" + htmlInputPath;
        }
        System.out.println("## HTML INPUT PATH" + htmlInputPath);
        String htmlOutPath = fillDataToHtml(userId, htmlInputPath, dataList);
        Map<String, String> result = new HashMap<>(2);
        try {
            String originPdfPath = html2PdfService.execute(htmlOutPath, option);
            result.put("pdfPath", originPdfPath);
            result.put("imagePath", Pdf2PngUtils.pdf2png(PathUtils.getClassRootPath(originPdfPath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
