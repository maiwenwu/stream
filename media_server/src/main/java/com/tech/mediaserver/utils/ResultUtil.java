package com.tech.mediaserver.utils;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tech.mediaserver.enums.ResultEnum;
import com.tech.mediaserver.vo.ResultVo;

/**
 * <h2>Response返回值工具类，200成功，201下发失败，202失败</h2>
 * @author chudichen
 * @date 2018/9/27
 */
public interface ResultUtil {

    /**
     * <h2>返回成功 code 200</h2>
     * @return {@link ResultEnum} 200
     */
    static ResultVo returnSuccess() {
        return new ResultVo(ResultEnum.SUCCESS.getCode());
    }

    /**
     * <h2>返回成功 code 200</h2>
     * @return {@link ResultEnum} 200
     */
    static ResultVo returnSuccess(String msg) {
        return new ResultVo(ResultEnum.SUCCESS.getCode(), msg);
    }

    /**
     * <h2>返回下发失败 code 201</h2>
     * @return {@link ResultEnum} 201
     */
    static ResultVo returnIssueFailed() {
        return new ResultVo(ResultEnum.ISSUE_FAIL.getCode());
    }

    /**
     * <h2>返回下发失败 code 200</h2>
     * @param message {@link} 失败相关描述
     * @return {@link ResultEnum} 200
     */
    static ResultVo returnIssueFailed(String message) {
        return new ResultVo(ResultEnum.ISSUE_FAIL.getCode(), message);
    }

    /**
     * <h2>返回失败 code 202</h2>
     * @return {@link ResultEnum} 202
     */
    static ResultVo returnFail() {
        return new ResultVo(ResultEnum.FAIL.getCode());
    }
    
    /**
     * <h2>返回失败 code 203</h2>
     * @return {@link ResultEnum} 203
     */
    static ResultVo returnExist(String message) {
        return new ResultVo(ResultEnum.EXIST.getCode(), message);
    }
    
    /**
     * <h2>返回失败 code 200</h2>
     * @param message {@link} 失败相关描述
     * @return {@link ResultEnum} 200
     */
    static ResultVo returnFail(String message) {
        return new ResultVo(ResultEnum.FAIL.getCode(), message);
    }

    /**
     * <h2>响应结果验证</h2>
     * @param responseString {@link String} 验证String
     * @return {@link Boolean} true or false
     */
    static boolean validResult(String responseString) {
        JsonParser jsonParser = new JsonParser();
        if (responseString == null) {
            return false;
        }
        JsonObject jsonObject = jsonParser.parse(responseString).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("channels");
        final String code = "code";
        final String channelId = "channel_id";
        List<String> failedChannelIdList = new ArrayList<>();
        jsonArray.forEach((JsonElement jsonElement) -> {
            if (jsonElement.getAsJsonObject().get(code).getAsInt() == ResultEnum.SUCCESS.getCode()) {
                failedChannelIdList.add(jsonElement.getAsJsonObject().get(channelId).getAsString());
            }
        });
        return (failedChannelIdList.size() > 0 ? true : false);
    }

    /**
     * 获取所有失败的ChannelId
     * @param responseString {@link String} 响应
     * @return {@link String}
     */
    static JSONArray getFailedResult(String responseString, String fieldName) {
        JSONArray messageArray = new JSONArray();
        
        JSONObject jsonObject = JSONObject.parseObject(responseString);
        JSONArray jsonArray = jsonObject.getJSONArray("channels");
        final String code = "code";
        
        for(int i=0; i<jsonArray.size(); i++) {
        	JSONObject resultItem = jsonArray.getJSONObject(i);
        	if(resultItem.getInteger(code) == ResultEnum.ISSUE_FAIL.getCode()) {
        		JSONObject object = new JSONObject();
            	object.put(resultItem.getString(fieldName),
            			resultItem.getInteger(code));
            	messageArray.add(object);
        	}
        }
        return messageArray;
    }
}
