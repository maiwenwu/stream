package com.tech.mediaserver.entity;

public class Zone {
	
	private int id;
	private String zone_id;
	private String zone_name;
	private String gmt_name;
	private int isDaylight;
	
	public int getIsDaylight() {
		return isDaylight;
	}
	public void setIsDaylight(int isDaylight) {
		this.isDaylight = isDaylight;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getZone_id() {
		return zone_id;
	}
	public void setZone_id(String zone_id) {
		this.zone_id = zone_id;
	}
	public String getZone_name() {
		return zone_name;
	}
	public void setZone_name(String zone_name) {
		this.zone_name = zone_name;
	}
	public String getGmt_name() {
		return gmt_name;
	}
	public void setGmt_name(String gmt_name) {
		this.gmt_name = gmt_name;
	}
	@Override
	public String toString() {
		return "Zone [id=" + id + ", zone_id=" + zone_id + ", zone_name="
				+ zone_name + ", gmt_name=" + gmt_name + "]";
	}
	
}
