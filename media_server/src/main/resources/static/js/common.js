function CommonMgr()
{
   var self = this;
}

$(function() {
	var common_mgr = new CommonMgr();
	common_mgr.init();
})

CommonMgr.prototype =
{
	constructor:CommonMgr,
	init:function()
	{
		var self = this;
		self.LOGOUT = 10;
		self.handler = 0;
		$("body").keydown(function(e){
            var ev = window.event || e;
            var code = ev.keyCode || ev.which;
            if (code == 13) {
            	$("#ok").focus();
            }
        });
		self.init_view();
		self.init_data();
		self.init_event();
	},
	init_view:function()
	{
		var self = this;
		self.DialogInit();
	},
	init_data:function()
	{
		var self = this;
	},
	init_event:function()
	{
		var self = this;
		$("#btn_logout").click(function(){
			self.modalShow(self.LOGOUT,"Info","Do you really want to logout?",true,true);
		})
	},
	DialogInit:function()
	{
		var self = this;
	    options={
	        OkClick:function (handler) {
	            if(handler == self.LOGOUT)
	            {
	            	self.logout();
	            }
	        },
	        CancelClick:function (handler) {
	        }
	    };
	    self.initDialog(options);
	},
	logout:function() {
		var self = this;
		$.ajax({
			url : "/user/logoutUser?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			success : function(data) {
				if (data.result) {
					window.location.href = "/";
				} 
			}
		})
	},
	initDialog:function(options)
	{
		var self = this;
		$('#ok').click(function() {
			$('#public_modal').modal('hide');
			setTimeout(() => {
				$('#cancel').html("Cancel");
				$('#ok').html("OK");
			}, 300);
			options.OkClick(self.handler);
		})
		
		$('#cancel').click(function() {
			$('#public_modal').modal('hide');
			setTimeout(() => {
				$('#cancel').html("Cancel");
				$('#ok').html("OK");
			}, 300);
			options.CancelClick(self.handler);
		})
	},
	modalShow:function(handlerid,title,msg,isOk,isCancel){
		var self = this;
		self.handler = handlerid;
		$('#public_modal').modal('show');
			
		if (isOk) {
			$('#ok').show();
		} else {
			$('#ok').hide();
		}
		
		if (isCancel) {
			$('#cancel').show();
		} else {
			$('#cancel').hide();
		}
		
		if (!isOk && !isCancel && self.handler == "") {
			$('#logout').show();
			$('#cancel').show();
		} else {
			$('#logout').hide();
		}
		
		$('#ok').click(function() {
			$('#public_modal').modal('hide');
		})

		$('#cancel').click(function() {
			$('#public_modal').modal('hide');
		})
		
		
		$('#title').html(title);
		$('#content').html(msg);
	},
	initTableHeight:function()
	{
		var self =this;
		var height = window.screen.height * 0.55;
		$("#table").css({"height": height});
	},
};

/*
 * will delete 
 * */
//var handler = -2;
//var total = 0;
//
//function initDialog(options)
//{
//	$('#ok').click(function() {
//		$('#public_modal').modal('hide');
//		setTimeout(() => {
//			$('#cancel').html("Cancel");
//			$('#ok').html("OK");
//		}, 300);
//		options.OkClick(handler);
//	})
//	
//	$('#cancel').click(function() {
//		$('#public_modal').modal('hide');
//		setTimeout(() => {
//			$('#cancel').html("Cancel");
//			$('#ok').html("OK");
//		}, 300);
//		options.CancelClick(handler);
//	})
//}
//
//function modalShow(handlerid,title,msg,isOk,isCancel){
//	
//	handler = handlerid;
//	$('#public_modal').modal('show');
//		
//	if (isOk) {
//		$('#ok').show();
//	} else {
//		$('#ok').hide();
//	}
//	
//	if (isCancel) {
//		$('#cancel').show();
//	} else {
//		$('#cancel').hide();
//	}
//	
//	if (!isOk && !isCancel && handler == "") {
//		$('#logout').show();
//		$('#cancel').show();
//	} else {
//		$('#logout').hide();
//	}
//	
//	$('#ok').click(function() {
//		$('#public_modal').modal('hide');
//	})
//
//	$('#cancel').click(function() {
//		$('#public_modal').modal('hide');
//	})
//	
//	
//	$('#title').html(title);
//	$('#content').html(msg);
//}
//
//var get_hreat_info = 0;
//
//var state = '';
//function getHeartInfos() {
//	$.ajax({
//		type : "post",
//		url : "/search/getHeartInfo",
//		dataType : "json",
//		data : {
//			"board_id" : 0,
//			"module_id" : 0,
//			"type" : 0 //0->获取全部的心跳 1->获取单个心跳
//		},
//		success : function(data) {
//			var count = 0;
//			var running = 0;
//			var error = 0;
//			state = JSON.parse(data.data);
//			for (var i = 0; i < 5; i++) {
//				for (var j = 0; j < 6; j++) {
//					var state_info = state[count].server_status.stream_status;
//					for (var k = 0; k < state_info.length; k++) {
//						if (state_info[k].status == 0) {
//							running ++;
//						} else {
//							error ++;
//						}
//					}
//					count ++;
//				}
//			}
////			console.log("total:",total);
////			console.log("running:",running);
////			console.log("error",error);
//			$('#running').html(running);
////			$('#error').html(error);
//			if (total >= running) {
//				var stop = total-running;
////				console.log("stop",stop);
//				$('#stop').html(stop);
//			}
//		},
//		error : function(textStatus){
//			clearTimeout(get_hreat_info);
//		}
//	})
//}







