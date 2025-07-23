package utility;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class AutoInstantiate {

    public static void initializeStaticFields(Class<?> targetClass) {
        Field[] fields = targetClass.getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    Class<?> fieldType = field.getType();
                    try {
                        java.lang.reflect.Constructor<?> constructor = fieldType.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        Object newInstance = constructor.newInstance();
                        field.set(null, newInstance);
                    } catch (NoSuchMethodException e) {
                    }
                } catch (InstantiationException | IllegalAccessException |
                         java.lang.reflect.InvocationTargetException e) {
                }
            } else {
                //throw error
            }
        }
    }


}
//TODO Add fault/log

