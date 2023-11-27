package ink.task.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 10:02:27
 * @Description:
 */
public final class ClassUtils {
    private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * 获取抽象类所有的实现类
     * @param clazz 抽象类Class
     * @return 返回实现类集合
     * @param <T> 泛型
     */
    public static <T> List<Class<? extends T>> getAbstractImpl(Class<T> clazz) {
        List<Class<? extends T>> implClasses = new ArrayList<>(5);
        try {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                List<String> classPaths = getClassPaths(clazz);
                for (String path : classPaths) {
                    Class<?> aClass = Class.forName(path);
                    if (aClass.isInterface() || aClass.getSuperclass() == null || Modifier.isAbstract(aClass.getModifiers())) {
                        continue;
                    }
                    Class<?> superclass = aClass.getSuperclass();
                    if (superclass.equals(clazz)) {
                        implClasses.add((Class<? extends T>) aClass);
                    }
                }
            } else {
                logger.warn("Class对象不是一个抽象类");
            }
        } catch (Exception e) {
            logger.error("获取抽象类实现异常，抽象类：{}，错误信息：{}", clazz.toString(), e.getMessage());
        }
        return implClasses;
    }

    /**
     * 返回一个接口的所有实现类
     * @param clazz 接口Class
     * @return 返回接口的实现类列表
     * @param <T> 泛型
     */
    public static <T> List<Class<? extends T>> getInterfaceImpl(Class<T> clazz) {
        List<Class<? extends T>> implClasses = new ArrayList<>(5);
        try {
            if (clazz.isInterface()) {
                List<String> classPaths = getClassPaths(clazz);
                for (String path : classPaths) {
                    Class<?> aClass = Class.forName(path);
                    if (aClass.isInterface() || aClass.getInterfaces().length == 0 || Modifier.isAbstract(aClass.getModifiers())) {
                        continue;
                    }
                    List<Class<?>> interfaceList = Arrays.stream(aClass.getInterfaces()).toList();
                    if (interfaceList.contains(clazz)) {
                        implClasses.add((Class<? extends T>) aClass);
                    }
                }
            } else {
                logger.warn("Class对象不是一个接口，{}", clazz);
            }
        } catch (Exception ex) {
            logger.error("获取接口类实现异常，接口类：{}，错误信息：{}", clazz.toString(), ex.getMessage());
        }
        return implClasses;
    }
    private static List<String> getClassPaths(Class<?> clazz) {
        String basePackagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File[] files = new File(basePackagePath).listFiles();
        List<String> classPaths = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                listPackages(clazz, file.getName(), classPaths);
            }
        }
        return classPaths;
    }

    private static void listPackages(Class<?> clazz , String basePackagePath, List<String> classNames) {
        URL resource = clazz.getClassLoader().getResource("./" + basePackagePath.replaceAll("\\.", "/"));
        if (resource == null) {
            return;
        }
        File directory = new File(resource.getFile());
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                listPackages(clazz, basePackagePath + "." + file.getName(), classNames);
            } else {
                String className = file.getName();
                if (className.endsWith(".class")) {
                    classNames.add(basePackagePath + "." + className.replace(".class", ""));
                }
            }
        }
    }
}
