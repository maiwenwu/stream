package com.tech.mediaserver.utils;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.OutputStream;
//import java.net.URLEncoder;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.springframework.web.multipart.MultipartFile;
//
//import cn.afterturn.easypoi.excel.ExcelExportUtil;
//import cn.afterturn.easypoi.excel.ExcelImportUtil;
//import cn.afterturn.easypoi.excel.entity.ExportParams;
//import cn.afterturn.easypoi.excel.entity.ImportParams;
//
//public class ExcelUtil {
//
//	public static void exportExcel(List<?> list, Class<?> pojoClass, 
//			String fileName, HttpServletResponse response) throws Exception {
//		Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), pojoClass, list);
//		if (workbook != null) { 
//			downLoadExcel(fileName, response, workbook);
//		}
//	}
//
//	private static void downLoadExcel(String fileName, HttpServletResponse response, 
//			Workbook workbook) throws Exception {
//		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//		response.setCharacterEncoding("UTF-8");
//		response.reset();
//		response.setHeader("Content-Disposition", "attachment;filename=" + 
//				URLEncoder.encode(fileName, "UTF-8") + "-" + 
//				dateFormat.format(new Date()) + ".xls");
//		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
//        response.setHeader("Pragma", "no-cache");
//        response.setHeader("Cache-Control", "no-cache");
//        response.setDateHeader("Expires", 0);
//        
//        OutputStream output = response.getOutputStream();
//        BufferedOutputStream bos = new BufferedOutputStream(output);
//        workbook.write(bos);
//        bos.flush();
//        bos.close();
//        output.close();
//	}
//
//	public static <T> List<T> importExcel(String filePath,  Integer headerRows, 
//			Class<T> pojoClass) throws Exception {
//		if (StringUtils.isBlank(filePath)){
//			return null;
//		}
//		ImportParams params = new ImportParams();
//		params.setHeadRows(headerRows);
//		List<T> list = null;
//		list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
//		return list;
//	}
//	
//	public static <T> List<T> importExcel(MultipartFile file, Integer headerRows, 
//			Class<T> pojoClass) throws Exception {
//		if (file == null) {
//			return null;
//		}
//		ImportParams params = new ImportParams();
//		params.setHeadRows(headerRows);
//		List<T> list = null;
//        list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
//		return list;
//	}
//
//}


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import com.tech.mediaserver.config.Constant;

import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
 
/**
 * @author gourd
 */
public class ExcelUtil {
 
 
    public static boolean isExcel2003(String filePath)
    {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }
 
    public static boolean isExcel2007(String filePath)
    {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }
 
    /**
     * 得到Workbook对象
     * @param file
     * @return
     * @throws IOException
     */
    public static Workbook getWorkBook(MultipartFile file) throws IOException{
        //这样写  excel 能兼容03和07
        InputStream is = file.getInputStream();
        Workbook hssfWorkbook = null;
        try {
            hssfWorkbook = new HSSFWorkbook(is);
        } catch (Exception ex) {
            is =file.getInputStream();
            hssfWorkbook = new XSSFWorkbook(is);
        }
        return hssfWorkbook;
    }
 
    /**
     * 得到错误信息
     * @param sb
     * @param list
     * @param i
     * @param obj
     * @param name  用哪个属性名去表明不和规定的数据
     * @param msg
     * @throws Exception
     */
    public static void getWrongInfo(StringBuilder sb,List<?> list,int i,Object obj,String name,String msg) throws Exception{
        Class<? extends Object> clazz=obj.getClass();
        Object str=null;
        //得到属性名数组
        Field[] fields = clazz.getDeclaredFields();
        for(Field f : fields){
            if(f.getName().equals(name)){
                //用来得到属性的get和set方法
                PropertyDescriptor pd = new PropertyDescriptor(f.getName(), clazz);
                //得到get方法
                Method getMethod=pd.getReadMethod();
                str = getMethod.invoke(obj);
            }
        }
        if(i==0) {
            sb.append(msg + str + ";");
        }
        else if(i==(list.size()-1)) {
            sb.append(str + "</br>");
        }
        else {
            sb.append(str + ";");
        }
    }
 
 
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass,String fileName,boolean isCreateHeader, HttpServletResponse response){
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);
 
    }
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass,String fileName, HttpServletResponse response){
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
    }
    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response){
        defaultExport(list, fileName, response);
    }
 
    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        if (workbook != null){
            downLoadExcel(fileName, response, workbook);
        }
 
    }
 
    public static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            //设置浏览器响应头对应的Content-disposition
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            //编码
            response.setCharacterEncoding("UTF-8");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        if (workbook != null){
            downLoadExcel(fileName, response, workbook);
        }
 
    }
 
    public static <T> List<T> importExcel(String filePath,Integer titleRows,Integer headerRows, Class<T> pojoClass){
        if (StringUtils.isBlank(filePath)){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        }catch (NoSuchElementException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass){
        if (file == null){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        }catch (NoSuchElementException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static <T> List<T> importExcel(MultipartFile file, 
			Integer headerRows, Class<T> pojoClass) throws Exception {
		if (file == null) {
			return null;
		}
		ImportParams params = new ImportParams();
		params.setHeadRows(headerRows);
		List<T> list = null;
        list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
		return list;
	}
    
    public static void exportBigExcel(List<?> list, Class<?> pojoClass, 
			String fileName, HttpServletResponse response) throws Exception {
		ExportParams params = new ExportParams();
		params.setType(ExcelType.XSSF);
		Workbook workbook = ExcelExportUtil.exportExcel(params, pojoClass, list);
		if (workbook != null) {
			downLoadExcel(fileName, response, workbook);
		}
	}
    
	public static <T> ExcelImportResult<T> importExcelByVerify(MultipartFile file, 
			Class<T> pojoClass, ImportParams params) throws Exception {
		if (file == null) {
			return null;
		}
		ExcelImportResult<T> importResult = ExcelImportUtil.importExcelMore(
				file.getInputStream(), pojoClass, params);
        return importResult;
	}
    
    public static void downloadTemplate(String fileName, 
			HttpServletResponse response) throws Exception {
		ClassPathResource resource = new ClassPathResource(Constant.TEMPLATE_PATH + fileName);
        fileName = new String(fileName.getBytes("GB2312"),"ISO-8859-1");
    	response.setCharacterEncoding("UTF-8");
    	response.reset();
    	response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
    	//response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
    	response.setContentType("application/octet-stream");
    	response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        
	    byte[] buffer = new byte[1024];
	    InputStream fis = resource.getInputStream();
	    BufferedInputStream bis = null;
	    try {
	        bis = new BufferedInputStream(fis);
	        OutputStream os = response.getOutputStream();
	        int i = bis.read(buffer);
	        while (i != -1) {
	            os.write(buffer, 0, i);
	            i = bis.read(buffer);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if(bis != null) {
	            try {
	                bis.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        if(fis != null) {
	            try {
	                fis.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
    
    
    
}
