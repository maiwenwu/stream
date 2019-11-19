package com.tech.mediaserver.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.tech.mediaserver.constant.WebConstant;

public class OSUtils {
	private List<Long> rx_list;
	private List<Long> tx_list;
	
	public List<Long> getRx_list() {
		return rx_list;
	}

	public List<Long> getTx_list() {
		return tx_list;
	}

	private long rxbps;
    private long txbps;
    private Sigar sigar;
    
    public static OSUtils instance = null;
    
	private OSUtils() {
		init();
		startTimer();
	}
	
	private void init() {
		sigar = new Sigar();
		rx_list = new ArrayList<Long>();
		tx_list = new ArrayList<Long>();
		for(int i =0 ;i < 120 ;i++) 
		{
			rx_list.add((long) 0);
			tx_list.add((long) 0);
		}
	}
	public static OSUtils getInstance() {
		if (instance == null) { 
			synchronized (StreamSocketApi.class) { 
				if (instance == null) { 
					instance = new OSUtils(); 
					} 
				} 
			} 
		return instance;
	}
    
	private void startTimer() {
		new Timer("testTimer").schedule(new TimerTask() {
            @Override
            public void run() {
					try {
						populate(sigar,WebConstant.NET_INGERFACE);
					} catch (SigarException e) {
//						e.printStackTrace();
					}
            	rx_list.remove(0);
            	rx_list.add((Long) rxbps/1024/1024);
            	
            	tx_list.remove(0);
            	tx_list.add((Long) txbps/1024/1024);
            }
        }, 0,1000);
	}
	
	/**
	 * 网卡速率
	 * @param sigar
	 * @param name
	 * @throws SigarException
	 */
    private void populate(Sigar sigar, String name) throws SigarException {
    	sigar.getNetInterfaceConfig(name);
        long start = System.currentTimeMillis();
        NetInterfaceStat statStart = sigar.getNetInterfaceStat(name);
		long rxBytesStart = statStart.getRxBytes();
		long txBytesStart = statStart.getTxBytes();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("no device:"+WebConstant.NET_INGERFACE);
//			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		NetInterfaceStat statEnd = sigar.getNetInterfaceStat(name);
		long rxBytesEnd = statEnd.getRxBytes();
		long txBytesEnd = statEnd.getTxBytes();

		rxbps = (rxBytesEnd - rxBytesStart)*8 /(end-start)*1000;
		txbps = (txBytesEnd - txBytesStart)*8/(end-start)*1000;
		sigar.getNetInterfaceStat(name);
	}
    
    /**
     * cpu　使用率
     * @return
     */
    public float cpuUsage() {
        try {
            Map<?, ?> map1 = cpuinfo();
            Thread.sleep(1 * 1000);
            Map<?, ?> map2 = cpuinfo();
 
            long user1 = Long.parseLong(map1.get("user").toString());
            long nice1 = Long.parseLong(map1.get("nice").toString());
            long system1 = Long.parseLong(map1.get("system").toString());
            long idle1 = Long.parseLong(map1.get("idle").toString());
 
            long user2 = Long.parseLong(map2.get("user").toString());
            long nice2 = Long.parseLong(map2.get("nice").toString());
            long system2 = Long.parseLong(map2.get("system").toString());
            long idle2 = Long.parseLong(map2.get("idle").toString());
 
            long total1 = user1 + system1 + nice1;
            long total2 = user2 + system2 + nice2;
            float total = total2 - total1;
 
            long totalIdle1 = user1 + nice1 + system1 + idle1;
            long totalIdle2 = user2 + nice2 + system2 + idle2;
            float totalidle = totalIdle2 - totalIdle1;
 
            float cpusage = (total / totalidle) * 100;
            return cpusage;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Map<?, ?> cpuinfo() {
        InputStreamReader inputs = null;
        BufferedReader buffer = null;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            inputs = new InputStreamReader(new FileInputStream("/proc/stat"));
            buffer = new BufferedReader(inputs);
            String line = "";
            while (true) {
                line = buffer.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("cpu")) {
                    StringTokenizer tokenizer = new StringTokenizer(line);
                    List<String> temp = new ArrayList<String>();
                    while (tokenizer.hasMoreElements()) {
                        String value = tokenizer.nextToken();
                        temp.add(value);
                    }
                    map.put("user", temp.get(1));
                    map.put("nice", temp.get(2));
                    map.put("system", temp.get(3));
                    map.put("idle", temp.get(4));
                    map.put("iowait", temp.get(5));
                    map.put("irq", temp.get(6));
                    map.put("softirq", temp.get(7));
                    map.put("stealstolen", temp.get(8));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
                inputs.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return map;
    }
 
    /**
     * 功能：内存使用率
     */
    public Map<String, Object> memoryUsage() {
        Map<String, Object> map = new HashMap<String, Object>();
        InputStreamReader inputs = null;
        BufferedReader buffer = null;
        try {
            inputs = new InputStreamReader(new FileInputStream("/proc/meminfo"));
            buffer = new BufferedReader(inputs);
            String line = "";
            while (true) {
                line = buffer.readLine();
                if (line == null)
                    break;
                int beginIndex = 0;
                int endIndex = line.indexOf(":");
                if (endIndex != -1) {
                    String key = line.substring(beginIndex, endIndex);
                    beginIndex = endIndex + 1;
                    endIndex = line.length();
                    String memory = line.substring(beginIndex, endIndex);
                    String value = memory.replace("kB", "").trim();
                    map.put(key, value);
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
                inputs.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }
    
    public Map<String ,Object> system_info()
    {
		Map<String, Object> map = new HashMap<String, Object>();
		OperatingSystem OS = OperatingSystem.getInstance();
        map.put("system_name", OS.getArch() + " " +OS.getDescription()); //操作系统的描述
        map.put("system_version", OS.getVersion()); //操作系统的版本号
          
		try {
			CpuInfo infos[] = sigar.getCpuInfoList();
			map.put("system_cpu_num", infos.length);
			map.put("system_cpu_type", infos[0].getModel());
		} catch (SigarException e) {
			e.printStackTrace();
		}
	  return map;
    }
    //cat /sys/class/thermal/thermal_zone0/temp
    public Map<String,Object> system_temp()
    {
    	Map<String, Object> map = CommandUtils.exec_temp();
    	return map;
    }
    
    public Map<String,Object> system_uptime()
    {
    	Map<String, Object> map = CommandUtils.exec_uptime();
    	return map;
    }
}
