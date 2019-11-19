package com.tech.mediaserver.constant;

/**
 * @author chudichen
 * @date 2018/10/13
 */
public interface HlsConstant {
    // 直播频道英文导出
    String en_fileName = "Channel Reacord";
    String[] en_id = {"Channel Id"};
	String[] en_name = {"Channel Name"};
	String[] en_src = {"Channel Source"};
	String[] en_onDemand = {"Pull On Demand"};
	String[] en_onDemandSelect = {"No_0","Yes_1"};
	String[] en_selectApp = {"App ID"};
	String[] en_storeTime = {"Store Time"};
	String[] en_storedTime = {"Stored Time"};
	String[] en_bitrate = {"Pull Bitrate"};
	String[] en_speed = {"Pull Speed"};
	String[] en_origin = {"Origin"};
	
	// 直播频道中文导出
	String zh_fileName = "直播录制";
	String[] zh_id = {"节目标识"};
	String[] zh_name = {"节目名称"};
	String[] zh_src = {"节目源地址"};
	String[] zh_onDemand = {"是否按需拉流"};
	String[] zh_onDemandSelect = {"否_0","是_1"};
	String[] zh_selectApp = {"应用ID"};
	String[] zh_storeTime = {"录制时长"};
	String[] zh_storedTime = {"已录时长"};
	String[] zh_bitrate = {"拉流码率"};
	String[] zh_speed = {"拉流速度"};
	String[] zh_origin = {"来源"};
	
	// 直播频道英文错误提示
	String[] en_errorMsg = {"Error Message"};
	String en_channelIdSize = "The length of Channel Id is 128.";
	String en_channelNameSize = "The length of Channel Name is 512.";
	String en_sourceUrlPattern = "The format of Channel Source is incorrect.";
	String en_onDemandNotNull = "Pull On Demand can't be empty.";
	String en_originSize = "The length of Origin is 512.";
	String en_selectAppNotNull = "App ID can't be empty.";
	String en_storeTimeMin = "Store Time can't be less than 0.";
	String en_storeTimeMax = "Store Time can't be greater than 168.";
	
	// 直播频道中文错误提示
	String[] zh_errorMsg = {"错误提示"};
	String zh_channelIdSize = "节目标识的长度在128以内";
	String zh_channelNameSize = "节目名称的长度在512以内";
	String zh_sourceUrlPattern = "节目源地址的格式不正确";
	String zh_onDemandNotNull = "是否按需拉流不能为空";
	String zh_originSize = "来源的长度在512以内";
	String zh_selectAppNotNull = "应用ID不能为空";
	String zh_storeTimeMin = "录制时长不能小于0";
	String zh_storeTimeMax = "录制时不能大于168";
	
	// 直播频道英文下载模板
	String en_template = "Channel Record-Template.xlsx";
	
	// 直播频道中文下载模板
	String zh_template = "直播录制-模板.xlsx";
	
	// 直播频道英文错误提示文件
	String en_importErrorFile = "Channel Record-Error Message.xlsx";
	
	// 直播频道中文错误提示文件
	String zh_importErrorFile = "直播录制-错误提示.xlsx";
	
}
