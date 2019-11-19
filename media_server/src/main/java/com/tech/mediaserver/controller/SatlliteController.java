package com.tech.mediaserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.entity.RespBean;
import com.tech.mediaserver.entity.Satllite;
import com.tech.mediaserver.service.SatlliteService;

@RestController
@RequestMapping("/satllite")
public class SatlliteController {

	@Autowired
	private SatlliteService satlliteService;
	
	@RequestMapping("/selectAll")
	public PageInfo<Satllite> selectAll(Integer page,Integer size,String keyWord){
		return satlliteService.selectAll(page, size,keyWord);
	}
	
	@RequestMapping(value = "/addSatllite",method = RequestMethod.POST)
	public RespBean addSatllite(@RequestBody Satllite satllite) {
		Integer result = satlliteService.addSatllite(satllite);
		if (result == 1) {
			return RespBean.ok("Successful!",result);
		}
		return RespBean.error("Failed!",-1);
	}
	
	@RequestMapping("/updateSatllite")
	public RespBean updateSatllite(@RequestBody Satllite satllite) {
		Integer result = satlliteService.updateSatllite(satllite);
		if (result == 1) {
			return RespBean.ok("Successful!",result);
		}
		return RespBean.error("Failed!",-1);
	}
	
	@RequestMapping("/deleteSatllite")
	public RespBean deleteSatllite(@RequestBody Integer[] ids) {
		Integer result = satlliteService.deleteSatllite(ids);
		if (result == 1) {
			return RespBean.ok("Successful!",result);
		}
		return RespBean.error("Failed!",-1);
	}
	
	@RequestMapping("/checkSatToDelete")
	public RespBean checkSatToDelete(@RequestBody Integer[] ids) {
		return RespBean.ok("", satlliteService.checkSatToDelete(ids));
	}
	
}
