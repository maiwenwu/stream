package com.tech.mediaserver.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.dto.ChannelIdListDto;
import com.tech.mediaserver.dto.ChannelRecordDto;
import com.tech.mediaserver.entity.AudioPids;
import com.tech.mediaserver.entity.ChannelRecord;
import com.tech.mediaserver.entity.LockSignal;
import com.tech.mediaserver.entity.OutPort;
import com.tech.mediaserver.entity.OutputConfig;
import com.tech.mediaserver.entity.Programs;
import com.tech.mediaserver.entity.Rf;
import com.tech.mediaserver.entity.Satllite;
import com.tech.mediaserver.entity.SubtitlePids;
import com.tech.mediaserver.entity.Transponder;
import com.tech.mediaserver.service.AudioPidsService;
import com.tech.mediaserver.service.ChannelRecordService;
import com.tech.mediaserver.service.ConfigurationService;
import com.tech.mediaserver.service.LockSignalService;
import com.tech.mediaserver.service.OutputConfigService;
import com.tech.mediaserver.service.ProgramsService;
import com.tech.mediaserver.service.RfService;
import com.tech.mediaserver.service.SatlliteService;
import com.tech.mediaserver.service.SubtitleService;
import com.tech.mediaserver.service.TpService;
import com.tech.mediaserver.utils.ExcelUtil;
import com.tech.mediaserver.utils.FilesUtils;
import com.tech.mediaserver.utils.StreamSocketApi;
import com.tech.mediaserver.utils.FilesUtils.Port;
import com.tech.mediaserver.utils.FilesUtils.Ports;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;

@Service
public class ConfigurationServiceImpl implements ConfigurationService{

	private StreamSocketApi mStreamSocketApi = StreamSocketApi.getInstance();
	
	@Autowired
	private SatlliteService satService;
	@Autowired 
	private TpService tpService;
	@Autowired
	private RfService rfService;
	@Autowired
	private OutputConfigService outputConfigService;
	@Autowired
	private ChannelRecordService channelRecordService;
	@Autowired
	private AudioPidsService audioPidsService;
	@Autowired
	private SubtitleService subtitleService;
	@Autowired
	private LockSignalService lockSignalService;
	@Autowired 
	private ProgramsService programsService;
	
	@Override
	public void exportExcel(HttpServletResponse response) {
		try {
			Workbook workbook = null;
			
			List<Satllite> satellites = satService.getAllSatllite();
			List<Rf> rfs = rfService.getAllRf();
			List<Transponder> transponders = tpService.getAllTp();
			List<OutputConfig> outputConfigs = outputConfigService.getAllOutPut();
			List<ChannelRecord> channelRecords = channelRecordService.getAllChannelRecord();
			List<AudioPids> audioPids = audioPidsService.getAllAudioPids();
			List<SubtitlePids> subtitlePids = subtitleService.getAllSubtitle();
			List<LockSignal> lockSignals = lockSignalService.getAllLockSignal();
			List<Programs> programs = programsService.getAllPrograms();
			
			for (Rf rf : rfs) {
				rf.setBoardId(rf.getBoardId() + 1);
			}
			
			for (OutputConfig outputConfig : outputConfigs) {
				outputConfig.setBoardId(outputConfig.getBoardId() + 1);
				outputConfig.setModuleId(outputConfig.getModuleId() + 1);
			}
			
			for (ChannelRecord channelRecord : channelRecords) {
				channelRecord.setBoardId(channelRecord.getBoardId() + 1);
				channelRecord.setModuleId(channelRecord.getModuleId() + 1);
			}
			
			for (AudioPids aPids : audioPids) {
				aPids.setBoardId(aPids.getBoardId() + 1);
				aPids.setModuleId(aPids.getModuleId() + 1);
			}
			
			for (SubtitlePids sPids : subtitlePids) {
				sPids.setBoardId(sPids.getBoardId() + 1);
				sPids.setModuleId(sPids.getModuleId() + 1);
			}
			
			for (LockSignal lockSignal : lockSignals) {
				lockSignal.setBoardId(lockSignal.getBoardId() + 1);
				lockSignal.setModuleId(lockSignal.getModuleId() + 1);
			}
			
			for (Programs program : programs) {
				program.setBoardId(program.getBoardId() + 1);
				program.setModuleId(program.getModuleId() + 1);
			}
			List<Ports> ports = FilesUtils.readPort();
			List<Port> unuseList = new ArrayList<Port>(); 
			List<OutPort> outPorts = new ArrayList<>();
			
			for(int i = 0 ; i < ports.size() ; i++) {
				unuseList = ports.get(i).getUnuse_list();
				Collections.sort(unuseList);
				for (int j = 0; j < unuseList.size(); j++) {
					OutPort outPort = new OutPort();
					outPort.setBoardId(ports.get(i).getBoard() + 1);
					outPort.setModuleId(ports.get(i).getMoudle() + 1);
					outPort.setPort(unuseList.get(j).getPort());
					outPorts.add(outPort);
				}
			}
			
			// 设置sheetName
			ExportParams satelliteParam = new ExportParams();
			satelliteParam.setSheetName("satellite");
			ExportParams rfParam = new ExportParams();
			rfParam.setSheetName("rf");
			ExportParams transponderParam = new ExportParams();
			transponderParam.setSheetName("transponder");
			ExportParams outputParam = new ExportParams();
			outputParam.setSheetName("outputConfig");
			ExportParams channelRecordParam = new ExportParams();
			channelRecordParam.setSheetName("channelRecord");
			ExportParams audioPidsParam = new ExportParams();
			audioPidsParam.setSheetName("audioPids");
			ExportParams subtitlePidsParam = new ExportParams();
			subtitlePidsParam.setSheetName("subtitlePids");
			ExportParams lockSignalParam = new ExportParams();
			lockSignalParam.setSheetName("lockSignal");
			ExportParams programsParam = new ExportParams();
			programsParam.setSheetName("programs");
			ExportParams outPortsParam = new ExportParams();
			outPortsParam.setSheetName("outPorts");
			
			Map<String, Object> satelliteMap = new HashMap<>();
			satelliteMap.put("title", satelliteParam);
			satelliteMap.put("entity", Satllite.class);
			satelliteMap.put("data", satellites);

			Map<String, Object> rfMap = new HashMap<>();
			rfMap.put("title", rfParam);
			rfMap.put("entity", Rf.class);
			rfMap.put("data", rfs);
			
			Map<String, Object> transponderMap = new HashMap<>();
			transponderMap.put("title", transponderParam);
			transponderMap.put("entity", Transponder.class);
			transponderMap.put("data", transponders);
			
			Map<String, Object> outputMap = new HashMap<>();
			outputMap.put("title", outputParam);
			outputMap.put("entity", OutputConfig.class);
			outputMap.put("data", outputConfigs);
			
			Map<String, Object> channelRecordMap = new HashMap<>();
			channelRecordMap.put("title", channelRecordParam);
			channelRecordMap.put("entity", ChannelRecord.class);
			channelRecordMap.put("data", channelRecords);
			
			Map<String, Object> audioPidsMap = new HashMap<>();
			audioPidsMap.put("title", audioPidsParam);
			audioPidsMap.put("entity", AudioPids.class);
			audioPidsMap.put("data", audioPids);
			
			Map<String, Object> subtitlePidsMap = new HashMap<>();
			subtitlePidsMap.put("title", subtitlePidsParam);
			subtitlePidsMap.put("entity", SubtitlePids.class);
			subtitlePidsMap.put("data", subtitlePids);
			
			Map<String, Object> lockSignalMap = new HashMap<>();
			lockSignalMap.put("title", lockSignalParam);
			lockSignalMap.put("entity", LockSignal.class);
			lockSignalMap.put("data", lockSignals);
			
			Map<String, Object> programsMap = new HashMap<>();
			programsMap.put("title", programsParam);
			programsMap.put("entity", Programs.class);
			programsMap.put("data", programs);

			Map<String, Object> outPortsMap = new HashMap<>();
			outPortsMap.put("title", outPortsParam);
			outPortsMap.put("entity", OutPort.class);
			outPortsMap.put("data", outPorts);
			
			List<Map<String, Object>> sheetsList = new ArrayList<>();

			sheetsList.add(satelliteMap);
			sheetsList.add(rfMap);
			sheetsList.add(transponderMap);
			sheetsList.add(outputMap);
			sheetsList.add(channelRecordMap);
			sheetsList.add(audioPidsMap);
			sheetsList.add(subtitlePidsMap);
			sheetsList.add(lockSignalMap);
			sheetsList.add(programsMap);
			sheetsList.add(outPortsMap);
			
			WebConstant.logger.info("channelRecords:" + channelRecords);
			workbook = ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF) ;
			// 当前日期，用于导出文件名称
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
	        String dateStr = "stream"+"-"+sdf.format(new Date());
			ExcelUtil.downLoadExcel(dateStr + ".xls", response, workbook);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int importExcel(MultipartFile file) {
		int result = 1;
		List<Satllite> sateliteList = null;
		List<Rf> rfList = null;
		List<Transponder> transponderList = null;
		List<OutputConfig> outputConfigList = null;
		List<ChannelRecord> channelRecordList = null;
		List<AudioPids> audioPidsList = null;
		List<SubtitlePids> subtitlePidsList = null;
		List<LockSignal> lockSignalsList = null;
		List<Programs> programsList = null;
		List<OutPort> outPortList = null;
		try {

			Workbook workbook = ExcelUtil.getWorkBook(file);
			ImportParams params = new ImportParams();

			for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
				// 第几个sheet页
				params.setStartSheetIndex(numSheet);

				// 验证数据
				params.setNeedVerfiy(true);
				
				switch (workbook.getSheetName(numSheet)) {
					case "satellite":
						ExcelImportResult<Satllite> satelliteResult = ExcelImportUtil.importExcelMore(file.getInputStream(),Satllite.class, params);
						sateliteList = satelliteResult.getList();
						break;
					case "rf":
						ExcelImportResult<Rf> rfResult = ExcelImportUtil.importExcelMore(file.getInputStream(),Rf.class, params);
						rfList = rfResult.getList();
						break;
					case "transponder":
						ExcelImportResult<Transponder> transponderResult = ExcelImportUtil.importExcelMore(file.getInputStream(),Transponder.class, params);
						transponderList = transponderResult.getList();
						break;
					case "outputConfig":
						ExcelImportResult<OutputConfig> outputResult = ExcelImportUtil.importExcelMore(file.getInputStream(),OutputConfig.class, params);
						outputConfigList = outputResult.getList();
						break;
					case "channelRecord":
						ExcelImportResult<ChannelRecord> channelRecordResult = ExcelImportUtil.importExcelMore(file.getInputStream(),ChannelRecord.class, params);
						channelRecordList = channelRecordResult.getList();
						break;
					case "audioPids":
						ExcelImportResult<AudioPids> audioPidsResult = ExcelImportUtil.importExcelMore(file.getInputStream(),AudioPids.class, params);
						audioPidsList = audioPidsResult.getList();
						break;
					case "subtitlePids":
						ExcelImportResult<SubtitlePids> subtitlePidsResult = ExcelImportUtil.importExcelMore(file.getInputStream(),SubtitlePids.class, params);
						subtitlePidsList = subtitlePidsResult.getList();
						break;
					case "lockSignal":
						ExcelImportResult<LockSignal> lockSignalResult = ExcelImportUtil.importExcelMore(file.getInputStream(),LockSignal.class, params);
						lockSignalsList = lockSignalResult.getList();
						break;
					case "programs":
						ExcelImportResult<Programs> programsResult = ExcelImportUtil.importExcelMore(file.getInputStream(),Programs.class, params);
						programsList = programsResult.getList();
						break;
					case "outPorts":
						ExcelImportResult<OutPort> outPortsResult = ExcelImportUtil.importExcelMore(file.getInputStream(),OutPort.class, params);
						outPortList = outPortsResult.getList();
						break;
					default:
						result = 0;
						break;
				}
			}
			if (result == 1) {
				
				//add satellite
				if (sateliteList.size() > 0) {
					satService.deleteSatellite();
					satService.addSatelliteList(sateliteList);
				}
				
				//add rf
				if (rfList.size() > 0) {
					rfService.deleteRf();
					for (Rf rf : rfList) {
						rf.setBoardId(rf.getBoardId() - 1);
					}
					rfService.addRf(rfList);
				}
				
				//add transponder
				if (transponderList.size() > 0) {
					tpService.deleteTp();
					for (Transponder transponder : transponderList) {
						if (transponder.getEmmPids() == null) {
							transponder.setEmmPids("");
						}
					}
					tpService.addTp(transponderList);	
				}
				
				//add output config
				if (outputConfigList.size() > 0) {
					JSONObject root = new JSONObject();
					root.put("type", 0);
					mStreamSocketApi.deleteAllStream(root.toString());
					outputConfigService.deleteAllOutPut();
					for (int i = 0; i < outputConfigList.size(); i++) {
						if (outputConfigList.get(i).getSubtitlePids() == null) {
							outputConfigList.get(i).setSubtitlePids("");
						}
						if (outputConfigList.get(i).getSetPids() == null) {
							outputConfigList.get(i).setSetPids("");
						}
						if (outputConfigList.get(i).getHlsOutIp() == null) {
							outputConfigList.get(i).setHlsOutIp("");
						}
						outputConfigList.get(i).setBoardId(outputConfigList.get(i).getBoardId() - 1);
						outputConfigList.get(i).setModuleId(outputConfigList.get(i).getModuleId() - 1);
					}
					outputConfigService.addOutput(outputConfigList);
				}
				
				//add channel record
				if (channelRecordList.size() > 0) {
					channelRecordService.deleteChannelRecord();
					File f = new File(WebConstant.HLS_DB_PATH);
					FilesUtils.deleteFile(f);
					
					for (int i = 0; i < channelRecordList.size(); i++) {
						ChannelRecordDto channelRecordDto = new ChannelRecordDto();
						channelRecordDto.setChannelId(channelRecordList.get(i).getChannelId());
						channelRecordDto.setBoardId(channelRecordList.get(i).getBoardId() - 1);
						channelRecordDto.setModuleId(channelRecordList.get(i).getModuleId() - 1);
						channelRecordDto.setChannelName(channelRecordList.get(i).getChannelName());
						channelRecordDto.setSourceUrl(channelRecordList.get(i).getSourceUrl());
						channelRecordDto.setOnDemand(0);
						channelRecordDto.setStoreTime(0);
						channelRecordDto.setIssueState(0);
						channelRecordDto.setOrigin("");
						channelRecordDto.setSelectApp(channelRecordList.get(i).getSelectApp());
						channelRecordDto.setHlsOutMode(channelRecordList.get(i).getHlsOutMode());
						if (channelRecordList.get(i).getHlsOutIp() == null) {
							channelRecordDto.setHlsOutIp("");
						} else {
							channelRecordDto.setHlsOutIp(channelRecordList.get(i).getHlsOutIp());
						}
						channelRecordDto.setHlsOutPort(channelRecordList.get(i).getHlsOutPort());
						channelRecordService.save(channelRecordDto);
					}
					
				}
				
				//add audioPids
				if (audioPidsList.size() > 0) {
					audioPidsService.deleteAudioPids();
					for (AudioPids aPids : audioPidsList) {
						aPids.setBoardId(aPids.getBoardId() - 1);
						aPids.setModuleId(aPids.getModuleId() - 1);
					}
					audioPidsService.addAudioPids(audioPidsList);
				}
				
				//add subtitlePids
				if (subtitlePidsList.size() > 0) {
					subtitleService.deleteSubtitle();
					for (SubtitlePids sPids : subtitlePidsList) {
						sPids.setBoardId(sPids.getBoardId() - 1);
						sPids.setModuleId(sPids.getModuleId() - 1);
					}
					subtitleService.addSubtitle(subtitlePidsList);	
				}
				
				//add lockSignal
				if (lockSignalsList.size() > 0) {
					lockSignalService.deleteLockSignal();
					for (LockSignal lockSignal : lockSignalsList) {
						lockSignal.setBoardId(lockSignal.getBoardId() - 1);
						lockSignal.setModuleId(lockSignal.getModuleId() - 1);
					}
					lockSignalService.addLockSignal(lockSignalsList);
				}
				
				//add programs 
				if (programsList.size() > 0) {
					programsService.deletePrograms();
					for (Programs program : programsList) {
						program.setBoardId(program.getBoardId() - 1);
						program.setModuleId(program.getModuleId() - 1);
						if (program.getEcmPids() == null) {
							program.setEcmPids("");
						}
					}
					programsService.addPrograms(programsList);
				}
				
				//add outPort
				if (outPortList.size() > 0) {
					List<Ports> ports = FilesUtils.readPort();
					List<Port> unuseList = new ArrayList<Port>();
					for (int i = 0; i < ports.size(); i++) {
						unuseList = new ArrayList<>();
						for (int j = 0; j < outPortList.size(); j++) {
							if (ports.get(i).getBoard() == (outPortList.get(j).getBoardId() -1) && ports.get(i).getMoudle() == (outPortList.get(j).getModuleId() - 1)) {
								Port port = new Port();
								port.setPort(outPortList.get(j).getPort());
								unuseList.add(port);
								Collections.sort(unuseList);
							}
						}
						HashSet<Port> h = new HashSet<Port>(unuseList);  
						unuseList.clear();  
						unuseList.addAll(h); 
						Collections.sort(unuseList);
						ports.get(i).setUnuse_list(unuseList);
					}
					FilesUtils.savePort(ports);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void deleteChannelRecord() {
		List<ChannelRecord> channelRecords = channelRecordService.getAllChannelRecord();
	       
        List<String> idList = new ArrayList<>();
        
        for (int i = 0; i <channelRecords.size(); i++) {
        	idList.add(channelRecords.get(i).getId().toString());
		}
        
        String[] idArray = new String[idList.size()];
		idList.toArray(idArray);
        
		if (idArray.length != 0) {
			ChannelIdListDto channelIdListDto = new ChannelIdListDto();
			channelIdListDto.setIdList(idArray);
			channelRecordService.deleteChannelRecord(channelIdListDto);
		}
	}

	@Override
	public boolean uploadFile(MultipartFile file) {
		if (file.isEmpty()) {
			return false;
		}

		// 获取文件名
		String fileName = file.getOriginalFilename();

		// 设置文件存储路径
		String filePath = "/opt/update/";
		String path = filePath + fileName;
		File dest = new File(path);

		// 检测是否存在目录
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();// 新建文件夹
		}
		try {
			file.transferTo(dest);
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public JSONObject readLog(Integer type, Integer file_pos, Integer log_level) {
		String path = getFilePath(type, log_level);
		File file = null;
		file = new File(path);
		if (file.exists()) {
			JSONObject root = new JSONObject();
			root = FilesUtils.readLog(file, file_pos);
			return root;
		} else {
			return null;
		}
	}

	@Override
	public Long getFileSize(Integer type, Integer log_level) {
		// TODO Auto-generated method stub
		String path = getFilePath(type, log_level);
		File file = null;
		file = new File(path); 
		if (file.exists()) {
			Long size = FilesUtils.getFileSize(file);
			return size;
		} else {
			return 0L;
		}
	}
	
	public String getFilePath(Integer type, Integer log_level) {
		
		String path = "";
		
		if (type == 0) {
			if (log_level == 0) {
				path = "/opt/stream_module/log_manage/log_app_tsserver.debug";
			} else if (log_level == 1) {
				path = "/opt/stream_module/log_manage/log_app_tsserver.warn";
			} else if (log_level == 2) {
				path = "/opt/stream_module/log_manage/log_app_tsserver.error";
			} 
		} else if (type == 1) {
			if (log_level == 0) {
				path = "/opt/stream_server/media_control/log_manage/streaming_debug.log";
			} else if (log_level == 1) {
				path = "/opt/stream_server/media_control/log_manage/streaming_warning.log";
			} else if (log_level == 2) {
				path = "/opt/stream_server/media_control/log_manage/streaming_error.log";
			}
		}
		
		return path;
	}

	@Override
	public JSONArray getChangeLog() {
		// TODO Auto-generated method stub
		return FilesUtils.getChangeTag();
	}
}
