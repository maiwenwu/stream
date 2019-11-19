function FactoryMgr()
{
    var self = this;
    self.init();
}

$(function() {
	var factory_mgr = new FactoryMgr();
})

FactoryMgr.prototype =
{
	constructor:FactoryMgr,
	init:function()
	{
		var self = this;
		
		self.common_mgr = new CommonMgr();
		self.HANDLE_FACTORY_RESET = 200;
		self.HANDLE_DATA_RESET = 201;
		self.HANDLE_STREAMS_RESET = 202;
		self.setIntervals = 0;
		self.type = 0;
		self.init_view();
		self.init_data();
		self.init_event();
		self.init_wait_modal();
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
		$('#reset_factory').click(function(){
			$('#cancel').html("No");
			$('#ok').html("Yes");
			self.type = 0;
			self.common_mgr.modalShow(self.HANDLE_FACTORY_RESET,"Info","Do you really want to reset？",true,true);
		})
		
		$('#reset_data').click(function(){
			$('#cancel').html("No");
			$('#ok').html("Yes");
			self.type = 1;
			self.common_mgr.modalShow(self.HANDLE_DATA_RESET,"Info","Do you really want to reset？",true,true);
		})
		
		$('#reset_streams').click(function(){
			$('#cancel').html("No");
			$('#ok').html("Yes");
			self.common_mgr.modalShow(self.HANDLE_STREAMS_RESET,"Info","Do you really want to reset？",true,true);
		})
		
	},
	init_wait_modal:function(){
		var self = this;
		$('#modal_wait').on('show.bs.modal', function(){
          var $this = $(this);
          var $modal_dialog = $this.find('.modal-dialog');
          // 关键代码，如没将modal设置为 block，则$modala_dialog.height() 为零
          $this.css('display', 'block');
          $modal_dialog.css({'margin-top': Math.max(0, ($(window).height() - $modal_dialog.height()) / 2) });
        });
		
		$(document.body).css({
			  "overflow-x":"auto",
			  "overflow-y":"auto"
			  });
	},
	save_net_work_info:function(){
		var self = this;
		var data = [{
			"ip":"192.168.1.12",
			"netmask":"255.255.255.0",
			"gateway":"192.168.1.1",
			"dns1":"8.8.8.8",
			"dns2":"8.8.8.8"
		},{
			"ip":"192.168.1.11",
			"netmask":"255.255.255.0",
			"gateway":"192.168.1.1",
			"dns1":"8.8.8.8",
			"dns2":"8.8.8.8"
		}]
		
		$.ajax({
			type : "post",
			url : "/os/saveNetWorkInfo?"+new Date().getTime(),
			contentType : "application/json",
			dataType : "json",
			data:JSON.stringify(data),
			success : function(data) {
				if (data.result == 1) {
					self.execNetplanApply();
				} else {
					$('#modal_wait').modal('toggle');
					setTimeout(() => {
						self.common_mgr.modalShow("","Info","Reset network failed!",true,false);
					}, 500);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {

			}
		})
	},
	execNetplanApply:function() {
		var self = this;
		$.ajax({
			type : "get",
			url : "/os/execNetplanApply?"+new Date().getTime(),
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				if (data.result == 1) {
					$('#modal_wait').modal('toggle');
					self.common_mgr.modalShow("", "info", "Successful!", true, false);
                } else {
                	$('#modal_wait').modal('toggle');
					setTimeout(() => {
						self.common_mgr.modalShow("","Info","Reset network failed!",true,false);
					}, 500);
                }
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {

			}
		})
	},
	save_ipmi_info:function() {
		var self = this;
		
		var data ={
			"ip":"192.168.1.10",
			"netmask":"255.255.255.0",
			"gateway":"192.168.1.1",
			"dns":"8.8.8.8"
		};
		$.ajax({
			type : "post",
			url : "/os/saveIpmiInfo?"+new Date().getTime(),
			dataType : "json",
			contentType : "application/json",
			data:JSON.stringify(data),
			success : function(data) {
				if (data.result == 1) {
					self.save_net_work_info();
				} else {
					setTimeout(() => {
						$('#modal_wait').modal('toggle');
						 self.common_mgr.modalShow("","Info","Reset ipmi failed!",true,false);
					}, 500);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {

			}
		})
	},
	resetFactory : function() {
		var self = this;
		$.ajax({
            url: "/configuration/resetFactory?"+new Date().getTime(),
            type: "get",
            success: function(result) {
            	if(result != null) {
            		self.setIntervals = setInterval(function() {
						self.getPCState();
					}, 1000);
            	}
            },
        });
	},
	resetStream : function() {
		var self = this;
		$.ajax({
            url: "/configuration/resetStreams?"+new Date().getTime(),
            type: "get",
            success: function(result) {
            	if (result != null) {
            		setTimeout(() => {
            			$('#modal_wait').modal('toggle');
            			self.common_mgr.modalShow("", "info", "Successful!", true, false);
					}, 500);
				}
            },
        });
	},
	getPCState : function() {
		var self = this;
		$.ajax({
			url : "/configuration/getPCState?"+new Date().getTime(),
			type : "get",
			success : function(data) {
				self.pc_state = JSON.parse(data.data);
				console.log(self.pc_state);
				if (self.pc_state.factory_default_status == 0) {
					clearTimeout(self.setIntervals);
					if (self.type == 0) {
						self.save_ipmi_info();
					} else if(self.type == 1) {
						$('#modal_wait').modal('toggle');
						self.common_mgr.modalShow("", "info", "Successful!", true, false);
					}
				} 
			}
		})
	},
	DialogInit:function()
	{
		var self = this;
	    options={
	        OkClick:function (handler) {
	        	if (self.HANDLE_FACTORY_RESET == handler) {
	        		$('#modal_wait').modal('toggle');
	        		self.resetFactory();
				}
	        	if (self.HANDLE_DATA_RESET == handler) {
	        		$('#modal_wait').modal('toggle');
	        		self.resetFactory();
				}
	        	if (self.HANDLE_STREAMS_RESET == handler) {
	        		$('#modal_wait').modal('toggle');
	        		self.resetStream();
				}
	        },
	        CancelClick:function (handler) {
	        }
	    };
	   self.common_mgr.initDialog(options);
	},
}