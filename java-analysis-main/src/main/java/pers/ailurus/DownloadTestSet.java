package pers.ailurus;

import pers.ailurus.model.MavenRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadTestSet {
    public static void main(String[] args) {
        // 根据数据库中的数据下载测试集并保存测试集CSV
        // 1. 从数据库中读取所有的maven的下载地址
        try {
            DataOperator.initOperator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<MavenRepository> mavenList = DataOperator.selectMavenDownloadList(100);
        List<String[]> csv = new ArrayList<>();
        String folderPath = "C:\\Users\\wzy\\Desktop\\test";
        csv.add(new String[]{"md5", "url", "file_name", "name", "version", "predicted_name", "predicted_version"});
        for (MavenRepository maven : mavenList) {
            String url = maven.getUrl();
            String urlCN = url.replace("https://repo1.maven.org/maven2/", "https://maven.aliyun.com/repository/public/");
            System.out.println(urlCN);
            csv.add(new String[]{maven.getMd5(), urlCN, String.format("%s-%s.jar", maven.getName(), maven.getVersion()), maven.getName(), maven.getVersion(), "", ""});
            NetUtil.download(urlCN, String.format("%s-%s.jar", maven.getName(), maven.getVersion()), folderPath, 0);
        }
        FileUtil.writeCSV(csv, "C:\\Users\\wzy\\Desktop\\test\\test.csv");
    }
}
