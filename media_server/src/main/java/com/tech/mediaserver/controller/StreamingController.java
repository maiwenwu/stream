package com.tech.mediaserver.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.dto.ChannelIdListDto;
import com.tech.mediaserver.dto.ChannelRecordDto;
import com.tech.mediaserver.dto.HeartBeatDto;
import com.tech.mediaserver.entity.AudioPids;
import com.tech.mediaserver.entity.ChannelRecord;
import com.tech.mediaserver.entity.IpAddrInfo;
import com.tech.mediaserver.entity.LockSignal;
import com.tech.mediaserver.entity.OutputConfig;
import com.tech.mediaserver.entity.Programs;
import com.tech.mediaserver.entity.RespBean;
import com.tech.mediaserver.entity.Rf;
import com.tech.mediaserver.entity.Satllite;
import com.tech.mediaserver.entity.SubtitlePids;
import com.tech.mediaserver.entity.Transponder;
import com.tech.mediaserver.service.AudioPidsService;
import com.tech.mediaserver.service.ChannelRecordService;
import com.tech.mediaserver.service.HeartbeatService;
import com.tech.mediaserver.service.LockSignalService;
import com.tech.mediaserver.service.OutputConfigService;
import com.tech.mediaserver.service.ProgramsService;
import com.tech.mediaserver.service.RfService;
import com.tech.mediaserver.service.SatlliteService;
import com.tech.mediaserver.service.SubtitleService;
import com.tech.mediaserver.service.TpService;
import com.tech.mediaserver.utils.FilesUtils;
import com.tech.mediaserver.utils.FilesUtils.Port;
import com.tech.mediaserver.utils.FilesUtils.Ports;
import com.tech.mediaserver.utils.StreamSocket.MessageBean;
import com.tech.mediaserver.vo.PageVo;
import com.tech.mediaserver.utils.StreamSocketApi;

@RestController
@RequestMapping("/streaming")
public class StreamingController {
	private StreamSocketApi mStreamSocketApi = StreamSocketApi.getInstance();
	
	@Autowired
	private RfService rfService;

	@Autowired
	private ProgramsService proService;

	@Autowired
	private TpService tpService;

	@Autowired
	private SatlliteService satlliteService;

	@Autowired
	private OutputConfigService outputService;

	@Autowired
	private AudioPidsService audioService;

	@Autowired
	private SubtitleService subtitleService;
	
	@Autowired
	private LockSignalService lockSignalService;
	
	@Autowired
    private HeartbeatService heartbeatService;
	
    @Autowired
    private ChannelRecordService channelRecordService;
    
	@RequestMapping("/getSatWithPro")
	public List<Satllite> getSatWithPro(Integer board_id, Integer module_id) {
		List<Satllite> satllites = null;
		List<Integer> tpList = proService.getTpIdList(board_id, module_id);
		
		if (tpList.size() != 0) {
			List<Integer> satList = tpService.getSatIdByTpList(tpList);
			if (satList.size() != 0) {
				satllites = satlliteService.getSatBySatList(satList);
				return satllites;
			} else {
				return satllites;
			}
		} else {
			return satllites;
		}
	}
	
	@RequestMapping("/getSatById")
	public Satllite getSatById(Integer sat_id) {
		return satlliteService.getSatlliteBySatId(sat_id);
	}

	@RequestMapping("/getOutputById")
	public List<OutputConfig> getOutputById(Integer board_id, Integer module_id) {
		List<OutputConfig> outputConfigs = outputService.getOutputById(board_id, module_id);
		return outputConfigs;
	}
	
	@RequestMapping("/selectAllStreams")
	public PageInfo<OutputConfig> selectAllStreams(Integer board_id, Integer module_id, Integer page, Integer size) {
		PageInfo<OutputConfig> pageInfo = outputService.selectAllStreams(board_id, module_id, page, size);
		List<OutputConfig> outputConfigs = pageInfo.getList();
		pageInfo.setList(outputConfigs);
		return pageInfo;
	}

	@RequestMapping("/getTpListWithPro")
	public JSONObject getTpListWithPro(Integer sat_id, Integer board_id) {
		Satllite satllite = satlliteService.getSatlliteBySatId(sat_id);
		List<Transponder> tList = tpService.getTpListWithPro(sat_id,board_id);
		JSONObject sat_tp_info = new JSONObject();
		sat_tp_info.put("tp_info", tList);
		sat_tp_info.put("lnb_type", satllite.getLnbType());
		sat_tp_info.put("sat_name", satllite.getName());
		return sat_tp_info;
	}
	
	@RequestMapping("/getAllOutput")
	public RespBean getAllOutput() {
		JSONArray module_array = new JSONArray();
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 6; j++) {
				JSONObject data =new JSONObject();
				List<OutputConfig> configs = outputService.getOutputById(i, j);
				data.put("board_id", i);
				data.put("module_id", j);
				data.put("configs", configs);
				module_array.add(data);
			}
		}
		return RespBean.ok("", module_array);
	}
	
	@RequestMapping("/getAllOutputCount")
	public RespBean getAllOutputCount() {
		List<OutputConfig> outputConfigs = outputService.getAllOutPut();
		return RespBean.ok("", outputConfigs.size());
	}

	@RequestMapping("/getAudioByProId")
	public List<AudioPids> getAudioByProId(Integer pro_id, Integer board_id) {
		List<AudioPids> audioPids = audioService.getAudioByProId(pro_id, board_id);
		return audioPids;
	}

	@RequestMapping("/getSubByProId")
	public List<SubtitlePids> getSubByProId(Integer pro_id, Integer board_id) {
		List<SubtitlePids> subtitlePids = subtitleService.getSubtitleByProId(pro_id, board_id);
		return subtitlePids;
	}
	
	@RequestMapping("/addOutputConfig")
	public RespBean addOutputConfig(@RequestBody OutputConfig outputConfig) throws NumberFormatException, IOException {
		
		//1.check type and count
		int check_tp = checkStreamCountAndType(outputConfig);
		if(check_tp < 0) {
			return RespBean.ok("", check_tp);
		}
		//2.check pid
		int pid_count = getModulePids(outputConfig);
		if(pid_count > WebConstant.MAX_PIDS) {
			return RespBean.ok("", -3);
		}
		//3.check repeat channel id
		int channel_id = checkChannelId(outputConfig);
		if (channel_id < 0) {
			return RespBean.ok("", channel_id);
		}
	
		List<Ports> ports = FilesUtils.readPort();
		List<Port> unuseList = new ArrayList<Port>(); 
		int index = 0;
		for(int i = 0 ; i < ports.size() ; i++) {
			if (ports.get(i).getBoard() == outputConfig.getBoardId() && ports.get(i).getMoudle() == outputConfig.getModuleId()) {
				index = i;
				unuseList = ports.get(i).getUnuse_list();
				if (unuseList.size() != 0) {
					outputConfig.setOutPort(unuseList.get(0).getPort());
					unuseList.remove(0);
				} 
				break;
			}
		}
		
		List<IpAddrInfo> ipAddrInfos = initIpAddr();
			
		for (int j = 0; j < ipAddrInfos.size(); j++) {
			if (outputConfig.getOutMode() == 0) {
				if (ipAddrInfos.get(j).getBoardId() == outputConfig.getBoardId() && ipAddrInfos.get(j).getModuleId() == outputConfig.getModuleId()) {
					outputConfig.setOutIp(ipAddrInfos.get(j).getIpAddr());
				}
			}
		}
		
		Collections.sort(unuseList);
		ports.get(index).setUnuse_list(unuseList);
		FilesUtils.savePort(ports);
		Integer result = 0;
		outputConfig.setOutState(0);
		if (outputConfig.getHlsOutIp() == null) {
			outputConfig.setHlsOutIp("");
		}
		result = outputService.addOutputConfig(outputConfig);
		
		return RespBean.ok("", result);
	}
	
	private int checkStreamCountAndType(OutputConfig outputConfig) {
		int result = 0;
		List<OutputConfig> outputConfigs = outputService.getOutputById(outputConfig.getBoardId(), outputConfig.getModuleId());
		if(outputConfigs.size() >= WebConstant.MAZ_STREAMS) {
			return -2;
		}
		for(int i = 0;i<outputConfigs.size();i++) {
			if(outputConfigs.get(i).getType() == WebConstant.TYPE_TP) {
				return -1;
			}
		}
		return result;
	}
	
	private int checkChannelId(OutputConfig outputConfig) {
		List<OutputConfig> outputConfigs = outputService.getAllOutPut();
		int result = 0;
		for (int i = 0; i < outputConfigs.size(); i++) {
				if (outputConfig.getLableId().equals(outputConfigs.get(i).getLableId())) {
					result = -4;
					break;
			}
		}
		return result;
		
	}
	
	private int getModulePids(OutputConfig outputConfig) {
		List<OutputConfig> outputConfigs = outputService.getOutputById(outputConfig.getBoardId(), outputConfig.getModuleId());
		List<String> video_pid_list = new ArrayList<String>();
		List<String> audio_pid_list = new ArrayList<String>();
		List<String> subtitle_pid_list = new ArrayList<String>();
		List<String> set_pid_list = new ArrayList<String>();
		List<String> all_pid_list = new ArrayList<String>();
		if (outputConfigs.size() > 0) {
			//origin pid
			for(int i = 0;i < outputConfigs.size();i++ ) {
				if(outputConfigs.get(i).getType() == WebConstant.TYPE_PROGRAM) {
					video_pid_list.add(outputConfigs.get(i).getProgramId());
				}
				
				String audio_string = outputConfigs.get(i).getAudioPids();
				if(!audio_string.equals(""))
				{
					audio_pid_list.addAll(Arrays.asList(audio_string.split(",")));
				}
				
				String subtitle_string = outputConfigs.get(i).getSubtitlePids();
				if(!subtitle_string.equals(""))
				{
					subtitle_pid_list.addAll(Arrays.asList(subtitle_string.split(",")));
				}
				
				String set_string = outputConfigs.get(i).getSetPids();
				if(!set_string.equals(""))
				{
					set_pid_list.addAll(Arrays.asList(set_string.split(",")));
				}
			}
		}
		if(outputConfig.getType() == WebConstant.TYPE_PROGRAM) {//program
			//add pid
			video_pid_list.add(outputConfig.getProgramId());
		}
		String audio_string = outputConfig.getAudioPids();
		if(!audio_string.equals(""))
		{
			audio_pid_list.addAll(Arrays.asList(audio_string.split(",")));
		}
		String subtitle_string = outputConfig.getSubtitlePids();
		if(!subtitle_string.equals(""))
		{
			subtitle_pid_list.addAll(Arrays.asList(subtitle_string.split(",")));
		}
		
		String set_string = outputConfig.getSetPids();
		if(!set_string.equals(""))
		{
			set_pid_list.addAll(Arrays.asList(set_string.split(",")));
		}
		
		all_pid_list.addAll(video_pid_list);
		all_pid_list.addAll(audio_pid_list);
		all_pid_list.addAll(subtitle_pid_list);
		all_pid_list.addAll(set_pid_list);
		
		List<String> pids_list= new ArrayList<String>();
		for(int i = 0;i < all_pid_list.size(); i++) {
			if(!pids_list.contains(all_pid_list.get(i))) {
				pids_list.add(all_pid_list.get(i));
			}
		}
		return pids_list.size();
	}
	
	@RequestMapping("/getStreamByBoardId")
	public RespBean getStreamBySatId(Integer board_id) {
		
		List<OutputConfig> outputConfigs = outputService.getOutputByBoardId(board_id);
		
		if (outputConfigs.size() > 0) {
			return RespBean.ok("", false);
		} else {
			return RespBean.ok("", true);
		}
		
	}
	
	@RequestMapping("/getStreamByBoardIdAndModuleId")
	public RespBean getStreamByBoardIdAndModuleId(Integer board_id, Integer module_id) {
		
		boolean result = true;
		
		List<OutputConfig> outputConfigs = outputService.getOutputById(board_id, module_id);

		if (outputConfigs.size() > 0) {
			result = false;
		}
		
		return RespBean.ok("", result);
		
	}

	@RequestMapping("/getOutput")
	public OutputConfig getOutput(Integer id) {
		return outputService.getOutput(id);
	}
	
	@RequestMapping("/startStreaming")
	public RespBean startStreaming(@RequestBody Integer[] ids) {

		List<Integer> id_list = Arrays.asList(ids);
		List<OutputConfig> outputConfigs = outputService.getOutputByIdList(id_list);
		Rf rf = rfService.getRfByBoardId(outputConfigs.get(0).getBoardId());
		
		for (int i = 0; i < outputConfigs.size(); i++) {
			if (outputConfigs.get(i).getOutState() == 0) {
				
				OutputConfig outputConfig  = outputConfigs.get(i);
				
				Transponder transponder = tpService.getTpByTpId(outputConfig.getLockSignal().getTpId());
				Programs programs = proService.getProById(Integer.parseInt(outputConfig.getProgramId()), outputConfig.getBoardId());
				
				String channelName = null;
				String sourceUrl = null;
				
				outputConfig.setOutState(1);
				outputService.updateOutput(outputConfig);
				JSONObject streamObject = buildStreamingJsonString(outputConfig,rf);
				WebConstant.logger.error("start_stream_info:" + streamObject.toString());
				mStreamSocketApi.startStreaming(streamObject.toString());
				
				String hlsOutIp[] = outputConfig.getHlsOutIp().split(",");
				String hlsOutPort[] = outputConfig.getHlsOutPort().split(",");
				for (int j = 0 ; j < hlsOutIp.length ; j ++) {
					ChannelRecordDto channelRecordDto = new ChannelRecordDto();
					if (j == 0) {
						channelRecordDto.setChannelId(outputConfig.getLableId());
					} else {
						channelRecordDto.setChannelId(outputConfig.getLableId() + "_" + j);
					}
					
					if (outputConfig.getType() == 0) {
						channelName = programs.getServiceName();
					} else if (outputConfig.getType() == 1) {
						channelName = transponder.getFreq() + "/" + transponder.getSymbolRate() + "/" + WebConstant.ASTRUIPOL[transponder.getPolarization()];
					} else if (outputConfig.getType() == 2) {
						channelName = outputConfig.getSetPids();
					}
					
					if (outputConfig.getOutMode() == 0) {
						sourceUrl = "http://" + outputConfig.getOutIp() + ":" + outputConfig.getOutPort();
					} else if (outputConfig.getOutMode() == 1) {
						sourceUrl = "udp://" + outputConfig.getOutIp() + ":" + outputConfig.getOutPort();
					}
					
					PageVo<HeartBeatDto> pageVo = heartbeatService.getApplistByModuleType("HLS");
					String selectApp = "";
					if (pageVo.getList().size() > 0) {
						selectApp = pageVo.getList().get(0).getModuleId().toString();
					}
					channelRecordDto.setBoardId(outputConfig.getBoardId());
					channelRecordDto.setModuleId(outputConfig.getModuleId());
					channelRecordDto.setChannelName(channelName);
					channelRecordDto.setSourceUrl(sourceUrl + "/");
					channelRecordDto.setOnDemand(0);
					channelRecordDto.setStoreTime(0);
					channelRecordDto.setIssueState(0);
					channelRecordDto.setOrigin("");
					channelRecordDto.setSelectApp(selectApp);
					channelRecordDto.setHlsOutMode(outputConfig.getHlsOutMode());
					channelRecordDto.setHlsOutIp(hlsOutIp[j]);
					channelRecordDto.setHlsOutPort(Integer.valueOf(hlsOutPort[j]));
					
					channelRecordService.save(channelRecordDto);
				}
			}
		}
		return RespBean.ok("Successful!");
	}
	
	@RequestMapping("/getStreamInfo")
	public MessageBean getStreamInfo(String list) {
		
		MessageBean messageBean = mStreamSocketApi.getStreamInfo(list.toString());
		
		return messageBean;
		
	}
	
	@RequestMapping("/getModuleInfo")
	public MessageBean getModuleInfo(String list) {
		
		MessageBean messageBean = mStreamSocketApi.getModuleInfo(list.toString());
		
		return messageBean;
		
	}
	
	@RequestMapping("/stopStreaming")
	public RespBean stopStreaming(@RequestBody Integer[] ids) {

		List<Integer> id_list = Arrays.asList(ids);
		
		List<String> channelIdList = new ArrayList<>();
		List<String> idList = new ArrayList<>();
		List<OutputConfig> outputConfigs = outputService.getOutputByIdList(id_list);
		Rf rf = rfService.getRfByBoardId(outputConfigs.get(0).getBoardId());
		
		for (int i = 0; i < outputConfigs.size(); i++) {
			channelIdList.add(outputConfigs.get(i).getLableId());
			JSONObject root = new JSONObject();
			if (outputConfigs.get(i).getOutState() == 1) {
				outputConfigs.get(i).setOutState(0);
				outputService.updateOutput(outputConfigs.get(i));
				root = buildStreamingJsonString(outputConfigs.get(i), rf);
				WebConstant.logger.error("stop_stream_info:" + root.toString());
				mStreamSocketApi.stopAllStreaming(root.toString());
			}
		}
		
		List<ChannelRecord> channelRecords = channelRecordService.getChannelRecordByChannelIdList(channelIdList);
		
		for (int i = 0; i < channelRecords.size(); i++) {
			idList.add(channelRecords.get(i).getId().toString());
		}
		
		String[] idArray = new String[idList.size()];
		idList.toArray(idArray);
		
		if (idArray.length != 0) {
			ChannelIdListDto channelIdListDto = new ChannelIdListDto();
			channelIdListDto.setIdList(idArray);
			channelRecordService.deleteChannelRecord(channelIdListDto);
		}
		
		return RespBean.ok("Successful!");
		
	}
	
	@RequestMapping("/getSatlliteByTpId")
	public Satllite getSatlliteByTpId(Integer tp_id) {
		Transponder transponder = tpService.getTpByTpId(tp_id);
		Satllite satllite = satlliteService.getSatlliteBySatId(transponder.getSatId());
		
		return satllite;
	}
	
	public JSONObject buildStreamingJsonString(OutputConfig outputConfig, Rf rf) {

		JSONObject streamObject = new JSONObject();

		LockSignal lockSignal = lockSignalService.getLockSignalByBoardIdAndModuleId(outputConfig.getBoardId(), outputConfig.getModuleId());
		
		Transponder transponder = tpService.getTpByTpId(lockSignal.getTpId());
		Programs programs = proService.getProById(Integer.parseInt(outputConfig.getProgramId()), outputConfig.getBoardId());

		// output info
		streamObject.put("board_id", outputConfig.getBoardId());
		streamObject.put("module_id", outputConfig.getModuleId());
		streamObject.put("collect_type", outputConfig.getType());
		streamObject.put("out_mode", outputConfig.getOutMode());
		streamObject.put("ip", outputConfig.getOutIp());
		streamObject.put("port", outputConfig.getOutPort());
		streamObject.put("out_status", outputConfig.getOutState());
		streamObject.put("output_id", outputConfig.getId());

		JSONArray proArray = new JSONArray();
		JSONArray audioPidArray = new JSONArray();
		JSONArray SubtitlePidArray = new JSONArray();
		JSONArray setPidArray = new JSONArray();
		JSONObject pro_info = new JSONObject();

		// 0->spts 1->tp 2->select pids
		Integer type = outputConfig.getType();

		if (type == 0) {
			// audio pid
			
			String audioPids = outputConfig.getAudioPids();
			String audioArr[] = audioPids.split(",");
			if (audioPids != "" && !audioPids.equals("")) {
				for (int j = 0; j < audioArr.length; j++) {
					JSONObject audio_pids = new JSONObject();
					audio_pids.put("pid", audioArr[j]);
					audioPidArray.add(audio_pids);
				}
				pro_info.put("audio_pid", audioPidArray);
			}

			// subtitle pid
			String subtitlePids = outputConfig.getSubtitlePids();
			
			if (subtitlePids != null && !subtitlePids.equals("")) {
				String subtitleArr[] = subtitlePids.split(",");
				for (int j = 0; j < subtitleArr.length; j++) {
					JSONObject subtitle_pids = new JSONObject();
					subtitle_pids.put("pid", subtitleArr[j]);
					SubtitlePidArray.add(subtitle_pids);
				}
				pro_info.put("sub_pid", SubtitlePidArray);
			} 
			
			// channel info
			pro_info.put("service_id", programs.getServiceId());
			pro_info.put("video_pid", programs.getVidPid());
			pro_info.put("pcr_pid", programs.getPcrPid());
			pro_info.put("pmt_pid", programs.getPmtPid());
			pro_info.put("cas", programs.getCaType());
			pro_info.put("transport_stream_id", transponder.getTsId());
			pro_info.put("orginal", transponder.getOnId());
			
			
			proArray.add(pro_info);
			streamObject.put("channel_info", pro_info);

		} else if (type == 2) {
			String pids = outputConfig.getSetPids();
			String pidsArr[] = pids.split(",");
			for (int j = 0; j < pidsArr.length; j++) {
				JSONObject pidsObject = new JSONObject();
				pidsObject.put("pid", pidsArr[j]);
				setPidArray.add(pidsObject);
			}

			streamObject.put("ext_pid", setPidArray);

		}
		
		return streamObject;

	}

	public List<IpAddrInfo> initIpAddr(){
		List<IpAddrInfo> ipAddrInfos = new ArrayList<>();
		String ip_front = "11.12.13.";
		String ip_behind = "11.12.14.";
		int count_front = 0;
		int count_behind = 0;
		for(int i = 0 ; i < 3 ; i ++) {
			for (int j = 0 ; j < 6 ; j++) {
				IpAddrInfo ipAddrInfo = new IpAddrInfo();
				if (i == 2 && j > 2) {
					break;
				} else {
					ipAddrInfo.setBoardId(i);
					ipAddrInfo.setModuleId(j);
					ipAddrInfo.setIpAddr(ip_front + (121+count_front));
				}
				
				count_front ++;
				ipAddrInfos.add(ipAddrInfo);
			}	
		}
		for(int i = 2 ; i < 5 ; i ++) {
			for (int j = 0 ; j < 6 ; j++) {
				IpAddrInfo ipAddrInfo = new IpAddrInfo();
				if (i == 2 && j < 3) {
					continue;
				} else {
					ipAddrInfo.setBoardId(i);
					ipAddrInfo.setModuleId(j);
					ipAddrInfo.setIpAddr(ip_behind + (121+count_behind));
				}
				count_behind ++;
				ipAddrInfos.add(ipAddrInfo);
			}	
		}
		
		return ipAddrInfos;
	}
	
	@RequestMapping("getLocalIp")
	public String getLocalIP() {
		
//		String netCard = "enp0s31f6";
		String netCard = WebConstant.NET_INGERFACE;
		String ip = null;
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface inet = netInterfaces.nextElement();

				if (netCard.equals(inet.getName())) {
					Enumeration<InetAddress> net_list = inet.getInetAddresses();
					while (net_list.hasMoreElements()) {
						InetAddress net = net_list.nextElement();
						byte[] bs = net.getAddress();
						if (bs.length == 4) {
							String ips = net.getHostAddress();
							ip = ips;
						}
					}
				}
			}
        } catch (SocketException e) {
            e.printStackTrace();
        }
		return ip;
	}

	@RequestMapping("/getAllLockSignal")
	public List<LockSignal> getAllLockSignal() {
		List<LockSignal> lockSignals = lockSignalService.getAllLockSignal();
		return lockSignals;
	}
	
	@RequestMapping("/getLockSignal")
	public LockSignal getLockSignal(Integer board_id, Integer module_id) {
		LockSignal lockSignal = lockSignalService.getLockSignalByBoardIdAndModuleId(board_id, module_id);
		return lockSignal;
	}
	
	@RequestMapping("/updateLockSignal")
	public RespBean updateLockSignal(@RequestBody LockSignal lockSignal) {
		
		Integer result = lockSignalService.updateLockSignal(lockSignal);
		
		return RespBean.ok("Successful!", result);
	}
	
	@RequestMapping("/getOutputByBoardIdAndSatIdAndTpId")
	public List<OutputConfig> getOutputByBoardIdAndSatIdAndTpId(Integer board_id, Integer sat_id, Integer tp_id) {
		
		List<OutputConfig> outputConfigs = outputService.getOutputByBoardIdAndSatIdAndTpId(board_id, sat_id, tp_id);
		
		return outputConfigs;
	}
	
	@RequestMapping("/getAudioPidByProListAndBoard")
	public List<AudioPids> getAudioPidByProListAndBoard(Integer tp_id, Integer board_id) {
		List<Programs> programs = proService.getProgramsByTpId(tp_id,board_id);
		List<Integer> pro_id_list = new ArrayList<>();
		for (Programs program : programs) {
			pro_id_list.add(program.getId());
		}
		return audioService.getAudioByProList(pro_id_list, board_id);
	}
	
	@RequestMapping("/deleteStream")
	public RespBean deleteStream(@RequestBody Integer[] ids) throws NumberFormatException, IOException {
		List<Integer> id_list = Arrays.asList(ids);
		List<Integer> delete_port_list =new ArrayList<Integer>();
		List<OutputConfig> outputConfigs = outputService.getOutputByIdList(id_list);
		
		List<String> channelIdList = new ArrayList<>();
		List<String> idList = new ArrayList<>();
		
		for (int i = 0; i < outputConfigs.size(); i++) {
			delete_port_list.add(outputConfigs.get(i).getOutPort());
			
			channelIdList.add(outputConfigs.get(i).getLableId());
			
			JSONObject root = new JSONObject();
			if (outputConfigs.get(i).getOutState() == 1) {
				outputConfigs.get(i).setOutState(0);
				outputService.updateOutput(outputConfigs.get(i));
				Rf rf = rfService.getRfByBoardId(outputConfigs.get(i).getBoardId());
				root = buildStreamingJsonString(outputConfigs.get(i), rf);
				WebConstant.logger.error("stop stream:" + root.toString());
				mStreamSocketApi.stopAllStreaming(root.toString());
			}
		}
		List<ChannelRecord> channelRecords = channelRecordService.getChannelRecordByChannelIdList(channelIdList);
		for (int i = 0; i < channelRecords.size(); i++) {
			idList.add(channelRecords.get(i).getId().toString());
		}
		
		String[] idArray = new String[idList.size()];
		idList.toArray(idArray);
		
		List<Ports> ports = FilesUtils.readPort();
		int board_id = outputConfigs.get(0).getBoardId();
		int module_id = outputConfigs.get(0).getModuleId();
		List<Port> unuseList = new ArrayList<Port>();
		int index = 0; 
		for(int i = 0;i< ports.size();i++) {
			if(ports.get(i).getBoard() == board_id && ports.get(i).getMoudle() == module_id) {
				index = i;
				unuseList = ports.get(i).getUnuse_list();
				break;
			}
		}
		
		for(int i = 0;i< delete_port_list.size();i++) {
			Port port = new Port();
			port.setPort(delete_port_list.get(i));
			unuseList.add(port);
		}
		
		HashSet<Port> h = new HashSet<Port>(unuseList);  
		unuseList.clear();  
		unuseList.addAll(h); 
		Collections.sort(unuseList);
		
		ports.get(index).setUnuse_list(unuseList);
		FilesUtils.savePort(ports);
		
		Integer result = outputService.deleteOutputByIdList(id_list);
		if (idArray.length != 0) {
			ChannelIdListDto channelIdListDto = new ChannelIdListDto();
			channelIdListDto.setIdList(idArray);
			channelRecordService.deleteChannelRecord(channelIdListDto);
		}
		return RespBean.ok("Successful!",result);
	}
}
