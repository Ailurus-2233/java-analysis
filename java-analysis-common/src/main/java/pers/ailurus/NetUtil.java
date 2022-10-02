package pers.ailurus;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

public class NetUtil {
    public static boolean dowlnoad(String url, String fileName, String dir) {
        try {
            URL http = new URL(url);
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            FileUtils.copyURLToFile(http, new File(dir + File.separator + fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
