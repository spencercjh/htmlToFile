package top.spencercjh.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.spencercjh.service.Html2PdfService;
import top.spencercjh.service.MyService;
import top.spencercjh.utils.OsInfo;
import top.spencercjh.utils.Pdf2PngUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author spencercjh
 */
@Controller
public class TemplateController {

    private final Html2PdfService html2PdfService;
    private final Environment env;
    private final MyService myService;

    @Autowired
    public TemplateController(Html2PdfService html2PdfService, Environment env, MyService myService) {
        this.html2PdfService = html2PdfService;
        this.env = env;
        this.myService = myService;
    }

    @PostMapping("/getCert")
    @ResponseBody
    public Map<String, String> getCert(@RequestParam("data") String data) throws IOException {
        JSONObject jsonObject = JSON.parseObject(data);
        String userId = jsonObject.getString("userId");
        JSONArray certificate = jsonObject.getJSONArray("certificate");
        List<String> dataList = new ArrayList<>(certificate.size());
        for (Object o : certificate) {
            dataList.add(o.toString());
        }
        Map<String, Object> option = jsonObject.getJSONObject("option");
        String htmlInputPath = OsInfo.isWindows() ? env.getProperty("windows-template-path") : env.getProperty("linux-template-path");
        System.out.println("## HTML INPUT PATH" + htmlInputPath);
        String htmlOutPath = myService.fillDataToHtml(userId, htmlInputPath, dataList);
        Map<String, String> result = new HashMap<>(2);
        try {
            String originPdfPath = html2PdfService.execute(htmlOutPath, option);
            result.put("pdfPath", originPdfPath);
            result.put("imagePath", Pdf2PngUtils.pdf2png(originPdfPath,
                    OsInfo.isWindows() ? env.getProperty("windows-output-image-path") : env.getProperty("linux-output-image-path")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
