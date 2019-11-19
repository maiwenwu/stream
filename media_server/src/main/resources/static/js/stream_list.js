function StreamListMgr()
{
    var self = this;
    self.init();
}

$(function() {
	var streamListMgr = new StreamListMgr();
})

StreamListMgr.prototype =
{
	 constructor:StreamListMgr,
	 init:function(){
		var self = this ;
		
		self.common_mgr = new CommonMgr();
		
		self.all_lock_signal = "";
		self.aStrUipol = [ 'H', 'V', 'L', 'R' ];
		self.checkArray = new Array();
		self.DELETE_STREAM_OK = 300;
		self.all_online_info = "";
		self.pageNumber = 1;
		self.getStreamArray = [];
		self.refresh_get_stream_info = 0;
		
		self.init_view();
		self.init_data();
		self.init_event();
	 },
	 
	 init_view : function() {
		 var self = this;
		 self.initTableHeight();
		 self.DialogInit();
	 },
	 
	 init_data : function() {
		 var self = this;
		 
		 for (var i = 0; i < 5; i++) {
			for (var j = 0; j < 6; j++) {
				var obj=new Object();
				obj.board_id = i;
				obj.module_id = j;
				self.getStreamArray.push(obj);
			}
		}
		 
		 self.getAllLockSignal();
		 self.getBoardOnlineInfo();
		 self.getAllOutput();
	 },
	 
	 init_event : function() {
		 var self = this;
		
		$('#delete_stream').click(function (){
			if(self.checkArray.length < 1)
			{
				self.common_mgr.modalShow("","Warning","Please select a record to delete!",true,false);
				return;
			}else {
				$('#cancel').html("No");
				$('#ok').html("Yes");
				self.common_mgr.modalShow(self.DELETE_STREAM_OK,"Warning","Do you want to delete it ?",true,true);
			}
		});
		
		$('#board_id').change(function() {
			self.pageNumber = 1;
			self.getStreamArrays();
			self.getAllOutput();
		})
		
		$('#module_id').change(function() {
			self.pageNumber = 1;
			self.getStreamArrays();
			self.getAllOutput();
		})
		
		$('#page_size').change(function() {
			self.pageNumber = 1;
			self.getAllOutput();
		})
		 
	 },
	 
	 unique : function(array) {
		var re = [array[0]];
		for (var i = 1; i < array.length; i++) {
			var isunique = true;
			for (var r = 0; r < re.length; r++) {
		       		if (re[r].board_id === array[i].board_id && re[r].module_id === array[i].module_id) {
		              		isunique = false;
		       		}
			}
		    if (isunique) {
		            re.push(array[i]);
		    }
		}
		return re;
	 },
	 
	 getStreamArrays : function() {
		var self= this
		 
		self.getStreamArray = [];
		clearTimeout(self.refresh_get_stream_info);
		var board_id = $('#board_id option:selected').val();
		var module_id = $('#module_id option:selected').val();
		
		for (var i = 0; i < 5; i++) {
			for (var j = 0; j < 6; j++) {
				if (board_id == "" && module_id == "") {
					var obj=new Object();
					obj.board_id = parseInt(i);
					obj.module_id = parseInt(j);
					self.getStreamArray.push(obj);
				} else if (board_id != "" && module_id == "") {
					var obj=new Object();
					obj.board_id = parseInt(board_id);
					obj.module_id = parseInt(j);
					self.getStreamArray.push(obj);
				} else if (board_id == "" && module_id != "") {
					var obj=new Object();
					obj.board_id = parseInt(i);
					obj.module_id = parseInt(module_id);
					self.getStreamArray.push(obj);
				} else if (board_id != "" && module_id != "") {
					var obj=new Object();
					obj.board_id = parseInt(board_id);
					obj.module_id = parseInt(module_id);
					self.getStreamArray.push(obj);
					
				}
			}
		}
		self.getStreamArray = self.unique(self.getStreamArray);
	 },
	 
	getStreamInfo : function() {
			 
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
				console.log("textStatus:",status);
					clearTimeout(self.refresh_get_stream_info);
				}
			})
	},
	
	setStreamInfo : function(stream_info) {
		var self = this ; 
		for (var i = 0; i < stream_info.length; i++) {
			var info = stream_info[i].stream_status;
			for (var j = 0; j < info.length; j++) {
				var time_running = self.formatSeconds(info[j].time_running);
				$(".stream" + stream_info[i].board_id + stream_info[i].module_id + " #bitrate" + info[j].output_id).html(info[j].speed/10+" Mb/s");
				$(".stream" + stream_info[i].board_id + stream_info[i].module_id + " #run_time" + info[j].output_id).html(time_running);
			}
		}
	},
	
	formatSeconds : function(seconds) {
		
		var min = 0;
		var hour = 0;
		var result;
		
		if (seconds > 60) {
			min = parseInt(seconds/60);
			seconds = parseInt(seconds%60);
			if (min > 60) {
				hour = parseInt(min/60);
				min = parseInt(min%60);
			}
		}
		
		hour = (hour<10)?"0"+hour:hour;
		min = (min<10)?"0"+min:min;
		seconds = (seconds<10)?"0"+seconds:seconds;
		
		result = hour+":"+min+":"+seconds;
		
		return result;
		
	} ,
	
	 getAllOutput : function() {
		 var self = this;
		 var board_id = $('#board_id option:selected').val();
		 var module_id = $('#module_id option:selected').val();
		 var page_size = $('#page_size option:selected').val();
		 
		 $.ajax({
				url : "/streaming/selectAllStreams?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"board_id" : board_id,
					"module_id" : module_id,
					"page" : self.pageNumber,
					"size" : page_size
				},
				success : function(data) {
					if (data.list.length > 0) {
						self.refresh_get_stream_info = setInterval(() => {
							self.getStreamInfo();
						}, 1000);
					} else {
						clearTimeout(self.refresh_get_stream_info);
					}
					self.createOptionStream(data.list);
					self.createPagination(data);
				}
			})
	 },
	 
	 getAllLockSignal : function () {
		var self = this
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

	createOptionStream : function(stream_list) {
		var self = this;
		var list = "";
		var input = "";
		$('#stream_list').empty();
		for (var i = 0; i < stream_list.length; i++) {
			if (stream_list[i].type == 0) {
				input = stream_list[i].program.serviceName;
			} else if (stream_list[i].type == 1) {
				for (var j = 0; j < self.all_lock_signal.length; j++) {
					if (stream_list[i].boardId == self.all_lock_signal[j].boardId && stream_list[i].moduleId == self.all_lock_signal[j].moduleId) {
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
			} else if (stream_list[i].type == 2) {
				input= stream_list[i].setPids ; 
			}
			
			var ouput = ((stream_list[i].outMode == 0)?"http://":"udp://@") + stream_list[i].outIp + ":" + stream_list[i].outPort;
			
			list += "<tr  class=\"stream" + stream_list[i].boardId + stream_list[i].moduleId + "\">";
			list += "<td width=\"10%\">" + (stream_list[i].boardId + 1) + "</td>";
			list += "<td width=\"10%\">" + (stream_list[i].moduleId + 1) + "</td>";
			list += "<td width=\"20%\" title=\"" + input + "\" style=\"max-width:300px;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;\">" + input + "</td>";
			list += "<td width=\"20%\">" + ouput + "</td>";
			list += "<td width=\"10%\" class=\"bitrate\" id=\"bitrate" + stream_list[i].id + "\">" + "0 Mb/s" + "</td>";
			list += "<td width=\"15%\" class=\"run_time\" id=\"run_time" + stream_list[i].id + "\">" + "00:00:00" + "</td>";
			if (stream_list[i].outState == 1) {
				list += "<td width=\"10%\" class=\"status" + stream_list[i].boardId + stream_list[i].moduleId + "\">Running</td>";
			} else {
				list += "<td width=\"10%\" class=\"status" + stream_list[i].boardId + stream_list[i].moduleId + "\">Stopped</td>";
			}
			
			list += "</tr>";
		}
		$('#stream_list').append(list);
		
		self.checkOnline();
		
	},
	
	checkOnline : function() {
		var self = this;
		
		for (var i = 0; i < self.all_online_info.length; i++) {
			if (self.all_online_info[i].online_status == 1) {
				$('.stream' + self.all_online_info[i].board_id + self.all_online_info[i].module_id + ' input[type=checkbox]').removeClass("check_line");
				$('.stream' + self.all_online_info[i].board_id + self.all_online_info[i].module_id + ' input[type=checkbox]').attr("disabled",true);
				$('.stream' + self.all_online_info[i].board_id + self.all_online_info[i].module_id).removeClass("tr-link");
				$('.stream' + self.all_online_info[i].board_id + self.all_online_info[i].module_id).removeClass("tr-in");
				$('.stream' + self.all_online_info[i].board_id + self.all_online_info[i].module_id).addClass("Offline");
				$('.status' + self.all_online_info[i].board_id + self.all_online_info[i].module_id).html("Stopped");
			} else {
				$('.stream' + self.all_online_info[i].board_id + self.all_online_info[i].module_id).addClass("tr-link");
				$('.tr-link').unbind('click').click(function() {
					$('.tr-link').removeClass("tr-in");
					$(this).addClass("tr-in");
					var tr_in = $(this).find("input[type=checkbox]");
					if (tr_in.prop('checked')) {
						tr_in.prop('checked', false);
						for (var i = 0; i < self.checkArray.length; i++) {
							if (self.checkArray[i] == tr_in.val()) {
								self.checkArray.splice(i, 1);
							}
						}
					} else {
						tr_in.prop('checked', true);
						self.checkArray.push(tr_in.val());
					}
				});
			}
		}
		
	},
	
	deleteStream : function() {
		var self = this;
		$.ajax({
			url : "/streaming/deleteStream?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify(self.checkArray),
			success : function(data) {
				self.getAllOutput();
			}
		})
	},
	
	getBoardOnlineInfo : function() {
		var self = this;
		$.ajax({
			type : "get",
			url : "/search/getOnlineInfo?"+new Date().getTime(),
			dataType : "json",
			async : false,
			success : function(data) {
				self.all_online_info = JSON.parse(data.data).online_info;
			}
		})
	},
	
	initTableHeight:function()
	{
		var self =this;
		var height = window.screen.height * 0.6;
		$("#table").css({"height": height});
	},
	
	createPagination:function(data)
	{
		var self =this;
		$('.pagination').empty();
		var list = '';
		if (data.pageNum == 0) {
			list += "<li ><button class=\"page-link\"value=\"1\">First</button></li>";
			list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + 1
					+ "\" >Previou</button></li>";
			list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
					+ 1 + "\" >" + 1 + "</button></li>"
			list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
			list += "<li ><button class=\"page-link\"value=\"" + 1
					+ "\" >End</button></li>";
		} else {
			if (data.isFirstPage && !data.isLastPage) {
				if (data.navigateLastPage > 2) {
					list += "<li ><button class=\"page-link\"value=\"1\">First</button></li>";
					list += "<li class=\"page-item disabled\"><button class=\"page-link\">Previou</button></li>";
					list += "<li class=\"page-item active\"><button  class=\"page-link\" value=\""
							+ data.pageNum
							+ "\">"
							+ data.pageNum + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum + 1) + "\">"
							+ (data.pageNum + 1) + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum + 2) + "\">"
							+ (data.pageNum + 2) + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum + 1)
							+ "\">Next</button></li>"
					list += "<li ><button class=\"page-link\"value=\"" + data.pages
							+ "\">End</button></li>";
				} else {
					list += "<li ><button class=\"page-link\"value=\"1\">First</button></li>";
					list += "<li class=\"page-item disabled\"><button class=\"page-link\">Previou</button></li>";
					list += "<li class=\"page-item active\"><button  class=\"page-link\" value=\""
							+ data.pageNum
							+ "\">"
							+ data.pageNum + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum + 1) + "\">"
							+ (data.pageNum + 1) + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum + 1)
							+ "\">Next</button></li>"
					list += "<li ><button class=\"page-link\"value=\"" + data.pages
							+ "\">End</button></li>";
				}
				
			}

			if (!data.isFirstPage && !data.isLastPage) {
				list += "<li ><button class=\"page-link\"value=\"1\">First</button></li>";
				list += "<li ><button class=\"page-link\"value=\"" + (data.pageNum - 1)
						+ "\">Previou</button></li>";
				list += "<li ><button  class=\"page-link\" value=\""
						+ (data.pageNum - 1) + "\" >"
						+ (data.pageNum - 1) + "</button></li>"
				list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
						+ data.pageNum
						+ "\">"
						+ data.pageNum + "</button></li>"
				list += "<li ><button  class=\"page-link\" value=\""
						+ (data.pageNum + 1) + "\" >"
						+ (data.pageNum + 1) + "</button></li>"
				list += "<li ><button  class=\"page-link\" value=\""
						+ (data.pageNum + 1)
						+ "\">Next</button></li>"
				list += "<li ><button class=\"page-link\"value=\"" + data.pages
						+ "\"  >End</button></li>";
			}

			if (data.isLastPage && !data.isFirstPage) {
				if (data.navigateLastPage > 2) {
					list += "<li ><button class=\"page-link\"value=\"1\" >First</button></li>";
					list += "<li ><button class=\"page-link\"value=\"" + (data.pageNum - 1)
							+ "\" >Previou</button></li>";
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum - 2) + "\">"
							+ (data.pageNum - 2) + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum - 1) + "\">"
							+ (data.pageNum - 1) + "</button></li>"
					list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
							+ (data.pageNum)
							+ "\">"
							+ (data.pageNum) + "</button></li>"
					list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
					list += "<li ><button class=\"page-link\"value=\"" + data.pages
							+ "\">End</button></li>";
				} else {
					list += "<li ><button class=\"page-link\"value=\"1\" >First</button></li>";
					list += "<li ><button class=\"page-link\"value=\"" + (data.pageNum - 1)
							+ "\" >Previou</button></li>";
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum - 1) + "\">"
							+ (data.pageNum - 1) + "</button></li>"
					list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
							+ (data.pageNum)
							+ "\">"
							+ (data.pageNum) + "</button></li>"
					list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
					list += "<li ><button class=\"page-link\"value=\"" + data.pages
							+ "\">End</button></li>";
				}
			}

			if (data.isFirstPage && data.isLastPage) {
				list += "<li ><button class=\"page-link\"value=\"1\">First</button></li>";
				list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + (data.pageNum - 1)
						+ "\" >Previou</button></li>";
				list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
						+ (data.pageNum) + "\" >" + (data.pageNum) + "</button></li>"
				list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
				list += "<li ><button class=\"page-link\"value=\"" + data.pages
						+ "\" >End</button></li>";
			}
		}
		$('.pagination').append(list);
		$('.page-link').click(function(){
			self.doPagination(this);
		})
	},
	
	doPagination:function(obj) {
		var self =this;
		self.checkArray.length = 0;
		$('#check_all').prop('checked',false);
		var board_id = $('#board_id option:selected').val();
		var module_id = $('#module_id option:selected').val();
		var page_size = $('#page_size option:selected').val();
		$.ajax({
			url : "/streaming/selectAllStreams?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"page" : obj.value, 
				"size" : page_size,
				"board_id" : board_id,
				"module_id" : module_id
			},
			success : function(data) {
				if (data != null) {
					self.pageNumber = data.pageNum;
					self.createOptionStream(data.list);
					self.createPagination(data);
				}
			}
		})
	},
	
	DialogInit:function()
	{
		var self =this;
	    options={
	        OkClick:function (handler) {
	            if(handler == self.DELETE_STREAM_OK)
	            {
	            	self.deleteStream();
	            }
	        },
	        CancelClick:function (handler) {
	        }
	    };
	    self.common_mgr.initDialog(options);
	},
}
