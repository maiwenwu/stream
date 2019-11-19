package com.tech.mediaserver.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.config.Constant;
import com.tech.mediaserver.constant.HlsConstant;
import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.dto.ChannelIdListDto;
import com.tech.mediaserver.dto.SearchDto;
import com.tech.mediaserver.entity.ChannelRecord;
import com.tech.mediaserver.service.ChannelRecordService;
import com.tech.mediaserver.utils.ExcelUtil;
import com.tech.mediaserver.utils.ResultUtil;
import com.tech.mediaserver.vo.ResultVo;

import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;

/**
 * @author chudichen
 * @date 2018/10/16
 */
@RestController
@RequestMapping(value = "/cdn")
public class ChannelRecordApiController {

    @Autowired
    private ChannelRecordService channelRecordService;
    
//    @GetMapping("/record")
//    public PageVo<ChannelRecordDto> getChannelRecordPage(@RequestParam(value = "current", required = false) Integer current,
//                                                         @RequestParam(value = "pageSize", required = false) Integer pageSize,
//                                                         @RequestParam(value = "channel_id", required = false) String channelId,
//                                                         @RequestParam(value = "channel_name", required = false) String channelName,
//                                                         @RequestParam(value = "state", required = false) Integer state,
//                                                         @RequestParam(value = "issue_state", required = false) Integer issueState,
//                                                         @RequestParam(value = "app_id", required = false) String appId,
//                                                         @RequestParam(value = "on_demand", required = false) Integer onDemand) {
//        Pagination page = new Pagination();
//        page.setChannelId(channelId);
//        page.setChannelName(channelName);
//        page.setState(state);
//        page.setIssueState(issueState);
//        page.setAppId(appId);
//        page.setCurrent(1);
//        page.setPageSize(1000);
//        page.setOnDemand(onDemand);
//        return channelRecordService.getChannelRecordPage(page);
//    }
    
    @RequestMapping("/getRecord")
    public PageInfo<ChannelRecord> getRecord(Integer page,Integer size,String channel_id,Integer board_id,Integer module_id) {

        return channelRecordService.selectAllChannelRecord(page,size,channel_id,board_id,module_id);
    }
    
//    @GetMapping("/record")
//    public List<ChannelRecord> getChannelRecordPage() {
//    	PageBean pageBean = new PageBean();
//    	WebConstant.logger.info("pageBean:" + pageBean);
//    	return channelRecordService.getChannelRecordPage(pageBean);
//    }
    
//    @PostMapping("/record")
//    public ResultVo addChannelRecord(@RequestBody ChannelRecordDto channelRecordDto) {
//        return channelRecordService.save(channelRecordDto);
//    }

    @PostMapping("/record/remove")
    public ResultVo deleteChannelRecord(@RequestBody ChannelIdListDto channelIdListDto) {
        return channelRecordService.deleteChannelRecord(channelIdListDto);
    }

//    @PostMapping(value = "/record/issue")
//    public ResultVo issue (@RequestBody ChannelIdListDto channelIdListDto){
//        return channelRecordService.hlsRegister(channelIdListDto);
//    }

    @GetMapping("/record/sync")
    public List<ChannelRecord> syncRecord() {
    	
    	List<ChannelRecord> channelRecords = channelRecordService.syncRecord();
    	List<ChannelRecord> channelRecords2 = new ArrayList<>();
    	List<ChannelRecord> channelRecords3 = new ArrayList<>();
    	WebConstant.logger.info("channelRecords before:" + channelRecords);
    	for (int i = 0; i < channelRecords.size(); i++) {
			if (channelRecords.get(i).getChannelId().contains("_")) {
				channelRecords2.add(channelRecords.get(i));
			} else {
				channelRecords3.add(channelRecords.get(i));
			}
		}
    	
    	for (int i = 0; i < channelRecords2.size(); i++) {
			String channelId[] = channelRecords2.get(i).getChannelId().split("_");
			for (int j = 0; j < channelRecords.size(); j++) {
				if (channelId[0].equals(channelRecords.get(j).getChannelId())) {
					channelRecords2.get(i).setBitrate(channelRecords.get(j).getBitrate());
					channelRecords2.get(i).setState(channelRecords.get(j).getState());
				}
			}
		}
    	
    	for (int i = 0; i < channelRecords2.size(); i++) {
			channelRecords3.add(channelRecords2.get(i));
		}
    	WebConstant.logger.info("channelRecords after:" + channelRecords3);
        return channelRecords3;
    }

    @PostMapping(value = "/record/cancel")
    public ResultVo cancel (@RequestBody ChannelIdListDto channelIdListDto){
    	WebConstant.logger.info("channelIdListDto:" + channelIdListDto);
        return channelRecordService.hlsUnregister(channelIdListDto);
    }
    
    /**
	 * 下载EXCEL模板
	 */
	@GetMapping("/downloadRecordTemplate")
	public void downloadRecordTemplate(HttpServletRequest request, HttpServletResponse response) {
		String local = String.valueOf(request.getSession().getAttribute("local"));
		String fileName = Constant.EN_US.equals(local) ? 
				HlsConstant.en_template : HlsConstant.zh_template;
		try {
			ExcelUtil.downloadTemplate(fileName, response);
		} catch (Exception e) {
		}
	}

    @PostMapping("/record/import")
    public ResultVo importExcel(HttpServletRequest request, MultipartFile file) {
        return channelRecordService.importExcel(request, file);
    }
    
    /**
	 * 导入失败，返回错误信息的EXCEL
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/importRecordError")
	public void importRecordError(HttpServletRequest request, HttpServletResponse response) {
		ExcelImportResult<ChannelRecord> importResult = (ExcelImportResult<ChannelRecord>)
				request.getSession().getAttribute("recordImportError");
		if(importResult != null) {
			String local = String.valueOf(request.getSession().getAttribute("local"));
			String fileName = Constant.EN_US.equals(local) ? 
					HlsConstant.en_importErrorFile : HlsConstant.zh_importErrorFile;
			try {
				List<ChannelRecord> failList = importResult.getFailList();
				failList.addAll(importResult.getList());
				ExcelUtil.exportBigExcel(failList, ChannelRecord.class, fileName, response);
				request.getSession().removeAttribute("recordImportError");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 导入是否覆盖
	 */
	@PostMapping("/importRecordCover")
	public ResultVo importRecordCover(HttpServletRequest request, 
			@RequestBody Map<String, String> requestMap) {
		String cover = requestMap.get("cover");
		HttpSession session = request.getSession();
		if(Constant.UNCOVER.equals(cover)) {
			session.removeAttribute("newChannelRecords");
			session.removeAttribute("existChannelRecords");
			return ResultUtil.returnSuccess();
		}
		else if(Constant.COVER.equals(cover)) {
			@SuppressWarnings("unchecked")
			List<ChannelRecord> newChannelRecords = (List<ChannelRecord>)
					session.getAttribute("newChannelRecords");
			@SuppressWarnings("unchecked")
			List<ChannelRecord> existChannelRecords = (List<ChannelRecord>)
					session.getAttribute("existChannelRecords");
			if(newChannelRecords != null && existChannelRecords != null) {
				int saveCount = channelRecordService.saveCoverChannelRecords(newChannelRecords, existChannelRecords);
				session.removeAttribute("newChannelRecords");
				session.removeAttribute("existChannelRecords");
				if(saveCount > 0) {
					return ResultUtil.returnSuccess();
				}
			}
			else {
				return ResultUtil.returnIssueFailed();
			}
		}
		return ResultUtil.returnIssueFailed();
	}

    @GetMapping("/record/export")
    public void exportExcel(@RequestParam(value = "channel_id", required = false) String channelId,
                            @RequestParam(value = "channel_name", required = false) String channelName,
                            @RequestParam(value = "state", required = false) Integer state,
                            @RequestParam(value = "issue_state", required = false) Integer issueState,
                            @RequestParam(value = "app_id", required = false) String appId,
                            @RequestParam(value = "ids", required = false) List<Integer> ids,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        SearchDto searchDto = new SearchDto();
        searchDto.setChannelName(channelName);
        searchDto.setChannelId(channelId);
        searchDto.setState(state);
        searchDto.setIssueState(issueState);
        searchDto.setAppId(appId);
        searchDto.setIds(ids);
        channelRecordService.exportExcel(request, response, searchDto);
    }
}
