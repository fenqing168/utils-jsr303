package cn.fenqing168.utils.jsr303.handler.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author fenqing
 */
public class NotEmptyValidationHandler extends BaseValidationHandler {


    public NotEmptyValidationHandler(Annotation annotation, Object object, Field field) {
        super(annotation, object, field);
    }

    @Override
    public boolean validation() {
        boolean isNotNull = object != null;
        if(isNotNull){
            if(object instanceof String){
                return !"".equals(object);
            }
            if(object instanceof Collection){
                return !((Collection<?>) object).isEmpty();
            }
            if(object.getClass().isArray()){
                return Array.getLength(object) > 0;
            }
        }
        return false;
    }
}
