package pers.ailurus;

import pers.ailurus.model.MavenRepository;
import pers.ailurus.model.Result;

import java.io.IOException;
import java.util.List;

public class CompareWithoutExtract {

    public static void main(String[] args) {
        try {
            DataOperator.initOperator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        List<MavenRepository> mavenList = DataOperator.selectMavenDownloadList(1);
//        for (MavenRepository maven : mavenList) {
//            System.out.printf("%s - %s \n", maven.getName(), maven.getVersion());
//            System.out.println(maven.getMd5());
            List<Result> results = Comparator.deduceTPLByFileMd5("cbdd9acc63bc1759d1d86e48a10ef155");
            System.out.println(results);
//        }
    }

}
