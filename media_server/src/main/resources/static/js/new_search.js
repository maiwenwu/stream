function SearchMgr()
{
	var self = this;
}

$(function() {
	var search_mgr = new SearchMgr(); 
	search_mgr.init();
}); 

SearchMgr.prototype = {
	constructor:SearchMgr,
	
	init : function() {
		var self = this;
		
		self.common_mgr = new CommonMgr();
		
		self.lnb_freq = [ '5150', '5700', '5750', '9750', '10600', '10750', '11250', '11300', '11475', '5150/5750', 'Universal', '9750/10750', '9750/10700' ];
		self.aStrUipol = [ 'H', 'V', 'L', 'R' ];
		self.tone = [ 'On', 'Off' ];
		self.lnb_power = [ '13V', '18V' ];
		self.diseqc1_0 = [ 'None', 'LNB1', 'LNB2', 'LNB3', 'LNB4', 'LNB5' ];
		self.diseqc1_1 = [ 'None', 'LNB1', 'LNB2', 'LNB3', 'LNB4', 'LNB5', 'LNB6','LNB7', 'LNB8', 'LNB9', 'LNB10', 'LNB11', 'LNB12', 'LNB13', 'LNB14','LNB15' , 'LNB16' ];
		self.all_satellite = '';
		self.board_has_stream = false;
		self.isLock = false;
		self.minute = 0;
		self.seconds = 0;
		self.time= 0;
		self.lock_interval = 0;
		self.search_info = '';
		self.search_status = '';
		self.lock_status = '';
		self.change_board = '';
		self.module_has_stream = '';
		self.board_has_pro = '';
		self.total_pro = [];
		self.search_type = 0;  // 0 -> tp_saerch 1 -> satllite search 2 ->
								// blind search
		self.blind_search = false;
		self.pro = 0;
		self.sat_pro = 0;
		self.blind_pro = 0;
		self.tv_scroll = false;
		self.radio_scroll = false;
		self.search_tp_scroll = false;
		self.HANDLE_TP_SEARCH = 1;
		self.HANDLE_SAT_SEARCH = 2;
		self.HANDLE_BLIND_SEARCH = 3;
		self.search_tp_info = '';
		self.is_update_locksignal = false;
		
		self.init_widget();
		self.init_view();
		self.init_data();
		self.init_event();
		
	},
	
	init_widget : function() {
		var self = this;
		
		self.$lnb_type = $('#lnb_type');
		self.$strength = $('#strength');
		self.$quality = $('#quality');
		self.$satllite = $('#satllite');
		self.$tp = $("#tp");
		self.$board_id = $('#board_id');
		self.$tp_search = $('#tp_search');
		self.$sat_search = $('#sat_search');
		self.$blind_search = $('#blind_search');
		self.$exit = $('.exit');
	},
	
	init_view : function() {
		var self = this;
		
		self.$satllite.attr("disabled" , true);
		self.$tp.attr("disabled" , true);
		self.$lnb_type.attr("disabled" , true);
		self.$tp_search.attr("disabled" , true);
		self.$sat_search.attr("disabled" , true);
		self.$blind_search.attr("disabled" , true);
		
		self.initModalHeight();
		self.DialogInit();
		self.setSignalAndQuality(0,0);
		self.createLnbFreqOption();
		
	},
	
	init_data : function() {
		var self = this;
		
		clearTimeout(self.lock_interval);
		setTimeout(() => {
			self.autoRefreshBars();
		}, 3000);
		
		self.getBoardOnlineInfo();
		self.getSatlliteInfo();
	},
	
	init_event : function() {
		var self = this;
		
		self.$board_id.change(function(){
			
			self.getStreamByBoardIdAndModuleId();
			if (self.board_has_stream) {
				self.getRfByBoardId();
				self.getLockInfo();
				self.doLockSignal();
				self.change_board = $('#board_id option:selected').val();
				if (self.change_board == "") {
					self.$satllite.attr("disabled" , true);
					self.$tp.attr("disabled" , true);
					self.$lnb_type.attr("disabled" , true);
					self.$tp_search.attr("disabled" , true);
					self.$sat_search.attr("disabled" , true);
					self.$blind_search.attr("disabled" , true);
				} else {
					self.$satllite.attr("disabled" , false);
					self.$tp.attr("disabled" , false);
					self.$lnb_type.attr("disabled" , false);
					self.$tp_search.attr("disabled" , false);
					self.$sat_search.attr("disabled" , false);
					self.$blind_search.attr("disabled" , false);
				}
			} else {
				self.common_mgr.modalShow("","Info","There are some streams in the module, please delete first.",true,false);
				self.isLock = false;
				$("#board_id").val(self.change_board);
			}
		})
		
		self.$satllite.change(function(){
			self.getTpInfo();
			self.getStreamByBoardIdAndModuleId();
			if (self.board_has_stream) {
				if ($('#tp option:selected').val() == "") {
					self.isLock = false;
					self.setSignalAndQuality(0, 0);
				} else {
					self.doLockSignal();
				}
			} else {
				self.common_mgr.modalShow("","Info","There are some streams in the module, please delete first.",true,false);
				self.isLock = false;
			}
		})
		
		self.$tp.change(function() {
			self.getStreamByBoardIdAndModuleId();
			if (self.board_has_stream) {
				self.doLockSignal();
			} else {
				self.common_mgr.modalShow("","Info","There are some streams in the module, please delete first.",true,false);
				self.isLock = false;
			}
		})
		
		self.$lnb_type.change(function() {
			self.getStreamByBoardIdAndModuleId();
			if (self.board_has_stream) {
				self.doLockSignal();
				self.updateSatllite();
			} else {
				self.common_mgr.modalShow("","Info","There are some streams in the module, please delete first.",true,false);
				self.isLock = false;
			}
		})
		
		self.$exit.click(function(){
			self.stopSearch();
		})
		
		self.$tp_search.click(function() {	
			
			var board_id = $('#board_id option:selected').val();
			var sat_id = $('#satllite option:selected').val();
			var tp_id = $('#tp option:selected').val();
			var lnb_type = $('#lnb_type option:selected').text();	
			if (board_id == "") {
				self.common_mgr.modalShow("","Info","please select board_id",true,false);
			} else {
				self.getOutputByBoardIdAndSatIdAndTpId(board_id,sat_id,tp_id);
				self.getProgramsByTpId();
				if (self.module_has_stream.length > 0) {
					self.common_mgr.modalShow("","Info","There are some streams in the module, please delete first.",true,false);
				} else {
					if (self.board_has_pro.length > 0) {
						$('#cancel').html("No");
						$('#ok').html("Yes");
						self.common_mgr.modalShow(self.HANDLE_TP_SEARCH,"Info","Do you want to delete program exist？",true,true);
					} else {
						self.tpSearch();
					}
				}
			}
		})
		
		self.$sat_search.click(function() {
			var board_id = $('#board_id option:selected').val() ;
			var sat_id = $('#satllite option:selected').val();
			if (board_id == "") {
				self.common_mgr.modalShow("","Info","please select board_id",true,true);
			} else {
	// satSearch();
				self.getOutputByBoardIdAndSatIdAndTpId(board_id,sat_id,0);
				self.getProByBoardIdAndTpList();
				if (self.module_has_stream.length > 0) {
					self.common_mgr.modalShow("","Info","There are some streams in the module, please delete first.",true,false);
				} else {
					if (self.board_has_pro.length > 0) {
						$('#cancel').html("No");
						$('#ok').html("Yes");
						self.common_mgr.modalShow(self.HANDLE_SAT_SEARCH,"Info","Do you want to delete program exist？",true,true);
					} else {
						self.satSearch();
					}
				}
			}	
		})
		
		self.$blind_search.click(function() {
			var board_id = $('#board_id option:selected').val() ;
			var sat_id = $('#satllite option:selected').val();
			
			if (board_id == "") {
				self.common_mgr.modalShow("","Info","please select board_id",true,true);
			} else {
				self.getOutputByBoardIdAndSatIdAndTpId(board_id,sat_id,0);
				self.getProByBoardIdAndTpList();
				if (self.module_has_stream.length > 0) {
					self.common_mgr.modalShow("","Info","There are some streams in the module, please delete first.",true,false);
				} else {
					if (self.board_has_pro.length > 0) {
						$('#cancel').html("No");
						$('#ok').html("Yes");
						self.common_mgr.modalShow(self.HANDLE_BLIND_SEARCH,"Info","Do you want to delete program exist？",true,true);
					} else {
						self.blindSearch();
					}
				}
			}
		})
		
		$('#tv').scroll(function() {
	        var scrollTop = $(this)[0].scrollTop;
	        var scrollHeight =  $(this)[0].scrollHeight;
	        var windowHeight = $(this).height();
	 　		if(scrollTop + Math.ceil(windowHeight) >= Math.floor(scrollHeight)){
 　　　			self.tv_scroll = false;
 　　		} else {
 　　			self.tv_scroll = true;
 　  		}
		})
	
		$("#radio").scroll(function(){
			var scrollTop = $(this)[0].scrollTop;
	        var scrollHeight =  $(this)[0].scrollHeight;
	        var windowHeight = $(this).height();
	　		if(scrollTop + Math.ceil(windowHeight) >= Math.floor(scrollHeight)){
	　　　		self.radio_scroll = false;
	　　		} else {
	　　			self.radio_scroll = true;
	　　		}
		})
		
		$("#search_tp").scroll(function(){
			var scrollTop = $(this)[0].scrollTop;
	        var scrollHeight =  $(this)[0].scrollHeight;
	        var windowHeight = $(this).height();
	　		if(scrollTop + Math.ceil(windowHeight) >= Math.floor(scrollHeight)){
	　　　		self.search_tp_scroll = false;
	　　		} else {
	　　			self.search_tp_scroll = true;
	　　		}
		})
	},
	
	autoRefreshBars : function() {
		var self = this;
		
		self.lock_interval = setInterval(function() {
			if (self.isLock) {
				self.getSearchStatus();
			} 
		}, 1000);
	},
	
	tpSearch : function() {
		var self = this;
		self.is_update_locksignal = true;
		var board_id = $('#board_id option:selected').val();
		var sat_id = $('#satllite option:selected').val();
		var tp_id = $('#tp option:selected').val();
		var lnb_type = $('#lnb_type option:selected').text();	
		
		self.deleteSearchPrograms();
		$.ajax({
			url : "/search/tpSearch?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id ,
				"sat_id" : sat_id,
				"tp_id" : tp_id,
				"lnb_type" : lnb_type
			},
			success : function(data) {
				if (data.data == "SUCCESS:") {
					
					self.blind_search = false;
					self.search_type = 0;
					self.showSearch(self.search_type);
					
					console.log("search_status:",self.search_status);
					
					$('#search_process .progressbar-text').css({"width" : "100%"});
					$('#search_process').progressbar('setValue', 0);
					$('#search_process .progressbar-text').html("");
					
					self.pro = setInterval(() => {
						if (self.search_status == 0) {
							self.getProByTpId(self.search_type,0,board_id);
							$('#search_process').progressbar('setValue', "100");
							$('#search_process .progressbar-text').html("");
							$('#search_process .progressbar-value .progressbar-text').html("100%");
							self.completeSearch();
						} else if (self.search_status == 1) {
							self.getProByTpId(self.search_type,0,board_id);
						}
					}, 1000);
				}
			}
		})
	},
	
	satSearch : function() {
		var self = this;
		self.is_update_locksignal = true;
		var board_id = $('#board_id option:selected').val() ;
		var sat_id = $('#satllite option:selected').val();
		var lnb_type = $('#lnb_type option:selected').text();
		
		self.deleteSearchPrograms();
		
		$.ajax({
			url : "/search/satSearch?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id,
				"sat_id" : sat_id,
				"lnb_type" : lnb_type
			},
			success : function(data) {
				self.blind_search = false;
				self.total_pro = [];
				self.search_type = 1;
				self.showSearch(self.search_type);
				
				$('#search_process_div').css({"display" : "block"});
				$('#search_process .progressbar-text').css({"width" : "100%"});
				$('#search_process').progressbar('setValue', 0);
				$('#search_process .progressbar-text').html("");
				
				self.sat_pro = setInterval(() => {
					if (self.search_status == 0) {
						
						$('#search_process').progressbar('setValue', "100");
						$('#search_process .progressbar-text').html("");
						$('#search_process .progressbar-value .progressbar-text').html("100%");
						
						self.total_pro = [];
						self.getProByTpId(self.search_type,0,board_id);
						console.log("total_pro:",self.total_pro);
						self.showPrograms(self.search_type,self.total_pro);
						self.completeSearch();
						
					} else if(self.search_status == 1) {
						
						$('#search_process').progressbar('setValue', self.search_info.search_percent);
						$('#search_process .progressbar-text').html("");
						$('#search_process .progressbar-value .progressbar-text').html(self.search_info.search_percent + "%");
						
						self.total_pro = [];
						self.getProByTpId(self.search_type,0,board_id);
						console.log("total_pro:",self.total_pro);
						self.showPrograms(self.search_type,self.total_pro);
					}
				}, 1000);
			}
		})
	} ,
	
	blindSearch : function() {
		var self = this;
		self.is_update_locksignal = true;
		var board_id = $('#board_id option:selected').val();
		var sat_id = $('#satllite option:selected').val();
		var lnb_type = $('#lnb_type option:selected').text();
		
		self.deleteSearchPrograms();
		
		$.ajax({
			url : "/search/blindSearch?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id ,
				"sat_id" : sat_id,
				"lnb_type" : lnb_type
			},
			success : function(data) {
				if (data.data == "SUCCESS:") {
					
					self.isLock = true;
					
					self.deleteSearchPrograms();
					self.deleteSearchTp();
					
					$('#search_tp_process_div').css({"display" : "block"});
					$('#search_tp_process .progressbar-text').css({"width" : "100%"});
					$('#search_tp_process').progressbar('setValue', 0);
					$('#search_tp_process .progressbar-text').html("");
					
					$('#search_process_div').css({"display" : "block"});
					$('#search_process .progressbar-text').css({"width" : "100%"});
					$('#search_process').progressbar('setValue', 0);
					$('#search_process .progressbar-text').html("");
					
					self.total_pro = [];
					
					var sat_name= $('#satllite option:selected').text().split('-')[1];
					$('#tp_modal').modal('show');
					self.timedCount();
					$('.search_info').text(sat_name);
					
					self.blind_pro = setInterval(() => {
						console.log("search_status:",self.search_status);
						if (self.search_status == 0) {
							
							$('#search_process').progressbar('setValue', "100");
							$('#search_process .progressbar-text').html("");
							$('#search_process .progressbar-value .progressbar-text').html("100%");
							
							self.search_type = 1;
							self.total_pro = [];
							self.getProByTpId(self.search_type,0,board_id);
							console.log("total_pro:",self.total_pro);
							self.showPrograms(self.search_type,self.total_pro);
							
// self.max_tp_id = 0;
							self.getTpInfo();
							
							self.completeSearch();
							
						} else if (self.search_status == 1) {
							$('#search_process').progressbar('setValue', self.search_info.search_percent);
							$('#search_process .progressbar-text').html("");
							$('#search_process .progressbar-value .progressbar-text').html(self.search_info.search_percent + "%");
							
							$('#tp_modal').modal('hide');
							$('#myModalLabel').text("Blind Search");
							$('#myModal').modal('show');
							
							self.blind_search = true;
							self.search_type = 1;
							self.total_pro = [];
							self.getProByTpId(self.search_type,0,board_id);
							console.log("total_pro:",self.total_pro);
							self.showPrograms(self.search_type,self.total_pro);
							
						} else if (self.search_status == 2) {
							$('#search_tp_process').progressbar('setValue', self.search_info.search_percent);
							$('#search_tp_process .progressbar-text').html("");
							$('#search_tp_process .progressbar-value .progressbar-text').html(self.search_info.search_percent + "%");
							
							self.blind_search = true;
							
							self.getSearchTp();
							self.search_type = 2;
							self.showPrograms(self.search_type,self.search_tp_info.tp_info);
						}
					}, 1000);
				}
			}
		})
	},
	
	getProByTpId : function(search_type,tp_id,board_id) {	
		var self = this;
		
		$.ajax({
			url : "/programs/getSearchProgramsByTpId?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"tp_id" : tp_id,
				"board_id" : board_id
			},
			async : false,
			success : function(data) {
				if (data != null && data.length > 0) { 
					
	                if (search_type == 0) {        
	    				self.showPrograms(search_type,data);
					} else {
						for (var i = 0; i < data.length; i++) {
							self.total_pro.push(data[i]);
						}
					}
				}
			}
		})
	},
	
	showPrograms : function(search_type,data) {
		var self = this;
		
		var tv_list = '';
		var radio_list = '';
		var tp_list = '';
		var array_tv=new Array();
	    var array_radio=new Array();
	    
	    for (var i = 0; i < data.length; i++) {
	    	tp_list += "<div class=\"card-body pro\">";
	    	tp_list += "<div class=\"form-group row\">";
	    	tp_list += "<label class=\"col-md-3 col-form-label\">" + (i+1) + " - " + data[i].freq + "/" + data[i].symbolRate + "/" + self.aStrUipol[data[i].polarization] +"</label>";
	    	tp_list += "</div></div>";
		}
	    
		for (var i = 0; i < data.length; i++) {
			if(data[i].tvType==0){
		        array_tv.push(data[i]);
		    }else{
		        array_radio.push(data[i]);
		    }
		} 
		
		for (var i = 0; i < array_tv.length; i++) {
			if (array_tv[i].caType == 0) {
				tv_list += "<div class=\"card-body pro\">";
				tv_list += "<div style=\"float: left;width: 90%;\">";
				tv_list += "<label class=\"col-md-3 col-form-label\">" + (i+1) + " - " + array_tv[i].serviceName + "</label>";
				tv_list += "</div></div>";
			} else {
				tv_list += "<div class=\"card-body pro\">";
				tv_list += "<div style=\"float: left;width: 90%;\">";
				tv_list += "<label class=\"col-md-3 col-form-label\">" + (i+1) + " - " + array_tv[i].serviceName + "</label>";
				tv_list += "</div>";
				tv_list += "<div style=\"float: left;width: 10%;\">";
				tv_list += "<label class=\"col-md-3 col-form-label\">&#xF155</label>";
				tv_list += "</div></div>";
			}
			
		}	
		
		for (var i = 0; i < array_radio.length; i++) {
			if (array_radio[i].caType == 0) {
				radio_list += "<div class=\"card-body pro\">";
				radio_list += "<div style=\"float: left;width: 90%;\">";
				radio_list += "<label class=\"col-md-3 col-form-label\">" + (i+1) + " - " + array_radio[i].serviceName + "</label>";
				radio_list += "</div></div>";
			} else {
				radio_list += "<div class=\"card-body pro\">";
				radio_list += "<div style=\"float: left;width: 90%;\">";
				radio_list += "<label class=\"col-md-3 col-form-label\">" + (i+1) + " - " + array_radio[i].serviceName + "</label>";
				radio_list += "</div>";
				radio_list += "<div style=\"float: left;width: 10%;\">";
				radio_list += "<label class=\"col-md-3 col-form-label\">&#xF155</label>";
				radio_list += "</div></div>";
			}
		}
		
		if (search_type == 0) {
			$('#total_tv').html(array_tv.length);
			$('#total_radio').html(array_radio.length);
			$('#tv').html(tv_list);
			$('#radio').html(radio_list);
		} else if (search_type == 1) {
			$('#total_tv').html(array_tv.length);
			$('#total_radio').html(array_radio.length);
			$('#tv').html(tv_list);
			$('#radio').html(radio_list);
		} else if (search_type == 2) {
			$('#search_tp').html(tp_list);
			$('#new_tp').text(data.length);
		} 
		
		var tv_scroll_height = $("#tv")[0].scrollHeight;
		var radio_scroll_height = $("#radio")[0].scrollHeight;
		var search_tp_scroll_height = $("#search_tp")[0].scrollHeight;
		
		if (!self.tv_scroll) {
			$("#tv").scrollTop(tv_scroll_height);
		}
		
		if (!self.radio_scroll) {
			$("#radio").scrollTop(radio_scroll_height);
		}
		
		if (!self.search_tp_scroll) {
			$("#search_tp").scrollTop(search_tp_scroll_height);
		}
	},
	
	showSearch : function(search_type) {
		var self = this;
		
		var tp_name= $('#tp option:selected').text().split('-')[1];
	    var sat_name= $('#satllite option:selected').text().split('-')[1];
	    
		$('#myModal').modal('show');
		self.timedCount();
		
		if (search_type == 0) {
			$('#myModalLabel').text("TP Search");
			$('.search_info').text(sat_name);
		} else if (search_type == 1) {
			$('#myModalLabel').text("Satellite Search");
			$('.search_info').text(sat_name);
		} else if (search_type == 2) {
			$('#myModalLabel').text("Blind Search");
			$('.search_info').text(sat_name);
		}
	},
	
	timedCount : function() {
		var self = this;
		
		$('.search_time_m').html(self.minute < 10 ? "0" + self.minute : self.minute);
		$('.search_time_s').html(self.seconds < 10 ? "0" + self.seconds : self.seconds);
		self.seconds = self.seconds + 1;
		if (self.seconds == 60) {
			self.minute = self.minute + 1;
			self.seconds = 0;
		}
		self.time = setTimeout(() => {
			self.timedCount();
		}, 1000);
	},
	
	getOutputByBoardIdAndSatIdAndTpId : function(board_id,sat_id,tp_id) {
		var self = this;
		
		$.ajax({
			url : "/streaming/getOutputByBoardIdAndSatIdAndTpId?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" :board_id,
				"sat_id" : sat_id,
				"tp_id" : tp_id
			},
			async : false,
			success : function(data) {
				self.module_has_stream = data;
			}
		})
	},
	
	getProgramsByTpId : function() {
		var self = this;
		
		var board_id = $('#board_id option:selected').val();
		var tp_id = $('#tp option:selected').val();
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
				self.board_has_pro = data;
			}
		})
	},
	
	getProByBoardIdAndTpList : function() {
		var self = this;
		
		var board_id = $('#board_id option:selected').val();
		var sat_id = $('#satllite option:selected').val();
		$.ajax({
			url : "/programs/getProByBoardIdAndTpList?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"sat_id" : sat_id,
				"board_id" : board_id
			},
			async : false,
			success : function(data) {
				self.board_has_pro = data;
			}
		})
	},
	
	completeSearch : function() {
		var self = this;
//		setTimeout(() => {
//			self.is_update_locksignal = false;
//		}, 2000);
		self.stopCount();
		clearTimeout(self.pro);
		clearTimeout(self.sat_pro);
		clearTimeout(self.blind_pro);
//		self.updateLockSignal();
		self.updateSatllite();
	},
	
	stopCount : function() {
		var self = this;
		
		clearTimeout(self.time);
		self.seconds=0;
	    self.minute=0;
	},
	
	stopSearch : function() {
		var self = this;
		setTimeout(() => {
			self.is_update_locksignal = false;
		}, 2000);
		var board_id = $('#board_id option:selected').val();
		self.tv_scroll = false;
		self.radio_scroll = false;
		self.search_tp_scroll = false;
		$.ajax({
			url : "/search/exitSearch?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id
			},
			success : function(data) {
				if (data.data == "SUCCESS:") {
					$('#myModal').modal('hide');
					$('#tp_modal').modal('hide');
					$('.windows').html("");
					self.max_tp_id = 0;
					clearTimeout(self.pro);
					clearTimeout(self.sat_pro);
					clearTimeout(self.blind_pro);
					$('#total_tv').html(0);
					$('#total_radio').html(0);
					$('#new_tp').html(0);
					self.stopCount();
					self.updateSatllite();
					if (self.blind_search) {
						self.getTpInfo();
					}
					self.doLockSignal();
//					self.updateLockSignal();
				}
			}
		})
	},
	
	getSearchStatus : function() {
		var self = this;
		
		var board_id = self.$board_id.val();
		$.ajax({
			url : "/search/getSearchStatus?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id,
				"module_id" : 0
			},
			async : false,
			success : function(data) {
				self.search_info = JSON.parse(data.data);
				self.search_status = self.search_info.search_status;
				self.lock_status = self.search_info.lock;
				console.log("lock_status:",self.lock_status);
				if (!self.is_update_locksignal) {
					self.updateLockSignal();
				}
				var signal = 0;
				var quality = 0;
				if (data != null) {
					signal = self.search_info.streng;
					quality = self.search_info.quality;
					if (signal > 100) {
						signal = 100;
					}
					if (quality > 100) {
						quality = 100;
					} 
					self.setSignalAndQuality(signal, quality);
				}
			}
		});
	},
	
	getLockInfo : function() {
		var self = this;
		
		var board_id = $('#board_id option:selected').val();
		$.ajax({
			type : "post",
			url : "/streaming/getLockSignal?"+new Date().getTime(),
			dataType : "json",
			data : {
				"board_id" : board_id,
				"module_id" : 0,
			},
			async : false,
			success : function(data) {
				if (data.satId != 0 && data.tpId != 0) {
					for (var i = 0; i < self.all_satellite.length; i++) {
						if (self.all_satellite[i].id == data.satId) {
							self.$satllite.val(data.satId);
							self.getTpInfo();
							self.$tp.val(data.tpId);
						}
					}
				} else {
					self.getSatlliteInfo();
				}
			}
		})
	},
	
	getStreamByBoardIdAndModuleId : function() {
		var self = this;
		
		var board_id = $('#board_id option:selected').val();
		
		if (board_id == "") {
			self.board_has_stream = true;
		} else {
			$.ajax({
				url : "/streaming/getStreamByBoardIdAndModuleId?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"board_id" : board_id,
					"module_id" : 0
				},
				async : false,
				success : function(data) {
					self.board_has_stream = data.result;
				}
			})
		}
	},
	
	doLockSignal : function() {
		var self = this;
		
		var board_id = $('#board_id option:selected').val();
		var sat_id = $('#satllite option:selected').val();
		var tp_id = $('#tp option:selected').val();
		var lnb_type = $('#lnb_type option:selected').text();
		if (board_id == "" || tp_id == "") {
			self.setSignalAndQuality(0, 0);
		} else {
			$.ajax({
				url : "/search/lockSignal?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"board_id" : board_id ,
					"module_id" : 0,
					"sat_id" : sat_id,
					"tp_id" : tp_id,
					"lnb_type" : lnb_type
				},
				async : false,
				success : function(data) {
					if (data.data == "SUCCESS:") {
						self.isLock = true;
						self.updateSatllite();
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					console.log("textStatus:", textStatus);
				}
			})
		}
	},
	
	updateLockSignal : function() {
		var self = this;
		
		var board_id = $('#board_id option:selected').val();
		var sat_id = $('#satllite option:selected').val();
		var tp_id = $('#tp option:selected').val();
		if (self.lock_status == 1) {
			var data = {
				"boardId" : board_id,
				"moduleId" : 0,
				"satId" : sat_id,
				"tpId" : tp_id,
				"status" : self.lock_status
			}
		} else {
			var data = {
				"boardId" : board_id,
				"moduleId" : 0,
				"satId" : sat_id,
				"tpId" : tp_id,
				"status" : self.lock_status
			}
		}
			
		lock_json = JSON.stringify(data);
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
	
	updateSatllite : function() {
		var self = this;
		
		json_data =
		{
			"id":$('#satllite option:selected').val(),
			"name":null,
			"dir":"",
			"angle":"",
			"lnbType":$('#lnb_type option:selected').text()
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
	} ,
	
	getSatlliteInfo : function() {
		var self = this;
		
		$.ajax({
			url : "/search/getAllSatllite?"+new Date().getTime(),
			type : "get",
			dataType : "json",
			async : false,
			success : function(data) {
				self.all_satellite = data;
				self.createSatOption(data);
				self.getTpInfo();
			}
		})
	},
	
	getTpInfo : function() {
		var self = this;
		
		var sat_id = $('#satllite option:selected').val();// 选中的值
		$.ajax({
			url : "/search/getSatInfoById?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"id" : sat_id,
				"max_tp_id" : 0
			},
			async : false,
			success : function(data) {
				if (data != null) {
// sat_tp_info = data;
					self.selectLnbFreq(data);
					self.createTp(data.tp_info);
				}
			}
		})
	},
	
	getRfByBoardId : function() {
		var self = this;
		
		var board_id = $('#board_id option:selected').val();// 选中的值
		if (board_id != "") {
			$.ajax({
				url : "/rf/getRfByBoardId?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"board_id" : board_id
				},
				async : false,
				success : function(data) {
					if (data != null) {
						$('#tone').val(self.tone[data.tone]);
						$('#lnb_power').val(self.lnb_power[data.lnbPower]);
						$('#diseqc1_0').val(self.diseqc1_0[data.diseqc1_0]);
						$('#diseqc1_1').val(self.diseqc1_1[data.diseqc1_1]);

						$(".sideUp").slideDown();
					}
				}
			})
		} else {
			$(".sideUp").slideUp();
			self.isLock = false;
			self.setSignalAndQuality(0,0);
		}
	},
	
	getBoardOnlineInfo : function() {
		var self = this;
		
		$.ajax({
			type : "get",
			url : "/search/getOnlineInfo?"+new Date().getTime(),
			dataType : "json",
			success : function(data) {
				self.createBoardId(JSON.parse(data.data).online_info);
			}
		})
	},
	
	setSignalAndQuality : function(signal, quality) {
		var self = this;
		
		self.$strength.progressbar('setValue', signal);
		self.$quality.progressbar('setValue', quality);
	},
	
	createBoardId : function(boardOnlineInfo) {
		var list = '';
		for (var i = 0; i < 5; i++) {
			for (var j = 0; j < boardOnlineInfo.length; j++) {
				if (boardOnlineInfo[j].board_id == i && boardOnlineInfo[j].module_id == 0) {
					if (boardOnlineInfo[j].online_status == 1) {
						$("#board_id option[value='"+ (i) + "']").attr("disabled",true);
						$("#board_id option[value='"+ (i) + "']").html("Board-" + (i+1) + " (Offline)");
					} else {
						$("#board_id option[value='"+ (i) + "']").attr("disabled",false);
						$("#board_id option[value='"+ (i) + "']").css({"color":"black"});
						$("#board_id option[value='"+ (i) + "']").html("Board-" + (i+1) + " (Online)");
					}
				}
			}
		}
	},
	
	createSatOption : function(sat_info) {
		var self = this;
		
		self.$satllite.empty();
		if (sat_info != null && sat_info.length > 0) {
			for (var i = 0; i < sat_info.length; i++) {
				var dispName = (i + 1) + "-" + sat_info[i].name + " ("
						+ Math.floor((sat_info[i].angle / 10)) + "."
						+ ((sat_info[i].angle % 10))
						+ ((sat_info[i].dir == 0) ? "E" : "W") + ")";
				self.$satllite.append(
						"<option value='" + sat_info[i].id + "'>" + dispName
								+ "</option>");
			}

		} else {
			self.$satllite.append("<option ></option>");
		}
	},
	
	createTp : function(tp_info) {
		var self = this;
		
		self.$tp.empty();
		if (tp_info != null && tp_info.length > 0) {
			for (var i = 0; i < tp_info.length; i++) {
				var dispName = (i + 1) + "-" + tp_info[i].freq + "/"
						+ tp_info[i].symbolRate + "/"
						+ self.aStrUipol[tp_info[i].polarization];
				self.$tp.append(
						"<option value='" + tp_info[i].id + "' name='" + i + "'>"
								+ dispName + "</option>");
			}

		} else {
			self.$tp.append("<option ></option>");
		}
	},
	
	selectLnbFreq : function(sat_tp_info) {
		var self = this;
		
		var lnb = sat_tp_info.low_lnb + "/" + sat_tp_info.high_lnb;
		for (var i = 0; i < self.lnb_freq.length; i++) {
			if (self.lnb_freq[i] == sat_tp_info.lnb_type) {
				self.$lnb_type.val(i);
				break;
			}
		}
	},
	
	createLnbFreqOption : function() {
		var self = this;
		
		self.$lnb_type.empty();
		for (var i = 0; i < self.lnb_freq.length; i++) {
			self.$lnb_type.append("<option value='" + i + "'>" + self.lnb_freq[i] + "</option>");
		}
	},
	
	deleteProByTpId : function(board_id,tp_id) {
		if (tp_id == "") {
			self.common_mgr.modalShow("","Info","no tp_id",true,false);
		} else {
			$.ajax({
				url : "/programs/deleteProByTpId?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"tp_id" : tp_id,
					"board_id" : board_id
				},
				async : false,
				success : function(data) {
				}
			})
		}
	},
	
	deleteProByTpList : function(sat_id,board_id) {
		var self = this;
		
		if (sat_id == "") {
			self.common_mgr.modalShow("","Info","please select satllite",true,false);
		} else {
			$.ajax({
				url : "/programs/deleteProByTpList?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"sat_id" : sat_id,
					"board_id" : board_id
				},
				async : false,
				success : function(data) {
					self.total_pro = [];
				}
			})
		}
	},
	
	deleteSearchPrograms : function() {	
		$.ajax({
			url : "/programs/deleteSearchPrograms?"+new Date().getTime(),
			type : "get",
			dataType : "json",
			async : false,
			success : function(data) {
			}
		})
	},
	
	deleteSearchTp : function() {	
		$.ajax({
			url : "/transponder/deleteSearchTp?"+new Date().getTime(),
			type : "get",
			dataType : "json",
			success : function(data) {
			}
		})
	},
	
	getSearchTp : function() {
		var self = this;
		
		$.ajax({
			url : "/transponder/getSearchTp?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"sat_id" : $('#satllite option:selected').val()
			},
			async : false,
			success : function(data) {
				self.search_tp_info = data;
			}
		})
	},
	
	initModalHeight : function(){
		var height = window.screen.height * 0.7;
		$(".search-modal").css({"height": "600"});
		$(".process-modal").css({"height": "600"});
	},
	
	DialogInit:function()
	{
		var self = this;
	    options={
	        OkClick:function (handler) {
	        	var board_id = $('#board_id option:selected').val();
	        	var sat_id = $('#satllite option:selected').val();
	        	var tp_id = $('#tp option:selected').val();
	        	if(handler == self.HANDLE_TP_SEARCH)
	            {
	        		self.deleteProByTpId(board_id,tp_id);
	        		self.tpSearch();
	            }
	            if (handler == self.HANDLE_SAT_SEARCH) {
	            	self.deleteProByTpList(sat_id,board_id);
	            	self.satSearch();
				}
	            if (handler == self.HANDLE_BLIND_SEARCH) {
	            	self.deleteProByTpList(sat_id,board_id);
	            	self.blindSearch();
				}
	            
	        },
	        CancelClick:function (handler) {
	        	
	        	if(handler == self.HANDLE_TP_SEARCH)
	            {
	        		self.tpSearch();
	            }
	        	if(handler == self.HANDLE_SAT_SEARCH)
	            {
	        		self.satSearch();
	            }
	        	if (handler == self.HANDLE_BLIND_SEARCH) {
	        		self.blindSearch();
	 			}
	        	
	        }
	    };
	   self.common_mgr.initDialog(options);
	},
	
}