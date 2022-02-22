package io.oreto.latte.obj;

import io.oreto.latte.collections.Lists;
import io.oreto.latte.str.Noun;
import io.oreto.latte.str.Str;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Reflection Utilities
 */
public class Reflect {
    /**
     * Defines which field types are allowed for certain reflective operations
     */
    public static class Allow {
        /**
         * Allow no extra field types
         * @return The allow options object
         */
        public static Allow none() {
            return new Allow();
        }

        /**
         * Allow all extra field types
         * @return The allow options object
         */
        public static Allow all() {
            return none()
                    .allowTransient()
                    .allowStatic()
                    .allowFinal()
                    .allowJsonIgnore()
                    .allowUnderscore();
        }

        boolean trans;
        boolean _static;
        boolean _final = true;
        boolean underscore;
        boolean jsonIgnore = true;

        /**
         * Allow transient fields
         * @return The allow options object
         */
        public Allow allowTransient() {
            trans = true;
            return this;
        }

        /**
         * Allow static fields
         * @return The allow options object
         */
        public Allow allowStatic() {
            _static = true;
            return this;
        }

        /**
         * Allow final fields
         * @return The allow options object
         */
        public Allow allowFinal() {
            _final = true;
            return this;
        }

        /**
         * Allow fields annotated with JsonIgnore
         * @return The allow options object
         */
        public Allow allowJsonIgnore() {
            jsonIgnore = true;
            return this;
        }

        /**
         * Allow fields beginning with underscore
         * @return The allow options object
         */
        public Allow allowUnderscore() {
            underscore = true;
            return this;
        }

        /**
         * Ignore transient fields
         * @return The allow options object
         */
        public Allow ignoreTransient() {
            trans = false;
            return this;
        }

        /**
         * Ignore static fields
         * @return The allow options object
         */
        public Allow ignoreStatic() {
            _static = false;
            return this;
        }

        /**
         * Ignore final fields
         * @return The allow options object
         */
        public Allow ignoreFinal() {
            _final = false;
            return this;
        }

        /**
         * Ignore fields annotated with JsonIgnore
         * @return The allow options object
         */
        public Allow ignoreJsonIgnore() {
            jsonIgnore = false;
            return this;
        }

        /**
         * Ignore fields beginning with underscore
         * @return The allow options object
         */
        public Allow ignoreUnderscore() {
            underscore = false;
            return this;
        }
    }

    @SuppressWarnings("unchecked")
    private static void parameterNames(Map<String, Object> paramMap, String path, List<String> names) {
        for (String key : paramMap.keySet()) {
            Object val = paramMap.get(key);
            if (val instanceof List) {
                List<?> items = (List<?>) val;
                if (items.size() > 0 && items.get(0) instanceof Map) {
                    parameterNames((Map<String, Object>) items.get(0)
                            , Str.isEmpty(path) ? key : String.format("%s.%s", path, key)
                            , names);
                } else {
                    names.add(Str.isEmpty(path) ? key : String.format("%s.%s", path, key));
                }
            } else if (val instanceof Map) {
                parameterNames((Map<String, Object>) val
                        , Str.isEmpty(path) ? key : String.format("%s.%s", path, key)
                        , names);
            } else {
                names.add(Str.isEmpty(path) ? key : String.format("%s.%s", path, key));
            }
        }
    }

    /**
     * Get all unique parameter names from a parameter map
     * @param paramMap The parameter map with keys as names
     * @return A unique list of string names
     */
    public static List<String> parameterNames(Map<String, Object> paramMap) {
        List<String> names = new ArrayList<>();
        parameterNames(paramMap, "", names);
        return names.stream().distinct().collect(Collectors.toList());
    }

    private static List<Field> getAllFields(Class<?> aClass, List<Field> fields, Allow allow) {
        fields.addAll(
                Arrays.stream(aClass.getDeclaredFields())
                        .filter(it -> allow.trans || !Modifier.isTransient(it.getModifiers()))
                        .filter(it -> allow._static || !Modifier.isStatic(it.getModifiers()))
                        .filter(it -> allow._final || !Modifier.isFinal(it.getModifiers()))
                        .filter(it -> allow.underscore || !it.getName().startsWith("_"))
                        .collect(Collectors.toList())
        );

        if (aClass.getSuperclass() != null) {
            getAllFields(aClass.getSuperclass(), fields, allow);
        }

        return fields;
    }

    private static List<Field> getAllFields(Class<?> aClass, List<Field> fields) {
        return getAllFields(aClass, fields, Allow.none().allowStatic());
    }

    /**
     * Get all the fields of a class
     * @param aClass The class to get fields for
     * @param allow Which type of fields to allow
     * @return The list of fields
     */
    public static List<Field> getAllFields(Class<?> aClass, Allow allow) {
        return getAllFields(aClass, new ArrayList<>(), allow);
    }

    /**
     * Get all the fields of a class
     * @param aClass The class to get fields for
     * @return The list of fields
     */
    public static List<Field> getAllFields(Class<?> aClass) {
        return getAllFields(aClass, new ArrayList<>());
    }

    /**
     * Get all the fields of a class
     * @param o The object to get fields for
     * @param allow Which type of fields to allow
     * @return The list of fields
     */
    public static List<Field> getAllFields(Object o, Allow allow) {
        return getAllFields(o.getClass(), new ArrayList<>(), allow);
    }

    /**
     * Get all the fields of a class
     * @param o The object to get fields for
     * @return The list of fields
     */
    public static List<Field> getAllFields(Object o) {
        return getAllFields(o.getClass(), new ArrayList<>());
    }

    /**
     * Get field by name from a class
     * @param aClass The class to get the field from
     * @param field The field name
     * @return Optional field if the field exists, Optional.empty otherwise
     */
    public static Optional<Field> getField(Class<?> aClass, String field) {
        return getAllFields(aClass).stream().filter(it -> it.getName().equals(field)).findFirst();
    }

    /**
     * Get field by name from a class
     * @param o The object to get the field from
     * @param field The field name
     * @return Optional field if the field exists, Optional.empty otherwise
     */
    public static Optional<Field> getField(Object o, String field) {
        return o == null ? Optional.empty() : getField(o.getClass(), field);
    }

    /**
     * Determine if the specified field is public
     * @param o The object to investigate
     * @param name The name of the field
     * @return True if the field is public, false otherwise
     */
    public static boolean isFieldPublic(Object o, String name) {
        Optional<Field> field = getField(o, name);
        return field.isPresent() && isFieldPublic(field.get());
    }

    /**
     * Determine if the specified field is public
     * @param field The field to investigate
     * @return True if the field is public, false otherwise
     */
    public static boolean isFieldPublic(Field field) {
        return Modifier.isPublic(field.getModifiers());
    }

    /**
     * Get the value of an objects field
     * @param o The object to with the field
     * @param field The field of interest
     * @return The field value of object o
     * @throws ReflectiveOperationException If there is an error getting the field value
     */
    public static Object getFieldValue(Object o, Field field)
            throws ReflectiveOperationException {
        Object value;
        if (isFieldPublic(field)) {
            value = field.get(o);
        } else {
            Optional<Method> getter = getGetter(field, o);
            value = getter.orElseThrow(() ->
                    new NoSuchMethodException(String.format("No getters found for %s", field.getName())))
                    .invoke(o);
        }
        return value;
    }

    static boolean methodTypeMatches(Class<?> method, Class<?> cls){
        return method == cls || method.isAssignableFrom(cls)
                || (method.isPrimitive() &&
                cls.getSimpleName().toLowerCase().startsWith(method.getSimpleName()));
    }

    /**
     * Find the getter for the specified class field
     * @param field The field of interest
     * @param cls The class to search
     * @return Optional getter method if the method exists, Optional.empty otherwise
     */
    public static Optional<Method> getGetter(Field field, Class<?> cls) {
        for (Method method : cls.getMethods()) {
            if (method.getParameterCount() == 0 && methodTypeMatches(method.getReturnType(), field.getType())) {
                String fieldName = Str.capitalize(field.getName());
                if (method.getName().equals(String.format("get%s", fieldName))
                        || method.getName().equals(String.format("is%s", fieldName))
                        || method.getName().equals(field.getName()))
                    return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    /**
     * Find the getter for the specified class field
     * @param field The field of interest
     * @param o The object to search
     * @return Optional getter method if the method exists, Optional.empty otherwise
     */
    public static Optional<Method> getGetter(Field field, Object o) {
        return getGetter(field, o.getClass());
    }

    /**
     * Find the getter for the specified class field
     * @param name The field name of interest
     * @param cls The class to search
     * @return Optional getter method if the method exists, Optional.empty otherwise
     */
    public static Optional<Method> getGetter(String name, Class<?> cls) {
        return getField(cls, name).flatMap(value -> getGetter(value, cls));
    }

    /**
     * Find the getter for the specified class field
     * @param name The field name of interest
     * @param o The object to search
     * @return Optional getter method if the method exists, Optional.empty otherwise
     */
    public static Optional<Method> getGetter(String name, Object o) {
        return getField(o, name).flatMap(value -> getGetter(value, o));
    }

    /**
     * Get the value of an objects field
     * @param o The object to with the field
     * @param field The field name of interest
     * @return The field value of object o
     * @throws ReflectiveOperationException If there is an error getting the field value
     */
    public static Object getFieldValue(Object o, String field)
            throws ReflectiveOperationException {
        return getFieldValue(o, getField(o, field).orElseThrow(() -> new NoSuchFieldException("no such field " + field)));
    }

    /**
     * Get the specified attribute name from the annotation
     * @param annotation The annotation of interest
     * @param attribute The name of the attribute
     * @return The attribute value
     * @throws ReflectiveOperationException If the attribute doesn't exist
     */
    public static Object getAttributeValue(Annotation annotation, String attribute)
            throws ReflectiveOperationException {
        Method method = Arrays.stream(annotation.annotationType().getMethods())
                .filter(it -> it.getName().equals(attribute)).findFirst().orElseThrow(() ->
                        new NoSuchElementException("no such attribute " + attribute));
        return  method.invoke(annotation);
    }

    /**
     * Find the setter for the specified class field
     * @param field The field of interest
     * @param cls The class to search
     * @return Optional setter method if the method exists, Optional.empty otherwise
     */
    public static Optional<Method> getSetter(Field field, Class<?> cls) {
        for (Method method : cls.getMethods()) {
            Class<?> param = method.getParameterCount() == 1 ? method.getParameterTypes()[0] : null;

            if (Objects.nonNull(param)) {
                String capitalize = Str.capitalize(field.getName());
                String setter = String.format("set%s", capitalize);
                String with = String.format("with%s", capitalize);
                if (method.getName().equals(field.getName())
                        || method.getName().equals(setter)
                        || method.getName().equals(with)) {
                    if (methodTypeMatches(param, field.getType())) {
                        return Optional.of(method);
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Find the setter for the specified class field
     * @param field The field of interest
     * @param o The object to search
     * @return Optional setter method if the method exists, Optional.empty otherwise
     */
    public static Optional<Method> getSetter(Field field, Object o) {
        return getSetter(field, o.getClass());
    }

    /**
     * Find the setter for the specified class field
     * @param name The field name of interest
     * @param o The object to search
     * @return Optional setter method if the method exists, Optional.empty otherwise
     */
    public static Optional<Method> getSetter(String name, Object o) {
        return getField(o, name).flatMap(value -> getSetter(value, o));
    }

    /**
     * Find the setter for the specified class field
     * @param name The field name of interest
     * @param cls  The class to search
     * @return Optional setter method if the method exists, Optional.empty otherwise
     */
    public static Optional<Method> getSetter(String name, Class<?> cls) {
        return getField(cls, name).flatMap(value -> getSetter(value, cls));
    }

    private static Optional<Method> getAddRemoveMethod(String name, Field field, Class<?> cls) {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Type[] genericTypes = parameterizedType.getActualTypeArguments();

        for (Method method : cls.getMethods()) {
            Class<?> param = method.getParameterCount() > 0 ? method.getParameterTypes()[0] : null;
            if (Objects.nonNull(param)) {
                String methodName = Str.of(Noun.singular(field.getName())).capitalize().preface(name).toString();
                if (method.getName().equals(methodName)) {
                    if (methodTypeMatches(param.isArray() && method.isVarArgs()
                            ? param.getComponentType() : param, (Class<?>) genericTypes[0])) {
                        return Optional.of(method);
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get the adder method for the specified class field
     * addFoo(String foo)
     * @param field The field of interest
     * @param cls The class to search
     * @return An optional adder method if one exists, Optional.empty otherwise
     */
    public static Optional<Method> getAdder(Field field, Class<?> cls) {
        return getAddRemoveMethod("add", field, cls);
    }

    /**
     * Get the adder method for the specified class field
     * addFoo(String foo)
     * @param field The field of interest
     * @param o The object to search
     * @return An optional adder method if one exists, Optional.empty otherwise
     */
    public static Optional<Method> getAdder(Field field, Object o) {
        return getAdder(field, o.getClass());
    }

    /**
     * Get the adder method for the specified class field
     * addFoo(String foo)
     * @param name The field name of interest
     * @param o The object to search
     * @return An optional adder method if one exists, Optional.empty otherwise
     */
    public static Optional<Method> getAdder(String name, Object o) {
        return getField(o, name).flatMap(value -> getAdder(value, o));
    }

    /**
     * Get the adder method for the specified class field
     * addFoo(String foo)
     * @param name The field name of interest
     * @param cls The class to search
     * @return An optional adder method if one exists, Optional.empty otherwise
     */
    public static Optional<Method> getAdder(String name, Class<?> cls) {
        return getField(cls, name).flatMap(value -> getAdder(value, cls));
    }

    private static Object newArray(Object value, Class<?> type) {
        Object varargs = Array.newInstance(type, 1);
        Array.set(varargs, 0, value);
        return varargs;
    }

    /**
     * Use the adder method to add a value to the specified object field
     * @param o The object to add to
     * @param field The field to add to
     * @param value The value to add
     * @throws ReflectiveOperationException If no adder method exists
     */
    public static void addFieldValue(Object o, Field field, Object value) throws ReflectiveOperationException {
        Optional<Method> method = getAdder(field, o);
        if (method.isPresent()) {
            if (method.get().isVarArgs()) {
                method.get().invoke(o, newArray(value, method.get().getParameterTypes()[0].getComponentType()));
            } else {
                method.get().invoke(o, value);
            }
            return;
        }
        throw new NoSuchMethodException(String.format("No add method found for %s", field.getName()));
    }

    /**
     * Use the adder method to add a value to the specified object field
     * @param o The object to add to
     * @param field The field name to add to
     * @param value The value to add
     * @throws ReflectiveOperationException If no adder method exists
     */
    public static void addFieldValue(Object o, String field, Object value)
            throws ReflectiveOperationException {
        addFieldValue(o, getField(o, field).orElseThrow(() -> new NoSuchFieldException("no such field " + field)), value);
    }

    /**
     * Get the remover method for the specified class field
     * addFoo(String foo)
     * @param field The field of interest
     * @param cls The class to search
     * @return An optional remover method if one exists, Optional.empty otherwise
     */
    public static Optional<Method> getRemover(Field field, Class<?> cls) {
        return getAddRemoveMethod("remove", field, cls);
    }

    /**
     * Get the remover method for the specified class field
     * addFoo(String foo)
     * @param field The field of interest
     * @param o The object to search
     * @return An optional remover method if one exists, Optional.empty otherwise
     */
    public static Optional<Method> getRemover(Field field, Object o) {
        return getRemover(field, o.getClass());
    }

    /**
     * Get the remover method for the specified class field
     * addFoo(String foo)
     * @param name The field name of interest
     * @param o The object to search
     * @return An optional remover method if one exists, Optional.empty otherwise
     */
    public static Optional<Method> getRemover(String name, Object o) {
        return getField(o, name).flatMap(value -> getRemover(value, o));
    }

    /**
     * Get the remover method for the specified class field
     * addFoo(String foo)
     * @param name The field name of interest
     * @param cls The class to search
     * @return An optional remover method if one exists, Optional.empty otherwise
     */
    public static Optional<Method> getRemover(String name, Class<?> cls) {
        return getField(cls, name).flatMap(value -> getRemover(value, cls));
    }

    /**
     * Use the remover method to add a value to the specified object field
     * @param o The object to remove from
     * @param field The field to remove
     * @param value The value to remove
     * @throws ReflectiveOperationException If no remover method exists
     */
    public static void removeFieldValue(Object o, Field field, Object value) throws ReflectiveOperationException {
        Optional<Method> method = getRemover(field, o);
        if (method.isPresent()) {
            if (method.get().isVarArgs()) {
                method.get().invoke(o, newArray(value, method.get().getParameterTypes()[0].getComponentType()));
            } else {
                method.get().invoke(o, value);
            }
            return;
        }
        throw new NoSuchMethodException(String.format("No remove method found for %s", field.getName()));
    }

    /**
     * Use the remover method to add a value to the specified object field
     * @param o The object to remove from
     * @param field The field to remove
     * @param value The value to remove
     * @throws ReflectiveOperationException If no remover method exists
     */
    public static void removeFieldValue(Object o, String field, Object value)
            throws ReflectiveOperationException {
        removeFieldValue(o, getField(o, field).orElseThrow(() -> new NoSuchFieldException("no such field " + field)), value);
    }

    /**
     * Set the field value on the specified object
     * @param o The object of interest
     * @param field The field being set
     * @param value The field value
     * @throws ReflectiveOperationException If there is an error setting the field value
     */
    public static void setFieldValue(Object o, Field field, Object value)
            throws ReflectiveOperationException {

        if (isFieldPublic(field))
            field.set(o, value);

        Optional<Method> method = getSetter(field, o);
        if (method.isPresent()) {
            method.get().invoke(o, value);
            return;
        }
        throw new NoSuchMethodException(String.format("No setters found for %s", field.getName()));
    }

    /**
     * Set the field value on the specified object
     * @param o The object of interest
     * @param field The field name being set
     * @param value The field value
     * @throws ReflectiveOperationException If there is an error setting the field value
     */
    public static void setFieldValue(Object o, String field, Object value)
            throws ReflectiveOperationException {
        setFieldValue(o, getField(o, field).orElseThrow(() -> new NoSuchFieldException("no such field " + field)), value);
    }

    /**
     * Copy the field names from o1 to o2
     * @param o1 The object to copy to
     * @param o2 The object to copy from
     * @param names The field names to copy
     * @param copyOptions Options to use when updating fields
     * @throws ReflectiveOperationException If there is an error setting a field value
     */
    public static void copy(Object o1, Object o2, Iterable<String> names, CopyOptions copyOptions)
            throws ReflectiveOperationException {
        copy(o1, o2, names, copyOptions, new WeakHashMap<>());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void copy(Object o1
            , Object o2
            , Iterable<String> names
            , CopyOptions copyOptions
            , Map<Object, Object> visited)
            throws ReflectiveOperationException {
        if (o1 == null || o2 == null || visited.containsKey(o2))
            return;
        else
            visited.put(o2, o2);

        // convert names into all possible path segments:
        // [a.b.c, a.b.d] => [a, a.b, a.b.c, a.b.d]
        Set<String> nameSet = names == null
                ? new HashSet<>()
                : StreamSupport.stream(names.spliterator(), false)
                .map(it -> Lists.subLists(Arrays.asList(it.split("\\."))))
                .flatMap(it -> it.stream().map(sublist -> String.join(".", sublist)))
                .collect(Collectors.toSet());

        Iterable<Field> iterable = Objects.nonNull(names) && names.iterator().hasNext() ? getAllFields(o1, copyOptions.allow).stream()
                .filter(it -> isFieldPublic(it) || (getSetter(it, o1).isPresent() && getGetter(it, o1).isPresent()))
                    .filter(it -> copyOptions.exclusion != nameSet.contains(it.getName()))
                    .collect(Collectors.toSet())
                : getAllFields(o1).stream()
                    .filter(it -> isFieldPublic(it) || (getSetter(it, o1).isPresent() && getGetter(it, o1).isPresent()))
                    .collect(Collectors.toSet());

        for (Field field : iterable) {
            Object v1 = getFieldValue(o1, field);
            Object v2 = getFieldValue(o2, field);
            if (!copyOptions.nullsOnly || Obj.notInitialized(v1)) {
                if (copyOptions.mergeCollections && Collection.class.isAssignableFrom(field.getType())) {
                    Collection l1 = (Collection) v1;
                    Collection l2 = (Collection) v2;
                    if (Objects.nonNull(l1) && Objects.nonNull(l2)) {
                        boolean hasAdder = Reflect.getAdder(field, o1).isPresent();
                        boolean hasRemover = Reflect.getRemover(field, o1).isPresent();
                        for(Object it : l2) {
                            if(l1.contains(it) && !isPrimitive(field.getType())) {
                                String childPath = String.format("%s.", field.getName());
                                Iterable<String> children = nameSet.stream()
                                        .filter(name -> name.startsWith(childPath))
                                        .map(name -> name.replaceFirst(childPath, ""))
                                        .collect(Collectors.toSet());
                                Reflect.copy(l1.stream().filter(o -> o.equals(it)).findFirst().orElse(null)
                                        , it
                                        , children.iterator().hasNext() ? children : null
                                        , copyOptions
                                        , visited);
                            } else {
                                if(hasAdder)
                                    Reflect.addFieldValue(o1, field, it);
                                else
                                    l1.add(it);
                            }
                        }
                        if (copyOptions.updateCollections) {
                            Object[] removals = l1.stream().filter(it -> !l2.contains(it)).toArray();
                            for (Object removal : removals) {
                                if(hasRemover) Reflect.removeFieldValue(o1, field, removal);
                                else l1.remove(removal);
                            }
                        }
                    }
                } else if (copyOptions.mergeCollections && Map.class.isAssignableFrom(field.getType())) {
                    Map m1 = (Map) v1;
                    Map m2 = (Map) v2;
                    if (Objects.nonNull(m1) && Objects.nonNull(m2))
                        m1.putAll(m2);
                    if (copyOptions.updateCollections) {
                        Object[] keys = m1.keySet().stream().filter(it -> !m2.containsKey(it)).toArray();
                        for (Object key : keys)
                            m1.remove(key);
                    }
                } else {
                    if (isPrimitive(field.getType())) {
                        if (!Objects.equals(v1, v2)) {
                            setFieldValue(o1, field, v2);
                        }
                    } else {
                        String childPath = String.format("%s.", field.getName());
                        Iterable<String> children = nameSet.stream()
                                .filter(name -> name.startsWith(childPath))
                                .map(name -> name.replaceFirst(childPath, ""))
                                .collect(Collectors.toSet());
                        Reflect.copy(v1
                                , v2
                                , children.iterator().hasNext() ? children : null
                                , copyOptions
                                , visited);
                    }
                }
            }
        }
    }

    /**
     * Determine if the class type is a primitive type wrapper
     * String, Date, and Temporal are also considered primitive in this context
     * @param type The class type
     * @return True if the class is primitive, false otherwise
     */
    public static boolean isPrimitive(Class<?> type) {
        return type.isPrimitive()
                || CharSequence.class.isAssignableFrom(type)
                || Date.class.isAssignableFrom(type)
                || Temporal.class.isAssignableFrom(type);
    }

    /**
     * Copy the field names from o1 to o2
     * @param o1 The object to copy to
     * @param o2 The object to copy from
     * @param names The field names to copy
     * @throws ReflectiveOperationException If there is an error setting a field value
     */
    public static void copy(Object o1, Object o2, Iterable<String> names)
            throws ReflectiveOperationException {
        copy(o1, o2, names, CopyOptions.create());
    }

    /**
     * Copy the field names from o1 to o2
     * @param o1 The object to copy to
     * @param o2 The object to copy from
     * @param copyOptions Options to use when updating fields
     * @throws ReflectiveOperationException If there is an error setting a field value
     */
    public static void copy(Object o1, Object o2, CopyOptions copyOptions)
            throws ReflectiveOperationException {
        copy(o1, o2, Lists.EMPTY_STRING_LIST, copyOptions);
    }

    /**
     * Copy the field names from o1 to o2
     * @param o1 The object to copy to
     * @param o2 The object to copy from
     * @throws ReflectiveOperationException If there is an error setting a field value
     */
    public static void copy(Object o1, Object o2)
            throws ReflectiveOperationException {
        copy(o1, o2, Lists.EMPTY_STRING_LIST, CopyOptions.create());
    }

    /**
     * Copy the field names from o1 to o2
     * @param o1 The object to copy to
     * @param o2 The object to copy from
     * @param names The field names to copy
     * @throws ReflectiveOperationException If there is an error setting a field value
     */
    public static void copy(Object o1, Object o2, String... names)
            throws ReflectiveOperationException {
        copy(o1, o2, Arrays.asList(names), CopyOptions.create());
    }

    /**
     * Copy the field names from o1 to o2
     * @param o1 The object to copy to
     * @param values A map of field name values to copy from
     * @param copyOptions Options to use when updating fields
     * @throws ReflectiveOperationException If there is an error setting a field value
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void copy(Object o1, Map<String, Object> values, CopyOptions copyOptions)
            throws ReflectiveOperationException {

        Iterable<Field> iterable = getAllFields(o1, copyOptions.allow).stream()
                .filter(it -> isFieldPublic(it) || (getSetter(it, o1).isPresent() && getGetter(it, o1).isPresent()))
                .filter(it -> copyOptions.exclusion != values.containsKey(it.getName()))
                .collect(Collectors.toSet());

        for (Field field : iterable) {
            Object v1 = getFieldValue(o1, field);
            Object v2 = values.get(field.getName());
            if (!copyOptions.nullsOnly || Obj.notInitialized(v1)) {
                if (copyOptions.mergeCollections && Collection.class.isAssignableFrom(field.getType())) {
                    boolean hasAdder = Reflect.getAdder(field, o1).isPresent();
                    boolean hasRemover = Reflect.getRemover(field, o1).isPresent();
                    Collection l1 = (Collection) v1;
                    Collection l2 = (Collection) v2;
                    for(Object it : l2) {
                        if(!l1.contains(it)) {
                            if(hasAdder)
                                Reflect.addFieldValue(o1, field, it);
                            else
                                l1.add(it);
                        }
                    }
                    if (copyOptions.updateCollections) {
                        Object[] removals = l1.stream().filter(it -> !l2.contains(it)).toArray();
                        for (Object removal : removals) {
                            if(hasRemover) Reflect.removeFieldValue(o1, field, removal);
                            else l1.remove(removal);
                        }
                    }
                } else if (copyOptions.mergeCollections && Map.class.isAssignableFrom(field.getType())) {
                    Map m1 = (Map) v1;
                    Map m2 = (Map) v2;
                    m1.putAll(m2);
                    if (copyOptions.updateCollections) {
                        Object[] keys = m1.keySet().stream().filter(it -> !m2.containsKey(it)).toArray();
                        for (Object key : keys)
                            m1.remove(key);
                    }
                } else {
                    if (!Objects.equals(v1, v2))
                        setFieldValue(o1, field, v2);
                }
            }
        }
    }

    /**
     * Copy the field names from o1 to o2
     * @param o1 The object to copy to
     * @param values A map of field name values to copy from
     * @throws ReflectiveOperationException If there is an error setting a field value
     */
    public static void copy(Object o1, Map<String, Object> values)
            throws ReflectiveOperationException {
        copy(o1, values, CopyOptions.create());
    }

    /**
     * Options to define how to handle updating fields during a copy operation
     */
    public static class CopyOptions {
        /**
         * Create a new default CopyOptions
         * @return A new CopyOptions object
         */
        public static CopyOptions create() {
            return new CopyOptions();
        }

        private boolean nullsOnly;
        private boolean mergeCollections;
        private boolean updateCollections;
        private boolean exclusion;
        private Allow allow;

        protected CopyOptions(){
            allow = Allow.none().allowStatic();
        }

        /**
         * Update only fields which are set to null
         * @return The CopyOptions object
         */
        public CopyOptions nullsOnly() {
            nullsOnly = true;
            return this;
        }

        /**
         * When copying collections merge them instead of overwriting
         * @return The CopyOptions object
         */
        public CopyOptions mergeCollections() {
            mergeCollections = true;
            return this;
        }

        /**
         * When copying collections modify the collection to reflect the new collection state
         * @return The CopyOptions object
         */
        public CopyOptions updateCollections() {
            updateCollections = true;
            return mergeCollections();
        }

        /**
         * Exclude the specified field names instead of including them
         * @return The CopyOptions object
         */
        public CopyOptions exclusion() {
            exclusion = true;
            return this;
        }

        /**
         * Define which types of fields to allow in the copy operation
         * @param allow The allow options object
         * @return The CopyOptions object
         */
        public CopyOptions allowing(Allow allow) {
            this.allow = allow;
            return this;
        }
    }
}
