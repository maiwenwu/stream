package com.tech.mediaserver.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tech.mediaserver.config.Constant;
import com.tech.mediaserver.constant.HlsConstant;
import com.tech.mediaserver.constant.InterfaceConstant;
import com.tech.mediaserver.dao.ChannelRecordDao;
import com.tech.mediaserver.dto.ChannelIdListDto;
import com.tech.mediaserver.dto.ChannelRecordDto;
import com.tech.mediaserver.dto.ChannelRecordReceiveDto;
import com.tech.mediaserver.dto.ChannelRecordRegisterDto;
import com.tech.mediaserver.dto.HeartBeatDto;
import com.tech.mediaserver.dto.SearchDto;
import com.tech.mediaserver.entity.ChannelRecord;
import com.tech.mediaserver.enums.StateEnum;
import com.tech.mediaserver.exception.ConflictException;
import com.tech.mediaserver.utils.DataFormatUtil;
import com.tech.mediaserver.utils.ExcelUtil;
import com.tech.mediaserver.utils.PageBean;
import com.tech.mediaserver.utils.PageUtil;
import com.tech.mediaserver.utils.Pagination;
import com.tech.mediaserver.utils.RestUtil;
import com.tech.mediaserver.utils.ResultUtil;
import com.tech.mediaserver.utils.SendRequestTask;
import com.tech.mediaserver.vo.AppListVo;
import com.tech.mediaserver.vo.PageVo;
import com.tech.mediaserver.vo.ResultVo;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;

/**
 * @author chudichen
 * @date 2018/10/16
 */
@Service
public class ChannelRecordService {

    @Autowired
    private ChannelRecordDao channelRecordDao;

    @Autowired
    private HeartbeatService heartbeatService;

    public List<ChannelRecord> getAllChannelRecord() {
    	return channelRecordDao.getAllChannelRecord();
    }
    
    public Integer deleteChannelRecord() {
    	return channelRecordDao.deleteChannelRecord();
    }
    
    public PageVo<ChannelRecordDto> getChannelRecordPage(Pagination pagination) {
        PageBean pageBean = PageUtil.getPageBean(pagination);
        List<ChannelRecord> list = channelRecordDao.getList(pageBean);
        List<ChannelRecordDto> channelRecordDtos = new ArrayList<>();
        list.forEach(channelRecord -> {
            ChannelRecordDto channelRecordDto = new ChannelRecordDto();
            BeanUtils.copyProperties(channelRecord, channelRecordDto);
            channelRecordDtos.add(channelRecordDto);
        });
        int total = channelRecordDao.getCount(pageBean);
        pagination.setTotal(total);
        PageVo<ChannelRecordDto> page = new PageVo<>();
        page.setPagination(pagination);
        page.setList(channelRecordDtos);
        return page;
    }
    
    public PageInfo<ChannelRecord> selectAllChannelRecord(Integer page,Integer size,String channel_id,Integer board_id,Integer module_id) {
		PageHelper.startPage(page, size);
		List<ChannelRecord> channelRecords = channelRecordDao.selectAllChannelRecord(channel_id,board_id,module_id);
		
		PageInfo<ChannelRecord> sPageInfo = new PageInfo<>(channelRecords);
		return sPageInfo;
    }
    
//    public List<ChannelRecord> getChannelRecordPage(PageBean pageBean) {
//      
//		List<ChannelRecord> list = channelRecordDao.getList(pageBean);
//		 
//		return list;
//    }

    public ResultVo save(ChannelRecordDto channelRecordDto) {
        ChannelRecord channelRecord = new ChannelRecord();
        BeanUtils.copyProperties(channelRecordDto, channelRecord);
        if (channelRecord.getId() == null) {
            channelRecord.setState(StateEnum.ISSUE.getCode());
            channelRecord.setCreateTime(new Date());
            if (channelRecord.getChannelName() == null) {
                channelRecord.setChannelName("");
            }
            try {
                validConflict(channelRecord);
            } catch (ConflictException e) {
                return ResultUtil.returnFail();
            }
            channelRecordDao.save(channelRecord);
            ChannelIdListDto channelIdListDto = new ChannelIdListDto();
            String[] idList = {channelRecord.getId().toString()};
            channelIdListDto.setIdList(idList);
            hlsRegister(channelIdListDto);
        } else {
            channelRecord.setUpdateTime(new Date());
            if (StateEnum.ISSUE.getCode().equals(channelRecord.getIssueState())) {
                String responseString = handleModify(channelRecord);
                if(responseString == null || "".equals(responseString)) {
                    return ResultUtil.returnFail();
                }
                JSONArray failedResult = ResultUtil.getFailedResult(responseString, "channel_id");
                if(failedResult.size() > 0) {
                    return ResultUtil.returnIssueFailed(failedResult.toString());
                }
            }
            channelRecordDao.update(channelRecord);
        }
        return ResultUtil.returnSuccess();
    }

    private String handleModify(ChannelRecord channelRecord) {
    	String responseString = null;
        try {
            HeartBeatDto heartBeat = heartbeatService.getHeartBeat(channelRecord.getSelectApp());
            StringBuilder url = new StringBuilder();
            url.append(InterfaceConstant.HTTP);
            url.append(heartBeat.getServerIp().get(0));
            url.append(InterfaceConstant.COLON);
            if(heartBeat.getModulePort() != null) {
            	url.append(heartBeat.getModulePort());
            }
            else {
            	url.append(InterfaceConstant.HLS_PORT);
            }
            url.append(InterfaceConstant.HLS_MODIFY);
            List<ChannelRecord> channelList = new ArrayList<>();
            channelList.add(channelRecord);
            JsonObject sendParam = handleModifyParam(channelList);
            responseString = RestUtil.doPost(url.toString(), sendParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return responseString;
    }

    private JsonObject handleModifyParam(List<ChannelRecord> channelList) {
        JsonArray jsonArray = new JsonArray();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser jsonParser = new JsonParser();
        channelList.forEach( channelRecord -> {
            try {
                ChannelRecordRegisterDto registerDto = new ChannelRecordRegisterDto();
                if(StringUtils.isEmpty(channelRecord.getOrigin())) {
                	channelRecord.setOrigin("");
                }
                BeanUtils.copyProperties(channelRecord, registerDto);
                JsonElement element = jsonParser.parse(objectMapper.writeValueAsString(registerDto));
                jsonArray.add(element);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("channels", jsonArray);
        return jsonObject;
    }
    
    private void handleRegisterParam(List<ChannelRecord> channelList,
    		Map<String, JSONArray> channelsJsonArrayMap, String[] appIdArray) {
        channelList.forEach(channelRecord -> {
        	ChannelRecordRegisterDto registerDto = new ChannelRecordRegisterDto();
            if(StringUtils.isEmpty(channelRecord.getOrigin())) {
            	channelRecord.setOrigin("");
            }
            if(StringUtils.isEmpty(channelRecord.getChannelName())) {
            	channelRecord.setChannelName("");
            }
            BeanUtils.copyProperties(channelRecord, registerDto);
            
            JSONObject element = (JSONObject) JSONObject.toJSON(registerDto);
            for(int i=0; i<appIdArray.length; i++) {
				JSONArray channelsJsonArray = channelsJsonArrayMap.get(appIdArray[i]);
				if(channelRecord.getSelectApp().equals(appIdArray[i])) {
					channelsJsonArray.add(element);
				}
			}
        });
    }

    public List<ChannelRecord> getChannelRecordByChannelIdList(List<String> channelIdList) {
    	return channelRecordDao.getByChannelIdList(channelIdList);
    }
    
    public ResultVo deleteChannelRecord(ChannelIdListDto channelIdListDto) {
        hlsUnregister(channelIdListDto);
        channelRecordDao.delete(channelIdListDto.getIdList());
        return ResultUtil.returnSuccess();
    }

    public ResultVo hlsUnregister(ChannelIdListDto channelIdListDto) {
        List<ChannelRecord> channelTranscodeList = channelRecordDao.getListByIdList(channelIdListDto.getIdList());
        return hlsUnregister(channelTranscodeList);
    }
    
    public ResultVo hlsUnregister(List<ChannelRecord> channelTranscodeList) {
    	Set<String> appIdSet = new LinkedHashSet<String>();
		for(ChannelRecord channelRecord : channelTranscodeList) {
			appIdSet.add(channelRecord.getSelectApp());
		}
		String[] appIdArray = new String[appIdSet.size()];
		appIdSet.toArray(appIdArray);
		Map<String, JSONObject> channelsJsonObjectMap = new HashMap<String, JSONObject>();
		Map<String, JSONArray> channelsJsonArrayMap = new HashMap<String, JSONArray>();
		for(int k=0; k<appIdArray.length; k++) {
			JSONArray channelsJsonArray = new JSONArray();
			channelsJsonArrayMap.put(appIdArray[k], channelsJsonArray);
			JSONObject channelsJsonObject = new JSONObject();
			channelsJsonObject.put("channels", channelsJsonArray);
			channelsJsonObjectMap.put(appIdArray[k], channelsJsonObject);
		}
    	
        for(ChannelRecord channelRecord : channelTranscodeList) {
        	JSONObject channelJsonObject = new JSONObject();
        	channelJsonObject.put("channel_id", channelRecord.getChannelId());
        	for(int i=0; i<appIdArray.length; i++) {
				JSONArray channelsJsonArray = channelsJsonArrayMap.get(appIdArray[i]);
				if(appIdArray[i].equals(channelRecord.getSelectApp())) {
					channelsJsonArray.add(channelJsonObject);
				}
			}
        }
        
        String responseResult[] = new String[appIdArray.length];
		ExecutorService pool = Executors.newFixedThreadPool(appIdArray.length);
		for(int m=0; m<appIdArray.length; m++) {
			HeartBeatDto heartBeat = heartbeatService.getHeartBeat(appIdArray[m]);
			if(heartBeat == null) {
				responseResult[m] = null;
			}
			else {
				JSONObject channelsJsonObject = channelsJsonObjectMap.get(appIdArray[m]);
				StringBuilder url = new StringBuilder();
	            url.append(InterfaceConstant.HTTP);
	            url.append(heartBeat.getServerIp().get(0));
	            url.append(InterfaceConstant.COLON);
	            if(heartBeat.getModulePort() != null) {
	            	url.append(heartBeat.getModulePort());
	            }
	            else {
	            	url.append(InterfaceConstant.HLS_PORT);
	            }
	            url.append(InterfaceConstant.HLS_UNREGIST);
				Callable<String> callable = new SendRequestTask(url.toString(), channelsJsonObject);
				Future<String> future = pool.submit(callable);
				try {
					responseResult[m] = future.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		pool.shutdown();
        
		JSONArray failedResult = new JSONArray();
		for(int x=0; x<appIdArray.length; x++) {
			if(responseResult[x] == null || "".equals(responseResult[x])) {
				JSONArray channelsJsonArray = channelsJsonArrayMap.get(appIdArray[x]);
				for(int n=0; n<channelsJsonArray.size();n++) {
					JSONObject object = new JSONObject();
					JSONObject channelObject = channelsJsonArray.getJSONObject(n);
					object.put(channelObject.getString("channel_id"), 101);
					failedResult.add(object);
				}
			}
			else {
		        failedResult.addAll(ResultUtil.getFailedResult(responseResult[x], "channel_id"));
	            channelTranscodeList.forEach(channelTranscode -> {
	                ChannelRecord channelRecord = new ChannelRecord();
	                channelRecord.setId(channelTranscode.getId());
	                channelRecord.setIssueState(StateEnum.DEFAULT.getCode());
	                channelRecordDao.update(channelRecord);
	            });
			}
		}
        return failedResult.size() == 0 ? ResultUtil.returnSuccess() : ResultUtil.returnIssueFailed(failedResult.toString());
    }
    public ResultVo hlsRegister(ChannelIdListDto channelIdListDto) {
    	List<ChannelRecord> channelRecordList = channelRecordDao.getListByIdList(channelIdListDto.getIdList());
    	Set<String> appIdSet = new LinkedHashSet<String>();
		for(ChannelRecord channelRecord : channelRecordList) {
			appIdSet.add(channelRecord.getSelectApp());
		}
		String[] appIdArray = new String[appIdSet.size()];
		appIdSet.toArray(appIdArray);
		Map<String, JSONObject> channelsJsonObjectMap = new HashMap<String, JSONObject>();
		Map<String, JSONArray> channelsJsonArrayMap = new HashMap<String, JSONArray>();
		for(int k=0; k<appIdArray.length; k++) {
			JSONArray channelsJsonArray = new JSONArray();
			channelsJsonArrayMap.put(appIdArray[k], channelsJsonArray);
			JSONObject channelsJsonObject = new JSONObject();
			channelsJsonObject.put("channels", channelsJsonArray);
			channelsJsonObjectMap.put(appIdArray[k], channelsJsonObject);
		}
		
        handleRegisterParam(channelRecordList, channelsJsonArrayMap, appIdArray);
        String responseResult[] = new String[appIdArray.length];
		ExecutorService pool = Executors.newFixedThreadPool(appIdArray.length);
		for(int m=0; m<appIdArray.length; m++) {
			HeartBeatDto heartBeat = heartbeatService.getHeartBeat(appIdArray[m]);
			if(heartBeat == null) {
				responseResult[m] = null;
			}
			else {
				JSONObject channelsJsonObject = channelsJsonObjectMap.get(appIdArray[m]);
				StringBuilder url = new StringBuilder();
	            url.append(InterfaceConstant.HTTP);
	            url.append(heartBeat.getServerIp().get(0));
	            url.append(InterfaceConstant.COLON);
	            if(heartBeat.getModulePort() != null) {
	            	url.append(heartBeat.getModulePort());
	            }
	            else {
	            	url.append(InterfaceConstant.HLS_PORT);
	            }
	            url.append(InterfaceConstant.HLS_REGIST);
				Callable<String> callable = new SendRequestTask(url.toString(), channelsJsonObject);
				Future<String> future = pool.submit(callable);
				try {
					responseResult[m] = future.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		pool.shutdown();
        
		JSONArray failedResult = new JSONArray();
		for(int x=0; x<appIdArray.length; x++) {
			if(responseResult[x] == null || "".equals(responseResult[x])) {
				JSONArray channelsJsonArray = channelsJsonArrayMap.get(appIdArray[x]);
				for(int n=0; n<channelsJsonArray.size();n++) {
					JSONObject object = new JSONObject();
					JSONObject channelObject = channelsJsonArray.getJSONObject(n);
					object.put(channelObject.getString("channel_id"), 101);
					failedResult.add(object);
				}
			}
			else {
		        failedResult.addAll(ResultUtil.getFailedResult(responseResult[x], "channel_id"));
	            channelRecordList.forEach(channelTranscode -> {
	                ChannelRecord updateChannel = new ChannelRecord();
	                updateChannel.setId(channelTranscode.getId());
	                updateChannel.setIssueState(StateEnum.ISSUE.getCode());
	                updateChannel.setIssueTime(new Date());
	                channelRecordDao.update(updateChannel);
	            });
			}
		}
        return failedResult.size() == 0 ? ResultUtil.returnSuccess() : ResultUtil.returnIssueFailed(failedResult.toString());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVo importExcel(HttpServletRequest request, MultipartFile file) {
    	Class<ChannelRecord> channelRecord = ChannelRecord.class;
    	String local = String.valueOf(request.getSession().getAttribute("local"));
		if(Constant.EN_US.equals(local)) {
			DataFormatUtil.setExcelField(channelRecord, "channelId", "name", HlsConstant.en_id);
			DataFormatUtil.setExcelField(channelRecord, "channelName", "name", HlsConstant.en_name);
			DataFormatUtil.setExcelField(channelRecord, "sourceUrl", "name", HlsConstant.en_src);
			DataFormatUtil.setExcelField(channelRecord, "onDemand", "name", HlsConstant.en_onDemand);
			DataFormatUtil.setExcelField(channelRecord, "onDemand", "replace", HlsConstant.en_onDemandSelect);
			DataFormatUtil.setExcelField(channelRecord, "selectApp", "name", HlsConstant.en_selectApp);
			DataFormatUtil.setExcelField(channelRecord, "storeTime", "name", HlsConstant.en_storeTime);
			DataFormatUtil.setExcelField(channelRecord, "origin", "name", HlsConstant.en_origin);
			
			DataFormatUtil.setExcelField(channelRecord, "errorMsg", "name", HlsConstant.en_errorMsg);
			DataFormatUtil.setSizeField(channelRecord, "channelId", HlsConstant.en_channelIdSize);
			DataFormatUtil.setSizeField(channelRecord, "channelName", HlsConstant.en_channelNameSize);
			DataFormatUtil.setPatternField(channelRecord, "sourceUrl", HlsConstant.en_sourceUrlPattern);
			DataFormatUtil.setNotNullField(channelRecord, "onDemand", HlsConstant.en_onDemandNotNull);
			DataFormatUtil.setNotNullField(channelRecord, "selectApp", HlsConstant.en_selectAppNotNull);
			DataFormatUtil.setMinField(channelRecord, "storeTime", HlsConstant.en_storeTimeMin);
			DataFormatUtil.setMaxField(channelRecord, "storeTime", HlsConstant.en_storeTimeMax);
			DataFormatUtil.setSizeField(channelRecord, "origin", HlsConstant.en_originSize);
		}
		else if(Constant.ZH_CN.equals(local)) {
			DataFormatUtil.setExcelField(channelRecord, "channelId", "name", HlsConstant.zh_id);
			DataFormatUtil.setExcelField(channelRecord, "channelName", "name", HlsConstant.zh_name);
			DataFormatUtil.setExcelField(channelRecord, "sourceUrl", "name", HlsConstant.zh_src);
			DataFormatUtil.setExcelField(channelRecord, "onDemand", "name", HlsConstant.zh_onDemand);
			DataFormatUtil.setExcelField(channelRecord, "onDemand", "replace", HlsConstant.zh_onDemandSelect);
			DataFormatUtil.setExcelField(channelRecord, "selectApp", "name", HlsConstant.zh_selectApp);
			DataFormatUtil.setExcelField(channelRecord, "storeTime", "name", HlsConstant.zh_storeTime);
			DataFormatUtil.setExcelField(channelRecord, "origin", "name", HlsConstant.zh_origin);
			
			DataFormatUtil.setExcelField(channelRecord, "errorMsg", "name", HlsConstant.zh_errorMsg);
			DataFormatUtil.setSizeField(channelRecord, "channelId", HlsConstant.zh_channelIdSize);
			DataFormatUtil.setSizeField(channelRecord, "channelName", HlsConstant.zh_channelNameSize);
			DataFormatUtil.setPatternField(channelRecord, "sourceUrl", HlsConstant.zh_sourceUrlPattern);
			DataFormatUtil.setNotNullField(channelRecord, "onDemand", HlsConstant.zh_onDemandNotNull);
			DataFormatUtil.setNotNullField(channelRecord, "selectApp", HlsConstant.zh_selectAppNotNull);
			DataFormatUtil.setMinField(channelRecord, "storeTime", HlsConstant.zh_storeTimeMin);
			DataFormatUtil.setMaxField(channelRecord, "storeTime", HlsConstant.zh_storeTimeMax);
			DataFormatUtil.setSizeField(channelRecord, "origin", HlsConstant.zh_originSize);
		}
		
		List<ChannelRecord> newChannelRecords = new ArrayList<ChannelRecord>();
		List<ChannelRecord> existChannelRecords = new ArrayList<ChannelRecord>();
		JSONArray existChannelIds = new JSONArray();
        try {
            List<String> channelIds = channelRecordDao.selectAllChannelIds();
            ImportParams params = new ImportParams();
			params.setNeedVerfiy(true);
			params.setHeadRows(1);
			ExcelImportResult<ChannelRecord> importResult = ExcelUtil.importExcelByVerify(
					file, ChannelRecord.class, params);
			List<ChannelRecord> failList = importResult.getFailList();
			List<ChannelRecord> importList = importResult.getList();
			if(failList.size() > 0) {
				request.getSession().setAttribute("recordImportError", importResult);
				return ResultUtil.returnIssueFailed();
			}
            for(ChannelRecord channel : importList) {
            	if(channelIds.contains(channel.getChannelId())) {
            		ChannelRecord oldRecord = channelRecordDao.getByChannelId(channel.getChannelId());
            		channel.setIssueState(oldRecord.getIssueState());
            		existChannelIds.add(channel.getChannelId());
            		existChannelRecords.add(channel);
            	}
            	else {
            		newChannelRecords.add(channel);
            	}
            }
            if(existChannelRecords.size() == 0 && newChannelRecords.size() > 0) {
            	channelRecordDao.saveList(newChannelRecords);
            	return ResultUtil.returnSuccess();
            }
            else {
            	HttpSession session = request.getSession();
            	session.setAttribute("newChannelRecords", newChannelRecords);
            	session.setAttribute("existChannelRecords", existChannelRecords);
				return ResultUtil.returnExist(existChannelIds.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.returnFail();
        }
    }
    
    public int saveCoverChannelRecords(
    		List<ChannelRecord> newChannelRecords,
    		List<ChannelRecord> existChannelRecords) {
    	if(existChannelRecords != null && existChannelRecords.size() > 0) {
    		int modifyCount = 0;
    		int insertCount = 0;
    		for(ChannelRecord existChannelRecord : existChannelRecords) {
    			if(existChannelRecord.getIssueState() == StateEnum.ISSUE.getCode()) {
    				List<ChannelRecord> unregistRecord = new ArrayList<>();
    				unregistRecord.add(existChannelRecord);
    				ResultVo resultVo = hlsUnregister(unregistRecord);
    				if(resultVo.getCode() == ResultUtil.returnSuccess().getCode()) {
    					existChannelRecord.setIssueState(StateEnum.DEFAULT.getCode());
                	}
    			}
    			
    			channelRecordDao.updateByChannelId(existChannelRecord);
    			modifyCount = modifyCount + 1;
    		}
    		if(newChannelRecords != null && newChannelRecords.size() > 0) {
    			channelRecordDao.saveList(newChannelRecords);
    		}
    		return (modifyCount + insertCount);
    	}
    	else {
    		if(newChannelRecords != null && newChannelRecords.size() > 0) {
    			return channelRecordDao.saveList(newChannelRecords);
    		}
    		else {
    			return 0;
    		}
    	}
    }

    private void validConflict(ChannelRecord channelRecord) throws ConflictException {
        if (StringUtils.isEmpty(channelRecord.getSelectApp())) {
            return;
        }
        ChannelRecord old = channelRecordDao.getByChannelId(channelRecord.getChannelId());
        if (old != null && old.getSelectApp() != null && old.getSelectApp().equals(channelRecord.getSelectApp())) {
            throw new ConflictException();
        }
    }

    public void exportExcel(HttpServletRequest request, HttpServletResponse response, 
    		SearchDto searchDto) {
    	Class<ChannelRecord> channelRecord = ChannelRecord.class;
    	String local = String.valueOf(request.getSession().getAttribute("local"));
		String fileName = HlsConstant.en_fileName;
		if(Constant.EN_US.equals(local)) {
			DataFormatUtil.setExcelField(channelRecord, "channelId", "name", HlsConstant.en_id);
			DataFormatUtil.setExcelField(channelRecord, "channelName", "name", HlsConstant.en_name);
			DataFormatUtil.setExcelField(channelRecord, "sourceUrl", "name", HlsConstant.en_src);
			DataFormatUtil.setExcelField(channelRecord, "onDemand", "name", HlsConstant.en_onDemand);
			DataFormatUtil.setExcelField(channelRecord, "onDemand", "replace", HlsConstant.en_onDemandSelect);
			DataFormatUtil.setExcelField(channelRecord, "selectApp", "name", HlsConstant.en_selectApp);
			DataFormatUtil.setExcelField(channelRecord, "storeTime", "name", HlsConstant.en_storeTime);
			DataFormatUtil.setExcelField(channelRecord, "storedTime", "name", HlsConstant.en_storedTime);
			DataFormatUtil.setExcelField(channelRecord, "bitrate", "name", HlsConstant.en_bitrate);
			DataFormatUtil.setExcelField(channelRecord, "speed", "name", HlsConstant.en_speed);
			DataFormatUtil.setExcelField(channelRecord, "origin", "name", HlsConstant.en_origin);
		}
		else if(Constant.ZH_CN.equals(local)) {
			fileName = HlsConstant.zh_fileName;
			DataFormatUtil.setExcelField(channelRecord, "channelId", "name", HlsConstant.zh_id);
			DataFormatUtil.setExcelField(channelRecord, "channelName", "name", HlsConstant.zh_name);
			DataFormatUtil.setExcelField(channelRecord, "sourceUrl", "name", HlsConstant.zh_src);
			DataFormatUtil.setExcelField(channelRecord, "onDemand", "name", HlsConstant.zh_onDemand);
			DataFormatUtil.setExcelField(channelRecord, "onDemand", "replace", HlsConstant.zh_onDemandSelect);
			DataFormatUtil.setExcelField(channelRecord, "selectApp", "name", HlsConstant.zh_selectApp);
			DataFormatUtil.setExcelField(channelRecord, "storeTime", "name", HlsConstant.zh_storeTime);
			DataFormatUtil.setExcelField(channelRecord, "storedTime", "name", HlsConstant.zh_storedTime);
			DataFormatUtil.setExcelField(channelRecord, "bitrate", "name", HlsConstant.zh_bitrate);
			DataFormatUtil.setExcelField(channelRecord, "speed", "name", HlsConstant.zh_speed);
			DataFormatUtil.setExcelField(channelRecord, "origin", "name", HlsConstant.zh_origin);
		}
		
		String[] emptyName = {""};
		DataFormatUtil.setExcelField(channelRecord, "errorMsg", "name", emptyName);
    	
    	List<ChannelRecord> all = channelRecordDao.getAll(searchDto);
    	try {
            ExcelUtil.exportBigExcel(all, ChannelRecord.class, fileName, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ChannelRecord> syncRecord() {
        AppListVo heartBeat = heartbeatService.getApplist();
        List<HeartBeatDto> heartBeatDtoList = heartBeat.getAppList();
        String hls = "HLS";
//        boolean syncResult = false;
        List<ChannelRecord> channelRecords = new ArrayList<>();
        channelRecordDao.updateAllIssueState();
        for(HeartBeatDto heartBeatDto : heartBeatDtoList) {
            if (hls.equals(heartBeatDto.getModuleType())) {
                try {
                	StringBuilder url = new StringBuilder();
    	            url.append(InterfaceConstant.HTTP);
    	            url.append(heartBeatDto.getServerIp().get(0));
    	            url.append(InterfaceConstant.COLON);
    	            if(heartBeatDto.getModulePort() != null) {
    	            	url.append(heartBeatDto.getModulePort());
    	            }
    	            else {
    	            	url.append(InterfaceConstant.HLS_PORT);
    	            }
    	            url.append(InterfaceConstant.HLS_SYNC);
                    String responseString = RestUtil.doPost(url.toString(), "{}");
                    if (responseString != null && !"".equals(responseString)) {
                    	channelRecords = handleSync(responseString, heartBeatDto);
//                    	syncResult = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
//        if(syncResult) {
//        	return ResultUtil.returnSuccess();
//        }
//        else {
//        	return ResultUtil.returnFail();
//        }
        return channelRecords;
    }

    private List<ChannelRecord> handleSync(String responseString, HeartBeatDto heartBeatDto) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(responseString).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("channels");
        ObjectMapper objectMapper = new ObjectMapper();
        List<ChannelRecord> channelRecords = new ArrayList<>();
        jsonArray.forEach(jsonElement -> {
            try {
                JsonObject channel = jsonElement.getAsJsonObject();
                ChannelRecordReceiveDto recordRegisterDto = objectMapper.readValue(channel.toString(), ChannelRecordReceiveDto.class);
                ChannelRecord channelRecord = new ChannelRecord();
                BeanUtils.copyProperties(recordRegisterDto, channelRecord);
                channelRecord.setIssueState(StateEnum.ISSUE.getCode());
//                ChannelRecord old = channelRecordDao.getByChannelId(channelRecord.getChannelId());
//                if (old == null) {
//                    channelRecord.setSelectApp(heartBeatDto.getModuleId().toString());
//                    channelRecord.setIssueTime(new Date());
////                    channelRecordDao.save(channelRecord);
//                } else {
////                    channelRecordDao.updateByChannelId(channelRecord);
//                }
                channelRecords.add(channelRecord);
               
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return channelRecords;
    }
}
