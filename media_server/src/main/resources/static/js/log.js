var logMgr;
$(function () {
    if (logMgr == undefined)
    {
    	logMgr = new LogMgr();
    }
});


function LogMgr()
{
    var self = this;
    self.init();

}

LogMgr.prototype={
	constructor: LogMgr,
	init:function() {
		var self = this;

		self.file_pos = 0;
        self.scroll_height = 0;
        self.scroll_loading = 0;
		self.refresh_module_log_status = 0;
		self.has_log = false;
		
		self.status = $('.loading-warp .text');
		
		self.initHeight();
		self.getCoreLog();
		self.init_event();
	},
	
	init_event : function() {
		var self = this;
		
		$('#log_level').change(function() {
			$("#core_log").html("");
			$('#load_gif').show();
            self.status.text("Loading");
            $('.loading-warp').show();
            self.getCoreLog();
            self.scroll_height = $(".card-body")[0].scrollHeight;
            $(".card-body").scrollTop(self.scroll_height);
		})
		
		$('.card-body').scroll(function () {
            if ($(this).scrollTop() <= 0) {
                if (self.file_pos == 0 ) {
                	if (!self.has_log) {
                		 $('#load_gif').hide();
                         self.status.text("No logs to load...");
                         $('.loading-warp').show();
                         setTimeout(function () {
                             $('.loading-warp').hide();
                         },1000)
					}
                } else {
                	self.loadCoreLog();
                }
            }
        });
	},
	isEmptyObject:function(e) {
		var t;
		for (t in e) {
			return !1;
		}
		return !0
	},
	getCoreLog:function() {
		var self = this;
		var log_level = $('#log_level option:selected').val();
		$.ajax({
			url : "/configuration/readLog?"+new Date().getTime(),
			type : "post",
			data : {
				"file_pos" : 0,
				"type" : 1,
				"log_level" : log_level
			},
			async : false,
			success : function(data) {
				if (data.str == "" || self.isEmptyObject(data)) {
					self.file_pos = 0;
					self.has_log = true;
					$('#load_gif').hide();
                    self.status.text("No logs to show...");
//                    $('.loading-warp').show();
				} else {
					$("#core_log").html(data.str);
					self.file_pos = data.file_pos;
					$(".loading-warp").hide();
					self.scroll_height = $(".card-body")[0].scrollHeight;
	                $(".card-body").scrollTop(self.scroll_height);
				}
			}
		})
	},
	
	loadCoreLog:function () {
		var self = this;
		var log_level = $('#log_level option:selected').val();
		$.ajax({
			url : "/configuration/readLog?"+new Date().getTime(),
			type : "get",
			dataType : "json",
			data : {
				"file_pos" : self.file_pos,
				"type" : 1,
				"log_level" : log_level
			},
			success : function(data) {
				self.has_log = false;
                $('#load_gif').hide();
                self.status.text("");

                var str = $('#core_log').html();
                $('#core_log').html(data.str+str );
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
		$("#core_log").css({"height": height});
	}
}