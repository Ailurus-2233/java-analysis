package pers.ailurus;


import java.io.*;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;

import static pers.ailurus.MyFileUtil.dacFile;

public class CommonUtil {

    public static <T> String object2json(T obj) {
        JSONObject json = JSONUtil.parseObj(obj, false);
        return json.toStringPretty();
    }

    /**
     * 保存对象到目标文件
     *
     * @param filePath 文件路径
     * @param object      对象
     * @return boolean
     */
    public static boolean saveObject2File(Serializable object, String filePath) {
        if (object == null) {
            return false;
        }
        if (!dacFile(filePath)) {
            return false;
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            IoUtil.writeObj(fos, true, object);
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
        if (!dacFile(filePath)) {
            return false;
        }
        File file = new File(filePath);
        FileWriter fw = new FileWriter(file);
        fw.write(content);
        return true;
    }


    public static boolean isLinux() {
        OsInfo os = SystemUtil.getOsInfo();
        return os.isLinux();
    }


    public static boolean runCmd(String cmd) {
        try {
            RuntimeUtil.execForStr(cmd);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getStrMd5(String source) {
        return SecureUtil.md5(source);
    }
}
