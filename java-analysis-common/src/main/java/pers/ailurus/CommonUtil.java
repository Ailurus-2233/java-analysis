package pers.ailurus;

import com.alibaba.fastjson2.JSON;

import java.io.*;

import static pers.ailurus.FileUtil.deleteAndCreateFile;

public class CommonUtil {

    public static  <T> String object2json(T obj) {
        return JSON.toJSONString(obj);
    }

    /**
     * 保存对象到目标文件
     *
     * @param filePath 文件路径
     * @param object      对象
     * @return boolean
     */
    public static boolean saveObject2File(Object object, String filePath) {
        if (object == null) {
            return false;
        }
        if (!deleteAndCreateFile(filePath)) {
            return false;
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * 保存字符串到目标文件
     *
     * @param filePath 文件路径
     * @param content  内容
     * @return boolean
     */
    public static boolean saveString2File(String content, String filePath) {
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


    public static boolean isLinux() {
        String os = System.getProperty("os.name");
        return !os.toLowerCase().startsWith("win");
    }


    public static boolean runCmd(String inputCmd) {
        String[] cmd;
        if (isLinux()) {
            cmd = new String[]{"/bin/sh", "-c", inputCmd};
        } else {
            cmd = new String[]{"cmd", "/c", inputCmd};
        }
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
