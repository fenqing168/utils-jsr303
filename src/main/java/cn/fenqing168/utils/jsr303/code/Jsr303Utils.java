package cn.fenqing168.utils.jsr303.code;

import cn.fenqing168.utils.jsr303.bean.ErrorInfo;
import cn.fenqing168.utils.jsr303.bean.ErrorInfos;
import cn.fenqing168.utils.jsr303.bean.RegisterContainerManager;
import cn.fenqing168.utils.jsr303.handler.ValidationHandler;
import cn.fenqing168.utils.jsr303.handler.impl.NotBlankValidationHandler;
import cn.fenqing168.utils.jsr303.handler.impl.NotEmptyValidationHandler;
import cn.fenqing168.utils.jsr303.handler.impl.NotNullValidationHandler;
import cn.fenqing168.utils.jsr303.handler.impl.NullValidationHandler;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 校验核心类
 * @author fenqing
 */
public class Jsr303Utils {

    static {

        /**
         * 预先注册
         */
        RegisterContainerManager.register(NotNull.class, NotNullValidationHandler.class);
        RegisterContainerManager.register(NotEmpty.class, NotEmptyValidationHandler.class);
        RegisterContainerManager.register(Null.class, NullValidationHandler.class);
        RegisterContainerManager.register(NotBlank.class, NotBlankValidationHandler.class);

    }

    /**
     * 校验单个对象
     * @param object 对象
     * @param group 分组
     * @return 错误信息，size=0就没有异常
     */
    @SuppressWarnings("all")
    public static List<ErrorInfo> validation(Object object, Class group){
        if(object == null){
            throw new NullPointerException("对象不能为null");
        }
        List<ErrorInfo> errorInfos = new ArrayList<>();
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if(RegisterContainerManager.canHandle(annotation.annotationType())){
                    ValidationHandler validationHandler = ValidationHandler.create(annotation, object, field);
                    List<Class> groups = validationHandler.getGroups();
                    if(groups.contains(group) && !validationHandler.validation()){
                        ErrorInfo errorInfo = new ErrorInfo();
                        errorInfo.setField(field.getName());
                        errorInfo.setMessage(validationHandler.getMessage());
                        errorInfos.add(errorInfo);
                    }
                }
            }
        }
        return errorInfos;
    }

    /**
     * 校验 多个数据
     * @param collection 多个数据的集合
     * @param group 分组
     * @return 错误信息，size=0就没有异常
     */
    @SuppressWarnings("all")
    public static List<ErrorInfos> validations(Collection collection, Class group){
        if(collection == null){
            throw new NullPointerException("对象不能为null");
        }
        List<ErrorInfos> errorInfos = new ArrayList<>();
        Iterator<?> iterator = collection.iterator();
        for (int i = 0; iterator.hasNext();i++){
            Object o = iterator.next();
            List<ErrorInfo> validation = validation(o, group);
            for (ErrorInfo errorInfo : validation) {
                ErrorInfos errorInfos1 = new ErrorInfos();
                errorInfos1.setRow(i);
                errorInfos1.setField(errorInfo.getField());
                errorInfos1.setMessage(errorInfo.getMessage());
                errorInfos.add(errorInfos1);
            }
        }
        return errorInfos;
    }

}
