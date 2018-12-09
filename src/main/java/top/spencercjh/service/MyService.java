package top.spencercjh.service;

import java.io.IOException;
import java.util.List;

/**
 * @author spencercjh
 */
public interface MyService {
    String fillDataToHtml(String userId, String templateFilePath, List<String> data)throws IOException;
}
