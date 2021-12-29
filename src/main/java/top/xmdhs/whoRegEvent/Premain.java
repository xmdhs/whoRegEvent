package top.xmdhs.whoRegEvent;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.HashSet;

public class Premain {
    public static void premain(String agentArgs, Instrumentation inst) {
        HashSet<ClassLoader> h = new HashSet<>();
        ClassPool pp = ClassPool.getDefault();
        pp.importPackage("top.xmdhs.whoRegEvent.Callback");
        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (className.startsWith("org/bukkit/event")) {
                try {
                    ClassPool p = ClassPool.getDefault();
                    if (!h.contains(loader)) {
                        p.appendClassPath(new LoaderClassPath(loader));
                        h.add(loader);
                    }
                    CtClass c = p.makeClass(new ByteArrayInputStream(classfileBuffer));
                    if (c.isInterface()) {
                        return null;
                    }
                    CtClass cb = p.get("top.xmdhs.whoRegEvent.Callback");
                    CtField f = new CtField(cb, "callbackWre", c);
                    f.setModifiers(Modifier.PUBLIC);
                    c.addField(f, "null");
                    CtMethod mm = CtNewMethod.make("public void dosome(String a,Object[] args){}", c);
                    c.addMethod(mm);
                    mm.addLocalVariable("callbackWre", cb);
                    mm.setBody("{if (callbackWre != null){\n" +
                            "callbackWre.callback($1,$2);\n" +
                            "};\n" +
                            "}");

                    for (CtMethod m : c.getDeclaredMethods()) {
                        if (Modifier.isStatic(m.getModifiers()) || m.isEmpty()) {
                            continue;
                        }
                        if (m.getName().equals("dosome") || m.getName().equals("getEventName")) {
                            continue;
                        }
                        m.insertBefore("{dosome(\"" + m.getName() + "\",$args);}");
                    }
                    return c.toBytecode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

}
