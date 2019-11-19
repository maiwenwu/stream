var systemSettingsMgr;
$(function () {
    if (systemSettingsMgr == undefined)
    {
    	systemSettingsMgr = new SystemSettingsMgr();
    	systemSettingsMgr.init();
    }
});

function SystemSettingsMgr()
{
    var self = this;
}

SystemSettingsMgr.prototype={
	constructor: SystemSettingsMgr,
	init:function() {
		var self = this;
		
		self.common_mgr = new CommonMgr();
		
		self.userInfo = "";
		
		self.HANDLER_UPDATE_USERINFO = 300;
		self.HANDLER_UPDATE_DATEZONE = 100;
		self.zone_list = [];
		self.DialogInit();
		self.getUserInfo();
		self.initView();
		self.initData();
		
		self.initEvent();
		
	},
	initView:function(){
		$('.form_datetime').datetimepicker({
		    weekStart: 1,
		    todayBtn:  1,
			autoclose: 1,
			todayHighlight: 1,
			startView: 2,
			forceParse: 0,
		    showMeridian: 1,
		    endDate:new Date()
		});
	},
	initEvent:function() {
		
		var self = this;
		
		$('#update_password').click(function() {
			self.checkInfo();
		}) 
		
		$("#sync_gmt").click(function(){
			self.syncTime();
		})
		
		$("#save_time").click(function(){
			console.log("click")
			self.common_mgr.modalShow(self.HANDLER_UPDATE_DATEZONE,"Warning","System time will be updated,Do you want to continue?",true,true);
		})
		
		$('#time_zone_id').change(function() {
			var zone_index = $("#time_zone_id").val();
			
			var select_zone;
			for(var i = 0;i < self.zone_list.length;i++){
				if(self.zone_list[i].id == zone_index){
					select_zone = self.zone_list[i];
				}
			}
			if(select_zone.isDaylight == 1)
			{
				$("#day_light").css("display","block");
			}else{
				$("#day_light").css("display","none");
			}
			self.syncTime();
		})
	},
	
	DialogInit:function()
	{
		var self = this;
	    options={
	        OkClick:function (handler) {
	        	if (handler == self.HANDLER_UPDATE_USERINFO) {
	        		self.updateUser();
				}
	        	if(handler == self.HANDLER_UPDATE_DATEZONE){
	        		self.saveTimeZone();
	        	}
	        },
	        CancelClick:function (handler) {
	        }
	    };
	   self.common_mgr.initDialog(options);
	},
	checkInfo:function() {
		var self = this;
		var user_name = $('#user_name').val();
		var old_password = $('#old_password').val();
		var new_password = $('#new_password').val();
		var confirm_password = $('#confirm_password').val();
		
		if (old_password == "") {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","The old password cannot be null.",true,false);
			}, 500);
			return ;
		} 
		
		if (new_password == "") {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","The new password cannot be null.",true,false);
			}, 500);
			return ;
		} 
		
		if (confirm_password == "") {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","The confirm password cannot be null.",true,false);
			}, 500);
			return ;
		} 
		
		if (old_password != self.userInfo.password) {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","The old password is wrong!",true,false);
			}, 500);
			return ;
		}
		
		if (new_password.length < 6 || new_password.length > 30) {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","The password length must be between 6 and 30.",true,false);
			}, 500);
			return ;
		} 
		
		if (new_password != confirm_password) {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","The two passwords do not match!",true,false);
			}, 500);
			return ;
		}
		
		$('#cancel').html("No");
		$('#ok').html("Yes");
		self.common_mgr.modalShow(self.HANDLER_UPDATE_USERINFO,"Warning","Are you sure to change it?",true,true);
		
	},
	updateUser:function() {
		var self = this;
		var user_name = $('#user_name').val();
		var new_password = $('#new_password').val();
		
		var data = {
				"userName" : user_name,
				"password" : new_password
			}
		
		$.ajax({
			type : "post",
			url : "/user/updateUser?"+new Date().getTime(),
			contentType : "application/json",
			dataType : "json",
			data : JSON.stringify(data),
			sync : false,
			success : function(data) {
				console.log(data);
				setTimeout(() => {
					self.common_mgr.modalShow("","Info",data.msg,true,false);
				}, 500);
			}
		})
	},
	
	getUserInfo:function() {
		var self = this;
		$.ajax({
			type : "get",
			url : "/user/getUserInfo?"+new Date().getTime(),
			dataType : "json",
			success:function(data) {
				if (data.length > 0) {
					$('#user_name').val(data[0].userName);
					self.userInfo = data[0];
				}
			}
		})
	},
	initData:function(){
		var self = this;
		self.getTimeZoneList();
	},
	getTimeZoneList:function(){
		var self = this;
		$.ajax({
			type : "get",
			url : "/os/getTimeZoneList?"+new Date().getTime(),
			dataType : "json",
			success : function(data) {
				console.log(data);
				var default_zone;
				self.zone_list = data.zone_list;
				$("#time_zone_id").empty();
				for (var i = 0; i < data.zone_list.length; i++) {
					var object = data.zone_list[i];
					if(data.time_zone == object.id){
						default_zone = object;
					}
					var key = object.id;
					var value = "("+object.gmt_name+") "+object.zone_name;
					$("#time_zone_id").append(
							"<option value='" + key + "'>" + value + "</option>")
				}
				$("#time_zone_id").val(data.time_zone);
				if(default_zone.isDaylight == 1)
				{
					$("#day_light").css("display","block");
				}
				$("#defaul_time").val(data.date_time);
			}
		})
	},
	saveTimeZone:function(){
		var self = this;
		var time_zone = $("#time_zone_id").val();
		var date_time = $("#defaul_time").val();
		var data={
				"zone_index":time_zone,
				"date_time":date_time
		}
		$.ajax({
			type : "post",
			url : "/os/saveTimeZone?"+new Date().getTime(),
			dataType : "json",
			data : data,
			success : function(data) {
				if (data.result == 1) {
					setTimeout(() => {
						self.common_mgr.modalShow("","Info","Update time Success!",true,false);
					}, 500);
				}
			}
		})
	},
	syncTime:function(){
		var self = this;
		var time_zone = $("#time_zone_id").val();
		var data={
				"zone_index":time_zone
		}
		$.ajax({
			type : "post",
			url : "/os/getTimeByZoneId?"+new Date().getTime(),
			dataType : "json",
			data : data,
			success : function(data) {
				$("#defaul_time").val(data.date_time);
			}
		})
	}
}