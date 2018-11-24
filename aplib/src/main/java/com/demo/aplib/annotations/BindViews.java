package com.demo.aplib.annotations;

import android.support.annotation.AnimRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.ElementType.FIELD;

@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindViews {
    @AnimRes int value();
}
