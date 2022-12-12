package pers.ailurus;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.List;

/**
 * 文件工具
 *
 * @author wzy
 * @date 2022/09/17
 */
public class MyFileUtil {

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
    public static boolean extractJarFile(String filePath, String target) {
        return CommonUtil.runCmd("7z x -y \"" + filePath + "\" -o\"" + target + "\"");
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
            return FileTypeUtil.getType(file).equals("jar");
        }
    }

    /**
     * 得到解压目标路径
     *
     * @param filePath 文件路径
     * @return {@code String}
     */
    public static String getExtractTargetPath(String filePath) {
        return StrUtil.sub(filePath, 0, filePath.lastIndexOf(".")) + "_extract";
    }


    /**
     * 删除并创建文件
     *
     * @param filePath 文件路径
     */
    public static boolean dacFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            FileUtil.touch(file);
        } else {
            FileUtil.del(filePath);
            FileUtil.touch(file);
        }
        return true;
    }

    public static List<CsvRow> readCSVFile(String filePath) throws IOException {
        CsvReader reader = CsvUtil.getReader();
        //从文件中读取CSV数据
        CsvData data = reader.read(FileUtil.file(filePath));
        return data.getRows();
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
     * @param info 信息
     * @param path 路径
     */
    public static void writeCSV(List<String[]> info, String path) {
        dacFile(path);
        CsvWriter writer = CsvUtil.getWriter(path, CharsetUtil.CHARSET_UTF_8);
        writer.write(info);
    }

    /**
     * 追加写入csv文件
     *
     * @param info 信息
     * @param path 路径
     */
    public static void writeCSVAppend(List<String[]> info, String path) {
        CsvWriter writer = CsvUtil.getWriter(path, CharsetUtil.CHARSET_UTF_8);
        writer.write(info);
    }


    /**
     * 获取文件名称
     * @param path 文件路径
     */
    public static String getFileNameWithOutSuffix(String path) {
        File file = new File(path);
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

}
