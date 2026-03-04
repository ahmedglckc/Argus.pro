package wentra.utils.mapper.transformers.etc.impl.utils;

import wentra.utils.mapper.Mapper;

import javax.crypto.SecretKey;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class Reflector {
    public class FieldID {
        public String owner;
        public String name;
        public String signature;

        private FieldID(String owner, String name, String signature) {
            this.owner = owner;
            name = name;
            this.signature = signature;
        }
    }

    public static List<FieldID> nonStaticGetFieldIDs = new ArrayList<>();

    public static int access;

    private static List<Class<?>> loadedClasses = new ArrayList<>();

    public static void initialize(List<Class<?>> classes) {
        loadedClasses = classes;
    }

    public static Vector<Class<?>> getLoadedClasses() {
        List<Class<?>> classes = loadedClasses;
        Vector<Class<?>> filteredClasses = new Vector<>();

        for (Class<?> clazz : classes) {
            if (clazz.getName().startsWith("craftrise")) {
                if (!clazz.getName().contains("$$Lambda")) {
                    filteredClasses.add(clazz);
                }
            }
        }
        return filteredClasses;
    }

    public static String getDescriptor(Class<?> trgt, Class<?>... parameterTypes) {
        try {
            Class<?> targetClass = trgt;
            Method[] methods = targetClass.getDeclaredMethods();

            for (Method method : methods) {
                Class<?>[] methodParameterTypes = method.getParameterTypes();

                if (Arrays.equals(methodParameterTypes, parameterTypes)) {
                    return getMethodDescriptor(method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Desc bulunamadı";
    }

    public static Class<?> findSwingPacket() {
        List<Class<?>> classes = Reflector.getLoadedClasses();
        Class<?> packetClass = Mapper.PacketClass;

        List<Class<?>> matchingClasses = new ArrayList<>();

        for (Class<?> clazz : classes) {
            if (packetClass.isAssignableFrom(clazz) && clazz != packetClass) {

                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                boolean hasOnlyParameterlessConstructors = true;

                for (Constructor<?> constructor : constructors) {
                    if (constructor.getParameterCount() > 0) {
                        hasOnlyParameterlessConstructors = false;
                        break;
                    }
                }

                if (hasOnlyParameterlessConstructors) {
                    matchingClasses.add(clazz);
                }

            }
        }

        if (matchingClasses.size() < 2) {
            return null;
        } else {
            Class<?> secondClass = matchingClasses.get(1);
            return secondClass;
        }
    }

    public static String findConstructorParamSize(Class<?> cls, int param_count) {
        StringBuilder descriptor = new StringBuilder();
        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == param_count) {
                descriptor.append("(");
                for (Class<?> type : constructor.getParameterTypes()) {
                    descriptor.append(getTypeDescriptor(type));
                }
                descriptor.append(")");
                descriptor.append(getTypeDescriptor(void.class));
            }
        }
        return descriptor.toString();
    }

    public static Field findListUsingParam(Class<?> cls, Class<?> listType) {
        Field[] fields = cls.getFields();
        for (Field field : fields) {
            if (List.class.isAssignableFrom(field.getType())) {
                Type fieldType = field.getGenericType();
                if (fieldType instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) fieldType;
                    Type[] typeArguments = pType.getActualTypeArguments();
                    if (typeArguments.length == 1) {
                        if (typeArguments[0].equals(listType)) {
                            return field;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Field findField(Class<?> cls, String name, String descriptor) {
        for (Field field : cls.getDeclaredFields()) {
            if (field.getName().equals(name)) {
                if (getDescriptor(field).equals(descriptor)) {
                    return field;
                }
            }
        }
        return null;
    }

    private static String getDescriptor(Field field) {
        Class<?> type = field.getType();
        if (type.isPrimitive()) {
            if (type == int.class) return "I";
            if (type == boolean.class) return "Z";
            if (type == short.class) return "S";
            if (type == long.class) return "J";
            if (type == char.class) return "C";
            if (type == byte.class) return "B";
            if (type == float.class) return "F";
            if (type == double.class) return "D";
        } else {
            return "L" + type.getName().replace('.', '/') + ";";
        }
        return null;
    }

    private static String getMethodDescriptor(Method method) {
        StringBuilder descriptor = new StringBuilder();

        descriptor.append("(");
        for (Class<?> parameterType : method.getParameterTypes()) {
            descriptor.append(getTypeDescriptor(parameterType));
        }
        descriptor.append(")");
        descriptor.append(getTypeDescriptor(method.getReturnType()));

        return descriptor.toString();
    }

    private static String getTypeDescriptor(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == int.class) return "I";
            if (clazz == void.class) return "V";
            if (clazz == boolean.class) return "Z";
            if (clazz == byte.class) return "B";
            if (clazz == char.class) return "C";
            if (clazz == short.class) return "S";
            if (clazz == long.class) return "J";
            if (clazz == float.class) return "F";
            if (clazz == double.class) return "D";
        }
        return "L" + clazz.getName().replace('.', '/') + ";";
    }


    public static Class<?> findClassByName(String className) {
        Vector<Class<?>> loadedClasses = getLoadedClasses();
        for (Class<?> clazz : loadedClasses) {
            if (clazz.getName().equals(className)) {
                return clazz;
            }
        }
        return null;
    }


    public static Class<?> findClassUsingTypes(Object... types) {
        Map<Class<?>, Integer> typeInstance = new HashMap<>();
        for (int i = 0; i < types.length; i += 2) {
            Class<?> type = (Class<?>) types[i];
            Integer count = (Integer) types[i + 1];
            typeInstance.put(type, count);
        }

        Vector<Class<?>> loadedClasses = new Vector<>(getLoadedClasses());
        Class<?> bestMatch = null;
        int highestMatchCount = 0;

        final int TOLERANCE = 3;

        for (Class<?> clz : loadedClasses) {
            Map<Class<?>, Integer> foundCounts = new HashMap<>();
            for (Field field : clz.getDeclaredFields()) {
                Class<?> fieldType = field.getType();
                if (typeInstance.containsKey(fieldType)) {
                    foundCounts.put(fieldType, foundCounts.getOrDefault(fieldType, 0) + 1);
                }
            }

            boolean matches = true;
            int matchCount = 0;

            for (Map.Entry<Class<?>, Integer> entry : typeInstance.entrySet()) {
                int requiredCount = entry.getValue();
                int foundCount = foundCounts.getOrDefault(entry.getKey(), 0);
                if (foundCount < requiredCount || foundCount > (requiredCount + TOLERANCE)) {
                    matches = false;
                    break;
                }

                matchCount += Math.min(requiredCount, foundCount);
            }

            if (matches && matchCount > highestMatchCount) {
                highestMatchCount = matchCount;
                bestMatch = clz;
            }
        }

        return bestMatch;
    }

    public static Class<?> findClassUsingMethodTypes(Class<?> targetType, int count) {
        Vector<Class<?>> loadedClasses = new Vector<>(getLoadedClasses());

        for (Class<?> loadedClass : loadedClasses) {
            int methodCount = 0;
            for (Method method : loadedClass.getDeclaredMethods()) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length > 0 && parameterTypes[0].equals(targetType)) {
                    methodCount++;
                }
            }
            if (methodCount == count) {
                return loadedClass;
            }
        }
        return null;
    }

    public static Field getFieldByIndex(Class<?> clazz, int index) {
        Field[] fields = clazz.getDeclaredFields();
        int rindex = index - 1;
        if (rindex >= 0 && rindex < fields.length) {
            return fields[rindex];
        } else {
            return null;
        }
    }

    public static Method getMethodByIndex(Class<?> clazz, int index) {
        Method[] methods = clazz.getMethods();
        int rindex = index - 1;
        if (rindex >= 0 && rindex < methods.length) {
            return methods[rindex];
        } else {
            return null;
        }
    }

    public static Method getMethodByDeclaredIndex(Class<?> clazz, int index) {
        Method[] methods = clazz.getDeclaredMethods();
        int rindex = index - 1;
        if (rindex >= 0 && rindex < methods.length) {
            return methods[rindex];
        } else {
            return null;
        }
    }

    public static Method getMethodUsingParameters(Class<?> clazz, Class<?>... parameterTypes) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Arrays.equals(method.getParameterTypes(), parameterTypes)) {
                return method;
            }
        }
        return null;
    }


    public static Class<?> findClassUsingSuper(Class<?> superClass) {
        for (Class<?> cls : getLoadedClasses()) {
            if (cls.getSuperclass() == superClass) {
                return cls;
            }
        }
        return null;
    }

    public static Field getFieldByType(Class<?> clazz, Class<?> type) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == type) {
                return field;
            }
        }
        return null;
    }

    public static List<Field> getFieldsByType(Class<?> clazz, Class<?> type) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> matchingFields = new ArrayList<>();

        for (Field field : fields) {
            if (field.getType() == type) {
                matchingFields.add(field);
            }
        }

        return matchingFields;
    }

    public static Method getMethodUsingParameterType(Class<?> cls) {
        Method[] methods = cls.getDeclaredMethods();

        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();

            for (Class<?> paramType : parameterTypes) {
                if (paramType.isInterface() && !paramType.equals(SecretKey.class)) {
                    return method;
                }
            }
        }
        return null;
    }

    public static Class<?> findClassUsingFieldNames(String... names) {
        Vector<Class<?>> loadedClasses = getLoadedClasses();
        for (Class<?> clazz : loadedClasses) {
            Set<String> fieldNamesSet = new HashSet<>();
            for (Field field : clazz.getDeclaredFields()) {
                fieldNamesSet.add(field.getName());
            }
            boolean allMatch = true;
            for (String name : names) {
                if (!fieldNamesSet.contains(name)) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                return clazz;
            }
        }
        return null;
    }

    public static Field findPrivateIntField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isPrivate(field.getModifiers())) continue;
            if (field.getType().equals(int.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

    public static Method findSpecificMethod(Class<?> clazz, Class<?> param) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) continue;

            if (!method.getReturnType().equals(void.class)) continue;

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) continue;

            if (!params[0].equals(param)) continue;

            return method;
        }
        return null;
    }

    public static Class<?> getInnerClassByIndex(Class<?> org, int index) {
        String outerClassName = org.getName();
        Vector<Class<?>> loadedClasses = getLoadedClasses();
        List<Class<?>> innerClasses = loadedClasses.stream()
                .filter(clazz -> clazz.getName().startsWith(outerClassName + "$"))
                .collect(Collectors.toList());
        if (index >= 0 && index < innerClasses.size()) {
            return innerClasses.get(index);
        }
        return null;
    }

    public static Method getMethodByReturnType(Class<?> clazz, Class<?> returnType) {
        if (clazz == null || returnType == null) {
            return null;
        }
        for (Method method : clazz.getMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && method.getReturnType().equals(returnType)) {
                return method;
            }
        }
        return null;
    }

    public static String getOuterClassName(String className) {
        int innerClassDelimiter = className.indexOf('$');
        if (innerClassDelimiter != -1) {
            return className.substring(0, innerClassDelimiter);
        } else {
            return className;
        }
    }

    public static Method findContainerGetValue(Class<?> cls, Class<?> methodReturnType) {
        Method[] methods = cls.getDeclaredMethods();

        for (Method method : methods) {
            if (method.getReturnType().equals(methodReturnType)) {
                return method;
            }
        }

        return null;
    }
}