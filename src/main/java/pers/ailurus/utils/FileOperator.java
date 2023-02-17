package pers.ailurus.utils;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileOperator {
    public static boolean isJarFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            return FileTypeUtil.getType(file).equals("jar");
        }
    }

    public static boolean extractJarFile(String filePath, String target) {
        FileUtil.del(new File(target));
        File tar = ZipUtil.unzip(filePath, target);
        return tar.exists();
    }

    public static String getFileMd5(String filePath) throws IOException {
        InputStream inputStream = new FileInputStream(filePath);
        String md5 =  DigestUtil.md5Hex(inputStream);
        inputStream.close();
        return md5;
    }

    public static String getExtractTargetPath(String path) {
        return StrUtil.sub(path, 0, path.lastIndexOf(".")) + "_extract";
    }

    public static String downloadFile(String url, File target) {
        // 判断是文件夹还是文件
        if (!isFile(target)) {
            FileUtil.mkdir(target);
            target = FileUtil.file(target, url.substring(url.lastIndexOf("/") + 1));
        }
        HttpUtil.downloadFile(url, target);
        return target.getAbsolutePath();
    }

    public static void writeFile(String text, String path) {
        FileUtil.writeUtf8String(text, path);
    }

    public static boolean isFile(File file) {
        // 根据文件是否后缀名判断是否是文件
        return file.getName().contains(".");
    }

    public static long getNetFileSize(String url) throws MalformedURLException {
        // 获取网络文件的大小
        if(url == null || "".equals(url)){
            return 0L ;
        }
        URL u = new URL(url);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows 7; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36 YNoteCef/5.8.0.1 (Windows)");
            return conn.getContentLengthLong();
        } catch (IOException e) {
            return 0L;
        } finally {
            conn.disconnect();
        }
    }
}
