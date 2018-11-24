package com.demo.aplib.compiler;

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


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //告知ProcessingEnvironment该处理器能处理的注解类型
        Set<String> types = new LinkedHashSet<>();
        types.add(Override.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        MethodSpec createDefaultName = MethodSpec
                .methodBuilder("createDefaultName")
                .addParameter(Integer.class, "num")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(String.class)
                .addStatement("return num +  $S", "牛铁柱")
                .build();


        FieldSpec name = FieldSpec.builder(String.class, "NAME")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$N(001)", createDefaultName)
                .build();


        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "test")
                .build();


        MethodSpec today = MethodSpec
                .methodBuilder("getToday")
                .addModifiers(Modifier.PUBLIC)
                .returns(Date.class)
                .addComment("//返回当前时间！")
                .addStatement("return new $T()", Date.class)
                .build();


        // 类
        TypeSpec buildClass = TypeSpec.classBuilder("GeneratedClass")
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
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
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
