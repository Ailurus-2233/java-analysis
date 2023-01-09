import sootup.core.Project;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.model.SootClass;
import sootup.core.model.SootMethod;
import sootup.core.views.View;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.bytecode.inputlocation.JavaModulePathAnalysisInputLocation;
import sootup.java.core.JavaProject;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.language.JavaLanguage;
import sootup.java.core.views.JavaView;
import sootup.java.sourcecode.inputlocation.JavaSourcePathAnalysisInputLocation;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        // class to jimple
        String path = "D:\\Documents\\java-test\\target\\java-test-jar-with-dependencies.jar";
        classToJimple(path, "");
    }

    public static void classToJimple(String classPath, String jimplePath) {
        Path pathToBinary = Paths.get(classPath);

        AnalysisInputLocation<JavaSootClass> inputLocation =
                new JavaClassPathAnalysisInputLocation(pathToBinary.toString());

        JavaLanguage language = new JavaLanguage(17);

        Project project =
                JavaProject.builder(language).addInputLocation(inputLocation).build();

        JavaView view = (JavaView) project.createFullView();

        for (JavaSootClass sc : view.getClasses()) {
            for (JavaSootMethod sm : sc.getMethods()) {
                System.out.println(sm.getBody());
            }
        }

    }

}
