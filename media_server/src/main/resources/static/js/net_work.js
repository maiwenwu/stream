function NetWorkMgr()
{
	var self = this;
}

$(function() {
	var net_mgr = new NetWorkMgr(); 
	net_mgr.init();
});

NetWorkMgr.prototype =
{
	constructor:NetWorkMgr,
	init:function()
	{
		var self = this;
		self.common_mgr = new CommonMgr();
		
		self.HANDLE_SAVE_NET_WORK = 200;
		self.HANDLE_SAVE_IPMI = 201;
		
		self.init_view();
		self.init_data();
		self.init_event();
		self.init_update_net_work();
	},
	init_view:function()
	{
		var self = this;
		self.DialogInit();
	},
	init_data:function()
	{
		var self = this;
		self.get_net_info();
	},
	init_update_net_work:function(){
		var self = this;
		$("#modal_net_work").on('show.bs.modal', function(){
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
	init_event:function()
	{
		var self = this;
		
		$('#save').click(function() {
			$('#cancel').html("No");
			$('#ok').html("Yes");
			self.common_mgr.modalShow(self.HANDLE_SAVE_NET_WORKE,"Warning","Do you need to modify the network settings?",true,true);
		})
		
		$('#ipmi_save').click(function(){
			$('#cancel').html("No");
			$('#ok').html("Yes");
			self.common_mgr.modalShow(self.HANDLE_SAVE_IPMI,"Warning","Do you need to modify the network settings?",true,true);
		})
		
		
	},
	DialogInit:function()
	{
		var self = this;
	    options={
	        OkClick:function (handler) {
	        	if (handler == self.HANDLE_SAVE_NET_WORKE) {
	        		self.save_net_work_info();
				}
	        	if (handler == self.HANDLE_SAVE_IPMI) {
	        		$("#modal_net_work").modal('toggle');
	        		self.save_ipmi_info();
				}
	        },
	        CancelClick:function (handler) {
	        }
	    };
	   self.common_mgr.initDialog(options);
	},
	get_net_info:function()
	{
		var self = this;
		$.ajax({
			type : "get",
			url : "/os/getNetInfo?"+new Date().getTime(),
			dataType : "json",
			success : function(data) {
				self.setNmsInfo(data.nms_info);
				self.setDataInfo(data.data_info);
				self.setIpmiInfo(data.ipmi_info);
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {

			}
		})
	},
	
	setNmsInfo : function(nms_info) {
		$('#nms_ip').val(nms_info.ip);
		$('#nms_netmask').val(nms_info.netmask);
		$('#nms_gateway').val(nms_info.gateway);
		$('#nms_dns1').val(nms_info.dns1);
	},
	
	setDataInfo : function(data_info) {
		$('#lan1_ip').val(data_info.ip);
		$('#lan1_netmask').val(data_info.netmask);
		$('#lan1_gateway').val(data_info.gateway);
		$('#lan1_dns1').val(data_info.dns1);
	},
	
	setIpmiInfo:function(ipmi_info){
		var self = this;
		$('#ipmi_ip').val(ipmi_info.ip);
		$('#ipmi_netmask').val(ipmi_info.netmask);
		$('#ipmi_gateway').val(ipmi_info.gateway);
	},
	
	save_net_work_info:function()
	{
		var self = this;
		
		var nms_ip = $('#nms_ip').val();
		var nms_netmask = $('#nms_netmask').val();
		var nms_gateway = $('#nms_gateway').val();
		var nms_dns1 = $('#nms_dns1').val();
		var lan1_ip = $('#lan1_ip').val();
		var lan1_netmask = $('#lan1_netmask').val();
		var lan1_gateway = $('#lan1_gateway').val();
		var lan1_dns1 = $('#lan1_dns1').val();
		
		if(!self.IsIp(nms_ip))
        {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","Invalid nms ip !",true,false);
			}, 500);
            return ;
        }
		
		if(!self.checkNetMask(nms_netmask))
        {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","Invalid nms netmask !",true,false);
			}, 500);
            return ;
        }
		
		if(!self.IsIp(nms_gateway))
        {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","Invalid nms gateway !",true,false);
			}, 500);
            return ;
        }
		
		if(!self.IsIp(nms_dns1))
        {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","Invalid nms dns !",true,false);
			}, 500);
			
            return ;
        }
		
		if(!self.IsIp(lan1_ip))
        {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","Invalid data ip !",true,false);
			}, 500);
			
            return ;
        }
		
		if(!self.checkNetMask(lan1_netmask))
        {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","Invalid data netmask !",true,false);
			}, 500);
			
            return ;
        }
		
		if(!self.IsIp(lan1_gateway))
        {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","Invalid data gateway !",true,false);
			}, 500);
			
            return ;
        }
		
		if(!self.IsIp(lan1_dns1))
        {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","Invalid data dns !",true,false);
			}, 500);
			
            return ;
        }
		
		var data = [{
			"ip":$('#nms_ip').val(),
			"netmask":$('#nms_netmask').val(),
			"gateway":$('#nms_gateway').val(),
			"dns1":$('#nms_dns1').val(),
			"dns2":"8.8.8.8"
		},{
			"ip":$('#lan1_ip').val(),
			"netmask":$('#lan1_netmask').val(),
			"gateway":$('#lan1_gateway').val(),
			"dns1":$('#lan1_dns1').val(),
			"dns2":"8.8.8.8"
		}]
		
		$.ajax({
			type : "post",
			url : "/os/saveNetWorkInfo?"+new Date().getTime(),
			contentType : "application/json",
			dataType : "json",
			data:JSON.stringify(data),
			success : function(data) {
				self.execNetplanApply();
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
					setTimeout(() => {
						self.common_mgr.modalShow("","Warning","Successful!",true,false);
					}, 500);
				} else {
					setTimeout(() => {
						self.common_mgr.modalShow("","Warning","Failed!",true,false);
					}, 500);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
			}
		})
	},
	save_ipmi_info:function()
	{
		var self = this;
		
		var ipmi_ip = $('#ipmi_ip').val();
		var ipmi_netmask = $('#ipmi_netmask').val();
		var ipmi_gateway = $('#ipmi_gateway').val();
		
		if(!self.IsIp(ipmi_ip))
        {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","Invalid ipmi ip !",true,false);
			}, 500);
            return ;
        }
		
		if(!self.checkNetMask(ipmi_netmask))
        {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","Invalid ipmi netmask !",true,false);
			}, 500);
            return ;
        }
		
		if(!self.IsIp(ipmi_gateway))
        {
			setTimeout(() => {
				self.common_mgr.modalShow("","Warning","Invalid ipmi gateway !",true,false);
			}, 500);
            return ;
        }
		
		var data ={
			"ip":$('#ipmi_ip').val(),
			"netmask":$('#ipmi_netmask').val(),
			"gateway":$('#ipmi_gateway').val(),
			"dns":"8.8.8.8"
		};
		$.ajax({
			type : "post",
			url : "/os/saveIpmiInfo?"+new Date().getTime(),
//			dataType : "json",
			contentType : "application/json",
			data:JSON.stringify(data),
			success : function(data) {
				console.log("Ipmi data:" , data);
				$("#modal_net_work").modal('toggle');
				if (data.result == 1) {
					setTimeout(() => {
						self.common_mgr.modalShow("","Warning","Successful!",true,false);
					}, 500);
				} else {
					setTimeout(() => {
						self.common_mgr.modalShow("","Warning","Failed!",true,false);
					}, 500);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
			}
		})
	},
    IsIp:function (v)
    {
        var re=/^(?:(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))$/;
        if(re.test(v) == false)
        {
            return false;
        }
        var buf=v.split(".");
        if(buf[3]<1||buf[3]>254)
        {
            return false;
        }
        return true;
    },
    checkNetMask:function(netmask)
    {
        //子网掩码正则表达式
        var exp=/^(254|252|248|240|224|192|128|0)\.0\.0\.0|255\.(254|252|248|240|224|192|128|0)\.0\.0|255\.255\.(254|252|248|240|224|192|128|0)\.0|255\.255\.255\.(254|252|248|240|224|192|128|0)$/;
        if (exp.test(netmask) == false)
        {
            return false;
        }
        return true;
    },
}