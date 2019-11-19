var showModuleLogMgr;
$(function () {
    if (showModuleLogMgr == undefined)
    {
    	showModuleLogMgr = new ShowModuleLogMgr();
    }
});


function ShowModuleLogMgr()
{
    var self = this;
    self.init();
}

ShowModuleLogMgr.prototype={
	constructor: ShowModuleLogMgr,
	init:function() {
		var self = this;
		self.board_id = 0;
		self.module_id = 0;
		self.file_pos = 0;
        self.scroll_height = 0;
        self.scroll_loading = 0;
		self.refresh_module_log_status = 0;
		self.refresh_file_exits = 0;
		self.get_file_size = 0;
		self.has_log = false;
		self.file_size = 0;
		
		self.status = $('.loading-warp .text');
		
		self.initHeight();
		self.getLogFile();
		self.init_event();
	},
	
	init_event : function() {
		var self = this;
		
		$('#log_level').change(function() {
			self.file_pos = 0;
			self.has_log = true;
			$("#module_log").html("");
			clearTimeout(self.refresh_module_log_status);
			clearTimeout(self.refresh_file_exits);
			$('#load_gif').show();
            self.status.text("Loading");
            $('.loading-warp').show();
			self.getLogFile();
			self.scroll_height = $(".card-body")[0].scrollHeight;
            $(".card-body").scrollTop(self.scroll_height);
		})
		
		$('.card-body').scroll(function () {
            if ($(this).scrollTop() <= 0) {
                if (self.file_pos == 0) {
                	if (!self.has_log) {
                		$('#load_gif').hide();
                        self.status.text("No logs to load...");
                        $('.loading-warp').show();
                        setTimeout(function () {
                            $('.loading-warp').hide();
                        },1000)
                	}
                } else {
                	console.log("aaaa");
                	self.loadModuleLog();
                }
            }
        });
	},
	
	getLogFile:function() {
		var self = this;
		
		self.board_id = $('#board_id').text()-1;
		self.module_id = $('#module_id').text()-1;
		var log_level = $('#log_level option:selected').val();
		
		$.ajax({
			type : "post",
			url : "/configuration/getModuleLog?"+new Date().getTime(),
			dataType : "json",
			data : {
				"board_id" : self.board_id,
				"module_id" : self.module_id,
				"log_level" : log_level
			},
			success:function(data) {
				self.refresh_module_log_status = setInterval(() => {
					self.getPCState();
				}, 1000);
				
			}
		})
		
	},
	
	getPCState:function() {
		var self = this;
		
		$.ajax({
			url : "/configuration/getPCState?"+new Date().getTime(),
			type : "get",
			success : function(data) {
				pc_state = JSON.parse(data.data);
				if (pc_state.get_module_log_status == 0) {
					clearTimeout(self.refresh_module_log_status);
					self.get_file_size = setInterval(() => {
						self.getFileSize();
					}, 2000);
				}
			}
		})
	},
	getFileSize:function() {
		var self = this;
		var log_level = $('#log_level option:selected').val();
		$.ajax({
			url : "/configuration/getFileSize?"+new Date().getTime(),
			type : "post",
			data : {
				"type" : 0,
				"log_level" : log_level
			},
			success : function(data) {
				console.log("Previous file size:" , self.file_size);
				console.log("next file size:" ,data);
				if (self.file_size == data) {
					self.file_size = 0;
					clearTimeout(self.get_file_size);
					self.refresh_file_exits = setInterval(() => {
						self.getModuleLog();
					}, 3000);
				} else {
					self.file_size = data;
				}
			}
		})
	},
	getModuleLog:function() {
		var self = this;
		var log_level = $('#log_level option:selected').val();
		$.ajax({
			url : "/configuration/readLog?"+new Date().getTime(),
			type : "get",
			data : {
				"file_pos" : 0,
				"type" : 0,
				"log_level" : log_level
			},
			async : false,
			success : function(data) {
				console.log(data);
				if (data != "") {
					$("#module_log").html(data.str);
					self.file_pos = data.file_pos;
					self.has_log = false;
					$(".loading-warp").hide();
					self.scroll_height = $(".card-body")[0].scrollHeight;
	                $(".card-body").scrollTop(self.scroll_height);
	                clearTimeout(self.refresh_file_exits);
				} else {
					self.file_pos = 0;
					self.has_log = true;
					$('#load_gif').hide();
                    self.status.text("No logs to show...");
                    $('.loading-warp').show();
				}
			}
		})
	},
	
	loadModuleLog:function () {
		var self = this;
		var log_level = $('#log_level option:selected').val();
		$.ajax({
			url : "/configuration/readLog?"+new Date().getTime(),
			type : "get",
			dataType : "json",
			data : {
				"file_pos" : self.file_pos,
				"type" : 0,
				"log_level" : log_level
			},
			success : function(data) {
				self.has_log = false;
                $('#load_gif').hide();
                self.status.text("");
                var str = $('#module_log').html();
                $('#module_log').html(data.str+str );
                self.file_pos = data.file_pos;
                self.scroll_loading = $(".card-body")[0].scrollHeight;
                $(".card-body").scrollTop(self.scroll_loading - self.scroll_height);
                self.scroll_height = self.scroll_loading;
			}
		})
	},
	
	initHeight:function(){
		var self = this;
		var height = window.screen.height * 0.55 + "px";
		$("#module_log").css({"height": height});
	}
}