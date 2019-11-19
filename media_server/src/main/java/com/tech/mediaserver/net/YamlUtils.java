package com.tech.mediaserver.net;

import java.io.File;
import java.io.FileNotFoundException;
import org.ho.yaml.Yaml;
import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.utils.FilesUtils;

public class YamlUtils {
	public static YamlUtils instance = null;
	private File dumpFile;
	private YamlUtils() {
		dumpFile= new File(WebConstant.NET_FILE_PATH);
	}
	
	public static YamlUtils getInstance() {
		if (instance == null) { 
			synchronized (YamlUtils.class) { 
				if (instance == null) { 
					instance = new YamlUtils(); 
					} 
				} 
			} 
		return instance;
	}
	
	private NetRoot ReadFile(){
		NetRoot net_root = null;
		if (dumpFile.exists()) {
			try {
				net_root = (NetRoot) Yaml.loadType(dumpFile, NetRoot.class);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		return net_root;
	}
	
	private void WriteFile(NetRoot root) {
		try {
			 Yaml.dump(root, dumpFile,true);
			 FilesUtils.readAndWriteFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public NetWorkSetting ReadNmsInterface() {//eno7
		NetRoot net_root = ReadFile();
		NetWorkSetting nms_setting = new NetWorkSetting();
		if (net_root != null) {
			String addresses = net_root.getNetwork().getEthernets().getEno7().getAddresses().get(0);
			String gateway = net_root.getNetwork().getEthernets().getEno7().getRoutes().get(0).get("via").toString();
			String dns1 = net_root.getNetwork().getEthernets().getEno7().getNameservers().getAddresses().get(0);
			String dns2 = net_root.getNetwork().getEthernets().getEno7().getNameservers().getAddresses().get(1);

			String temp[] = addresses.split("/");
			String ip = temp[0];
			String netmask = calcMaskByPrefixLength(Integer.parseInt(temp[1]));
			
			nms_setting.setIp(ip);
			nms_setting.setNetmask(netmask);
			nms_setting.setGateway(gateway);
			nms_setting.setDns1(dns1);
			nms_setting.setDns2(dns2);
		}
		
		return nms_setting;
	}
	
	public int WriteNmsInterface(NetWorkSetting nms_info) {
		NetRoot net_root = ReadFile();
		StringBuilder builder = new StringBuilder();
		StringBuilder builder2 = new StringBuilder();
		
		if (net_root != null) {
			builder2.append("0.0.0.0/0");
			builder.append(nms_info.getIp()).append("/").append(calcPrefixLengthByMack(nms_info.getNetmask()));
			net_root.getNetwork().getEthernets().getEno7().getAddresses().set(0, builder.toString());
//			net_root.getNetwork().getEthernets().getEno7().setGateway4(nms_info.getGateway());
			net_root.getNetwork().getEthernets().getEno7().getRoutes().get(0).put("to", builder2.toString());
			net_root.getNetwork().getEthernets().getEno7().getRoutes().get(0).put("via", nms_info.getGateway());
			net_root.getNetwork().getEthernets().getEno7().getNameservers().getAddresses().set(0, nms_info.getDns1());
			net_root.getNetwork().getEthernets().getEno7().getNameservers().getAddresses().set(1, nms_info.getDns2());
			
			WriteFile(net_root);
			
			return 1;
		} else {
			return 0;
		}
		
	}
	
	public NetWorkSetting ReadDataInterface() {//bind0
		NetRoot net_root = ReadFile();
		NetWorkSetting data_info = new NetWorkSetting();
		if (net_root != null) {
			String addresses = net_root.getNetwork().getBonds().getBond0().getAddresses().get(0);
			String temp[] = addresses.split("/");
			String ip = temp[0];
			String netmask = calcMaskByPrefixLength(Integer.parseInt(temp[1]));
			String gateway = net_root.getNetwork().getBonds().getBond0().getRoutes().get(0).get("via").toString();
			String dns1 = net_root.getNetwork().getBonds().getBond0().getNameservers().getAddresses().get(0);
			String dns2 = net_root.getNetwork().getBonds().getBond0().getNameservers().getAddresses().get(1);
			
			data_info.setIp(ip);
			data_info.setNetmask(netmask);
			data_info.setGateway(gateway);
			data_info.setDns1(dns1);
			data_info.setDns2(dns2);
		}
		return data_info;
	}
	
	public int WriteDataInterface(NetWorkSetting data_info) {
		NetRoot net_root = ReadFile();
		StringBuilder builder = new StringBuilder();
		StringBuilder builder2 = new StringBuilder();
		if (net_root != null) {
			String[] DataTo = data_info.getIp().split("\\.");
			builder2.append(DataTo[0]).append(".").append(DataTo[1]).append(".").append(DataTo[2]).append(".").append("0").append("/").append(calcPrefixLengthByMack(data_info.getNetmask()));
			builder.append(data_info.getIp()).append("/").append(calcPrefixLengthByMack(data_info.getNetmask()));
			net_root.getNetwork().getBonds().getBond0().getAddresses().set(0, builder.toString());
//			net_root.getNetwork().getBonds().getBond0().setGateway4(data_info.getGateway());
			net_root.getNetwork().getBonds().getBond0().getRoutes().get(0).put("to", builder2.toString());
			net_root.getNetwork().getBonds().getBond0().getRoutes().get(0).put("via", data_info.getGateway());
			net_root.getNetwork().getBonds().getBond0().getNameservers().getAddresses().set(0, data_info.getDns1());
			net_root.getNetwork().getBonds().getBond0().getNameservers().getAddresses().set(1, data_info.getDns2());

			WriteFile(net_root);
			
			return 1;
		} else {
			return 0;
		}
		
	}

	private String calcMaskByPrefixLength(int length) {//输出长度获得子网掩码
		
		String result = "";
		
		if (length == 0) {
			result = "0.0.0.0";
		} else {
			int mask = -1 << (32 - length);
			int partsNum = 4;
			int bitsOfPart = 8;
			int maskParts[] = new int[partsNum];
			int selector = 0x000000ff;
			for (int i = 0; i < maskParts.length; i++) {
				int pos = maskParts.length - 1 - i;
				maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
			}
			
			result = result + maskParts[0];
			for (int i = 1; i < maskParts.length; i++) {
				result = result + "." + maskParts[i];
			}
		}
		return result;
	}
	
	private int calcPrefixLengthByMack(String strip) {//输入子网掩码获得长度
		StringBuffer sbf;
		String str;
		// String strip = "255.255.255.0"; // 子网掩码
		int inetmask = 0, count = 0; // 子网掩码缩写代码
		String[] ipList = strip.split("\\.");
		for (int n = 0; n < ipList.length; n++) {
		sbf = toBin(Integer.parseInt(ipList[n]));
		str = sbf.reverse().toString();

		// 统计2进制字符串中1的个数
		count = 0;
		for (int i = 0; i < str.length(); i++) {
			i = str.indexOf('1', i); // 查找 字符'1'出现的位置
			if (i == -1) {
				break;
			}
				count++; // 统计字符出现次数
			}
			inetmask += count;
		}
		return inetmask;
	}

	private StringBuffer toBin(int x) {
		StringBuffer result = new StringBuffer();
		result.append(x % 2);
		x /= 2;
		while (x > 0) {
			result.append(x % 2);
			x /= 2;
		}
		return result;
	}
}
