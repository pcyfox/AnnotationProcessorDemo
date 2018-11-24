package com.demo.aplib.compiler;


import com.demo.annotationlib.annotations.BindViews;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
public class DemoProcessor extends AbstractProcessor {
    private static final String OPTION_SDK_INT = "aplib.minSdk";
    private static final String OPTION_DEBUGGABLE = "aplib.debuggable";
    /**
     * 文件相关的辅助类
     */
    private Filer mFiler;
    /**
     * 元素相关的辅助类
     */
    private Elements mElementUtils;
    /**
     * 日志相关的辅助类
     */
    private Messager mMessager;


    private int sdk = 1;
    private boolean debuggable = true;
    private Types typeUtils;

    //初始化，编译时会被自动调用，与JNI中的JNI_OnLoad(JINEnv env)方法类似
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        String sdk = env.getOptions().get(OPTION_SDK_INT);
        if (sdk != null) {
            try {
                this.sdk = Integer.parseInt(sdk);
            } catch (NumberFormatException e) {
                env.getMessager()
                        .printMessage(Diagnostic.Kind.WARNING, "Unable to parse supplied minSdk option '"
                                + sdk
                                + "'. Falling back to API 1 support.");
            }
        }

        debuggable = !"false".equals(env.getOptions().get(OPTION_DEBUGGABLE));
        typeUtils = env.getTypeUtils();
        mFiler = env.getFiler();
        mMessager = env.getMessager();
        mElementUtils = env.getElementUtils();
        super.init(env);
    }


    //告知 ProcessingEnvironment该自定义注解处理器所支持的注解类型
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //告知ProcessingEnvironment该处理器能处理的注解类型
        Set<String> types = new LinkedHashSet<>();
        types.add(BindViews.class.getCanonicalName());
        return types;
    }


    //编译时发现有该处理器支持的注解时调用该方法
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {


        //自动生成Java文件示例
        createJavaFile();
        return true;
    }

    private void createJavaFile() {
        //定义方法
        MethodSpec createDefaultName = MethodSpec
                .methodBuilder("createDefaultName")
                .addParameter(Integer.class, "num")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(String.class)
                .addStatement("return num +  $S", "牛铁柱")
                .build();

        //定义字段
        FieldSpec name = FieldSpec.builder(String.class, "NAME")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                //调用上面定义的方法来初始化字段
                .initializer("$N(001)", createDefaultName)
                .build();

       //定义构造函数
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "test")
                .build();

       //构建带返回值的方法
        MethodSpec today = MethodSpec
                .methodBuilder("getToday")
                .addModifiers(Modifier.PUBLIC)
                .returns(Date.class)
                .addComment("//返回当前时间！")//添加注释
                .addStatement("return new $T()", Date.class)//会自动导包
                .build();


        // 定义要生成的类
        TypeSpec buildClass = TypeSpec.classBuilder("GeneratedClassTest")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor)
                .addMethod(createNewName("狗"))
                .addMethod(today)
                .addField(name)
                .addMethod(createDefaultName)
                .build();


        // 创建Java文件
        JavaFile javaFile = JavaFile.builder("com.demo.ap", buildClass).build();

        try {
            //生成类文件（在app/build/generated/source/apt/debug目录下）
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static MethodSpec createNewName(String oldName) {
        return MethodSpec
                .methodBuilder("createName")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return $S", "阿" + oldName)
                .build();
    }


}
