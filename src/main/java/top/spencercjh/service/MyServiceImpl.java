package top.spencercjh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import top.spencercjh.utils.BaseUtils;
import top.spencercjh.utils.FilesUtils;
import top.spencercjh.utils.OsInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author spencercjh
 */
@Service
public class MyServiceImpl implements MyService {
    private final Environment env;

    @Autowired
    public MyServiceImpl(Environment env) {
        this.env = env;
    }

    @Override
    public String fillDataToHtml(String userId, String templateFilePath, List<String> data) throws IOException {
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
        String writerStr = new String(stringBuilder.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        String htmlOutPath = OsInfo.isWindows() ? env.getProperty("windows-output-html-path") +
                BaseUtils.getDateStr("yyyyMMdd") + "/cert_" + userId + ".html" :
                env.getProperty("linux-output-html-path") + BaseUtils.getDateStr("yyyyMMdd") +
                        "/cert_" + userId + ".html";
        FilesUtils.checkFolderAndCreate(Objects.requireNonNull(htmlOutPath));
        Files.write(Paths.get(Objects.requireNonNull(htmlOutPath)), writerStr.getBytes());
        return htmlOutPath;
    }
}
