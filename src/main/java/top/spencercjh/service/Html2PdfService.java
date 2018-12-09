package top.spencercjh.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import top.spencercjh.utils.*;

import java.util.Map;
import java.util.Objects;

/**
 * html转pdf的服务
 *
 * @author 欧阳洁
 * @since 2018-03-28 11:50
 */
@Service
public class Html2PdfService {
    private final Environment env;
    /**
     * windows执行文件
     */
    private String windowExePath;
    /**
     * linux执行文件
     */
    private String linuxExePath;

    @Autowired
    public Html2PdfService(Environment env) {
        this.env = env;
    }

    /**
     * 解析生成PDF
     *
     * @param pageUrl
     * @return
     * @throws Exception
     */
    public String execute(String pageUrl, Map<String, Object> option) throws Exception {
        String outputPath = OsInfo.isWindows() ? env.getProperty("windows-output-pdf-path") +
                BaseUtils.getDateStr("yyyyMMdd") + "/" + BaseUtils.uuid2() + ".pdf" :
                env.getProperty("linux-output-pdf-path") + BaseUtils.getDateStr("yyyyMMdd") +
                        "/" + BaseUtils.uuid2() + ".pdf";
        String cmdStr = getCmdStr(pageUrl, outputPath, option);
        boolean success = CmdUtils.excute(cmdStr);
        if (success) {
            return outputPath;
        } else {
            if (FilesUtils.isExistNotCreate(outputPath)) {
                return outputPath;
            } else {
                throw new Exception("转化异常！[" + outputPath + "]");
            }
        }
    }

    /**
     * 根据操作系统类别，获取不同的cmd字符串
     *
     * @param pageUrl
     * @param outputPath
     * @return
     */
    private String getCmdStr(String pageUrl, String outputPath, Map<String, Object> parameter) {
        String option;
        if (parameter == null) {
            option = " --page-width 88 --page-height 125 -B 0 -L 0 -R 0 -T 0 ";
        } else {
            option = dealParameter(parameter);
        }
        StringBuilder cmdStr = new StringBuilder();
        String absoluteOutputPath = outputPath;
        FilesUtils.checkFolderAndCreate(Objects.requireNonNull(absoluteOutputPath));
        String absoluteExePath;
        if (OsInfo.isWindows()) {
            absoluteExePath = getWindowExePath();
            absoluteOutputPath = PathUtils.getWindowsRightPath(absoluteOutputPath);
            cmdStr.append(absoluteExePath).
                    append(option).
                    append(pageUrl).append(" ").append(absoluteOutputPath);
        } else {
            absoluteExePath = getLinuxExePath();
            CmdUtils.excute("chmod +x " + absoluteExePath);
            cmdStr.append(absoluteExePath).
                    append(option).
                    append(pageUrl).append(" ").append(absoluteOutputPath);
        }
        System.out.println("## CMD STRING " + cmdStr.toString());
        return cmdStr.toString();
    }

    private String dealParameter(Map<String, Object> parameter) {
        StringBuilder stringBuilder = new StringBuilder(" ");
        if (StringUtils.isNumeric((CharSequence) parameter.get("margin-bottom"))) {
            stringBuilder.append("-B ").
                    append(parameter.get("margin-bottom")).
                    append(" ");
        }
        if (StringUtils.isNumeric((CharSequence) parameter.get("margin-left"))) {
            stringBuilder.append("-L ").
                    append(parameter.get("margin-left")).
                    append(" ");
        }
        if (StringUtils.isNumeric((CharSequence) parameter.get("margin-right"))) {
            stringBuilder.append("-R ").
                    append(parameter.get("margin-right")).
                    append(" ");
        }
        if (StringUtils.isNumeric((CharSequence) parameter.get("margin-top"))) {
            stringBuilder.append("-T ").
                    append(parameter.get("margin-top")).
                    append(" ");
        }
        if (StringUtils.isNumeric((CharSequence) parameter.get("page-height"))) {
            stringBuilder.append("--page-height ").
                    append(parameter.get("page-height")).
                    append(" ");
        }
        if (StringUtils.isNumeric((CharSequence) parameter.get("page-width"))) {
            stringBuilder.append("--page-width ").
                    append(parameter.get("page-width")).
                    append(" ");
        }
        if (parameter.get("lowquality") != null) {
            stringBuilder.append("--lowquality ");
        }
        return stringBuilder.toString();
    }

    private String getWindowExePath() {
        return env.getProperty("windows-plugin-path");
    }

    public void setWindowExePath(String windowExePath) {
        this.windowExePath = windowExePath;
    }

    private String getLinuxExePath() {
        return env.getProperty("linux-plugin-path");
    }

    public void setLinuxExePath(String linuxExePath) {
        this.linuxExePath = linuxExePath;
    }
}
