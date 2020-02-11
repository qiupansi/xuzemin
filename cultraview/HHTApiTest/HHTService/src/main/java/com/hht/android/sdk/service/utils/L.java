package com.hht.android.sdk.service.utils;

import android.util.Log;

/**
 * Log统一管理类
 * 
 * @author wang
 * 
 */
public class L {
	public static boolean isDebug = true;// 是否需要打印bug，
	private static final String TAG = "HHTService";

	// 下面四个是默认tag的函数

	public static void i(String msg) {
		if (isDebug)
			Log.i(TAG, msg);
	}

	public static void d(String msg) {
		if (isDebug)
			Log.d(TAG, msg);
	}

	public static void e(String msg) {
		if (isDebug)
			Log.e(TAG, msg);
	}

	public static void v(String msg) {
		if (isDebug)
			Log.v(TAG, msg);
	}

	// 下面是传入类名打印log
	public static void i(Class<?> _class, String msg) {
		if (isDebug)
			Log.i(_class.getName(), msg);
	}

	public static void d(Class<?> _class, String msg) {
		if (isDebug)
			Log.i(_class.getName(), msg);
	}

	public static void e(Class<?> _class, String msg) {
		if (isDebug)
			Log.i(_class.getName(), msg);
	}

	public static void v(Class<?> _class, String msg) {
		if (isDebug)
			Log.i(_class.getName(), msg);
	}

	// 下面是传入自定义tag的函数
	public static void i(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void i(String tag, String format, Object... args) {
		if (isDebug){
			String msg = String.format(format,args);
			Log.i(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (isDebug)
			Log.d(tag, msg);
	}

	public static void d(String tag, String format, Object... args) {
		if (isDebug){
			String msg = String.format(format,args);
			Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (isDebug)
			Log.e(tag, msg);
	}

	public static void e(String tag, String format, Object... args) {
		if (isDebug){
			String msg = String.format(format,args);
			Log.e(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (isDebug)
			Log.v(tag, msg);
	}

	public static void v(String tag, String format, Object... args) {
		if (isDebug){
			String msg = String.format(format,args);
			Log.v(tag, msg);
		}
	}
}
