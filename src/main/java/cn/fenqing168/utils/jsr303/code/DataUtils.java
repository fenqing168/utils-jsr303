package cn.fenqing168.utils.jsr303.code;


import cn.fenqing168.utils.jsr303.constant.AbstractConst;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author fenqing
 */
public class DataUtils {

    static final String THIS = "this";

    /**
     * 获取对象里属性值
     * @param o     对象
     * @param field 字段名
     * @return      属性值
     */
    public static Object getValue(Object o, String field){
        if(o == null){
            return null;
        }
        Class<?> clazz = o.getClass();
        Matcher matcher = AbstractConst.PATTERN.matcher(field);
        if(matcher.find()){
            String group = matcher.group(0);
            int index = Integer.parseInt(group);
            if(o instanceof List){
                return ((List<?>) o).get(index);
            }else{
                return Array.get(o, index);
            }
        }else{
            if(o instanceof Map){
                Method get = AbstractConst.getMapGetMethod();
                try {
                    return get.invoke(o, field);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else{
                try {
                    Field classField = clazz.getDeclaredField(field);
                    classField.setAccessible(true);
                    return classField.get(o);
                } catch (Exception e) {
                    Method getMethod = getGetMethod(clazz, field);
                    try {
                        return getMethod.invoke(o);
                    } catch (Exception ex) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * 获取对象里属性值
     * @param o     对象
     * @param field 字段名
     * @return      属性值
     */
    @SuppressWarnings("unchecked")
    public static boolean setValue(Object o, String field, Object value){
        if(o == null){
            return false;
        }
        Class<?> clazz = o.getClass();
        Matcher matcher = AbstractConst.PATTERN.matcher(field);
        if(matcher.find()){
            String group = matcher.group(0);
            int index = Integer.parseInt(group);
            if(o instanceof List){
                ((List<Object>) o).set(index, value);
                return true;
            }else{
                Array.set(o, index, value);
                return true;
            }
        }else{
            try {
                Field classField = clazz.getDeclaredField(field);
                classField.setAccessible(true);
                classField.set(o, value);
                return true;
            } catch (Exception e) {
                Method getMethod = getSetMethod(clazz, field, value.getClass());
                try {
                    getMethod.invoke(o, value);
                    return true;
                } catch (Exception ex) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 获取对象里属性值
     * @param o          对象
     * @param field      属性
     * @param returnType 返回类型
     * @param <T>        返回类型
     * @return           属性值
     */
    public static <T> T getValue(Object o, String field, Class<T> returnType){
        return (T) getValue(o, field);
    }

    /**
     * 获取对象里属性值
     * @param o     对象
     * @param field 字段名,支持级联
     * @return      属性值
     */
    public static Object getValueDeep(Object o, String field){
        String[] split = field.split("\\.");
        return getValueDeep(o, split, 0);
    }

    /**
     * 获取对象里属性值
     * @param o     对象
     * @param field 字段名,支持级联
     * @return      属性值
     */
    public static <T> T getValueDeep(Object o, String field, Class<T> returnType){
        return (T) getValueDeep(o, field);
    }

    static Class<?> getFieldType(Class<?> clazz, String field){
        try {
            Field declaredField = clazz.getDeclaredField(field);
            return declaredField.getType();
        }catch (Exception e){
            return getGetMethod(clazz, field).getReturnType();
        }
    }

    /**
     * 获取对象里属性值
     * @param o      对象
     * @param fields 字段名,支持级联
     * @return       属性值
     */
    private static Object getValueDeep(Object o, String[] fields, int index){
        if(o == null || index == fields.length){
            return o;
        }
        String field = fields[index];
        Matcher matcher = AbstractConst.PATTERN.matcher(field);
        int indexStart = field.indexOf("[");
        if(matcher.find() && indexStart != 0){
            String fidld1 = field.substring(0, indexStart);
            String indexField = field.substring(indexStart);
            Object value1 = getValue(o, fidld1);
            Object value2 = getValue(value1, indexField);
            return getValueDeep(value2, fields, index + 1);
        }else{
            Object value = getValue(o, field);
            return getValueDeep(value, fields, index + 1);
        }
    }

    /**
     * 获取get方法
     * @param clazz 类
     * @param field 属性名
     * @return      方法对象
     */
    private static Method getGetMethod(Class<?> clazz, String field){
        try {
            return clazz.getMethod("get" + field.toUpperCase().substring(0, 1) + field.substring(1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取get方法
     * @param clazz 类
     * @param field 属性名
     * @return      方法对象
     */
    private static Method getSetMethod(Class<?> clazz, String field, Class<?> fieldClass){
        try {
            return clazz.getMethod("set" + field.toUpperCase().substring(0, 1) + field.substring(1), getFieldType(clazz, field));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean isNotEmpty(Object obj) {
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        boolean flag = false;
        int fieLen = fields.length;
        for(int i = 0; i < fieLen; ++i) {
            Field field = fields[i];
            if (flag) {
                break;
            }

            try {
                field.setAccessible(true);
                Object val = field.get(obj);
                if (val != null) {
                    Class<?> fieldType = field.getType();
                    Integer len;
                    if (fieldType.isArray()) {
                        Field length = fieldType.getField("length");
                        len = (Integer)length.get(val);
                        flag = len > 0;
                    } else if (!Map.class.isAssignableFrom(fieldType) && !Collection.class.isAssignableFrom(fieldType)) {
                        if (String.class.equals(fieldType)) {
                            flag = !"".equals(val);
                        } else if (!fieldType.isPrimitive() && !isWrapClass(fieldType)) {
                            flag = isEmpty(val);
                        }
                    } else {
                        Method size = fieldType.getMethod("size");
                        len = (Integer)size.invoke(val);
                        flag = len > 0;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return flag;
    }

    public static boolean isEmpty(Object obj) {
        return !isNotEmpty(obj);
    }

    /**
     * 是否包装类型
     * @param clz 类
     * @return 是否为包装类
     */
    public static boolean isWrapClass(Class<?> clz) {
        try {
            return ((Class<?>)clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

}

