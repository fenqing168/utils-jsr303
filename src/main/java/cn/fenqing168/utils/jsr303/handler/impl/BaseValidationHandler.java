package cn.fenqing168.utils.jsr303.handler.impl;

import cn.fenqing168.utils.jsr303.bean.RegisterContainerManager;
import cn.fenqing168.utils.jsr303.handler.ValidationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author fenqing
 */
public abstract class BaseValidationHandler implements ValidationHandler {

    protected Annotation annotation;
    protected Object object;
    protected Field field;
    private final Method messageMethod;
    private final Method groupsMethod;

    public static BaseValidationHandler newInstance(Annotation annotation, Object object, Field field) {
        try {
            return RegisterContainerManager.getHandler(annotation.annotationType()).getConstructor().newInstance(annotation, object, field);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("all")
    public BaseValidationHandler(Annotation annotation, Object object, Field field) {
        this.annotation = annotation;
        this.object = object;
        this.field = field;
        Class<?> annotationClass = (Class<?>) annotation.getClass();
        try {
            messageMethod = annotationClass.getMethod("message");
            groupsMethod = annotationClass.getMethod("groups");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("系统异常");
        }
    }

    @Override
    @SuppressWarnings("all")
    public List<Class> getGroups() {
        try {
            return Arrays.asList((Class[]) groupsMethod.invoke(annotation));
        } catch (Exception e) {
            throw new RuntimeException("系统异常");
        }
    }

    @Override
    public String getMessage() {
        try {
            return (String) messageMethod.invoke(annotation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
