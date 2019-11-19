package com.tech.mediaserver.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tech.mediaserver.entity.Zone;

public class TimeZoneUtils {

	public static TimeZoneUtils instance = null;
	private List<Zone> mZoneList = null;

	private TimeZoneUtils() {
		mZoneList = parseZoneXml();
	}

	public List<Zone> getmZoneList() {
		return mZoneList;
	}

	public static TimeZoneUtils getInstance() {
		if (instance == null) {
			synchronized (TimeZoneUtils.class) {
				if (instance == null) {
					instance = new TimeZoneUtils();
				}
			}
		}
		return instance;
	}

	public String getTimeByZoneId(int index) {
		TimeZone timeZone = TimeZone.getTimeZone(mZoneList.get(index).getZone_id());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(timeZone);
		String time = simpleDateFormat.format(Calendar.getInstance().getTime());
		return time;
	}
	
	public int getDefaultTimeZone() {
		TimeZone zone = TimeZone.getDefault();
		int index = -1;
		for(int i = 0; i < mZoneList.size() ;i++) {
			if(mZoneList.get(i).getZone_id().equals(zone.getID())) {
				index = i;
				break;
			}
		}
		if(index == -1) {
			index = 64;//Asia/Shanghai
		}
		return index;
	}

	public void setTimeZone(int index) {
		TimeZone zone = TimeZone.getTimeZone(mZoneList.get(index).getZone_id());
		TimeZone.setDefault(zone);
		try {
			String cmd_line = "timedatectl set-timezone " + mZoneList.get(index).getZone_id();
			Runtime.getRuntime().exec(cmd_line);
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDateTime(String date_time) {

		try {
			String cmd_line = "timedatectl set-time " + date_time;
			Runtime.getRuntime().exec(cmd_line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<Zone> parseZoneXml() {
		DocumentBuilderFactory bdf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = bdf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document document = null;
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("timezones.xml");
			document = db.parse(inputStream);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList zone_list = document.getElementsByTagName("timezone");

		List<Zone> mlist = new ArrayList<Zone>();
		for (int i = 0; i < zone_list.getLength(); i++) {

			Node node = zone_list.item(i);
			String name = node.getFirstChild().getNodeValue();
			NamedNodeMap namedNodeMap = node.getAttributes();
			String id = namedNodeMap.getNamedItem("id").getTextContent();

			TimeZone time_zone = TimeZone.getTimeZone(id);
			int offset = time_zone.getRawOffset();
			int p = Math.abs(offset);
			StringBuilder gmt_name = new StringBuilder();
			gmt_name.append("UTC");
			if (offset < 0) {
				gmt_name.append("-");
			} else {
				gmt_name.append("+");
			}
			gmt_name.append(String.format("%02d", p / (3600 * 1000)));
			gmt_name.append(":");

			int min = p / 60000;
			min %= 60;

			if (min < 10) {
				gmt_name.append("0");
			}
			gmt_name.append(min);

			Zone item = new Zone();
			item.setId(i);
			item.setZone_id(id);
			item.setZone_name(name);
			item.setGmt_name(gmt_name.toString());
			item.setIsDaylight(time_zone.useDaylightTime()==true?1:0);

			mlist.add(item);
		}
		return mlist;
	}

}
