package pers.ailurus;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public class NetUtil {
    private static Logger logger = LoggerFactory.getLogger(NetUtil.class);
    public static boolean dowlnoad(String url, String fileName, String dir) {
        try {
            URL http = new URL(url);
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            FileUtils.copyURLToFile(http, new File(dir + File.separator + fileName));
        } catch (Exception e) {
            logger.warn("网络异常，重新请求下载：" + url);
            dowlnoad(url, fileName, dir);
        }
        return true;
    }
}
