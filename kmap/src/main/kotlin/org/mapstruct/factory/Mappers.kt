/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.factory

import java.lang.reflect.InvocationTargetException
import java.util.ServiceLoader

/**
 * Factory for obtaining mapper instances if no explicit component model such as CDI is configured via
 * [Mapper.componentModel].
 *
 *
 * Mapper implementation types are expected to have the same fully qualified name as their interface type, with the
 * suffix `Impl` appended. When using this factory, mapper types - and any mappers they use - are instantiated by
 * invoking their public no-args constructor.
 *
 *
 * By convention, a single instance of each mapper is retrieved from the factory and exposed on the mapper interface
 * type by declaring a member named `INSTANCE` like this:
 *
 * <pre>
 * &#064;Mapper
 * public interface CustomerMapper {
 *
 * CustomerMapper INSTANCE = Mappers.getMapper( CustomerMapper.class );
 *
 * // mapping methods...
 * }
</pre> *
 *
 * @author Gunnar Morling
 */
object Mappers {
    private const val IMPLEMENTATION_SUFFIX = "Impl"

    /**
     * Returns an instance of the given mapper type.
     *
     * @param clazz The type of the mapper to return.
     * @param <T> The type of the mapper to create.
     *
     * @return An instance of the given mapper type.
    </T> */
    fun <T> getMapper(clazz: Class<T>): T {
        try {
            val classLoaders = collectClassLoaders(clazz.getClassLoader())

            return getMapper<T>(clazz, classLoaders)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        }
    }

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class)
    private fun <T> getMapper(mapperType: Class<T>, classLoaders: Iterable<ClassLoader>): T {
        for (classLoader in classLoaders) {
            val mapper = doGetMapper<T>(mapperType, classLoader)
            if (mapper != null) {
                return mapper
            }
        }

        throw ClassNotFoundException("Cannot find implementation for " + mapperType.getName())
    }

    @Throws(NoSuchMethodException::class)
    private fun <T> doGetMapper(clazz: Class<T>, classLoader: ClassLoader): T? {
        try {
            @Suppress("UNCHECKED_CAST") val implementation = classLoader.loadClass(clazz.getName() + IMPLEMENTATION_SUFFIX) as Class<T>
            val constructor = implementation.getDeclaredConstructor()
            constructor.setAccessible(true)

            return constructor.newInstance()
        } catch (e: ClassNotFoundException) {
            return getMapperFromServiceLoader(clazz, classLoader)
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Returns the class of the implementation for the given mapper type.
     *
     * @param clazz The type of the mapper to return.
     * @param <T> The type of the mapper to create.
     *
     * @return A class of the implementation for the given mapper type.
     *
     * @since 1.3
    </T> */
    fun <T> getMapperClass(clazz: Class<T>): Class<out T> {
        try {
            val classLoaders = collectClassLoaders(clazz.getClassLoader())

            return getMapperClass<T>(clazz, classLoaders)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
    }

    @Throws(ClassNotFoundException::class)
    private fun <T> getMapperClass(mapperType: Class<T>, classLoaders: Iterable<ClassLoader>): Class<out T> {
        for (classLoader in classLoaders) {
            val mapperClass = doGetMapperClass<T>(mapperType, classLoader)
            if (mapperClass != null) {
                return mapperClass
            }
        }

        throw ClassNotFoundException("Cannot find implementation for " + mapperType.getName())
    }

    private fun <T> doGetMapperClass(clazz: Class<T>, classLoader: ClassLoader): Class<out T>? {
        try {
            @Suppress("UNCHECKED_CAST") return classLoader.loadClass(clazz.getName() + IMPLEMENTATION_SUFFIX) as Class<out T>?
        } catch (e: ClassNotFoundException) {
            val mapper = getMapperFromServiceLoader<T>(clazz, classLoader)
            if (mapper != null) {
                return mapper.javaClass as Class<out T>
            }

            return null
        }
    }

    private fun <T> getMapperFromServiceLoader(clazz: Class<T>, classLoader: ClassLoader?): T? {
        val loader = ServiceLoader.load<T>(clazz, classLoader)

        for (mapper in loader) {
            if (mapper != null) {
                return mapper
            }
        }

        return null
    }

    private fun collectClassLoaders(classLoader: ClassLoader?): MutableList<ClassLoader> {
        val classLoaders: MutableList<ClassLoader> = ArrayList<ClassLoader>(3)
        classLoaders.add(classLoader!!)

        if (Thread.currentThread().getContextClassLoader() != null) {
            classLoaders.add(Thread.currentThread().getContextClassLoader())
        }

        classLoaders.add(Mappers::class.java.getClassLoader())

        return classLoaders
    }
}
