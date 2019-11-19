package com.tech.mediaserver.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.entity.RespBean;
import com.tech.mediaserver.entity.User;
import com.tech.mediaserver.service.UserService;
import com.tech.mediaserver.utils.RedisUtil;

@RestController
@RequestMapping("/user")
public class UserController {

	public final static Logger logger =  LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	UserService userService;
	@Autowired
	private RedisUtil redisUtil;
	
	@RequestMapping("/loginUser")
	public RespBean loginUSer(@RequestBody User user, HttpServletRequest request){

		User loginUser = userService.loginUser(user);
		
		if (loginUser == null) {
			return RespBean.ok("", false);
		} else {
//			request.getSession().setAttribute("user", loginUser);
//			request.getSession().setMaxInactiveInterval(12*60*60);
			redisUtil.set("user", loginUser.getUserName());
			return RespBean.ok("", true);
		}
		
	}
	
	@RequestMapping("/logoutUser")
	public RespBean logoutUser(HttpServletRequest request) {
		
		WebConstant.logger.info("logoutUser");
		
		redisUtil.remove("user");
//		HttpSession session = request.getSession();
//		session.removeAttribute("user");
//		session.invalidate();
		
		return RespBean.ok("", true);
	}
	
	@RequestMapping("/getUserInfo")
	public List<User> getUserInfo() {
		return userService.getUserInfo();
	}
	
	@RequestMapping("/updateUser")
	public RespBean updateUser(@RequestBody User user) {
		Integer result = userService.updateUser(user);
		if (result == 1) {
			return RespBean.ok("Successful!", result);
		}
		return RespBean.error("Failed!", result);
	}
	
}
