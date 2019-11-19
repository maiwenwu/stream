function StreamMgr()
{
	var self = this;
}

$(function() {

	var stream_mgr = new StreamMgr();
	stream_mgr.init();

})

StreamMgr.prototype =
{
	constructor:StreamMgr,
	init:function()
	{
		var self = this;
		self.common_mgr = new CommonMgr();
		self.aStrUipol = [ 'H', 'V', 'L', 'R' ];
		self.lnb_freq = [ '5150', '5700', '5750', '9750', '10600', '10750', '11250','11300', '11475', '5150/5750', 'Universal', '9750/10750', '9750/10700' ];
		self.type_arr = [ 'Program', 'TP', 'PID' ];
		self.protocol_hls_arr = [ 'http', 'unicast', 'multicast'];
		self.protocol_arr = [ 'udp' , 'http' ];
		self.PAT_PID = 0;
		self.CAT_PID = 1;
		self.NIT_PID = 16;
		self.SDT_PID = 17;
		self.EIT_PID = 18;
		self.TDT_PID = 20;
		self.module_info = '';
		self.output_info = '';
		self.output_list = '';
		self.output_id = '';
		self.satllite_info_by_sat = '';
		self.lock_info = '';
		self.pid_list= new Array();
		self.tp_list_with_pro = '';
		self.refresh_get_stream_info = 0;
		self.refresh_get_search_info = "";
		self.lock_status = '';
		self.sat_has_program = '';
		self.singal_output_info = '';
		self.getStreamArray = [];
		self.all_lock_signal = [];
		self.module_array = [];
		self.refresh_module_info = 0;
		self.audioSelect = '';
		self.subtitleSelect = '';
		self.teletextSelect = '';
		self.pidsListSelect = '';
		self.add_output_info = '';
		self.refresh_stream_count = 0;
		self.localIp = "";
		self.pro_info = "";
		self.tp_info = "";
		self.audio_pid_info = "";
		
		self.init_view();
		self.init_data();
		self.init_event();
	},
	init_view:function()
	{
		var self = this;
		$('#board_id').val(localStorage.getItem('board_id'));
		$('#module_id').val(localStorage.getItem('module_id'));
		
		$(".progressbar-text").width(560);
		self.initTableHeight();
		self.createStreamListOption();
		self.DialogInit();
		self.getStreamArrays();
	},
	DialogInit:function()
	{
		var self =this;
	    options={
	        OkClick:function (handler) {
	        },
	        CancelClick:function (handler) {
	        }
	    };
	    self.common_mgr.initDialog(options);
	},
	init_data:function()
	{
		var self = this;
		self.getLocalIp();
		self.getAllLockSignal();
		self.getAllOutput();
		self.getAllOutputCount();
		self.init_module_array();
		self.getModuleInfo();
		self.refresh_module_info = setInterval(() => {
			self.getModuleInfo();
		}, 1000);
		
		// self.get_stream_info();
		
		self.getAllStreamInfo();
		self.refresh_stream_count = setInterval(() => {
			self.getAllStreamInfo();
		}, 5000);
	},
	init_event:function()
	{
		var self = this;
		self.audioSelect = $('#audio').select2({
		    placeholder: '',
		    allowClear: true
		});
		
		self.subtitleSelect = $('#subtitle').select2({
		    placeholder: '',
		    allowClear: true
		});
		
		self.teletextSelect = $('#teletext').select2({
		    placeholder: '',
		    allowClear: true
		});
		
		self.pidsListSelect = $('#pids_list').select2({
		    placeholder: '',
		    allowClear: true
		});
		
		self.widgetEvent();
	},
	getAllStreamInfo :function(){
		var self = this;
		var all_module_array = [];
		var running_count = 0;
		var error_count = 0;
		var stop_count = 0;
		for (var i = 0; i < 5; i++) {
			for (var j = 0; j < 6; j++) {
				var obj=new Object();
				obj.board_id = i;
				obj.module_id = j;
				all_module_array.push(obj);
			}
		}
		$.ajax({
			type : "post",
			url : "/streaming/getStreamInfo?"+new Date().getTime(),
			dataType : "json",
			data : {
				"list" : JSON.stringify(all_module_array)
			},
			success : function(data) {
				if (data != null) {
					var stream_info = JSON.parse(data.data).stream_list;
					for(var i = 0;i < stream_info.length;i++)
					{
						var stream_status = stream_info[i].stream_status;
						for(var j = 0;j<stream_status.length;j++){
							if(stream_status[j].status == 0)
							{
								running_count++;
							}
							else
							{
								error_count++;
							}
						}
					}
					var total_count = $('#total').html();
					if (total_count == 0) {
						$('#running').html(running_count);
						$('#stop').html(0);
						$('#error').html(0);
					} else if (total_count < running_count) {
						$('#running').html(running_count);
					} else {
						stop_count = total_count - running_count - error_count;
						$('#running').html(running_count);
						$('#stop').html(stop_count);
						$('#error').html(error_count);
					}
					
				} 
			},
			error : function(status){
				clearTimeout(self.refresh_stream_count);
			}
		})
	},
	createStreamListOption:function() {
		var self = this;
		$('#stream_list').empty();
		var list = '';
		var count = 1;
		for (var i = 0; i < 5; i++) {
			for (var j = 0; j < 6; j++) {
				list += "<tr id=\"tr" +i+j+ "\" class=\"info\">";
				list += "<td width=\"10%\" id=\"board_id\">" + (i + 1) + "</td>";
				list += "<td width=\"10%\" id=\"module_id\">" + (j + 1) + "</td>";
				list += "<td width=\"10%\" class=\"status\"><div class=\"all_status\" style=\"width:5px;height:5px;border-radius:50%;margin:auto\"></div></td>";
				list += "<td width=\"10%\" class=\"temp\">N/A</td>";
				list += "<td width=\"10%\" class=\"locked\">no</td>";
				list += "<td width=\"10%\" class=\"count1\"><span class=\"running\">0</span><span>/</span><span class=\"total\">0</span></td>";
				list += "<td width=\"20%\" class=\"total_bitrate\">0 Mb/s</td>";
				list += "<td width=\"20%\" ><button id=\"lock_setting" + i+j + "\" ><i class=\"fa fa-cog\"  title = \"Lock\"></i></button>";
				list += "<button id=\"module_log" + i+j + "\" ><i class=\"fa fa-file-text-o\"  title = \"Module Log\"></i></button>";
				list += "<button id = \"add" + i+j + "\"><i class=\"fa fa-plus\" title=\"Add\"></i></button>";
				list += "<button><a href=\"/link\" id = \"link" + i+j + "\"><i class=\"fa fa-external-link\" title=\"Link\"></i></a></button></td></tr>";
				list += "<tr id=\"stream_info" + i+j +"\" class=\"stream_info\" style=\"display: none; background-color: #f8f8f8\">";
				list += "<td colspan=\"10\"><div class=\"div-inline\"><table ><tbody><tr >";
				list += "<td style=\"display:none\"><input type=\"checkbox\"/></td>";
				list += "<td ><button class = \"play\" id=\"total_play" + i+j + "\" ><i class=\"fa fa-play\"  title = \"Start\"></i></button> </td>";
				list += "<td ><button class = \"stop\" id=\"total_stop" + i+j + "\" ><i class=\"fa fa-stop\"  title = \"Stop\"></i></button> </td>";
				list += "<td ><button class = \"delete\" id=\"total_delete" + i+j + "\" ><i class=\"fa fa-trash-o\"  title = \"Delete\"></i></button> </td>";
				list += "</tr></tbody></table></div>";
				list +=	"<div class=\"div-inline\"><table ><tbody><tr >";
				list += "<td ><input type=\"checkbox\" id=\"check_all" + i+j + "\" /> </td>";
				list += "<td id=\"sat_name" + i+j +"\">Satellite:</td>";
				list += "<td></td><td></td>";
				list += "<td id=\"tp" + i+j +"\">TP:</td>";
				list += "<td></td><td></td>";
				list += "<td class=\"run_status\"></td>";
				list += "</tr></tbody></table></div>";
				list += "<div class=\"div-inline\" ><table ><tbody id=\"streaming_info" + i+j +"\" ></tbody></table></div></td></tr>";
				count ++ ;
			}
		}
		$('#stream_list').append(list);
	},
	initTableHeight:function(){
		var self = this;
		var height = window.screen.height * 0.5 ;
		$(".table-body").css({"height": height});
	},
	getLocalIp : function() {
		var self = this;
		
		$.ajax({
			type : "get",
			url : "/streaming/getLocalIp?"+new Date().getTime(),
			async : false,
			success : function(data) {
				self.localIp = data;
			}
		})
	},
	getAllOutput:function(){
		var self = this;
		var self = this;
		$.ajax({
			url : "/streaming/getAllOutput?"+new Date().getTime(),
			type : "get",
			dataType : "json",
			async : false,
			success : function(data) {
				for(var i= 0;i< data.result.length;i++)
				{
					var module_config = data.result[i];
					self.singal_output_info = module_config.configs;
					self.drawAddDialog(module_config.configs,module_config.board_id,module_config.module_id)
					self.drawStreamingInfo(module_config.board_id,module_config.module_id);
				}
			}
		})
	},
	drawAddDialog:function(module_config,board_id,module_id){
		var self = this;
		var data = module_config;
		self.setDefault(data);
		$("#tr"+board_id+module_id+" .total").html(data.length);
		if (data.length != 0) {
			var list = "";
			var list_2 = ""
			$('#output').show();
			$('#output_list').empty();
			$('#output_list_2').empty();
			for (var i = 0; i < data.length; i++) {
// var output = ((data[i].outMode == 0)?"http://":"udp://@") + data[i].outIp +
// ":" + data[i].outPort;
				var output = data[i].lableId;
				if(i%2 == 0)
				{
					list += "<div class=\"form-control\" id=\"out_put\" style=\"border: none;\">" + output + "</div>";
				}else
				{
					list_2 += "<div class=\"form-control\" id=\"out_put\" style=\"border: none;\">" + output + "</div>";
				}
			}
			$('#output_list').append(list);
			$('#output_list_2').append(list_2);
			var tv_scroll_height = $("#out_put_scroll_y")[0].scrollHeight;
			$("#out_put_scroll_y").scrollTop(tv_scroll_height);
		} 
		else {
			$('#output_list').empty();
			$('#output').hide();
		}
	},
	getAllOutputCount:function() {
		var self = this;
		$.ajax({
			url : "/streaming/getAllOutputCount?"+new Date().getTime(),
			type : "get",
			dataType : "json",
			success : function(data) {
				$('#total').html(data.result);
			}
		})
	},
	getAllLockSignal:function() {
		var self = this;
		$.ajax({
			type : "get",
			url : "/streaming/getAllLockSignal?"+new Date().getTime(),
			dataType : "json",
			async : false,
			success : function(data) {
				self.all_lock_signal = data;
			}
		})
	},
	init_module_array:function()
	{
		var self = this;
		for (var i = 0; i < 5; i++) {
			for (var j = 0; j < 6; j++) {
				var obj=new Object();
				obj.board_id = i;
				obj.module_id = j;
				self.module_array.push(obj);
			}
		}
	},
	getModuleInfo:function()
	{
		var self = this;
		$.ajax({
			type : "post",
			url : "/streaming/getModuleInfo?"+new Date().getTime(),
			dataType : "json",
			data : {
				"list" : JSON.stringify(self.module_array)
			},
			async : false,
			success : function(data) {
				if (data != null) {
					self.module_info = JSON.parse(data.data).module_list;
					self.setModuleInfo(self.module_info);
				}
			},
			error : function(status){
				clearTimeout(self.refresh_module_info);
			}
		})
	},
	get_stream_info:function()
	{
		var self = this;
		for (var i = 0; i < 5; i++) {
			for (var j = 0; j < 6; j++) {
				self.getOutputById(i,j);
				self.drawStreamingInfo(i,j);
			}
		}
	},
	widgetEvent:function(){
		var self = this;
		
		$("#save_pid").click(function() {
			$("#pids").val($("#pid_list").val());
			$('#showPidModal').modal('hide');
			$("#pid_list").val("");
		})
		
		$('#pro').change(function() {
			self.getAudioInfo();
			self.getVedioInfo();
			self.getSubTitleInfo();
		})
		
		$('#hls_protocol').change(function() {
			var hls_protocol = $('#hls_protocol option:selected').val();
			if (hls_protocol == 0) {
				$("#hls_ip").slideUp();
			} else {
				$("#hls_ip").slideDown();
			}
			$('#hls_out_ip').val("");
			$('#hls_out_port').val("");
		})
		
		$('#show_pid').click(function () {
			$(".show_pid").css({"height": $('.add_stream').outerHeight()});
			$('#showPidModal').modal('show');
			self.getAudioPidByProListAndBoard();
			$("#pid_list").val($("#pids").val());
			self.showPid();
		})
		
//		$('#add_pid').click(function () {
//			var pid = $('#pids').val();
//			var pid_arr = [];
//			
//			pid_arr = pid.split(",");
//			
//			if (pid == "") {
//				self.common_mgr.modalShow("","Info","The PID cannot be null!",true, false);
//				return ;
//			}
//			
//			var pids_arr = self.pidsListSelect.val();
//			
//			for (var i = 0; i < pid_arr.length; i++) {
//				if (!self.IsNumber(pid_arr[i])) {
//					self.common_mgr.modalShow("","Info","The PID must be a positive number!",true, false);
//					return ;
//				}
//				if (pid_arr[i] < 1 || pid_arr[i] > 65535) {
//					self.common_mgr.modalShow("","Info","The PID range from 1 to 65535!",true, false);
//					return ;
//				}
//				
//				var result = $.inArray(pid_arr[i], self.pid_list);
//				if (result == -1) {
//					self.pid_list.push(pid_arr[i]);
//				} 
//				
//				var result = $.inArray(pid_arr[i], pids_arr);
//				if (result == -1) {
//					pids_arr.push(pid_arr[i]);
//				} else {
//					self.common_mgr.modalShow("","Info","The PID already exists!",true, false);
//					return ;
//				}
//				
//			}
//			
//			$(".pids_list").css({"display" : "block"});
////			var pids_arr = self.pidsListSelect.val();
////			
////			var result = $.inArray(pid, self.pid_list);
////			if (result == -1) {
////				self.pid_list.push(pid);
////			} 
////			
////			var result = $.inArray(pid, pids_arr);
////			if (result == -1) {
////				pids_arr.push(pid);
////			} else {
////				self.common_mgr.modalShow("","Info","The PID already exists!",true, false);
////			}
//			
//			$('#pids_list').empty();
// 
//			for (var i = 0; i < self.pid_list.length; i++) {
//				$("#pids_list").append("<option value='" + self.pid_list[i] + "'>" + self.pid_list[i] + "</option>");
//			}
//			self.pidsListSelect.val(pids_arr).trigger("change");
//			$('#pids').val("");
//		}) 
		
		$('#add_stream_modal').on("hide.bs.modal", function() {
			$(this).find('input').each(function () {
	            $(this).val("")
	       });
			$(this).find('select').each(function () {
	            $(this).val("")
	       });
		})
		
		$('#type').change(function() {
			var type = $('#type option:selected').val();
			if (type == 0) {
				$('#set_pids').slideUp();
				$('#programs').slideDown();
				$('#pids_list').empty();
				$(".pids_list").css({"display" : "none"});
			} else if (type == 1) {
				$('#set_pids').slideUp();
				$('#programs').slideUp();
				$('#pids_list').empty();
				$(".pids_list").css({"display" : "none"});
			} else if (type == 2) {
				$('#set_pids').slideDown();
				$('#programs').slideUp();
				$('#pids_list').empty();
				$(".pids_list").css({"display" : "none"});
			}
			
			self.getProInfo();
			self.getAudioInfo();
			self.getVedioInfo();
			self.getSubTitleInfo();
			
		})
		
		$('#add_config').click(function() {
			self.doCheckOutputInfo();
			self.addOutputConfig(self.add_output_info,$('#add_board_id').val()-1,$('#add_module_id').val()-1);
		})
		
		$('#board_id').change(function() {
			localStorage.setItem("board_id",$('#board_id option:selected').val());
			self.getStreamArrays();
		})
		
		$('#module_id').change(function() {
			localStorage.setItem("module_id",$('#module_id option:selected').val());
			self.getStreamArrays();
		})
		
		$('.info').mouseenter(function() {
			var currentRow=$(this).closest("tr");
			var board_id=currentRow.find("td:eq(0)").text() - 1; // 获得当前行第二个TD值
			var module_id=currentRow.find("td:eq(1)").text() - 1; // 获得当前行第三个TD值

			$('#check_all'+board_id+module_id).unbind('click');
			$('#check_all'+board_id+module_id).click(function() {
				
				var ch=$("#streaming_info"+board_id+module_id + " input[type=checkbox]");
				if (this.checked) {
					 for(var i=0;i<ch.length;i++)
		             {
		                 if(ch[i].checked){
		                 }else{
		                     ch[i].checked = true;
		                 }
		             }
				}else{
					for (var i = 0; i < ch.length; i++) {
		                ch[i].checked = false;
		            }
				}
			})
			
			$('.check_line' + board_id + module_id).unbind('click');
			$('.check_line' + board_id + module_id).click(function() {
				if(!$('#check_line').checked) {
					$('#check_all'+board_id+module_id).prop("checked",false);
				} 
				var choicelength=$('.check_line' + board_id + module_id).length;
			    var choiceselect=$(".check_line" + board_id + module_id + ":checked").length;
			    
			    if (choicelength == choiceselect) {
			    	$('#check_all'+board_id+module_id).prop("checked",true);
				}

			})
			
			$('#lock_sat').unbind('change');
			$('#lock_sat').change(function() {
				self.getAllTpInfo();
				if (self.satllite_info_by_sat) {
					self.doLockSignal();
				} else {
					self.common_mgr.modalShow("","Info","There are some streams in the module, please delete first.",true, false);
					self.getLockInfo(board_id,module_id);
					if (self.lock_info.tpId != 0) {
						self.getSatlliteByTpId(self.lock_info.tpId);
					} else {
						self.getAllTpInfo();
					}
					self.doLockSignal();
				}
			})
			
			$('#lock_tp').unbind('change');
			$('#lock_tp').change(function() {
				if (self.satllite_info_by_sat) {
					self.doLockSignal();
				} else {
					self.common_mgr.modalShow("","Info","There are some streams in the module, please delete first.",true, false);
					self.getLockInfo(board_id,module_id);
					if (self.lock_info.tpId != 0) {
						self.getSatlliteByTpId(self.lock_info.tpId);
					} else {
						self.getAllTpInfo();
					}
					self.doLockSignal();
				}
			})
			
			$('#lock_lnb_freq').unbind('change');
			$('#lock_lnb_freq').change(function() {
				if (self.satllite_info_by_sat) {
					self.doLockSignal();
				} else {
					self.common_mgr.modalShow("","Info","There are some streams in the module, please delete first.",true, false);
					self.getLockInfo(board_id,module_id);
					if (self.lock_info.tpId != 0) {
						self.getSatlliteByTpId(self.lock_info.tpId);
					} else {
						self.getAllTpInfo();
					}
					self.doLockSignal();
				}
			})
			
			$('#lock_close').unbind('click');
			$('#lock_close').click(function() {
				self.getSatById($('#lock_sat option:selected').val(),board_id, module_id);
				self.getTpInfo($('#lock_tp option:selected').val(),board_id, module_id);
				$('#lock_signal_modal').modal('hide');
				clearTimeout(self.refresh_get_search_info);
				if (self.lock_status == 0) {
					self.unLockSignal();
				}
				self.setSignalAndQuality(0,0);
				self.getAllLockSignal();
			})
			
			$('#total_play'+board_id+module_id).unbind('click');
			$('#total_play'+board_id+module_id).click(function(event) {
				event.stopPropagation();
				var checkArray = [];
				var ch=$("#streaming_info"+board_id+module_id + " input[type=checkbox]");
				for (var i = 0; i < ch.length; i++) {
					if(ch[i].checked){
						checkArray.push(ch[i].value);
	                }
				}
				if (checkArray.length == 0) {
					self.common_mgr.modalShow("","Info","Please select at least one stream to start!",true,false);
				} else {
					$.ajax({
						url : "/streaming/startStreaming?"+new Date().getTime(),
						type : "post",
						dataType : "json",
						contentType : "application/json",
						data : JSON.stringify(checkArray),
						success : function(data) {
						}
					})
				}
			})

			$('#total_stop'+board_id+module_id).unbind('click');
			$('#total_stop' + board_id + module_id).click(function(event) {
				event.stopPropagation();
				var checkArray = [];
				var ch=$("#streaming_info"+board_id+module_id + " input[type=checkbox]");
				for (var i = 0; i < ch.length; i++) {
					if(ch[i].checked){
						checkArray.push(ch[i].value);
	                }
				}
				if (checkArray.length == 0) {
					self.common_mgr.modalShow("","Info","Please select at least one stream to stop!",true,false);
				} else {
					$.ajax({
						url : "/streaming/stopStreaming?"+new Date().getTime(),
						type : "post",
						dataType : "json",
						contentType : "application/json",
						data : JSON.stringify(checkArray),
						success : function(data) {
						}
					})
				}
			})
			
			$('#total_delete'+ board_id + module_id).unbind('click');
			$('#total_delete'+ board_id + module_id).click(function() {
				
				var checkArray = [];
				var choiceselect=$(".check_line" + board_id + module_id + ":checked").length;
				var ch=$("#streaming_info"+board_id+module_id + " input[type=checkbox]");
				
				if (choiceselect > 0) {
					for (var i = 0; i < ch.length; i++) {
						if(ch[i].checked){
							checkArray.push(ch[i].value);
		                 }
					}
					$('#deleteModal').modal('show');
					$('#delete').unbind('click');
					$('#delete').click(function() {
						self.deleteStream(board_id,module_id,checkArray);
						$('#check_all'+board_id+module_id).prop("checked",false);
					})
				} else {
					self.common_mgr.modalShow("","Info","Please select at least one stream to delete!",true, false);
				}
			})
			
			$('#add'+board_id+module_id).unbind('click');
			$('#add'+board_id+module_id).click(function(event){
				event.stopPropagation();
				
				$('#add_config').attr('disabled',false);
				$(".pids_list").css({"display" : "none"});
				$("#ip").slideUp();
				$("#hls_ip").slideUp();
				self.pid_list.length = 0;
				$('#add_title').text("Add Stream");
				$('#add_config').text("Save");
				$('#add_board_id').val(board_id+1);
				$('#add_module_id').val(module_id+1);
				
				self.createTypeOption();
				self.createProtocolOption();
				self.createHlsProtocolOption();
				
				self.getLockInfo(board_id,module_id);
				self.getOutputById(board_id,module_id);
				
				if (self.lock_info.status != 0) {
					self.getAddSatlliteByTpId(self.lock_info.tpId);
				} else {
					$("#audio").empty();
					self.common_mgr.modalShow("", "info", "No lock！", true, false);
					$('#add_config').attr('disabled',true);
				}
				
				var type = $('#type option:selected').val();
				if (type == 0) {
					$('#set_pids').slideUp();
					$('#programs').slideDown();
				} else if (type == 1) {
					$('#set_pids').slideUp();
					$('#programs').slideUp();
				} else if (type == 2) {
					$('#set_pids').slideDown();
					$('#programs').slideUp();
				}
				$('#add_stream_modal').modal('show');
			})
			
			$('#module_log' +board_id+module_id).unbind('click');
			$('#module_log' +board_id+module_id).click(function(event) {
				event.stopPropagation();
				window.location.href = "/module_log?board_id="+board_id+"&module_id="+module_id;
			})
			
			$('#lock_setting' + board_id + module_id).unbind('click');
			$('#lock_setting' + board_id + module_id).click(function(event) {
				event.stopPropagation();
				self.setSignalAndQuality(0,0);
				$("#lock_close").attr("disabled",true);
				setTimeout(() => {
					$("#lock_close").attr("disabled",false);
				}, 2000);
				$('#lock_signal_modal').modal('show');
				$('#lock_board_id').val(board_id+1);
				$('#lock_module_id').val(module_id+1);
				self.getStreamByBoardIdAndModuleId();
				self.getSatlliteInfo();
				self.getLockInfo(board_id, module_id);
				if (self.lock_info.tpId != 0) {
					self.getSatlliteByTpId(self.lock_info.tpId);
				} else {
					self.getAllTpInfo();
				}
				self.doLockSignal();
				self.getSearchStatus();
				self.refresh_get_search_info = setInterval(() => {
					self.getSearchStatus();
				}, 1000);
//				self.refresh_get_search_info.push(refresh_search);
			})
			
			$('.info').unbind('click');
			$(".info").click(function(event) {
				event.stopPropagation();
				self.getLockInfo(board_id, module_id);
				if (self.lock_info.status == 1) {
					self.getSatById(self.lock_info.satId,board_id, module_id);
					self.getTpInfo(self.lock_info.tpId,board_id, module_id);
				} else {
					$("#sat_name"+board_id+module_id).text("Satellite: ");
					$("#tp"+board_id+module_id).text("TP: ");
				}
				
				if($("#stream_info"+board_id+module_id).attr("style").indexOf("display") != (-1)) {
					var obj=new Object();
					obj.board_id = board_id;
					obj.module_id = module_id;
					self.getStreamArray.push(obj);
					self.getStreamArray = self.unique(self.getStreamArray);
					
				} else {
					for (var i = 0; i < self.getStreamArray.length; i++) {
						if (self.getStreamArray[i].board_id == board_id && self.getStreamArray[i].module_id == module_id) {
							self.getStreamArray.splice(i,1);
						}
					}
				}
				
				if (self.getStreamArray.length > 0) {
					clearTimeout(self.refresh_get_stream_info);
					self.getStreamInfo();
					self.refresh_get_stream_info = setInterval(() => {
						self.getStreamInfo();
					}, 1000);
				} else {
					clearTimeout(self.refresh_get_stream_info);
				}
				
				$("#stream_info"+board_id+module_id).toggle();
			});
		})
	},
	unique:function(array) {
		var self = this;
		var re = [array[0]];
		for (var i = 1; i < array.length; i++) 
		{
			var isunique = true;
			for (var r = 0; r < re.length; r++) 
			{
	       		if (re[r].board_id === array[i].board_id && re[r].module_id === array[i].module_id) 
	       		{
              		isunique = false;
	       		}
			}
		    if (isunique) {
	            re.push(array[i]);
		    }
		}
		return re;
	},
	getSearchStatus:function() {
		var self = this;
		var board_id = $('#lock_board_id').val()-1;
		var module_id = $('#lock_module_id').val()-1;
		$.ajax({
			url : "/search/getSearchStatus?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id,
				"module_id" : module_id
			},
			async : false,
			success : function(data) {
				var search_info = JSON.parse(data.data);
				self.search_status = search_info.search_status;
				self.lock_status = search_info.lock;
				console.log("self.lock_status:",self.lock_status);
//				setTimeout(() => {
					self.updateLockSignal();
//				}, 1000);
				var signal = 0;
				var quality = 0;
				if (data != null) {
					signal = search_info.streng;
					quality = search_info.quality;
					if (signal > 100) {
						signal = 100;
					}
					if (quality > 100) {
						quality = 100;
					} 
					self.setSignalAndQuality(signal,quality);
				}
			}
		});
	},
	getLockInfo:function(board_id,module_id) {
		var self = this;
		$.ajax({
			type : "post",
			url : "/streaming/getLockSignal?"+new Date().getTime(),
			dataType : "json",
			data : {
				"board_id" : board_id,
				"module_id" : module_id,
			},
			async : false,
			success : function(data) {
				self.lock_info = data;
			}
		})
	},
	getSatlliteByTpId:function(tp_id,board_id,module_id) {
		var self = this;
		$.ajax({
			url : "/streaming/getSatlliteByTpId?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"tp_id" : tp_id
			},
			async : false,
			success : function(data) {
				for (var i = 0; i < self.sat_has_program.length; i++) {
					if (self.sat_has_program[i].id == data.id) {
						$('#lock_sat').val(data.id);
					} 
				}
				
				self.getAllTpInfo();
				for (var i = 0; i < self.tp_list_with_pro.length; i++) {
					if (self.tp_list_with_pro[i].id == tp_id) {
						$('#lock_tp').val(tp_id);
					} 
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("textStatus:", textStatus);
			}
		})
	},
	getStreamArrays:function() {
		var self = this;
		
		var board_id = localStorage.getItem('board_id');
		var module_id = localStorage.getItem('module_id');
		if (board_id == null) {
			board_id = "";
		}
		if (module_id == null) {
			module_id = "";
		}
		self.module_array.length = 0;
		
		for (var i = 0; i < 5; i++) {
			for (var j = 0; j < 6; j++) {
				if (board_id == "" && module_id == "") {
					$('.info').show();
					var obj=new Object();
					obj.board_id = parseInt(i);
					obj.module_id = parseInt(j);
					self.module_array.push(obj);
				} else if (board_id != "" && module_id == "") {
					if (board_id != i) {
						$('#tr' + i + j).hide();
					} else {
						$('#tr' + i + j).show();
					}
					var obj=new Object();
					obj.board_id = parseInt(board_id);
					obj.module_id = parseInt(j);
					self.module_array.push(obj);
				} else if (board_id == "" && module_id != "") {
					if (module_id != j) {
						$('#tr' + i + j).hide();
					} else {
						$('#tr' + i + j).show();
					}
					var obj=new Object();
					obj.board_id = parseInt(i);
					obj.module_id = parseInt(module_id);
					self.module_array.push(obj);
				} else if (board_id != "" && module_id != "") {
					$('#tr' + i + j).hide();
					$('#tr' + board_id + module_id).show();
					var obj=new Object();
					obj.board_id = parseInt(board_id);
					obj.module_id = parseInt(module_id);
					self.module_array.push(obj);
				}
				$("#stream_info"+i+j).hide();
			}
		}
		self.module_array = self.unique(self.module_array);
	},
	getStreamInfo:function() {
		var self = this;
		$.ajax({
			type : "post",
			url : "/streaming/getStreamInfo?"+new Date().getTime(),
			dataType : "json",
			data : {
				"list" : JSON.stringify(self.getStreamArray)
			},
			async : false,
			success : function(data) {
				if (data != null) {
					var stream_info = JSON.parse(data.data).stream_list;
					self.setStreamInfo(stream_info);
				} 
			},
			error : function(status){
				clearTimeout(self.refresh_get_stream_info);
			}
		})
	},
	setModuleInfo:function(module_info){
		var self = this;
		var count = 0;
		for (var i = 0; i < module_info.length; i++) {
			var lock = (module_info[i].lock==1)?"Yes":"No";
			$("#tr" + module_info[i].board_id + module_info[i].module_id + " .locked").html(lock);
			$("#tr" + module_info[i].board_id + module_info[i].module_id + " .streng").html(module_info[i].streng);
			$("#tr" + module_info[i].board_id + module_info[i].module_id + " .quality").html(module_info[i].quality);
			$("#tr" + module_info[i].board_id + module_info[i].module_id + " .total_bitrate").html(module_info[i].eth_speed/10 + " Mb/s");
			$("#tr" + module_info[i].board_id + module_info[i].module_id + " .temp").html(Math.floor((module_info[i].temp / 10)) + "." + ((module_info[i].temp % 10)) + "℃");
			$("#tr" + module_info[i].board_id + module_info[i].module_id + " .running").html(module_info[i].stream_normal_num);
			var running = module_info[i].stream_normal_num;
			var total = $("#tr" + module_info[i].board_id + module_info[i].module_id + " .total").text();
			if (running != total) {
				$("#tr" + module_info[i].board_id + module_info[i].module_id + " .running").css({"color" : "red"});
			} else {
				$("#tr" + module_info[i].board_id + module_info[i].module_id + " .running").css({"color" : "black"});
			}
			var error_num = 0;
			var short = module_info[i].short;
			var status = module_info[i].server_status;
			var stream_add_status = module_info[i].stream_add_status;
			var color = (status == 0)?"#00FF33":"red";
			var short_color = (short == 0)?"#00FF33":"red";
			var stream_add_status_color = (stream_add_status == 0)?"#00FF33":"red";
			if (status == 0) {
				$("#stream_info" + module_info[i].board_id + module_info[i].module_id + " .run_status").html("");
			} else if (status == 1) {
				$("#stream_info" + module_info[i].board_id + module_info[i].module_id + " .run_status").html("");
			} else if (status == 2) {
				$("#stream_info" + module_info[i].board_id + module_info[i].module_id + " .run_status").html("Status : Overflow").css({"color":color});
				error_num ++;
			} else if (status == 3) {
				$("#stream_info" + module_info[i].board_id + module_info[i].module_id + " .run_status").html("Status : TsServer Error").css({"color":color});
				error_num ++;
			} else if (status == 4) {
				$("#stream_info" + module_info[i].board_id + module_info[i].module_id + " .run_status").html("Status : Offline").css({"color":color});
				error_num ++;
			} 
			if (short == 1) {
				$("#stream_info" + module_info[i].board_id + module_info[i].module_id + " .run_status").html("Status : LNB Short").css({"color":short_color});
				error_num ++;
			}
			if (stream_add_status == 2) {
				$("#stream_info" + module_info[i].board_id + module_info[i].module_id + " .run_status").html("Status : Cpu overflow").css({"color":stream_add_status_color});
				error_num ++;
			}
			if (status == 4) {
				$('#module_log' + module_info[i].board_id + module_info[i].module_id).attr('disabled',true);
				$('#lock_setting' + module_info[i].board_id + module_info[i].module_id).attr('disabled',true);
				$('#add' + module_info[i].board_id + module_info[i].module_id).attr('disabled',true);
				$('#link' + module_info[i].board_id + module_info[i].module_id).attr('href','#'); 
				$("#module_log" + module_info[i].board_id + module_info[i].module_id).children("i").css({"color" : "#868E96"});
				$("#lock_setting" + module_info[i].board_id + module_info[i].module_id).children("i").css({"color" : "#868E96"});
				$("#add" + module_info[i].board_id + module_info[i].module_id).children("i").css({"color" : "#868E96"});
				$("#link" + module_info[i].board_id + module_info[i].module_id).children("i").css({"color" : "#868E96"});
				$('#total_play' + module_info[i].board_id + module_info[i].module_id).attr('disabled',true);
				$('#total_stop' + module_info[i].board_id + module_info[i].module_id).attr('disabled',true);
				$('#total_delete' + module_info[i].board_id + module_info[i].module_id).attr('disabled',true);
				$("#total_play" + module_info[i].board_id + module_info[i].module_id).children("i").css({"color" : "#868E96"});
				$("#total_stop" + module_info[i].board_id + module_info[i].module_id).children("i").css({"color" : "#868E96"});
				$("#total_delete" + module_info[i].board_id + module_info[i].module_id).children("i").css({"color" : "#868E96"});
			} else {
				$('#module_log' + module_info[i].board_id + module_info[i].module_id).attr('disabled',false);
				$('#lock_setting' + module_info[i].board_id + module_info[i].module_id).attr('disabled',false);
				$('#add' + module_info[i].board_id + module_info[i].module_id).attr('disabled',false);
				$('#link' + module_info[i].board_id + module_info[i].module_id).attr('href','/link'); 
				$('#link' + module_info[i].board_id + module_info[i].module_id).attr('target','_blank'); 
				$('#total_play' + module_info[i].board_id + module_info[i].module_id).attr('disabled',false);
				$('#total_stop' + module_info[i].board_id + module_info[i].module_id).attr('disabled',false);
				$('#total_delete' + module_info[i].board_id + module_info[i].module_id).attr('disabled',false);
				$('#total_play' + module_info[i].board_id + module_info[i].module_id).children("i").removeAttr('style');
				$('#total_stop' + module_info[i].board_id + module_info[i].module_id).children("i").removeAttr('style');
				$('#total_delete' + module_info[i].board_id + module_info[i].module_id).children("i").removeAttr('style');
				$("#module_log" + module_info[i].board_id + module_info[i].module_id).children("i").removeAttr('style');
				$("#lock_setting" + module_info[i].board_id + module_info[i].module_id).children("i").removeAttr('style');
				$("#add" + module_info[i].board_id + module_info[i].module_id).children("i").removeAttr('style');
				$("#link" + module_info[i].board_id + module_info[i].module_id).children("i").removeAttr('style');
			}		
			count ++;	
			if (error_num > 0) {
				$("#tr" + module_info[i].board_id + module_info[i].module_id + " .all_status").css({"background-color" : "red"});
			} else {
				$("#tr" + module_info[i].board_id + module_info[i].module_id + " .all_status").css({"background-color" : "#00FF33"});
			}
		}
	},
	setStreamInfo:function(stream_info) {
		var self = this;
		$(".state").html("");
		for (var i = 0; i < stream_info.length; i++) {
			var info = stream_info[i].stream_status;
			$("#tr" + stream_info[i].board_id + stream_info[i].module_id + " .stream_status").children("i").attr("class","fa fa-times-circle-o");
			$("#tr" + stream_info[i].board_id + stream_info[i].module_id +" .stream_status").children("i").attr("title","Stopped");
			$("#tr" + stream_info[i].board_id + stream_info[i].module_id +" .bitrate" ).html("Bitrate : 0 Mb/s");
			$("#tr" + stream_info[i].board_id + stream_info[i].module_id +" .time_running").html("Time : 00:00:00");
			if (info.length > 0) {
				for (var m = 0; m < info.length; m++) {
					var error_num = 0;
					var time_running = self.formatSeconds(info[m].time_running);
					var status = info[m].status;
					var color = (info[m].status==0)?"#00FF33":"red" ;
					if (status == 0) {
						$("#tr" + stream_info[i].board_id + stream_info[i].module_id +" .status" + info[m].output_id).html("");
						$("#tr" + stream_info[i].board_id + stream_info[i].module_id +" #stream_status" + info[m].output_id).children("i").attr("class","fa fa-check-circle-o");
						$("#tr" + stream_info[i].board_id + stream_info[i].module_id +" #stream_status" + info[m].output_id).children("i").attr("title","Running");
					} else if (status == 1) {
						$("#tr" + stream_info[i].board_id + stream_info[i].module_id +" #status" + info[m].output_id).html("Send Failed").css({"color": color});
						error_num ++;
					} else if (status == 2) {
						$("#tr" + stream_info[i].board_id + stream_info[i].module_id +" #status" + info[m].output_id).html("Ringbuffer Full").css({"color": color});
						error_num ++;
					} else if (status == 3) {
						$("#tr" + stream_info[i].board_id + stream_info[i].module_id +" #status" + info[m].output_id).html("Record add pid failes").css({"color": color});
						error_num ++;
					} 
					$("#tr" + stream_info[i].board_id + stream_info[i].module_id +" #bitrate" + info[m].output_id).html("Bitrate : "+info[m].speed/10+" Mb/s");
					$("#tr" + stream_info[i].board_id + stream_info[i].module_id +" #time_running" + info[m].output_id).html("Time : "+time_running);
				}
			} 
		}
	},
	getSatById:function(sat_id,board_id, module_id) {
		var self = this;
		$.ajax({
			url : "/streaming/getSatById?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"sat_id" : sat_id
			},
			success : function(data) {
				var dispName = data.name + " ("+ Math.floor((data.angle / 10)) + "." + ((data.angle % 10)) + ((data.dir == 0) ? "E" : "W") + ")";
				$("#sat_name"+board_id+module_id).text("Satellite: " + dispName);
			}
		})
	},
	getOutputInfo:function(id) {
		var self = this;
		$.ajax({
			url : "/streaming/getOutput?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"id" : id
			},
			async : false,
			success : function(data) {
				if (data != null) {
					self.output_info = data;
				}
			}
		})
	},
	getOutputById:function(board_id, module_id) {
		var self = this;
		$.ajax({
			url : "/streaming/getOutputById?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id,
				"module_id" : module_id
			},
			async : false,
			success : function(data) {
				self.singal_output_info = data;
				self.drawAddDialog(data,board_id,module_id);
			}
		})
	},
	getTpInfo:function(tp_id,board_id,module_id) {
		var self = this;
		if (tp_id != 0) {
			$.ajax({
				url : "/transponder/getTpInfoById?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"id" : tp_id
				},
				async : false,
				success : function(result) {
					var dispName = result.freq + "/"+ result.symbolRate + "/"+ self.aStrUipol[result.polarization];
					$("#tp"+board_id+module_id).text("TP: "+dispName);
				},
				error : function(textStatus) {
					console.log("textStatus:",textStatus);
				}
			})
		}
	},
	formatSeconds:function(seconds) {
		var self = this;
		var min = 0;
		var hour = 0;
		var result;
		if (seconds >= 60) {
			min = parseInt(seconds/60);
			seconds = parseInt(seconds%60);
			if (min >= 60) {
				hour = parseInt(min/60);
				min = parseInt(min%60);
			}
		}
		hour = (hour<10)?"0"+hour:hour;
		min = (min<10)?"0"+min:min;
		seconds = (seconds<10)?"0"+seconds:seconds;
		result = hour+":"+min+":"+seconds;
		return result;
	},
	drawStreamingInfo:function(board_id,module_id) {
		var self = this;
		var list = "";
		$("#streaming_info"+board_id+module_id).empty();
		for (var i = 0; i < self.singal_output_info.length; i++) {
			var input = '';
			var audioPids = '';
			var num = 0;
			if (board_id == self.singal_output_info[i].boardId && module_id == self.singal_output_info[i].moduleId) {
				num ++;
				if (self.singal_output_info[i].type == 0) {
					input = self.singal_output_info[i].program.serviceName;
				} else if (self.singal_output_info[i].type == 1) {	
					for (var j = 0; j < self.all_lock_signal.length; j++) {
						if (board_id == self.all_lock_signal[j].boardId && module_id == self.all_lock_signal[j].moduleId) {
							$.ajax({
								url : "/transponder/getTpInfoById?"+new Date().getTime(),
								type : "post",
								dataType : "json",
								data : {
									"id" : self.all_lock_signal[j].tpId
								},
								async : false,
								success : function(result) {
									if (result != null) {
										var dispName = result.freq + "/" + result.symbolRate + "/" + self.aStrUipol[result.polarization];
										input = dispName ;
									}
								}
							})
						}
					}
				}else if (self.singal_output_info[i].type == 2) {
					input=self.singal_output_info[i].setPids ; 
//					var arr = [];
//					var destArr = [];
//					arr = input.split(",");
//					for (var j = 0; j < arr.length; j++) {
//						destArr.push("0x" + parseInt(arr[j]).toString(16));
//					}
//					input = destArr.join(",");
				}
			
				var out_url = [];
				var protocol = (self.singal_output_info[i].hlsOutMode == 0)?"http://":"udp://@";
				var lable_id= self.singal_output_info[i].lableId;
				var hls_ip = self.singal_output_info[i].hlsOutIp.split(",");
				var hls_port = self.singal_output_info[i].hlsOutPort.split(",");
				if (self.singal_output_info[i].hlsOutMode == 0) {
					out_url.push(protocol + self.localIp + ":8182/" + self.singal_output_info[i].lableId);
					var content = "";
					list += "<tr id=\"tr"+self.singal_output_info[i].boardId+self.singal_output_info[i].moduleId+"\">";
					list += "<td><input type=\"checkbox\" value=\"" + self.singal_output_info[i].id + "\" id=\"check_line\" class=\"check_line" + self.singal_output_info[i].boardId+self.singal_output_info[i].moduleId + "\"/></td>";
					list += "<td title=\""+ input +"\" style=\"max-width: 300px;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;\">" + "Input #" + i + " : " + input + "</td>";
					list += "<td >Output URL : </td>";
					list += "<td >" + out_url + "</td>";
					list += "<td class=\"state\" id=\"status" + self.singal_output_info[i].id + "\"></td>";
					list += "<td class=\"bitrate\" id=\"bitrate" + self.singal_output_info[i].id + "\">Bitrate : 0 Mb/s</td>"
					list += "<td class=\"time_running\" id=\"time_running" + self.singal_output_info[i].id + "\">Time : 00:00:00</td>";
					list += "<td><button class=\"stream_status\" id=\"stream_status" + self.singal_output_info[i].id + "\"><i class=\"fa fa-times-circle-o\" aria-hidden=\"true\"></i></button></td>";
					
					if (self.singal_output_info[i].type == 0) {
						$.ajax({
							url : "/streaming/getSubByProId?"+new Date().getTime(),
							type : "post",
							dataType : "json",
							data : {
								"pro_id" : self.singal_output_info[i].programId,
								"board_id" : self.singal_output_info[i].boardId
							},
							async : false,
							success : function(data) {
								var teletext = [];
								var sub_str = [];
								if (self.singal_output_info[i].subtitlePids == "") {
									content = "Audio Pid:" + self.singal_output_info[i].audioPids + "<br>" + "Video Pid:" + self.singal_output_info[i].program.vidPid + "<br>" + "Channel ID:" + lable_id;
								} else {
									var sub_arr = self.singal_output_info[i].subtitlePids.split(",");
									for (var m = 0; m < data.length; m++) {
										for (var n = 0; n < sub_arr.length; n++) {
											if (data[m].subPid == sub_arr[n]) {
												if (data[m].type == 1) {
													teletext.push(data[m].subPid);
												} else {
													sub_str.push(data[m].subPid);
												}
											}
										}
									}
									if (teletext.length > 0 && sub_str.length > 0 ) {
										content = "Audio Pid:" + self.singal_output_info[i].audioPids + "<br>" + "Video Pid:" + self.singal_output_info[i].program.vidPid + "<br>" + "Subtitle Pid:" + sub_str.join(",") + "<br>" + "Teletext:" + teletext.join(",") + "<br>" + "Channel ID:" + lable_id;
									}
									if (teletext.length > 0 && sub_str.length == 0 ) {
										content = "Audio Pid:" + self.singal_output_info[i].audioPids + "<br>" + "Video Pid:" + self.singal_output_info[i].program.vidPid + "<br>" + "Teletext:" + teletext.join(",") + "<br>" + "Channel ID:" + lable_id;
									}
									if (teletext.length == 0 && sub_str.length > 0 ) {
										content = "Audio Pid:" + self.singal_output_info[i].audioPids + "<br>" + "Video Pid:" + self.singal_output_info[i].program.vidPid + "<br>" + "Subtitle Pid:" + sub_arr.join(",") + "<br>" + "Channel ID:" + lable_id;
									}
								}
							}
						})
					} else {
						content = "Channel ID:" + lable_id;
					} 
					list += "<td><button data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + content + "\"> <i class=\"fa fa-info-circle\" aria-hidden=\"true\"></i></button></td>" 
					list += "</tr>";
				} else {
					for (var k = 0; k < hls_ip.length; k++) {
						if (k == 0) {
							var content = "";
							list += "<tr id=\"tr"+self.singal_output_info[i].boardId+self.singal_output_info[i].moduleId+"\">";
							list += "<td><input type=\"checkbox\" value=\"" + self.singal_output_info[i].id + "\" id=\"check_line\" class=\"check_line" + self.singal_output_info[i].boardId+self.singal_output_info[i].moduleId + "\"/></td>";
							list += "<td title=\""+ input +"\" style=\"max-width: 300px;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;\">" + "Input #" + i + " : " + input + "</td>";
							list += "<td >Output URL : </td>";
							list += "<td >" + protocol + hls_ip[k] + ":" + hls_port[k] + "</td>";
							list += "<td class=\"state\" id=\"status" + self.singal_output_info[i].id + "\"></td>";
							list += "<td class=\"bitrate\" id=\"bitrate" + self.singal_output_info[i].id + "\">Bitrate : 0 Mb/s</td>"
							list += "<td class=\"time_running\" id=\"time_running" + self.singal_output_info[i].id + "\">Time : 00:00:00</td>";
							list += "<td><button class=\"stream_status\" id=\"stream_status" + self.singal_output_info[i].id + "\"><i class=\"fa fa-times-circle-o\" aria-hidden=\"true\"></i></button></td>";
							
							if (self.singal_output_info[i].type == 0) {
								if (self.singal_output_info[i].subtitlePids == "") {
									content = "Audio Pid:" + self.singal_output_info[i].audioPids + "<br>" + "Video Pid:" + self.singal_output_info[i].program.vidPid + "<br>" + "Channel ID:" + lable_id;
								} else {
									if (self.singal_output_info[i].type == 1) {
										content = "Audio Pid:" + self.singal_output_info[i].audioPids + "<br>" + "Video Pid:" + self.singal_output_info[i].program.vidPid + "<br>" + "Teletext:" + self.singal_output_info[i].subtitlePids + "<br>" + "Channel ID:" + lable_id;
									} else {
										content = "Audio Pid:" + self.singal_output_info[i].audioPids + "<br>" + "Video Pid:" + self.singal_output_info[i].program.vidPid + "<br>" + "Subtitle Pid:" + self.singal_output_info[i].subtitlePids + "<br>" + "Channel ID:" + lable_id;
									}
								}
							} else {
								content = "Channel ID:" + lable_id;
							} 
							list += "<td><button data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + content + "\"> <i class=\"fa fa-info-circle\" aria-hidden=\"true\"></i></button></td>" 
							list += "</tr>";
						} else {
							list += "<tr>";
							list += "<td></td>";
							list += "<td></td>";
							list += "<td></td>";
							list += "<td>" + protocol + hls_ip[k] + ":" + hls_port[k] + "</td>";
							list += "<td></td>";
							list += "<td></td>"
							list += "<td></td>";
							list += "<td></td>";
							list += "<td></td>" 
							list += "</tr>";
						}
					}
				}
			}
		}
		$("#streaming_info"+board_id+module_id).append(list);
		$("[data-toggle='tooltip']").tooltip({
            html:true
        });
	},
	deleteStream:function (board_id,module_id,checkArray) {
		var self = this;
		$.ajax({
			url : "/streaming/deleteStream?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify(checkArray),
			success : function(data) {
				$('#deleteModal').modal('hide');
				self.getOutputById(board_id,module_id);
				self.drawStreamingInfo(board_id,module_id);
				self.getAllOutputCount();
			}
		})
	},
	getSatlliteInfo:function () {
		var self = this;
		$.ajax({
			url : "/streaming/getSatWithPro?"+new Date().getTime(),
			type : "post",
			data : {
				"board_id" : $('#lock_board_id').val() -1,
				"module_id" : ""
			},
			async : false,
			success : function(data) {
				self.sat_has_program = data;
				if (data == "") {
					self.common_mgr.modalShow("","Info","No Program!",true,false);	
					$('#lock_sat').empty();
					$("#lock_tp").empty();
					$('#lock_lnb_freq').empty();
				} else {
					self.createSatOption(data);
					self.createLnbFreqOption();
				}
			}
		})
	},
	createSatOption:function(data) {
		var self = this;
		$('#lock_sat').empty();
		for (var i = 0; i < data.length; i++) {
			var dispName = (i + 1) + "-" + data[i].name + " ("
					+ Math.floor((data[i].angle / 10)) + "."
					+ ((data[i].angle % 10)) + ((data[i].dir == 0) ? "E" : "W")
					+ ")";
			$("#lock_sat").append(
					"<option value='" + data[i].id + "'>" + dispName + "</option>");
		}
	},
	createLnbFreqOption:function () {
		var self = this;
		$('#lock_lnb_freq').empty();
		for (var i = 0; i < self.lnb_freq.length; i++) {
			$("#lock_lnb_freq").append("<option value='" + i + "'>" + self.lnb_freq[i] + "</option>");
		}
	},
	getAllTpInfo:function () {
		var self = this;
		var sat_id = $('#lock_sat option:selected').val();// 选中的值
		if (sat_id == undefined) {
			
		} else {
			$.ajax({
				url : "/streaming/getTpListWithPro?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"sat_id" : sat_id,
					"board_id" : $('#lock_board_id').val()-1
				},
				async : false,
				success : function(data) {
					self.tp_list_with_pro = data.tp_info;
					if (data != null) {
						self.selectLnbFreq(data);
						self.createTpOption(data.tp_info);
					}
				}
			})
		}
	},
	createTpOption:function(tp_info) {
		var self = this;
		$("#lock_tp").empty();
		if (tp_info != null && tp_info.length > 0) {
			for (var i = 0; i < tp_info.length; i++) {
				var dispName = (i + 1) + "-" + tp_info[i].freq + "/"+ tp_info[i].symbolRate + "/"+ self.aStrUipol[tp_info[i].polarization];
				$("#lock_tp").append("<option value='" + tp_info[i].id + "' name='" + i + "'>"+ dispName + "</option>");
			}
		} else {
			$("#lock_tp").append("<option ></option>");
		}
	},
	selectLnbFreq:function(data) {
		var self = this;
		for (var i = 0; i < self.lnb_freq.length; i++) {
			if (self.lnb_freq[i] == data.lnb_type) {
				$('#lock_lnb_freq').val(i);
				break;
			}
		}
	},
	doLockSignal:function() {
		var self = this;
		var board_id = $('#lock_board_id').val() - 1;
		var module_id = $('#lock_module_id').val() - 1;
		var sat_id = $('#lock_sat option:selected').val();
		var tp_id = $('#lock_tp option:selected').val();
		var lnb_type = $('#lock_lnb_freq option:selected').text();

		if (sat_id == undefined && tp_id == undefined) {
			sat_id = "";
			tp_id = "";
			lnb_type = "";
		} 
		
		$.ajax({
			url : "/search/lockSignal?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id,
				"module_id" : module_id,
				"sat_id" : sat_id,
				"tp_id" : tp_id,
				"lnb_type" : lnb_type
			},
			success : function(data) {
				if (data.data == "SUCCESS:") {
					//self.updateLockSignal();
					self.updateSatllite();
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("textStatus:", textStatus);
			}
		})
	},
	unLockSignal:function() {
		var self = this;
		var board_id = $('#lock_board_id').val() - 1;
		var module_id = $('#lock_module_id').val() - 1;
		$.ajax({
			url : "/search/lockSignal?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id ,
				"module_id" : module_id,
				"sat_id" : "",
				"tp_id" : "",
				"lnb_type" : ""
			},
			success : function(data) {
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("textStatus:", textStatus);
			}
		})
	},
	updateLockSignal:function() {
		var self = this;
		var board_id = $('#lock_board_id').val() - 1;
		var module_id = $('#lock_module_id').val() - 1;
		var sat_id = $('#lock_sat option:selected').val();
		var tp_id = $('#lock_tp option:selected').val();
		if (self.lock_status == 1) {
			var data = {
				"boardId" : board_id,
				"moduleId" : module_id,
				"satId" : sat_id,
				"tpId" : tp_id,
				"status" : self.lock_status
			}
		}
		else {
			var data = {
				"boardId" : board_id,
				"moduleId" : module_id,
				"satId" : 0,
				"tpId" : 0,
				"status" :self.lock_status
			}
		}
		
		var lock_json = JSON.stringify(data);
		$.ajax({
			url : "/streaming/updateLockSignal?"+new Date().getTime(),
			type : "post",
			contentType : "application/json",
			dataType : "json",
			data : lock_json,
			success : function(data) {
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("textStatus:", textStatus);
			}
		})
		
	},
	updateSatllite:function() {
		var self = this;
		json_data =
		{
			"id":$('#lock_sat option:selected').val(),
			"name":null,
			"dir":"",
			"angle":"",
			"lnbType":$('#lock_lnb_freq option:selected').text()
		}
		var data = JSON.stringify(json_data);
		$.ajax({
			url : "/satllite/updateSatllite?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : data,
			success : function(data) {
			}
		})
	},
	setSignalAndQuality:function(signal, quality) {
		var self = this;
		$('#strength').progressbar('setValue', signal);
		$('#quality').progressbar('setValue', quality);
	},
	getStreamByBoardIdAndModuleId:function() {
		var self = this;
		var board_id = $('#lock_board_id').val() - 1;
		var module_id = $('#lock_module_id').val() - 1;
		$.ajax({
			url : "/streaming/getStreamByBoardIdAndModuleId?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id,
				"module_id" : module_id
			},
			async : false,
			success : function(data) {
				self.satllite_info_by_sat = data.result;
			}
		})
	},
	createTypeOption:function() {
		var self = this;
		$("#type").empty();
		for (var i = 0; i < self.type_arr.length; i++) {
			$("#type").append("<option value='" + i + "'>" + (i + 1) + "-" + self.type_arr[i]+ "</option>");
		}
	},
	createProtocolOption:function() {
		var self = this;
		$("#protocol").empty();
		$("#protocol").append("<option value='1'>" + (1) + "-" + self.protocol_arr[0]+ "</option>");
		$("#protocol").append("<option value='0'>" + (2) + "-" + self.protocol_arr[1]+ "</option>");
	},
	createHlsProtocolOption:function() {
		var self = this;
		$("#hls_protocol").empty();
		for (var i = 0; i < self.protocol_hls_arr.length; i++) {
			$("#hls_protocol").append("<option value='" + i + "'>" + (i + 1) + "-" + self.protocol_hls_arr[i]+ "</option>");
		}
	},
	setDefault:function(data) {
		var self = this;
		$("#type option[value='0']").attr("disabled",false); 
		$("#type option[value='1']").attr("disabled",false); 
		$("#type option[value='2']").attr("disabled",false);
		if (data.length != 0) {
			for (var i = data.length; i > 0; i--) {
				if (data[i-1].type == 0) {
					 $("#type option[value='1']").attr("disabled",true); 
					 $("#type option[value='0']").css({"color":"black"});
					 $("#type option[value='2']").css({"color":"black"});
					 $("#type").val(0);
					 $('#set_pids').slideUp();
					 $('#programs').slideDown();
					 break;
				}
				if (data[i-1].type == 1) {
					$("#type option[value='0']").attr("disabled",true);
					$("#type option[value='2']").attr("disabled",true); 
					$("#type option[value='1']").css({"color":"black"});
					$("#type").val(1);
					$('#set_pids').slideUp();
					$('#programs').slideUp();
					 break;
				}
				
				if (data[i-1].type == 2) {
					 $("#type option[value='1']").attr("disabled",true); 
					 $("#type option[value='0']").css({"color":"black"});
					 $("#type option[value='2']").css({"color":"black"});
					 $("#type").val(2);
					 $('#set_pids').slideDown();
					 $('#programs').slideUp();
					 break;
				}
			
			}
		} else {
			$("#type option[value='0']").attr("disabled",false); 
			$("#type option[value='1']").attr("disabled",false); 
			$("#type option[value='2']").attr("disabled",false);
			$("#type").val(0);
		}
	},
	getAddSatlliteByTpId:function(tp_id) {
		var self = this;
		$.ajax({
			url : "/streaming/getSatlliteByTpId?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"tp_id" : tp_id
			},
			async : false,
			success : function(data) {
				self.createAddSatOption(data);
				$('#add_sat').val(data.id);
				$('#add_sat').attr("disabled","disabled");
				
				self.getTpInfoById(tp_id);
				$('#add_tp').val(tp_id);
				$('#add_tp').attr("disabled","disabled");
				self.getProInfo();
				self.getAudioInfo();
				self.getVedioInfo();
				self.getSubTitleInfo();
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("textStatus:", textStatus);
			}
		})
	},
	getTpInfoById:function(tp_id) {
		var self = this;
		if (tp_id != 0) {
			$.ajax({
				url : "/transponder/getTpInfoById?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"id" : tp_id
				},
				async : false,
				success : function(result) {
					self.tp_info = result;
					self.createAddTpOption(result);
				},
				error : function(textStatus) {
					console.log("textStatus:",textStatus);
				}
			})
		}
	},
	getProInfo:function() {
		var self = this;
		var board_id = $('#add_board_id').val()-1;
		var tp_id = '';
		tp_id = $('#add_tp option:selected').val();
		$.ajax({
			url : "/programs/getProgramsByTpId?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"tp_id" : tp_id,
				"board_id" : board_id
			},
			async : false,
			success : function(data) {
				self.pro_info = data;
				if (data.length != 0) {
					self.createProOption(data);
				} else {
					self.common_mgr.modalShow("","info","No Program",true,false);
					$('#add_config').attr('disabled',true);
				}
			}
		})
	},
	getAudioInfo:function() {
		var self = this;
		var board_id = $('#add_board_id').val()-1;
		var pro_id = $('#pro option:selected').val();
		if (pro_id == "") {
			self.createAudioOption("");
		} else {
			$.ajax({
				url : "/streaming/getAudioByProId?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"pro_id" : pro_id,
					"board_id" : board_id
				},
				async : false,
				success : function(data) {
					if (data != null) {
						self.createAudioOption(data);
					}
				}
			})
		}
	},
	getVedioInfo:function() {
		var self = this;
		var board_id = $('#add_board_id').val()-1;
		var pro_id = $('#pro option:selected').val();
		if (pro_id == undefined || pro_id == "") {
			self.createVedioOption("");
		} else {
			$.ajax({
				url : "/programs/getProById?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"pro_id" : pro_id,
					"board_id" : board_id
				},
				async : false,
				success : function(data) {
					if (data != null) {
						self.createVedioOption(data);
					}
				},
				error : function(textStatus) {
					console.log(textStatus);
				}
			})
		}
	},
	getSubTitleInfo:function() {
		var self = this;
		var board_id = $('#add_board_id').val()-1;
		var pro_id = $('#pro option:selected').val();
		$.ajax({
			url : "/streaming/getSubByProId?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"pro_id" : pro_id,
				"board_id" : board_id
			},
			async : false,
			success : function(data) {
				console.log(data);
				if (data.length > 0) {
					self.createSubtitleOption(data);
				} else {
					$(".sub").css('display','none');
				}
			}
		})
	},
	createAddSatOption:function(data) {
		var self = this;
		$('#add_sat').empty();
		var dispName = (1) + "-" + data.name + " ("+ Math.floor((data.angle / 10)) + "." + ((data.angle % 10))+ ((data.dir == 0) ? "E" : "W") + ")";
		$("#add_sat").append("<option value='" + data.id + "'>" + dispName + "</option>");
	},
	createAddTpOption:function(tp_info) {
		var self = this;
		$("#add_tp").empty();
		var dispName = (1) + "-" + tp_info.freq + "/"+ tp_info.symbolRate + "/" + self.aStrUipol[tp_info.polarization];
		$("#add_tp").append("<option value='" + tp_info.id + "' name='" + 0 + "'>"+ dispName + "</option>");
	},
	createProOption:function(pro_info) {
		var self = this;
		$("#pro").empty();
		if (pro_info != null && pro_info.length > 0) {
			for (var i = 0; i < pro_info.length; i++) {
				if (pro_info[i].caType == 0) {
					$("#pro").append("<option value='" + pro_info[i].id + "'>" + (i + 1) + "-"+ pro_info[i].serviceName + "</option>");
				} else {
					$("#pro").append("<option value='" + pro_info[i].id + "'>" + (i + 1) + "-"+ pro_info[i].serviceName + "   (" + " &#xF155 " + ")" + "</option>");
				}
			}
		} else {
			$("#pro").append("<option ></option>");
		}
	},
	createAudioOption:function(audio_info) {
		var self = this;
		$("#audio").empty();
		if (audio_info != "" && audio_info.length > 0) {
			for (var i = 0; i < audio_info.length; i++) {
				$("#audio").append("<option value='" + audio_info[i].audioPid + "'>" + (i + 1)+ "- " + audio_info[i].audioPid + "</option>");
			}
			self.audioSelect.val(audio_info[0].audioPid).trigger("change");
		} else {
			$("#audio").append("<option ></option>");
		}
	},
	createVedioOption:function(vedio_info) {
		var self = this;
		$("#vedio").empty();
		if (vedio_info != "") {
			$('#vedio').val(vedio_info.vidPid);
		} else {
			$("#vedio").append("<option ></option>");
		}
	},
	createSubtitleOption:function(subtitle_info) {
		var self = this;
		var sub_arr = [];
		var txt_arr = [];
		var count_sub = 1;
		var count_txt = 1;
		$("#subtitle").empty();
		$("#teletext").empty();
		if (subtitle_info != "" && subtitle_info.length > 0) {
			for (var i = 0; i < subtitle_info.length; i++) {
				if (subtitle_info[i].type == 1) {
					$(".teletext").css('display','block');
					txt_arr.push(subtitle_info[i].subPid);
					$("#teletext").append("<option value='" + subtitle_info[i].subPid + "'>"+ count_txt + "-" + subtitle_info[i].subPid + "</option>");
					count_txt++;
				} else {
					$(".sub").css('display','block');
					sub_arr.push(subtitle_info[i].subPid);
					$("#subtitle").append("<option value='" + subtitle_info[i].subPid + "'>"+ count_sub + "-" + subtitle_info[i].subPid + "</option>");
					count_sub++;
				}
			}
			self.teletextSelect.val(txt_arr[0]).trigger("change");
			self.subtitleSelect.val(sub_arr[0]).trigger("change");
		} else {
			$("#subtitle").append("<option value='" + "" + "' >No Subtitle</option>");
		}
	},
	showPid:function() {
		var self = this;
		$("#pid_content").empty();
		$("#common_pid_content").empty();
		var list = "";
		var common_list = "";
		var sub_pid = [];
		var teletext = [];
		var audio_pid = [];
		
		if (self.tp_info != "") {
			common_list += "<tr class=\"tr-link\">";
			if (self.tp_info.emmPids == "") {
				common_list += "<td style=\"white-space: nowrap;\">None</td>";
			} else {
				var emmPids = self.tp_info.emmPids.replace(/\s/g, "").split(",");
				var casIds = self.tp_info.caSystemIds.replace(/\s/g, "").split(",");
				var str = [];
				for (var i = 0; i < emmPids.length; i++) {
					str.push(emmPids[i]+"(" + casIds[i] + ")");
				}
				common_list += "<td style=\"white-space: nowrap;\">" + str.join(",") + "</td>";
			}
			common_list += "<td>" + self.PAT_PID + "</td>";
			common_list += "<td>" + self.CAT_PID + "</td>";
			common_list += "<td>" + self.SDT_PID + "</td>";
			if (self.tp_info.eitId != 0) {
				common_list += "<td>" + self.EIT_PID + "</td>";
				$('.eit_pid').show();
			} else {
				$('.eit_pid').hide();
			}
			common_list += "</tr>";
		}
		
		$("#common_pid_content").append(common_list);
		
		if (self.pro_info != "" && self.pro_info.length > 0) {
			for (var i = 0; i < self.pro_info.length; i++) {
//				if (self.pro_info[i].subtitlePids.length != 0 || self.pro_info[i].ecmPids != "") {
					for (var j = 0; j < self.pro_info[i].subtitlePids.length; j++) {
						if (self.pro_info[i].subtitlePids[j].type == 1) {
							teletext.push(self.pro_info[i].subtitlePids[j].subPid);
						} else {
							sub_pid.push(self.pro_info[i].subtitlePids[j].subPid);
						}
					}
					for (var k = 0; k < self.audio_pid_info.length; k++) {
						if (self.pro_info[i].id == self.audio_pid_info[k].programId) {
							audio_pid.push(self.audio_pid_info[k].audioPid);
						}
					}
					list += "<tr class=\"tr-link\">";
					list += "<td style=\"white-space: nowrap;\">" + self.pro_info[i].serviceName + "</td>";
					if (self.pro_info[i].vidPid == "") {
						list += "<td>None</td>";
					} else {
						list += "<td>" + self.pro_info[i].vidPid + "</td>";
					}
					if (audio_pid.join(",") == "") {
						list += "<td>None</td>";
					} else {
						list += "<td>" + audio_pid.join(",") + "</td>";
					}
					if (sub_pid.join(",") == "") {
						list += "<td>None</td>";
					} else {
						list += "<td>" + sub_pid.join(",") + "</td>";
					}
					if (teletext.join(",") == "") {
						list += "<td>None</td>";
					} else {
						list += "<td>" + teletext.join(",") + "</td>";
					}
					if (self.pro_info[i].ecmPids == "") {
						list += "<td style=\"white-space: nowrap;\">None</td>";
					} else {
						var ecmPids = self.pro_info[i].ecmPids.split(",");
						var casIds = self.pro_info[i].caSystemIds.split(",");
						var str = [];
						for (var j = 0; j < ecmPids.length; j++) {
							str.push(ecmPids[j] + "(" + casIds[j] + ")" );
						}
						list += "<td style=\"white-space: nowrap;\">" + str.join(",") + "</td>";
					}
					list += "</tr>";
					sub_pid = [];
					audio_pid = [];
					teletext = [];
				}
//			}
			$("#pid_content").append(list);
		}
		
		 $('.tr-link').unbind('click').click(function () {
			 $('.tr-link').removeClass("tr-in");
             $(this).addClass("tr-in");
		 });
		
	},
	doCheckOutputInfo:function() {
		var self = this;
		
		var program_id = "";
		var pids_list = "";
		var audio_arr = "";
		var audio_pids = "";
		var subtitle_arr = "";
		var subtitle_pids = "";
		var teletext_arr = "";
		var teletext_pids = "";
		var out_ip = "";
		var hls_out_ip = "";
		var hls_out_port = "";
		
		var id = $('#output_id').text();
		var board_id = $('#add_board_id').val()-1;
		var module_id = $('#add_module_id').val()-1;
		var type = $('#type option:selected').val();
		var sat_id = $('#add_sat option:selected').val();
		var tp_id = $('#add_tp option:selected').val();
		var out_mode = $('#protocol option:selected').val();
		var hls_out_mode = $('#hls_protocol option:selected').val();
		var lable_id = $('#channel_id').val();
		if (type == 0) {
			program_id = $('#pro option:selected').val();
			pids_list = "";
			audio_arr = self.audioSelect.val();
			audio_pids = audio_arr.join(",");
			subtitle_arr = self.subtitleSelect.val();
			subtitle_pids = subtitle_arr.join(",");
			teletext_arr = self.teletextSelect.val();
			teletext_pids = teletext_arr.join(",");
			
			if (subtitle_pids != "") {
				if (teletext_pids != "") {
					subtitle_pids = subtitle_pids + "," + teletext_pids;
				}
			} else {
				subtitle_pids = teletext_pids;
			}
		} else if (type == 1) {
			program_id = 0;
			audio_pids = "";
			subtitle_pids = "";
			teletext_pids = "";
			pids_list = "";
		} else if (type == 2) {
			program_id = 0;
//			pids_arr = self.pidsListSelect.val();
			pids_list = $("#pids").val();
			audio_pids = "";
			subtitle_pids = "";
			teletext_pids = "";
		}
		
		if (out_mode == 0) {
			out_ip = "";
		} else {
			if ((board_id == 2 && module_id < 3) || (board_id < 2 && module_id < 6)) {
				out_ip = "11.12.13.161";
			} 
			if ((board_id == 2 && module_id > 2) || (board_id > 2 && module_id < 6)) {
				out_ip = "11.12.14.161";
			}
		}

		if (hls_out_mode == 0) {
			hls_out_ip = "";
			hls_out_port = 0;
		} else {
			var string = $('#hls_out_ip').val();
			var ip_port = string.split(/[;；]/);
			var port = [];
			var ip = [];
			for (var i = 0 ; i < ip_port.length ; i ++) {
				var arr = ip_port[i].split(/[:：]/);
				var str = [];
				if (arr.length > 1) {
					for (var j = 1 ; j < arr.length ; j ++) {
						str.push(arr[j]);
					}
				} 
				port.push(str.join(","));
				ip.push(arr[0]);
				hls_out_port = port;
				hls_out_ip = ip;
			}
		} 
		self.add_output_info = {
			"id" : id,
			"boardId" : board_id,
			"moduleId" : module_id,
			"type" : type,
			"programId" : program_id,
			"setPids" : pids_list,
			"audioPids" : audio_pids,
			"subtitlePids" : subtitle_pids,
			"outMode" : out_mode,
			"outIp" : out_ip,
			"lableId" : lable_id,
			"hlsOutMode" : hls_out_mode,
			"hlsOutIp" : hls_out_ip,
			"hlsOutPort" : hls_out_port
		}
	},
	
	addOutputConfig:function(out_config,board_id,module_id) {
		var self = this;
		var out_config_array = [];
		if (out_config.type == 2) {
			if (out_config.setPids == "") {
				self.common_mgr.modalShow("","info","The PID list cannot be null!",true,false);
				return ;
			}
			var pid_arr = out_config.setPids.split(/[,，]/);
			var npid_arr = pid_arr.sort();
			for (var i = 0; i < pid_arr.length; i++) {
				if (!self.IsNumber(pid_arr[i])) {
					self.common_mgr.modalShow("","Info","The PID must be a positive number!",true, false);
					return ;
				}
				if (pid_arr[i] < 1 || pid_arr[i] > 65535) {
					self.common_mgr.modalShow("","Info","The PID range from 1 to 65535!",true, false);
					return ;
				}
				if (npid_arr[i] == npid_arr[i + 1]) {
					self.common_mgr.modalShow("","Info","The PID " + npid_arr[i] + " repeats!",true, false);
					return ;
				}
			}
		}
		if (out_config.lableId == "") {
			self.common_mgr.modalShow("","info","The channel ID cannot be null!",true,false);
			return ;
		} 
		if(out_config.hlsOutMode == 0) {
			self.saveOutput(out_config,board_id,module_id);
		} else {
			console.log(out_config);
			for (var i = 0; i < out_config.hlsOutIp.length; i++) {
				if (out_config.hlsOutIp[i] == "") {
					self.common_mgr.modalShow("","info","The hls ip cannot be null!",true,false);
					return ;
				}
				if(!self.IsIp(out_config.hlsOutIp[i]))
		        {
					self.common_mgr.modalShow("","info","Invalid hls ip!",true,false);
		            return ;
		        }
				if (out_config.hlsOutPort[i] == "") {
					self.common_mgr.modalShow("","info","The hls port cannot be null!",true,false);
					return ;
				}
				if (!self.IsNumber(out_config.hlsOutPort[i])) {
					self.common_mgr.modalShow("","info","The hls port must be a positive integer!",true,false);
					return ;
				}
				if (out_config.hlsOutPort[i] < 1025 || out_config.hlsOutPort[i] > 65534) {
					self.common_mgr.modalShow("","info","The hls port range from 1025 to 65534!",true,false);
					return ;
				}
			}
			if (out_config.hlsOutMode == 2) {
				if (out_config.hlsOutIp.length >= 2) {
					self.common_mgr.modalShow("","info","Multicast support one output",true,false);
					return ;
				}
			}
			out_config.hlsOutIp = out_config.hlsOutIp.join(",");
			out_config.hlsOutPort = out_config.hlsOutPort.join(",");
			self.saveOutput(out_config,board_id,module_id);
		}
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
    IsNumber:function(number) {
        var ret = /^[1-9]\d*$/;
        if (ret.test(number) == false) {
            return false;
        } else {
            return true;
        }
    },
    ipToNumber : function(ip) {
    	 var num = 0; 
    	 var aNum = ip.split(".");
		 num += parseInt(aNum[0]) << 24; 
		 num += parseInt(aNum[1]) << 16; 
		 num += parseInt(aNum[2]) << 8; 
		 num += parseInt(aNum[3]) << 0; 
		 num = num >>> 0;// 这个很关键，不然可能会出现负数的情况
		 return num; 
	},
	saveOutput : function(out_config_array,board_id,module_id) {
		var self = this
		$.ajax({
			url : "/streaming/addOutputConfig?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify(out_config_array),
			async : false,
			success : function(data) {
				var result = data.result;
				if (result > 0) {
					self.getOutputById(board_id,module_id);
					self.drawStreamingInfo(board_id,module_id);
					self.getAllOutputCount();
				} else if(result == -2){
					self.common_mgr.modalShow("","info","The number of streams exceeds the maximum!",true,false);
				} else if(result == -3){
					self.common_mgr.modalShow("","info","The number of PIDs exceeds the maximum!",true,false);
				} else if(result == -1){
					self.common_mgr.modalShow("","info","Only support one tp！",true,false);	
				} else if(result == -4){
					self.common_mgr.modalShow("","info","The Channel ID already exists!",true,false);	
				}
			}
		})
	},
	
	getAudioPidByProListAndBoard : function() {
		var self = this;
		var board_id = $('#add_board_id').val()-1;
		var tp_id = $('#add_tp option:selected').val();
		$.ajax({
			url : "/streaming/getAudioPidByProListAndBoard?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"tp_id" : tp_id,
				"board_id" : board_id
			},
			async : false,
			success : function(data) {
				self.audio_pid_info = data;
			}
		})
	},
	
	isEqualIPAddress : function(addr1,addr2,mask) {
		if(!addr1 || !addr2 || !mask){
			console.log("各参数不能为空");
			return false;
		}
		var res1 = [];
		var res2 = [];
		addr1 = addr1.split(".");
		addr2 = addr2.split(".");
		mask = mask.split(".");
		for(var i = 0,ilen = addr1.length; i < ilen ; i += 1){
			res1.push(parseInt(addr1[i]) & parseInt(mask[i]));
			res2.push(parseInt(addr2[i]) & parseInt(mask[i]));
		}
		if(res1.join(".") == res2.join(".")){
			console.log("在同一个网段");
			return true;
		}else{
			console.log("不在同一个网段");
			return false;
		}
	}
}



