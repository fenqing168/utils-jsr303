package cn.fenqing168.utils.jsr303.handler;


import cn.fenqing168.utils.jsr303.handler.impl.BaseValidationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author fenqing
 */
public interface ValidationHandler {

    /**
     * 校验
     * @return
     */
    boolean validation();

    /**
     * 获取分组信息
     * @return
     */
    List<Class> getGroups();

    /**
     * 获取消息
     * @return
     */
    String getMessage();

    /**
     * 创建
     * @param annotation 注解
     * @param object 需要被校验的对象
     * @param field
     * @return
     */
    static ValidationHandler create(Annotation annotation, Object object, Field field){
        return BaseValidationHandler.newInstance(annotation, object, field);
    }
}
