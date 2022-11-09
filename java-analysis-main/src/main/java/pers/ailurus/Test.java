package pers.ailurus;

import soot.G;
import soot.Scene;
import soot.options.Options;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        String path = "C:\\Users\\wzy\\Desktop\\test\\activation-1.0.2";
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_process_dir(List.of(path));
        Options.v().set_whole_program(true);
        Scene.v().loadNecessaryClasses();
        System.out.println(Scene.v().getBasicClasses().size());
        for (String sc: Scene.v().getBasicClasses().stream().toList()) {
            System.out.println(sc);
        }
        System.out.println();
    }
}
