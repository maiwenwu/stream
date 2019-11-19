package com.tech.mediaserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tech.mediaserver.entity.RespBean;
import com.tech.mediaserver.entity.Rf;
import com.tech.mediaserver.service.RfService;

@RestController
@RequestMapping("/rf")
public class RfController {

	@Autowired
	RfService rfService;
	
	@RequestMapping("/getAllRf")
	public List<Rf> getAllRf(){
		return rfService.getAllRf(); 
	} 
	
	@RequestMapping(value = "/getRfByBoardId", method = RequestMethod.POST)
	public Rf selectRfByBoardId(Integer board_id) {
		return rfService.getRfByBoardId(board_id);
	}
	
	@RequestMapping("/saveRfInfo")
	public RespBean saveRfInfo(@RequestBody Rf rf) {
		int result = rfService.saveRfInfo(rf);
		
		return RespBean.ok("Successful!", result);
	}
	
}
