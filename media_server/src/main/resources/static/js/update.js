var updateMgr;
$(function() {
	if (updateMgr == undefined) {
		updateMgr = new UpdateMgr();
	}
});

function UpdateMgr() {
	var self = this;
	self.init();
}

UpdateMgr.prototype = {
	constructor : UpdateMgr,
	init : function() {
		var self = this;

		self.invalid_file = false;
		self.setIntervals = "";
		self.getHeartInfoIntervals = "";
		self.refresh_module_info = ";"
		self.pc_state = "";
		self.heart_info = "";
		self.checkArray = new Array();
		self.$module_list = $('#module_list');
		self.modal_update = $("#modal_update");
		
		self.common_mgr = new CommonMgr();
		self.getHeartInfo();
		self.init_view();
		self.init_data();
		self.init_event();
		self.init_update_modal();
//		self.initTableHeight();
		self.DialogInit();
	},
	init_update_modal:function(){
		var self = this;
		self.modal_update.on('show.bs.modal', function(){
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
	initTableHeight:function()
	{
		var self =this;
		var height = window.screen.height * 0.6;
		$("#table").css({"height": height});
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
	init_event : function() {

		var self = this;
		$('#upload').click(function() {
			$("#uploadFile").trigger("click");
		})
		
		$('#update_module').click(function() {
			if (self.invalid_file) {
				self.updateModule();
			} else {
				self.common_mgr.modalShow("", "info", "Please upload a valid software！", true, false);
			}
		})

		$('input[id=uploadFile]').change(function(e) {
			var filepath = $(this).val();
			var index1 = filepath.lastIndexOf("\\");
			var index2 = filepath.length;
			var filename = filepath.substring(index1 + 1, index2);
			$('#fileName').val(filename);
			var fileExtension = filename.split('.').pop().toLowerCase();
			if (fileExtension != "bin") {
				self.common_mgr.modalShow("", "info", "The file format is wrong!", true, false);
				$("#fileName").css({
					"color" : "red"
				});
			} else {
				$("#fileName").css({
					"color" : ""
				});
				//初步校验bin文件，开始上传文件
				self.modal_update.modal({backdrop: 'static', keyboard: false});
				self.uploadFile(filename);
			}
			e.target.value = '';
		});
	},

	init_view : function() {
		var self = this;
		var count = 0;
		var list = "";
		self.$module_list.empty();
		for (var i = 0; i < self.heart_info.length; i++) {
			if (self.heart_info[i].online_status != 1) {
				list += "<tr id=\"module" + self.heart_info[i].board_id
						+ self.heart_info[i].module_id
						+ "\" class=\"tr-link\">";
				list += "<td>" + (self.heart_info[i].board_id * 1 + 1)
						+ "</td>";
				list += "<td>" + (self.heart_info[i].module_id * 1 + 1)
						+ "</td>";
				if (self.heart_info[i].version != "") {
					list += "<td>" + "V" + self.heart_info[i].version + "</td>"
				} else {
					list += "<td>" + self.heart_info[i].version + "</td>"
				}
				list += "<td class=\"run_time" + self.heart_info[i].board_id + self.heart_info[i].module_id + "\">00:00:00</td>";
				list += "<td class=\"update_status\">Online</td>";
				list += "</tr>";
				self.checkArray.push(i);
			} 
			else {
				list += "<tr class=\"Offline\">";
				list += "<td>" + (self.heart_info[i].board_id * 1 + 1)
						+ "</td>";
				list += "<td>" + (self.heart_info[i].module_id * 1 + 1)
						+ "</td>";
				if (self.heart_info[i].version != "") {
					list += "<td>" + "V" + self.heart_info[i].version + "</td>"
				} else {
					list += "<td>" + "N/A" + "</td>"
				}
				list += "<td class=\"run_time\">00:00:00</td>";
				list += "<td>Offline</td>";
				list += "</tr>";
			}
			
		}
		self.$module_list.html(list);
		self.refresh_module_info = setInterval(() => {
			self.getModuleInfo();
		}, 1000);
	},

	init_data : function() {
		var self = this;
		self.info = [
			{board_id : 0 , module_id : 0 , status : 0 }, 
			{board_id : 0 , module_id : 1 , status : 0 },
			{board_id : 0 , module_id : 2 , status : 0 },
			{board_id : 0 , module_id : 3 , status : 0 },
			{board_id : 0 , module_id : 4 , status : 0 },
			{board_id : 0 , module_id : 5 , status : 0 },
			{board_id : 1 , module_id : 0 , status : 0 },
			{board_id : 1 , module_id : 1 , status : 0 },
			{board_id : 1 , module_id : 2 , status : 0 },
			{board_id : 1 , module_id : 3 , status : 0 },
			{board_id : 1 , module_id : 4 , status : 0 },
			{board_id : 1 , module_id : 5 , status : 0 },
			{board_id : 2 , module_id : 0 , status : 0 },
			{board_id : 2 , module_id : 1 , status : 0 },
			{board_id : 2 , module_id : 2 , status : 0 },
			{board_id : 2 , module_id : 3 , status : 0 },
			{board_id : 2 , module_id : 4 , status : 0 },
			{board_id : 2 , module_id : 5 , status : 0 },
			{board_id : 3 , module_id : 0 , status : 0 },
			{board_id : 3 , module_id : 1 , status : 0 },
			{board_id : 3 , module_id : 2 , status : 0 },
			{board_id : 3 , module_id : 3 , status : 0 },
			{board_id : 3 , module_id : 4 , status : 0 },
			{board_id : 3 , module_id : 5 , status : 0 },
			{board_id : 4 , module_id : 0 , status : 0 },
			{board_id : 4 , module_id : 1 , status : 0 },
			{board_id : 4 , module_id : 2 , status : 0 },
			{board_id : 4 , module_id : 3 , status : 0 },
			{board_id : 4 , module_id : 4 , status : 0 },
			{board_id : 4 , module_id : 5 , status : 0 }
			]
	},
	getHeartInfo : function() {
		var self = this;
		
		var getModuleArray = [];
		for (var i = 0; i < 5; i++) {
			for (var j = 0; j < 6; j++) {
				var obj=new Object();
				obj.board_id = i;
				obj.module_id = j;
				getModuleArray.push(obj);
			}
		}
		
		$.ajax({
			type : "post",
			url : "/configuration/getUpdateInfo?"+new Date().getTime(),
			dataType : "json",
			data : {
				"list" : JSON.stringify(getModuleArray)
			},
			async : false,
			success : function(data) {
				if (data != null) {
					self.heart_info = JSON.parse(data.data).update_list;
				}
			},
			error : function(textStatus) {
				console.log("textStatus:", textStatus);
			}
		})
	},

	getModuleInfo:function()
	{
		var self = this;
		
		var boxArray=[];
        for(var i=0;i<self.checkArray.length;i++){
            var obj=new Object();
            obj.board_id = self.heart_info[self.checkArray[i]].board_id;
            obj.module_id = self.heart_info[self.checkArray[i]].module_id;
            boxArray.push(obj);
        }
        
		$.ajax({
			type : "post",
			url : "/streaming/getModuleInfo?"+new Date().getTime(),
			dataType : "json",
			data : {
				"list" : JSON.stringify(boxArray)
			},
			async : false,
			success : function(data) {
				if (data != null) {
					for (var i = 0; i < JSON.parse(data.data).module_list.length; i++) {
						var time_running = self.formatSeconds(JSON.parse(data.data).module_list[i].uptime);
						$(".run_time" + JSON.parse(data.data).module_list[i].board_id + JSON.parse(data.data).module_list[i].module_id).html(time_running);
					}
				}
			},
			error : function(status){
				clearTimeout(self.refresh_module_info);
				window.location.reload();
			}
		})
	},
	
	updateModule : function() {
		var self = this;
		clearTimeout(self.getHeartInfoIntervals);
		if (self.checkArray.length > 0) {
			
			var boxArray=[];
	        for(var i=0;i<self.checkArray.length;i++){
	            var obj=new Object();
	            obj.board_id = self.heart_info[self.checkArray[i]].board_id;
	            obj.module_id = self.heart_info[self.checkArray[i]].module_id;
	            boxArray.push(obj);
	            
	            $('input[type=checkbox]').removeClass("check_line");
				$('input[type=checkbox]').attr("disabled",true);
				
	        }
	        //开始更新
	        self.modal_update.modal({backdrop: 'static', keyboard: false});
			$.ajax({
				url : "/configuration/updateModule?"+new Date().getTime(),
				type : "post",
				data : {
					"update_list" : JSON.stringify(boxArray)
				},
				success : function(result) {
					if (result.data == "SUCCESS:") {
						$('a').attr('href','#'); 
						$('#upload').attr('disabled',true);
						$('#update_module').attr('disabled',true);
						self.doUpdateModule();
					}
				},
			});
		} else {
			self.common_mgr.modalShow("", "info", "Please select module to update!", true, false);
		}
	},
	doUpdateModule:function()
	{
		var self = this;
		self.getHeartInfoIntervals = setInterval(function() {
			var num = 0;
			self.getHeartInfo();
			for (var i = 0; i < self.checkArray.length; i++) {
				console.log("index:",i," status : ",self.heart_info[self.checkArray[i]].update_status , " process : ",self.heart_info[self.checkArray[i]].update_percent);
				if (self.heart_info[self.checkArray[i]].update_status == 0) {
					$('#module' + self.heart_info[self.checkArray[i]].board_id + self.heart_info[self.checkArray[i]].module_id + " .update_status").html("Update: successful" );
					if (self.heart_info[self.checkArray[i]].update_percent == 100) {
						self.info[self.checkArray[i]].status = 1;
						$('#module' + self.heart_info[self.checkArray[i]].board_id + self.heart_info[self.checkArray[i]].module_id + " .update_status").html("Update: successful" );
					}
				} 
				else if (self.heart_info[self.checkArray[i]].update_status == 1) {
					$('#module' + self.heart_info[self.checkArray[i]].board_id + self.heart_info[self.checkArray[i]].module_id + " .update_status").html("Update: " + self.heart_info[self.checkArray[i]].update_percent + "%");
				} else if (self.heart_info[self.checkArray[i]].update_status == 2) {
					self.info[self.checkArray[i]].status = 1;
					$('#module' + self.heart_info[self.checkArray[i]].board_id + self.heart_info[self.checkArray[i]].module_id + " .update_status").html(self.heart_info[self.checkArray[i]].update_msg).css('color',"red");
				}
			}
			
			for (var i = 0; i < self.info.length; i++) {
				if (self.info[i].status == 1) {
					num ++;
				}
			}
			
			console.log("num:",num);
			console.log("checkArray:",self.checkArray.length);
			
			if (num == self.checkArray.length) {
				clearTimeout(self.getHeartInfoIntervals);
				self.modal_update.modal('toggle');
				self.common_mgr.modalShow("11", "info", "Please wait and don't leave or refresh the page!", false, false);
				$('input[type=checkbox]').addClass("check_line");
				$('input[type=checkbox]').attr("disabled",false);
				setTimeout(() => {
					self.updatePC();
				}, 30000);
			}
		}, 1000);
	},
	updatePC : function() {
		var self = this;
		$.ajax({
			url : "/configuration/updatePC?"+new Date().getTime(),
			type : "post",
			success : function(result) {
				setTimeout(() => {
					window.location.reload();
				}, 2000);
			}
		})
	},

	uploadFile : function(file_name) {
		var self = this;
		var form = new FormData(document.getElementById("files"));
		$.ajax({
			url : "/configuration/uploadFile?"+new Date().getTime(),
			type : "post",
			data : form,
			processData : false,
			contentType : false,
			success : function(result) {
				if (result) {
					self.checkFile(file_name);
				} else {
					self.modal_update.modal('toggle');
					self.common_mgr.modalShow("", "info", "Upload failed！No authority!", true, false);
				}
			},
		});
	},

	checkFile : function(file_name) {
		var self = this;
		$.ajax({
			url : "/configuration/checkFile?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"file_name" : file_name
			},
			success : function(data) {
				if (data.data == "SUCCESS:") {
					self.setIntervals = setInterval(function() {
						self.getPCState();
					}, 1000);
				}
			}
		})
	},
	
	getPCState : function() {
		var self = this;
		$.ajax({
			url : "/configuration/getPCState?"+new Date().getTime(),
			type : "get",
			success : function(data) {
				self.pc_state = JSON.parse(data.data)
				if (self.pc_state.upload_status == 2) {
					self.modal_update.modal('toggle');
					self.common_mgr.modalShow("", "info", "Check file failed! Please upload a valid software", true, false);
					$("#fileName").css({
						"color" : "red"
					});
					clearTimeout(self.setIntervals);
				} else if (self.pc_state.upload_status == 0) {
					self.modal_update.modal('toggle');
					self.common_mgr.modalShow("", "info", "Upload OK.Check file success.", true, false);
					self.invalid_file = true;
					clearTimeout(self.setIntervals);
				}
			}
		})
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
	
}
