package pers.ailurus;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtil {
    private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

    public static boolean download(String url, String fileName, String dir, int count) {
        if (count == 5) {
            logger.warn("下载失败，请检查下载链接：" + url);
            return false;
        }
        try {
            URL http = new URL(url);
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            FileUtils.copyURLToFile(http, new File(dir + File.separator + fileName));
        } catch (Exception e) {
            logger.warn("网络异常，重新请求下载：" + url);
            download(url, fileName, dir, count + 1);
        }
        return true;
    }

    /**
     * 获取网络文件大小
     */
    public static long getFileLength(String downloadUrl) throws IOException{
        if(downloadUrl == null || "".equals(downloadUrl)){
            return 0L ;
        }
        URL url = new URL(downloadUrl);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows 7; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36 YNoteCef/5.8.0.1 (Windows)");
            return (long) conn.getContentLength();
        } catch (IOException e) {
            return 0L;
        } finally {
            conn.disconnect();
        }
    }
}
