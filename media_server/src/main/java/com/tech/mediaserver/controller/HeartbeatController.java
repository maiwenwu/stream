package com.tech.mediaserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.dto.HeartBeatDto;
import com.tech.mediaserver.service.HeartbeatService;
import com.tech.mediaserver.vo.ResponseVo;

/**
 * @author chudichen
 * @date 2018/9/14
 */
@RestController
public class HeartbeatController {

    @Autowired
    private HeartbeatService heartbeatService;

    @PostMapping(value = "/cdn_heartbeat")
    public ResponseVo heartbeat(@RequestBody HeartBeatDto heartBeatVO) {
        heartbeatService.saveHeartbeat(heartBeatVO);
        return new ResponseVo();
    }
}
