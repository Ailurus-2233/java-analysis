package pers.ailurus.utils;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.digest.DigestUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
}
