package com.example.bozhilun.android.siswatch.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;
import com.lidroid.xutils.util.LogUtils;



/**
 * SharedPreferences 工具类<br>
 * 内部已经封装了打印功能,只需要把DEBUG参数改为true即可<br>
 * 如果需要更换tag可以直接更改,默认为KEZHUANG
 * 
 * @author KEZHUANG
 *
 */
public class SharedPreferenceUtil {
	
	/**
	 * 保存在手机里面的文件名
	 */
	public static final String FILE_NAME = "share_data";

	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 *  @param context
	 * @param key
	 *            键值对的key
	 * @param object
	 */
	public static String put(Context context, String key, Object object) {

		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		} else if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float) {
			editor.putFloat(key, (Float) object);
		} else if (object instanceof Long) {
			editor.putLong(key, (Long) object);
		} else {
			editor.putString(key, object.toString());
		}
		LogUtils.i("SP存储      key=" + key + "-----value=" + object);
		SharedPreferencesCompat.apply(editor);
		return key;
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 * 
	 * @param context
	 * @param key 
	 * @param defaultObject 不能为NULL,取什么类型的数据就应该是什么类型的变量
	 * @return
	 */
	public static Object get(Context context, String key, Object defaultObject) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		if (defaultObject instanceof String) {
			String stringResult = sp.getString(key, (String) defaultObject);
			LogUtils.i("SP取出      key=" + key + "-----value=" + stringResult);
			return stringResult;
		} else if (defaultObject instanceof Integer) {
			Integer integerResult = sp.getInt(key, (Integer) defaultObject);
			LogUtils.i("SP取出      key=" + key + "-----value="+ integerResult);
			return integerResult;
		} else if (defaultObject instanceof Boolean) {
			Boolean booleanResult = sp.getBoolean(key, (Boolean) defaultObject);
			LogUtils.i("SP取出      key=" + key + "-----value="+ booleanResult);
			return booleanResult;
		} else if (defaultObject instanceof Float) {
			Float floatResult = sp.getFloat(key, (Float) defaultObject);
			LogUtils.i("SP取出     key=" + key + "-----value="	+ floatResult);
			return floatResult;
		} else if (defaultObject instanceof Long) {
			Long longResult = sp.getLong(key, (Long) defaultObject);
			LogUtils.i("SP取出      key=" + key + "-----value="+ longResult);
			return longResult;
		}

		return null;
	}

	/**
	 * 移除某个key值已经对应的值
	 * 
	 * @param context
	 * @param key
	 */
	public static void remove(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		LogUtils.i("已移除" + key);
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 清除所有数据
	 * 
	 * @param context
	 */
	public static void clear(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		LogUtils.i("已清空SP存储");
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 查询某个key是否已经存在
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean contains(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		boolean result = sp.contains(key);
		if (result)
			LogUtils.i(key + "的值已经存在");
		else
			LogUtils.i(key + "的值不存在");

		return result;
	}

	/**
	 * 返回所有的键值对
	 * 
	 * @param context
	 * @return
	 */
	public static Map<String, ?> getAll(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		return sp.getAll();
	}



	/**
	 * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
	 * 
	 * @author zhy
	 * 
	 */
	private static class SharedPreferencesCompat {
		private static final Method sApplyMethod = findApplyMethod();

		/**
		 * 反射查找apply的方法
		 * 
		 * @return
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private static Method findApplyMethod() {
			try {
				Class clz = SharedPreferences.Editor.class;
				return clz.getMethod("apply");
			} catch (NoSuchMethodException e) {
			}

			return null;
		}

		/**
		 * 如果找到则使用apply执行，否则使用commit
		 * 
		 * @param editor
		 */
		public static void apply(SharedPreferences.Editor editor) {
			try {
				if (sApplyMethod != null) {
					sApplyMethod.invoke(editor);
					return;
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			editor.commit();
		}
	}

}