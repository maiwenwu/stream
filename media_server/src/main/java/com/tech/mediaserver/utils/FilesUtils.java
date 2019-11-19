package com.tech.mediaserver.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tech.mediaserver.constant.WebConstant;

import lombok.Data;

public class FilesUtils {
	public static List<Ports> readPort() throws NumberFormatException, IOException {
		String line = null;
		File dest = new File(WebConstant.PORT_PATH);
		BufferedReader in = new BufferedReader(new FileReader(dest));
		line = in.readLine();
		JSONObject json = JSONObject.parseObject(line);
		JSONArray jsonArray = json.getJSONArray("port");
		List<Ports> ports_list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			Ports ports = new Ports();
			JSONObject obj_port = jsonArray.getJSONObject(i);

			Integer board = obj_port.getIntValue("board");
			Integer module = obj_port.getIntValue("module");

			JSONArray port_list = obj_port.getJSONArray("port_list");
			List<Port> portList = new ArrayList<>();
			for (int j = 0; j < port_list.size(); j++) {
				Port port = new Port();
				JSONObject pObject = port_list.getJSONObject(j);
				port.port = pObject.getIntValue("port");
				portList.add(port);
			}

			JSONArray unuse_list = obj_port.getJSONArray("unuse_list");
			List<Port> unuseList = new ArrayList<>();
			for (int j = 0; j < unuse_list.size(); j++) {
				Port port = new Port();
				JSONObject pObject = unuse_list.getJSONObject(j);
				port.port = pObject.getIntValue("port");
				unuseList.add(port);
			}

			ports.setBoard(board);
			ports.setMoudle(module);
			ports.setPort_list(portList);
			ports.setUnuse_list(unuseList);

			ports_list.add(ports);

		}
		in.close();
		return ports_list;
	}

	public static void savePort(List<Ports> ports) throws IOException {

		JSONObject portsObject = new JSONObject();
		JSONArray portArray = new JSONArray();

		for (int i = 0; i < ports.size(); i++) {
			JSONObject portJson = new JSONObject();
			portJson.put("board", ports.get(i).getBoard());
			portJson.put("module", ports.get(i).getMoudle());

			JSONArray portListArray = new JSONArray();
			for (int j = 0; j < ports.get(i).getPort_list().size(); j++) {
				JSONObject pObject = new JSONObject();
				pObject.put("port", ports.get(i).getPort_list().get(j).port);
				portListArray.add(pObject);
			}

			JSONArray unuseListArray = new JSONArray();
			for (int j = 0; j < ports.get(i).getUnuse_list().size(); j++) {
				JSONObject pObject = new JSONObject();
				pObject.put("port", ports.get(i).getUnuse_list().get(j).port);
				unuseListArray.add(pObject);
			}
			portJson.put("port_list", portListArray);
			portJson.put("unuse_list", unuseListArray);
			portArray.add(portJson);
		}

		portsObject.put("port", portArray);
		
		File dest = new File(WebConstant.PORT_PATH);
		FileWriter out = new FileWriter(dest);

		out.write(portsObject.toString());
		out.close();
	}

	public static JSONObject readLog(File file,Integer file_pos) {

		JSONObject root = new JSONObject();
		
		int num = 500;
		int count = 0;
		
		String result = "";
		long next = 0;
		
		RandomAccessFile randomAccessFile = null;
		
		try {
			randomAccessFile = new RandomAccessFile(file, "r");
			
			//file length
			long length = randomAccessFile.length();
			
			if (length != 0L) {
				if (file_pos == 0) {
					next = length - 1;
				} else {
					next = file_pos;
				}
				while (next > 0) {

					next --;
					
					randomAccessFile.seek(next);
					if (randomAccessFile.readByte() == '\n') {
						String line = randomAccessFile.readLine();
						result =  line + "<br>" + result ;
						count ++;
						if (count == num) {
							break;
						}
					}
				}
				if (next == 0) {
					randomAccessFile.seek(0);
					result = randomAccessFile.readLine() + "<br>" + result;
				}
				root.put("str", result);
				root.put("file_pos", next);
				
				randomAccessFile.close();
			} 
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return root;
	}
	
	public static JSONObject getVersion() {
		FileReader fi = null;
    	BufferedReader br = null;
    	JSONObject jsonObject = new JSONObject();
    	try
		{
    		String buffer = null;
    		int start = 0;
    		String media_control_version = null;
    		String media_server_version = null;
    		String media_cmdline_version = null;
    		String media_frontend_version = null;
    		String media_hls_version = null;
    		
			fi = new FileReader(WebConstant.VERSION_CFG_PATH);
			br = new BufferedReader(fi);

			while ((buffer = br.readLine()) != null)
			{
				if (buffer.contains(WebConstant.MEDIA_CONTROL_KEY) == true)
				{
					start = buffer.indexOf("=")+1;
					if (start < buffer.length()) {
						media_control_version = buffer.substring(start);
						jsonObject.put("media_control_version", media_control_version);
					}
						
				} else if (buffer.contains(WebConstant.MEDIA_SERVER_KEY) == true) {
					start = buffer.indexOf("=")+1;
					if (start < buffer.length()) {
						media_server_version = buffer.substring(start);
						jsonObject.put("media_server_version", media_server_version);
					}
				} else if (buffer.contains(WebConstant.MEDIA_CMDLINE_KEY) == true) {
					start = buffer.indexOf("=")+1;
					if (start < buffer.length()) {
						media_cmdline_version = buffer.substring(start);
						jsonObject.put("media_cmdline_version", media_cmdline_version);
					}
				} else if (buffer.contains(WebConstant.MEDIA_FRONTEND_KEY) == true) {
					start = buffer.indexOf("=")+1;
					if (start < buffer.length()) {
						media_frontend_version = buffer.substring(start);
						jsonObject.put("media_frontend_version", media_frontend_version);
					}
				} else if (buffer.contains(WebConstant.HLS_KEY) == true) {
					start = buffer.indexOf("=")+1;
					if (start < buffer.length()) {
						media_hls_version = buffer.substring(start);
						jsonObject.put("media_hls_version", media_hls_version);
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    	finally
    	{
    		try
			{
    			if (br != null) br.close();
    			if (fi != null) fi.close();
			}
			catch (IOException e)
			{
			}
    	}
		return jsonObject;
	}
	
	public static JSONArray getChangeTag() {
		FileReader fi = null;
    	BufferedReader br = null;
    	JSONArray jsonArray = new JSONArray();
    	try {
    		
    		String buffer = null;
    		int start = 0;
    		int middle = 0;
    		int end = 0;
    		
			fi = new FileReader(WebConstant.CHANGE_TAG_PATH);
			br = new BufferedReader(fi);
			
			while ((buffer = br.readLine()) != null) {
				if (buffer.contains(WebConstant.VERSION) && buffer.contains(WebConstant.DATE) && buffer.contains(WebConstant.DESCRIBE)) {
					JSONObject jsonObject = new JSONObject();
					start = buffer.indexOf(WebConstant.VERSION);
					middle = buffer.indexOf(WebConstant.DATE);
					end = buffer.indexOf(WebConstant.DESCRIBE);
					String version = buffer.substring(start, middle-1).substring(WebConstant.VERSION.length());
					String date = buffer.substring(middle, end-1).substring(WebConstant.DATE.length());
					String describe = buffer.substring(end).substring(WebConstant.DESCRIBE.length());
					jsonObject.put("version", version);
					jsonObject.put("date", date);
					jsonObject.put("describe", describe);
					jsonArray.add(jsonObject);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
    	finally
    	{
    		try
			{
    			if (br != null) br.close();
    			if (fi != null) fi.close();
			}
			catch (IOException e)
			{
			}
    	}
    	return jsonArray;
    	
	}

	public static boolean deleteFile(File file) {
		if (!file.exists()) {
			return false;
		}
		
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				deleteFile(f);
			}
 		}
		return file.delete();
	}
	
	public static void readAndWriteFile() {
		File dumpFile= new File(WebConstant.NET_FILE_PATH);
		StringBuffer res = new StringBuffer();
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(dumpFile));
			String line = "";
			while (true) {
				try {
					line = buffer.readLine();
					if (line == null) {
						break;
					}
					res.append(line + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			buffer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		String resource = res.toString().replaceAll("!java.util.HashMap", "");
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dumpFile));
			writer.write(resource);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Long getFileSize(File file) {
		RandomAccessFile randomAccessFile = null;
		long length = 0L;
		try {
			randomAccessFile = new RandomAccessFile(file, "r");
			
			//file length
			length = randomAccessFile.length();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return length;
	}
	
	@Data
	public static class Port implements Comparable<Port> {

		private Integer port;

		@Override
		public int compareTo(Port o) {
			return this.port.compareTo(o.port);
		}
	}

	@Data
	public static class Ports {
		private Integer board;
		private Integer moudle;
		List<Port> port_list;
		List<Port> unuse_list;
	}

}
