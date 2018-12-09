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
    public static String pdf2png(String pdfFilePath, String imagePath) throws IOException {
        System.out.println("## PDF FILE PATH" + pdfFilePath);
        File file = new File(pdfFilePath);
        try (PDDocument doc = PDDocument.load(file)) {
            if (!file.exists()) {
                throw new IOException("PDF文件不存在");
            }
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            if (pageCount == 1) {
                String path = imagePath + BaseUtils.getDateStr("yyyyMMdd") +
                        "/" + BaseUtils.uuid2() + ".png";
                BufferedImage image = renderer.renderImageWithDPI(0, 144);
                write(image, path);
                return path;
            } else {
                List<String> pathList = new ArrayList<>(16);
                for (int i = 0; i < pageCount; ++i) {
                    String path = imagePath + BaseUtils.getDateStr("yyyyMMdd") +
                            "/" + BaseUtils.uuid2() + "_" + i + ".png";
                    BufferedImage image = renderer.renderImageWithDPI(i, 144);
                    write(image, path);
                    pathList.add(path);
                }
                return JSON.toJSONString(pathList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("PDF转图片失败");
        }
    }

    private static void write(BufferedImage image, String path) throws IOException {
        FilesUtils.checkFolderAndCreate(Objects.requireNonNull(path));
        System.out.println("## IMAGE FILE PATH" + path);
        ImageIO.write(image, "png", new File(path));
    }
}