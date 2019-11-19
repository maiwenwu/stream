var rebootSettingsMgr;
$(function () {
    if (rebootSettingsMgr == undefined)
    {
    	rebootSettingsMgr = new RebootSettingsMgr();
    	rebootSettingsMgr.init();
    }
});


function RebootSettingsMgr()
{
    var self = this;
}

RebootSettingsMgr.prototype={
	constructor: RebootSettingsMgr,
	init:function() {
		var self = this;
		
		self.imme_reboot_system = 100;
		self.timer_reboot_system = 101;
		self.$every_day = $('#every_day');
		self.$every_week = $('#every_week');
		self.$once = $('#once');
		self.common_mgr = new CommonMgr();
		self.rebootInfo = [];
		self.enable = 0;
		
		self.DialogInit();
		self.initWidget();
		self.initData();
	},
	
	initWidget:function() {
		
		var self = this;
		
		$('#imme_reboot').click(function() {
			$('#cancel').html("No");
			$('#ok').html("Yes");
			self.common_mgr.modalShow(self.imme_reboot_system,"info","Are you sure to reboot the system?",true,true);
		});
		
		$('#timer_reboot').click(function() {
			self.setRebootParams();
		})
		
		$('#type').change(function() {
			self.showTimerRebootPart();
		})
		
//		$('#enable').change(function() {
//			if (this.checked) {
//				self.enable = 1;
//			} else {
//				self.enable = 0;
//			}
//			self.updateRebootInfo();
//		})
		
	},
	
	showTimerRebootPart:function() {
		
		var self = this;
		
		var type = $('#type option:selected').val();
		if (type === "0") {
			self.$every_day.show();
			self.$every_week.hide();
			self.$once.hide();
		} else if(type === "1") {
			self.$every_day.hide();
			self.$every_week.show();
			self.$once.hide();
		} else if(type === "2") {
			self.$every_day.hide();
			self.$every_week.hide();
			self.$once.show();
		} else if (type === "") {
			self.$every_day.hide();
			self.$every_week.hide();
			self.$once.hide();
		}
	},
	
	doImmeReboot:function() {
		var self = this;
		$.ajax({
			type : "get",
			url : "/configuration/immeReboot?"+new Date().getTime(),
			dataType : "json",
			success : function(data) {
				self.common_mgr.modalShow("","info","Reboot now,please wait.",false,false);
			}
		})
	},
	
	DialogInit:function () {
		var self = this;
	    options={
	        OkClick:function (handler) {
	            if (handler == self.imme_reboot_system)
	            {
	            	self.doImmeReboot();
	            }
	            if (handler == self.timer_reboot_system) {
	            	self.deleteRebootInfo();
	            	if ($('#type option:selected').val() === "") {
	            		setTimeout(() => {
	    					self.common_mgr.modalShow("","info","Successful!",true,false);
	    				}, 500);
	            		return ;
					} else {
						self.setRebootInfo();
					}
				}
	        },
	        CancelClick:function (handler) {
	        }
	    };
	    self.common_mgr.initDialog(options);
	},
	initData:function(){
		var self = this;
		self.getRebootInfo();
	},
	
	getRebootInfo:function() {
		var self = this;
		$.ajax({
			type : "get",
			url : "/os/getRebootInfo?"+new Date().getTime(),
			dataType : "json",
			async : false,
			success : function(data) {
				console.log("data:",data);
				self.setValues(data);
			}
		})
	},
	
	setValues:function(reboot_info) {
		var self = this;
		if (reboot_info.length != 0) {
			var timerType = reboot_info[0].type;
			$('#type').val(timerType);
			
			var weekDay = $("[name='week']");
			var onceDay = $("[name='once']");
			for (var i = 0; i < weekDay.length; i++) {
				weekDay[i].checked = false;
				onceDay[i].checked = false;
			}
			for (var i = 0; i < reboot_info.length; i++) {
				if (timerType == 0) {
					$('#day_input_h').val(reboot_info[i].hour);
					$('#day_input_m').val(reboot_info[i].minute);
					$('#week_input_h').val("");
					$('#week_input_m').val("");
					$('#once_input_h').val("");
					$('#once_input_m').val("");
				} else if (timerType == 1) {
					weekDay[reboot_info[i].weekDay].checked = true;
					$('#week_input_h').val(reboot_info[i].hour);
					$('#week_input_m').val(reboot_info[i].minute);
					$('#once_input_h').val("");
					$('#once_input_m').val("");
					$('#day_input_h').val("");
					$('#day_input_m').val("");
				} else if (timerType == 2) {
					onceDay[reboot_info[i].weekDay].checked = true;
					$('#once_input_h').val(reboot_info[i].hour);
					$('#once_input_m').val(reboot_info[i].minute);
					$('#day_input_h').val("");
					$('#day_input_m').val("");
					$('#week_input_h').val("");
					$('#week_input_m').val("");
				}
			}
			self.showTimerRebootPart();
		}
	},
	deleteRebootInfo:function() {
		var self = this;
		$.ajax({
			type : "get",
			url : "/os/deleteRebootInfo?"+new Date().getTime(),
			dataType : "json",
			async : false,
			success : function(data) {
			}
		})
	},
	setRebootParams:function() {
		var self = this;
		
		var type = $('#type option:selected').val();
		var timer_h = "";
		var timer_m = "";
		var week = [];
		if (type === "0") {
			timer_h = $('#day_input_h').val();
            timer_m = $('#day_input_m').val();
            week.push(0);
		} else if (type === "1") {
			var weekDay = $("[name='week']");
            for(var i = 0; i< weekDay.length; i++)
            {
                if(weekDay[i].checked == true)
                {
                    week.push(i);
                }
            }
			timer_h = $('#week_input_h').val();
            timer_m = $('#week_input_m').val();
		} else if (type === "2") {
			var onceDay = $("[name='once']");
            for(var i = 0; i< onceDay.length; i++)
            {
                if(onceDay[i].checked == true)
                {
                    week.push(i);
                }
            }
			timer_h = $('#once_input_h').val();
            timer_m = $('#once_input_m').val();
		} else if (type === "") {
			timer_h = 1;
            timer_m = 1;
            week.push(0);
		}
		
		if (timer_h == "") {
			setTimeout(() => {
				self.common_mgr.modalShow("","info","Please input hour.",true,false);
			}, 500);
			return ;
		}
		if (timer_h >= 24 || timer_h < 0 || !self.IsNumber(timer_h)) {
			setTimeout(() => {
				self.common_mgr.modalShow("","info","Unreasonable time.",true,false);
			}, 500);
			return ;
		}
		if (timer_m == "") {
			setTimeout(() => {
				self.common_mgr.modalShow("","info","Please input minute.",true,false);
			}, 500);
			return ;
		}
		if (timer_m >= 60 || timer_m < 0 || !self.IsNumber(timer_m)) {
			setTimeout(() => {
				self.common_mgr.modalShow("","info","Unreasonable time.",true,false);
			}, 500);
			return ;
		}
		if (week.length == 0) {
			setTimeout(() => {
				self.common_mgr.modalShow("","info","Please select days of the week.",true,false);
			}, 500);
			return ;
		} else {
			for (var i = 0; i < week.length; i++) {
				var obj = new Object();
				obj.enable = 1;
				obj.type = type;
				obj.weekDay = week[i];
				obj.hour = timer_h;
				obj.minute = timer_m;
				self.rebootInfo.push(obj);
			}
		}
		
		self.deleteRebootInfo();
    	if (type === "") {
    		setTimeout(() => {
				self.common_mgr.modalShow("","info","Successful!",true,false);
			}, 500);
    		return ;
		} else {
			self.setRebootInfo();
		}
		
//		$('#cancel').html("No");
//		$('#ok').html("Yes");
//		self.common_mgr.modalShow(self.timer_reboot_system,"info","Are you sure to set the settings?",true,true);
		
	},
	setRebootInfo:function() {
		var self = this;
		$.ajax({
			type : "post",
			url : "/os/addRebootInfo?"+new Date().getTime(),
			contentType : "application/json",
			dataType : "json",
			data:JSON.stringify(self.rebootInfo),
			success : function(data) {
				setTimeout(() => {
					self.common_mgr.modalShow("","info",data.msg,true,false);
				}, 500);
				self.getRebootInfo();
				self.rebootInfo = [];
			}
		})
	},
	
	updateRebootInfo:function() {
		var self = this;
		$.ajax({
			type : "post",
			url : "/os/updateRebootInfo?"+new Date().getTime(),
			contentType : "application/json",
			dataType : "json",
			data:{"enable" : self.enable},
			success : function(data) {
			}
		})
	},
	
	IsNumber:function(number) {
        var ret = /^[1-9]\d*$/;
        if (ret.test(number) == false) {
            return false;
        } else {
            return true;
        }
	},
}