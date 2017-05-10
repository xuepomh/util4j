package net.jueb.util4j.hotSwap;

import java.net.URL;
import java.net.URLClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义类加载器,默认使用当前线程的类加载器作为父加载器
 * 此类加载器可以加载jar,目录下的class文件,http以及ftp等class资源
 * @author juebanlin
 */
public class CustomClassLoader extends URLClassLoader {
	protected Logger log = LoggerFactory.getLogger(getClass());

	public CustomClassLoader() {
		this(new URL[] {}, Thread.currentThread().getContextClassLoader());
	}

	public CustomClassLoader(URL url) {
		this(new URL[] { url }, Thread.currentThread().getContextClassLoader());
	}

	public CustomClassLoader(ClassLoader parent) {
		this(new URL[] {}, parent);
	}

	public CustomClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	/**
	 * 加载类,如果是系统类则交给系统加载 如果当前类已经加载则返回类 如果当前类没有加载则定义并返回
	 */
	@Override
	protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
		Class<?> clazz = null;
		// 查找当前类加载中已加载的
		if (clazz == null) {
			clazz = findLoadedClass(className);
		}
		// 当前jar中加载
		if (clazz == null) {
			// 查找当前类加载器urls或者当前类加载器所属线程类加载器
			try {
				clazz = findClass(className);
			} catch (Exception e) {
			}
		}
		if (clazz == null) {// 系统类加载
			try {
				clazz = findSystemClass(className);
			} catch (Exception e) {

			}
		}
		String ClassLoader = null;
		if (clazz != null) {// 解析类结构
			ClassLoader = "" + clazz.getClassLoader();
			if (resolve) {
				resolveClass(clazz);
			}
		}
		log.debug("loadClass " + (clazz == null) + ":" + className + ",resolve=" + resolve + ",Clazz=" + clazz
				+ ",ClassLoader=" + ClassLoader);
		return clazz;
	}

	/**
	 * 查找类,这个方法一般多用于依赖类的查找,如果之前已经加载过,则重复加载会报错,所以需要添加findLoadedClass判断
	 */
	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		log.trace("findClass:" + name);
		Class<?> clazz = findLoadedClass(name);
		if (clazz != null) {
			return clazz;
		}
		return super.findClass(name);
	}

	@Override
	protected void addURL(URL url) {
		super.addURL(url);
	}
}