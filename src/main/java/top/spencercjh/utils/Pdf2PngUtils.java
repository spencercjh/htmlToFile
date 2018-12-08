package top.spencercjh.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + pdfFilePath);
        // 将pdf装图片 并且自定义图片得格式大小
        File file = new File(pdfFilePath);
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            String path = "/output/" + BaseUtils.getDateStr("yyyyMMdd") +
                    "/image/" + BaseUtils.uuid2() + ".png";
            for (int i = 0; i < pageCount; ++i) {
                BufferedImage image = renderer.renderImageWithDPI(0, 144);
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                String imageFilePath = PathUtils.getClassRootPath(path);
                if (!OsInfo.isWindows()) {
                    imageFilePath = "/" + imageFilePath;
                }
                FilesUtils.checkFolderAndCreate(Objects.requireNonNull(imageFilePath));
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + imageFilePath);
                ImageIO.write(image, "png", new File(imageFilePath));
            }
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 自由确定起始页和终止页
     *
     * @param fileAddress  文件地址
     * @param filename     pdf文件名
     * @param indexOfStart 开始页  开始转换的页码，从0开始
     * @param indexOfEnd   结束页  停止转换的页码，-1为全部
     * @param type         图片类型
     */
    public static void pdf2png(String fileAddress, String filename, int indexOfStart, int indexOfEnd, String type) {
        // 将pdf装图片 并且自定义图片得格式大小
        File file = new File(fileAddress + "\\" + filename + ".pdf");
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = indexOfStart; i < indexOfEnd; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 144);
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                File imageFile = new File(fileAddress + "\\" + filename + "_" + (i + 1) + "." + type);
                ImageIO.write(image, type, imageFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
