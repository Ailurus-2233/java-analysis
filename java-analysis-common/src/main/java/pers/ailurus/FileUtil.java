package pers.ailurus;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
        InputStream inputStream = new FileInputStream(filePath);
        String md5 =  DigestUtils.md5Hex(inputStream);
        inputStream.close();
        return md5;
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

//        sb.append("/usr/bin/7z x -y \"").append(filePath).append("\" -o\"").append(target).append("\"");
        sb.append("7z x -y \"").append(filePath).append("\" -o\"").append(target).append("\"");
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", sb.toString()});
//            Process process = Runtime.getRuntime().exec(sb.toString());
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

    public static List<String[]> readCSVFile(String filePath) throws IOException {
        // 创建 reader
        return Files.readAllLines(Paths.get(filePath)).stream().map(s -> s.split(",")).toList();
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                System.out.println(filePath);
            }
        }
    }

    public static void deleteFolder(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f.getAbsolutePath());
                } else {
                    deleteFile(f.getAbsolutePath());
                }
            }
            deleteFile(file.getAbsolutePath());
        }
    }

    /**
     * 追加一行字符串到文件中
     */
    public static void writeLine(String text, String filePath) {
        try {
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(text);
            bw.newLine();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeLines(List<String> info, String filePath) {
        try {
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String s : info) {
                bw.write(s);
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 写入新的csv文件
     * @param info
     * @param path
     * @return
     */
    public static void writeCSV(List<String[]> info, String path) {
        try {
            FileWriter fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String[] strings : info) {
                bw.write(String.join(",", strings));
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 追加写入csv文件
     *
     * @param info
     * @param path
     */
    public static void writeCSVAppend(List<String[]> info, String path) {
        try {
            FileWriter fw = new FileWriter(path, true);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String[] strings : info) {
                bw.write(String.join(",", strings));
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
