package com.tech.mediaserver.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CommandUtils {

	public static int execCommand(String command) {
		
		int result = 1;
		try {
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			
			if (p.exitValue() != 0) {
			    //说明命令执行失败
			    //可以进入到错误处理步骤中
				result = 0;
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String,Object> exec_uptime() {
		Map<String, Object> map = new HashMap<String, Object>();
		InputStreamReader inputs = null;
        BufferedReader buffer = null;
		try {
			Process p = Runtime.getRuntime().exec("uptime");
			p.waitFor();
			if (p.exitValue() != 0) {
			    //说明命令执行失败
			    //可以进入到错误处理步骤中
				return null;
			}
			inputs = new InputStreamReader(p.getInputStream());
			buffer = new BufferedReader(inputs);
			String line = "";
			while (true) {
			     line = buffer.readLine();
			     if (line == null) 
			     {
			    	  break; 
			     }
			     String tmp[] = line.split(",");
			     map.put("system_uptime", tmp[0]);
			     return map;
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            try {
                buffer.close();
                inputs.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
		}
		return null;
	}
	
	public static Map<String,Object> exec_temp() {
		Map<String, Object> map = new HashMap<String, Object>();
		InputStreamReader inputs = null;
        BufferedReader buffer = null;
		try {
			Process p = Runtime.getRuntime().exec("cat /sys/class/thermal/thermal_zone0/temp");
			p.waitFor();
			if (p.exitValue() != 0) {
				return null;
			}
			inputs = new InputStreamReader(p.getInputStream());
			buffer = new BufferedReader(inputs);
			String line = "";
			while (true) {
			     line = buffer.readLine();
			     if (line == null) 
			     {
			    	  break; 
			     }
			     Float temp = (float) (Integer.parseInt(line)/1000);
			     map.put("system_temp",temp + "°C" );
			     return map;
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            try {
                buffer.close();
                inputs.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
           
		}
		 return null;
	}
}
