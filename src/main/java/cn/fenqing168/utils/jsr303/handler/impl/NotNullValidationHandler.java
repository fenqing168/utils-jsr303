package cn.fenqing168.utils.jsr303.handler.impl;

import cn.fenqing168.utils.jsr303.code.DataUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author fenqing
 */
public class NotNullValidationHandler extends BaseValidationHandler {

    public NotNullValidationHandler(Annotation annotation, Object object, Field field) {
        super(annotation, object, field);
    }

    @Override
    public boolean validation() {
        Object value = DataUtils.getValue(object, field.getName());
        return value != null;
    }

}
