function HlsManagerMgr()
{
    var self = this;
    self.init();
}

$(function() {
	var hls_manager_mgr = new HlsManagerMgr();
})

HlsManagerMgr.prototype =
{
	constructor:HlsManagerMgr,
	init:function()
	{
		var self = this
		$("body").keydown(function(e){
            var ev = window.event || e;
            var code = ev.keyCode || ev.which;
            if (code == 13) {
            	$("#login").focus();
            }
        });
		
		self.checkArray = [];
		self.stream_state = ['Stable' , 'Interrupt' , 'Download.failed' , 'Transcoding' , 'Transcoding.failed' , 'Not.issue' ,'Issue'];
		self.refresh_time = 0;
		self.refresh_synchronize = 0;
		self.channelRecordList = "";
		self.localIp = "";
		self.pageNumber = 1;
		self.init_view();
		self.init_data();
		self.init_event();
	},
	initTableHeight:function(){
		var self = this;
		var height = window.screen.height * 0.45 ;
		$(".table-body").css({"height": height});
	},
	init_view:function()
	{
		var self = this;
	},
	init_data:function()
	{
		var self = this;
		$('#board_id').val(localStorage.getItem('hls_board_id'));
		$('#module_id').val(localStorage.getItem('hls_module_id'));
		self.getLocalIp();
		self.getBoardOnlineInfo();
		self.getChannelRecord();
		self.getSynchronize();
		self.initTableHeight();
		
//		clearTimeout(self.refresh_synchronize);
//  	    self.refresh_synchronize=setInterval(() => {
//  		    self.getSynchronize();
//  	    }, $('#refresh_time').val() * 1000);
	},
	init_event:function()
	{
		var self = this;
		
		$('#page_size').change(function() {
			self.pageNumber = 1;
			self.getChannelRecord();
			self.getSynchronize();
		})
		
		$('#key_word').on('input', function() {
			self.pageNumber = 1;
			self.getChannelRecord();
			self.getSynchronize();
		})
		
		$('#board_id').change(function() {
			localStorage.setItem("hls_board_id",$('#board_id option:selected').val());
			self.pageNumber = 1;
			self.getChannelRecord();
			self.getSynchronize();
		})
		
		$('#module_id').change(function() {
			localStorage.setItem("hls_module_id",$('#module_id option:selected').val());
			self.pageNumber = 1;
			self.getChannelRecord();
			self.getSynchronize();
		})
		
		$("#refresh_time").bind("input propertychange",function(event){
	       self.refresh_time = $("#refresh_time").val();
	       if (self.refresh_time == "") {
	    	   clearTimeout(self.refresh_synchronize);
	       } else {
	    	   var ival = parseInt(self.refresh_time);
	    	   if (!isNaN(ival)) { //判断是否是数字
	    		   if (ival == 0) {
	    			   clearTimeout(self.refresh_synchronize); 
	    		   } else {
	    			   clearTimeout(self.refresh_synchronize);
			    	   self.refresh_synchronize=setInterval(() => {
			    		   self.getSynchronize();
			    	   }, self.refresh_time * 1000);
	    		   }
	    	   } else {
	    		   clearTimeout(self.refresh_synchronize); 
	    	   }
	       }
		});
		
		$('#synchronize').click(function() {
			self.getSynchronize();
		})
		
	},
	
	getSynchronize : function() {
		var self = this;
		$.ajax({
			url : "/cdn/record/sync?"+new Date().getTime(),
			type : "get",
			dataType : "json",
			success : function(data) {
				console.log("data:" , data);
				for (var i = 0; i < data.length; i++) {
					var status = (data[i].state == 0)? "Interrupt" : self.stream_state[data[i].state - 100];
					$('.bitrate' + data[i].channelId).html(data[i].bitrate + " kbps");
					$('.status' + data[i].channelId).html(status);
				}
			}
		})
	},
	
	getChannelRecord : function() {
		var self = this;
		var page_size = $('#page_size option:selected').val();
		var channel_id = $('#key_word').val();
		var board_id = localStorage.getItem('hls_board_id');
		var module_id = localStorage.getItem('hls_module_id');
		$.ajax({
			type : "post",
			url : "/cdn/getRecord?"+new Date().getTime(),
			dataType : "json",
			async : false,
			data : {
				"channel_id" : channel_id,
				"page" : self.pageNumber,
				"size" : page_size,
				"board_id" : board_id,
				"module_id" : module_id
			},
			success : function(data) {
				self.ctreateOptionHls(data.list);
				self.channelRecordList = data.list;
				$('#total').html(" " + data.total + " ");
				self.createPagination(data);
			}
		})
	},
	
	getLocalIp : function() {
		var self = this;
		
		$.ajax({
			type : "get",
			url : "/streaming/getLocalIp",
			async : false,
			success : function(data) {
				self.localIp = data;
			}
		})
	},
	
	ctreateOptionHls : function(data) {
		var self = this;
		var list = '';
		$('#hls_list').empty();
		var page_size = $('#page_size option:selected').val();
		var id = (self.pageNumber-1) * page_size;
		if (data.length > 0) {
			for (var i = 0; i < data.length; i++) {
				var bitrate = (data[i].bitrate == null) ? 0 : data[i].bitrate;
				var status = (data[i].state == 0)? "Interrupt" : self.stream_state[data[i].state - 100];
				var out_url = (data[i].hlsOutMode == 0)?"http://" + self.localIp + ":8182/" + data[i].channelId : "udp://@" + data[i].hlsOutIp + ":" + data[i].hlsOutPort;
				list += "<tr class=\"stream" + data[i].boardId + data[i].moduleId + "\">";
				list += "<td width=\"5%\">" + (id*1 + i + 1) + "</td>";
				list += "<td width=\"10%\">" + data[i].channelId + "</td>";
				list += "<td width=\"10%\">" + (data[i].boardId + 1) + "/" + (data[i].moduleId + 1) + "</td>";
				list += "<td width=\"25%\" title=\"" + data[i].channelName + "\" style=\"max-width:200px;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;\">" + data[i].channelName + "</td>";
				list += "<td width=\"30%\">" + out_url + "</td>";
				list += "<td width=\"10%\" class=\"bitrate" + data[i].channelId + "\">" + bitrate + " kbps" + "</td>";
				list += "<td width=\"10%\" class=\"status" + data[i].channelId + "\">" + status + "</td>";
				list += "</tr>";
			}
			$('#hls_list').append(list);
			$('#first').html(" " + (id + 1) + "");
			$('#last').html(" " + (id + data.length) + "");
		} else {
			$('#first').html(" " + (0) + "");
			$('#last').html(" " + (0) + "");
		}
		
		$('.check_line').click(function(e){
			e.stopPropagation();
			if(this.checked)
			{
				self.checkArray.push(this.value);
			}else{
				for(var i=0;i<self.checkArray.length;i++)
	            {
	                if(self.checkArray[i] == this.value)
	                {
	                    self.checkArray.splice(i,1);
	                }
	            }
			}
		});
		self.checkOnline();
	},
	
	checkOnline : function () {
		var self = this;
		
		for (var i = 0; i < self.all_online_info.length; i++) {
			if (self.all_online_info[i].online_status == 1) {
				$('.stream' + self.all_online_info[i].board_id + self.all_online_info[i].module_id).removeClass("tr-link");
				$('.stream' + self.all_online_info[i].board_id + self.all_online_info[i].module_id).removeClass("tr-in");
				$('.stream' + self.all_online_info[i].board_id + self.all_online_info[i].module_id).addClass("Offline");
			} else {
				$('.stream' + self.all_online_info[i].board_id + self.all_online_info[i].module_id).addClass("tr-link");
				$('.tr-link').unbind('click').click(function() {
					$('.tr-link').removeClass("tr-in");
					$(this).addClass("tr-in");
				})
			}
		}
	},
	
	doPagination:function (obj) {
		var self = this;
		console.log(obj.value);
		var page_size = $('#page_size option:selected').val();
		var channel_id = $('#key_word').val();
		var board_id = $('#board_id option:selected').val();
		var module_id = $('#module_id option:selected').val();
		$.ajax({
			type : "post",
			url : "/cdn/getRecord?"+new Date().getTime(),
			dataType : "json",
			data : {
				"channel_id" : channel_id,
				"page" : obj.value,
				"size" : page_size,
				"board_id" : board_id,
				"module_id" : module_id
			},
			success : function(data) {
				if (data != null) {
					self.pageNumber = data.pageNum;
					self.ctreateOptionHls(data.list);
					$('#total').html(" " + data.total + " ");
					self.createPagination(data);
				}
			}
		})
	},
	
	createPagination:function(data) {
		var self = this;
		$('.pagination').empty();
		var list = '';
		if (data.pageNum == 0) {
			list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"1\">First</button></li>";
			list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + 1
					+ "\" >Previous</button></li>";
			list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
					+ 1 + "\" >" + 1 + "</button></li>"
			list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
			list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + 1
					+ "\" >Last</button></li>";
		} else {
			if (data.isFirstPage && !data.isLastPage) {
				if (data.navigateLastPage > 2) {
					list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"1\">First</button></li>";
					list += "<li class=\"page-item disabled\"><button class=\"page-link\">Previous</button></li>";
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
							+ "\">Last</button></li>";
				} else {
					list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"1\">First</button></li>";
					list += "<li class=\"page-item disabled\"><button class=\"page-link\">Previous</button></li>";
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
							+ "\">Last</button></li>";
				}
				
			}

			if (!data.isFirstPage && !data.isLastPage) {
				list += "<li ><button class=\"page-link\"value=\"1\">First</button></li>";
				list += "<li ><button class=\"page-link\"value=\"" + (data.pageNum - 1)
						+ "\">Previous</button></li>";
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
						+ "\"  >Last</button></li>";
			}

			if (data.isLastPage && !data.isFirstPage) {
				if (data.navigateLastPage > 2) {
					list += "<li ><button class=\"page-link\"value=\"1\" >First</button></li>";
					list += "<li ><button class=\"page-link\"value=\"" + (data.pageNum - 1)
							+ "\" >Previous</button></li>";
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
					list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + data.pages
							+ "\">Last</button></li>";
				} else {
					list += "<li ><button class=\"page-link\"value=\"1\" >First</button></li>";
					list += "<li ><button class=\"page-link\"value=\"" + (data.pageNum - 1)
							+ "\" >Previous</button></li>";
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum - 1) + "\">"
							+ (data.pageNum - 1) + "</button></li>"
					list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
							+ (data.pageNum)
							+ "\">"
							+ (data.pageNum) + "</button></li>"
					list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
					list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + data.pages
							+ "\">Last</button></li>";
				}
			}

			if (data.isFirstPage && data.isLastPage) {
				list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"1\">First</button></li>";
				list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + (data.pageNum - 1)
						+ "\" >Previous</button></li>";
				list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
						+ (data.pageNum) + "\" >" + (data.pageNum) + "</button></li>"
				list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
				list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + data.pages
						+ "\" >Last</button></li>";
			}
		}
		
		$('.pagination').append(list);
		
		$('.page-link').click(function(){
			self.doPagination(this);
			self.getSynchronize();
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
				console.log("getAllHeartInfo:",JSON.parse(data.data));
				self.all_online_info = JSON.parse(data.data).online_info;
			}
		})
	},
}