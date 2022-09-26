package pers.ailurus;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;

/**
 * 文件工具
 *
 * @author wzy
 * @date 2022/09/17
 */
public class FileUtil {

    /**
     * 得到文件md5
     *
     * @param filePath 文件路径
     * @return {@code String}
     */
    public static String getFileMd5(String filePath) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(filePath));
    }

    /**
     * 提取jar文件
     *
     * @param filePath 文件路径
     * @param target   目标
     * @return boolean
     * @throws RuntimeException 运行时异常
     */
    public static boolean extractJarFile(String filePath, String target) throws RuntimeException {
        StringBuilder sb = new StringBuilder();
        sb.append("7z x -y \"").append(filePath).append("\" -o\"").append(target).append("\"");
        try {
            Process process = Runtime.getRuntime().exec(sb.toString());
            process.waitFor();
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 是jar文件
     *
     * @param filePath 文件路径
     * @return boolean
     */
    public static boolean isJarFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            return ".jar".equals(filePath.substring(filePath.length() - 4));
        }
    }

    /**
     * 得到解压目标路径
     *
     * @param filePath 文件路径
     * @return {@code String}
     */
    public static String getTargetPath(String filePath) {
        return filePath.substring(0, filePath.length() - 4);
    }

    /**
     * 保存字符串到目标文件
     *
     * @param filePath 文件路径
     * @param content  内容
     * @return boolean
     */
    public static boolean save2File(String filePath, String content) {
        if (content == null) {
            return false;
        }
        if (!deleteAndCreateFile(filePath)) {
            return false;
        }
        try (PrintWriter out = new PrintWriter(filePath)) {
            out.println(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 删除并创建文件
     *
     * @param filePath 文件路径
     */
    public static boolean deleteAndCreateFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
