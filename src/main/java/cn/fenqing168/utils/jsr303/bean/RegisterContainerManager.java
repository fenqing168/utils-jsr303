package cn.fenqing168.utils.jsr303.bean;

import cn.fenqing168.utils.jsr303.handler.impl.BaseValidationHandler;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fenqing
 */
public class RegisterContainerManager {

    private static final Map<Class<? extends Annotation>, HandlerInfos> REGISTER_CONTAINER
            = new HashMap<>();

    private static final Constructor<BaseValidationHandler> BASE_VALIDATION_HANDLER_CONSTRUCTOR;
    private static final Class[] paramTypes;

    static {
        Class<BaseValidationHandler> baseValidationHandlerClass = BaseValidationHandler.class;
        try {
            BASE_VALIDATION_HANDLER_CONSTRUCTOR = baseValidationHandlerClass.getConstructor(Annotation.class, Object.class, Field.class);
            paramTypes = BASE_VALIDATION_HANDLER_CONSTRUCTOR.getParameterTypes();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void register(Class<? extends Annotation> annotation, Class<? extends BaseValidationHandler> handler) {
        if(!REGISTER_CONTAINER.containsKey(annotation)){
            try {
                Constructor<? extends BaseValidationHandler> constructor = handler.getConstructor(paramTypes);
                HandlerInfos handlerInfos = new HandlerInfos();
                handlerInfos.setClazz(handler);
                handlerInfos.setConstructor(constructor);
                REGISTER_CONTAINER.put(annotation, handlerInfos);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void overrideRegister(Class<? extends Annotation> annotation, Class<? extends BaseValidationHandler> handler) {
        Constructor<? extends BaseValidationHandler> constructor = null;
        try {
            constructor = handler.getConstructor(paramTypes);
            HandlerInfos handlerInfos = new HandlerInfos();
            handlerInfos.setClazz(handler);
            handlerInfos.setConstructor(constructor);
            REGISTER_CONTAINER.put(annotation, handlerInfos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HandlerInfos getHandler(Class<? extends Annotation> annotation) {
        return REGISTER_CONTAINER.get(annotation);
    }

    public static boolean canHandle(Class<? extends Annotation> annotation) {
        return REGISTER_CONTAINER.containsKey(annotation);
    }

    public static Map<Class<? extends Annotation>, HandlerInfos> getRegisterContainer(){
        return new HashMap<Class<? extends Annotation>, HandlerInfos>(8){{
            putAll(REGISTER_CONTAINER);
        }};
    }

    @Data
    public static class HandlerInfos{
        Class<? extends BaseValidationHandler> clazz;
        Constructor<? extends BaseValidationHandler> constructor;
    }

}
