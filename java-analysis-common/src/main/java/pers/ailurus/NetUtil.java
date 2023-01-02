package pers.ailurus;

import cn.hutool.core.lang.Console;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtil {

    public static boolean download(String url, String path) {
        File file = new File(path);
        String fileName = file.getName();
        String dir = file.getParent();
        return download(url, fileName, dir, 0);
    }

    public static boolean download(String url, String fileName, String dir, int count) {
        if (count == 3) {
            Console.error(String.format("[%s] Unable to download.", fileName));
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
            return download(url, fileName, dir, count + 1);
        }
        return true;
    }


    public static boolean downloadWithCheckSize(String url, String fileName, String dir, int checkSize) {
        HttpURLConnection conn = null;
        try {
            URL http = new URL(url);
            conn = (HttpURLConnection) http.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows 7; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36 YNoteCef/5.8.0.1 (Windows)");
            long size = conn.getContentLength();
            if (size == -1) {
                Console.log(String.format("[%s] Unable to obtain information of TPL file, skip the analysis step", fileName));
                return false;
            }
            if (size > checkSize) {
                Console.log(String.format("[%s] The TPL file size exceeds limit , skip the analysis step", fileName));
                return false;
            }
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            FileUtils.copyURLToFile(http, new File(dir + File.separator + fileName));
        } catch (Exception e) {
            return download(url, fileName, dir, 1);
        } finally {
            conn.disconnect();
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
