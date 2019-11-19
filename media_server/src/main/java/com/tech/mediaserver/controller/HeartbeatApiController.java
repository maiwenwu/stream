package com.tech.mediaserver.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tech.mediaserver.config.Constant;
import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.dto.HeartBeatDto;
import com.tech.mediaserver.service.HeartbeatService;
import com.tech.mediaserver.vo.PageVo;


/**
 * @author chudichen
 * @date 2018/9/19
 */
@RestController
@RequestMapping(value = "/cdn")
public class HeartbeatApiController {

    @Autowired
    private HeartbeatService heartbeatService;

    @PostMapping(value = "/heartbeat/get-all-by-type")
    public PageVo<HeartBeatDto> getAllByType(@RequestBody HeartBeatDto heartBeatDto) {
        return heartbeatService.getApplistByModuleType(heartBeatDto.getModuleType());
    }
    
    /**
	 * 切换语言（中/英）
	 */
	@PostMapping("/changeLocale")
	public void changeLocale(HttpServletRequest request, 
			@RequestBody Map<String, String> requestMap) {
		String local = requestMap.get("local");
		if(Constant.ZH_CN.equals(local)) {
			request.getSession().setAttribute("local", Constant.ZH_CN);
		}
		else if(Constant.EN_US.equals(local)) {
			request.getSession().setAttribute("local", Constant.EN_US);
		}
	}
    
}
