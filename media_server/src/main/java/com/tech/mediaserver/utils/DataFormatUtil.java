package com.tech.mediaserver.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.util.DigestUtils;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;

/**
 * Create Time：2018年8月22日 上午11:27:19
 * Project Name：mott-base
 * @author Gavin
 * @version 1.0
 * @since JDK 1.8.0_162
 * File Name: DataFormatUtil.java
 * Class Description: 判断或转换数据格式
 */
public class DataFormatUtil {
	
	public static java.util.regex.Pattern pattern = 
			java.util.regex.Pattern.compile("^[-\\+]?[\\d]*$");
	
	/**
	 * 利用正则表达式判断字符串是否是数字
	 */
	public static boolean isInteger(String str) {
		if(str == null) {
			return false;
		}
	    return pattern.matcher(str).matches();
	}
	
	/**
	 * 字符串若为null，返回""
	 */
	public static String nullToEmpty(String str) {
		if(str == null) {
			return "";
		}
		return str;
	}
	
	// 盐，用于混交md5
    private static final String slat = "*2018&8%22#@11/27-19";
    
    /**
     * 生成md5
     */
    public static String getMD5(String str) {
        String base = str +"/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
    
    /**
     * 动态修改@Excel注解
     */
    public static void setExcelField(Class profileClass, 
    		String field, String excelField, String[] values) {
		try {
			Field name = profileClass.getDeclaredField(field);
			if(name != null) {
				name.setAccessible(true);
				Excel nameExcel = name.getAnnotation(Excel.class);
				if(nameExcel != null) {
					InvocationHandler nameHandler = Proxy.getInvocationHandler(nameExcel);
					Field nameExcelField = nameHandler.getClass().getDeclaredField("memberValues");
					nameExcelField.setAccessible(true);
					Map memberValues = (Map) nameExcelField.get(nameHandler);
					memberValues.put(excelField, values.length > 1 ? values : values[0]);
				}
			}
		} catch (NoSuchFieldException | SecurityException e) {
			return;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return;
		}
	}
    
    /**
     * 动态修改@ExcelCollection注解
     */
    public static void setExcelCollectionField(Class profileClass, String field, String excelField, String[] values) {
		try {
			Field name = profileClass.getDeclaredField(field);
			if(name != null) {
				name.setAccessible(true);
				ExcelCollection nameExcel = name.getAnnotation(ExcelCollection.class);
				if(nameExcel != null) {
					InvocationHandler nameHandler = Proxy.getInvocationHandler(nameExcel);
					Field nameExcelField = nameHandler.getClass().getDeclaredField("memberValues");
					nameExcelField.setAccessible(true);
					Map memberValues = (Map) nameExcelField.get(nameHandler);
					memberValues.put(excelField, values.length > 1 ? values : values[0]);
				}
			}
		} catch (NoSuchFieldException | SecurityException e) {
			return;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return;
		}
	}
    
    /**
     * 动态修改@NotNull注解的message属性
     */
    public static void setNotNullField(Class profileClass, 
    		String field, String value) {
		try {
			Field name = profileClass.getDeclaredField(field);
			if(name != null) {
				name.setAccessible(true);
				NotNull nameExcel = name.getAnnotation(NotNull.class);
				if(nameExcel != null) {
					InvocationHandler nameHandler = Proxy.getInvocationHandler(nameExcel);
					Field nameExcelField = nameHandler.getClass().getDeclaredField("memberValues");
					nameExcelField.setAccessible(true);
					Map memberValues = (Map) nameExcelField.get(nameHandler);
					memberValues.put("message", value);
				}
			}
		} catch (NoSuchFieldException | SecurityException e) {
			return;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return;
		}
	}
    
    /**
     * 动态修改@Size注解的message属性
     */
    public static void setSizeField(Class profileClass, 
    		String field, String value) {
		try {
			Field name = profileClass.getDeclaredField(field);
			if(name != null) {
				name.setAccessible(true);
				Size nameExcel = name.getAnnotation(Size.class);
				if(nameExcel != null) {
					InvocationHandler nameHandler = Proxy.getInvocationHandler(nameExcel);
					Field nameExcelField = nameHandler.getClass().getDeclaredField("memberValues");
					nameExcelField.setAccessible(true);
					Map memberValues = (Map) nameExcelField.get(nameHandler);
					memberValues.put("message", value);
				}
			}
		} catch (NoSuchFieldException | SecurityException e) {
			return;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return;
		}
	}
    
    /**
     * 动态修改@Pattern注解的message属性
     */
    public static void setPatternField(Class profileClass, 
    		String field, String value) {
		try {
			Field name = profileClass.getDeclaredField(field);
			if(name != null) {
				name.setAccessible(true);
				Pattern nameExcel = name.getAnnotation(Pattern.class);
				if(nameExcel != null) {
					InvocationHandler nameHandler = Proxy.getInvocationHandler(nameExcel);
					Field nameExcelField = nameHandler.getClass().getDeclaredField("memberValues");
					nameExcelField.setAccessible(true);
					Map memberValues = (Map) nameExcelField.get(nameHandler);
					memberValues.put("message", value);
				}
			}
		} catch (NoSuchFieldException | SecurityException e) {
			return;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return;
		}
	}
    
    /**
     * 动态修改@Max注解的message属性
     */
    public static void setMaxField(Class profileClass, 
    		String field, String value) {
		try {
			Field name = profileClass.getDeclaredField(field);
			if(name != null) {
				name.setAccessible(true);
				Max nameExcel = name.getAnnotation(Max.class);
				if(nameExcel != null) {
					InvocationHandler nameHandler = Proxy.getInvocationHandler(nameExcel);
					Field nameExcelField = nameHandler.getClass().getDeclaredField("memberValues");
					nameExcelField.setAccessible(true);
					Map memberValues = (Map) nameExcelField.get(nameHandler);
					memberValues.put("message", value);
				}
			}
		} catch (NoSuchFieldException | SecurityException e) {
			return;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return;
		}
	}
    
    /**
     * 动态修改@Min注解的message属性
     */
    public static void setMinField(Class profileClass, 
    		String field, String value) {
		try {
			Field name = profileClass.getDeclaredField(field);
			if(name != null) {
				name.setAccessible(true);
				Min nameExcel = name.getAnnotation(Min.class);
				if(nameExcel != null) {
					InvocationHandler nameHandler = Proxy.getInvocationHandler(nameExcel);
					Field nameExcelField = nameHandler.getClass().getDeclaredField("memberValues");
					nameExcelField.setAccessible(true);
					Map memberValues = (Map) nameExcelField.get(nameHandler);
					memberValues.put("message", value);
				}
			}
		} catch (NoSuchFieldException | SecurityException e) {
			return;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return;
		}
	}
    
}
