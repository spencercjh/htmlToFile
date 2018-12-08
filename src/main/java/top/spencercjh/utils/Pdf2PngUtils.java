package top.spencercjh.utils;

import com.alibaba.fastjson.JSON;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author spencercjh
 */
public class Pdf2PngUtils {
    /**
     * 转换全部的pdf
     *
     * @param pdfFilePath pdf文件地址
     */
    public static String pdf2png(String pdfFilePath) {
        if (!OsInfo.isWindows()) {
            pdfFilePath = "/" + pdfFilePath;
        }
        System.out.println("## PDF FILE PATH" + pdfFilePath);
        File file = new File(pdfFilePath);
        try {
            if (!file.exists()) {
                throw new IOException("PDF文件不存在");
            }
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            if (pageCount == 1) {
                String path = "/output/" + BaseUtils.getDateStr("yyyyMMdd") +
                        "/image/" + BaseUtils.uuid2() + ".png";
                BufferedImage image = renderer.renderImageWithDPI(0, 144);
                write(image, renderer, path);
                return path;
            } else {
                List<String> pathList = new ArrayList<>(16);
                for (int i = 0; i < pageCount; ++i) {
                    String path = "/output/" + BaseUtils.getDateStr("yyyyMMdd") +
                            "/image/" + BaseUtils.uuid2() + "_" + i + ".png";
                    BufferedImage image = renderer.renderImageWithDPI(i, 144);
                    write(image, renderer, path);
                    pathList.add(path);
                }
                return JSON.toJSONString(pathList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void write(BufferedImage image, PDFRenderer renderer, String path) throws IOException {
        String imageFilePath = PathUtils.getClassRootPath(path);
        if (!OsInfo.isWindows()) {
            imageFilePath = "/" + imageFilePath;
        }
        FilesUtils.checkFolderAndCreate(Objects.requireNonNull(imageFilePath));
        System.out.println("## IMAGE FILE PATH" + imageFilePath);
        ImageIO.write(image, "png", new File(imageFilePath));
    }
}