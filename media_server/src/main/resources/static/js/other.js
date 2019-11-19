function OtherMgr()
{
    var self = this;
}

$(function() {
	var other_mgr = new OtherMgr();
	other_mgr.init();
})

OtherMgr.prototype =
{
	constructor:OtherMgr,
	init:function()
	{
		var self = this;
		
		$("body").keydown(function(e){
            var ev = window.event || e;
            var code = ev.keyCode || ev.which;
            if (code == 13) {
            	$("#login").focus();
            }
        });
		
		self.init_view();
		self.init_data();
		self.init_event();
	},
	init_view:function()
	{
		var self = this;
	},
	init_data:function()
	{
		var self = this;
		self.get_net_info();
		self.get_sys_info();
		self.get_sys_uptime_temp();
		self.get_version();
		setInterval(() => {
			self.get_sys_uptime_temp();
		}, 1000);
		
	},
	init_event:function()
	{
		var self = this;
	},
	get_net_info:function()
	{
		var self = this;
		$.ajax({
			type : "get",
			url : "/os/getNetInfo?"+new Date().getTime(),
			dataType : "json",
			success : function(data) {
				if (data != null) {
					$('#nms_ip').html(data.nms_info.ip);
					$('#nms_gateway').html(data.nms_info.gateway);
					$('#nms_netmask').html(data.nms_info.netmask);
					$('#data_ip').html(data.data_info.ip);
					$('#data_gateway').html(data.data_info.gateway);
					$('#data_netmask').html(data.nms_info.netmask);
					$('#ipmi_ip').val(data.ipmi_info.ip);
					$('#ipmi_gateway').val(data.ipmi_info.gateway);
					$('#ipmi_netmask').html(data.nms_info.netmask);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
			}
		})
	},
	
	get_sys_info:function()
	{
		var self = this;
		$.ajax({
			type : "get",
			url : "/os/getOsInfo?"+new Date().getTime(),
			dataType : "json",
			success : function(data) {
				if (data != null) {
					$('#system_name').html(data.system_name);
					$('#system_version').html(data.system_version);
					$('#cpu_num').html(data.system_cpu_num);
					$('#cpu_type').html(data.system_cpu_type);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {

			}
		})
	},
	get_sys_uptime_temp:function()
	{
		var self = this;
		$.ajax({
			type : "get",
			url : "/os/getOsUptime_Temp?"+new Date().getTime(),
			dataType : "json",
			success : function(data) {
				if (data != null) {
					$('#uptime').html(data.system_uptime);
					$('#temp').html(data.system_temp);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
			}
		})
	},
	get_version : function() {
		var self = this;
		$.ajax({
			type : "get",
			url : "/configuration/getVersion?"+new Date().getTime(),
			dataType : "json",
			success : function(data) {
				if (data != null) {
					$('#control_version').html(data.media_control_version);
					$('#server_version').html(data.media_server_version);
					$('#cmdline_version').html(data.media_cmdline_version);
					$('#frontend_version').html(data.media_frontend_version);
					$('#hls_version').html(data.media_hls_version);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
			}
		})
	}
}