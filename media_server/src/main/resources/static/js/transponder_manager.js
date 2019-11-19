function TpMgr()
{
    var self = this;
    self.init();
}

$(function() {
	var tp_mgr = new TpMgr();
	$('#tp_modal').on("hide.bs.modal", function() {
		$(this).find('input').each(function () {
            $(this).val("")
        });
		$(this).find('select').each(function () {
            $(this).val("")
        });
	})
})

TpMgr.prototype =
{
	constructor:TpMgr,
	init:function()
	{
		var self =this;
		self.pageNumber = 1;
		self.DELETE_TP_OK = 11;
		self.HANDLER_DELETE_PROGRAM = 12;
		self.HANDLER_DELETE_TRANSPONDER = 13;
		
		self.common_mgr = new CommonMgr();
		
		self.sat_id = 1;
		self.tp_list =[];
		self.pol_list = ['H','V'];
		self.fec_list = ['AUTO','1/2','2/3','3/4','5/6','7/8','9/10'];
		self.fuc_state = 0;//0->>add,1-->edit,2-->delete
		self.tp_data = '';
		self.checkArray = new Array();
		self.check_tp = true;
		self.init_view();
		self.init_data();
		self.init_event();
	},
	init_view:function()
	{
		var self =this;
		self.initTableHeight();
		self.DialogInit();
	},
	init_data:function()
	{
		var self =this;
		self.getSatlliteInfo();
		self.doPage();
	},
	init_event:function()
	{
		var self =this;
		$('#page_size').change(function() {
			self.pageNumber = 1;
			self.doPage();
		})

		$('#key_word').on('input', function() {
			self.pageNumber = 1;
			self.doPage();
		})
		
		$('#sat').change(function() {
			self.pageNumber = 1;
			self.doPage();
		})
		
		$('#check_all').click(function(){ 
			var ch=$("#transponder_list input[type=checkbox]");
			if(this.checked)
			{
				 for(var i=0;i<ch.length;i++)
	             {
	                 if(ch[i].checked){
	                 }else{
	                     ch[i].checked = true;
	                     self.checkArray.push(ch[i].value);
	                 }
	             }
			}else{
				for (var i = 0; i < ch.length; i++) {
	                ch[i].checked = false;
	                self.checkArray.pop();
	            }
			}
		})
		
		$('#add_tp').click(function() {
			self.fuc_state = 0;
			self.create_add_tp_option(0);
			$('#tp_modal').modal('show');
		})
		
		$('#delete_tp').click(function (){
			self.fuc_state = 2;
			if(self.checkArray.length < 1)
			{
				self.common_mgr.modalShow("","Warning","Please select Transponders to delete!",true,false);
				return;
			}else
			{
            	self.checkTp();
            	if (self.check_tp == 2) {
					setTimeout(() => {
						self.common_mgr.modalShow("","Info","There are some streams from the transponder in the module, please delete first.",true,false);
					}, 500);
				} else if (self.check_tp == 1) {
					$('#cancel').html("No");
					$('#ok').html("Yes");
					self.common_mgr.modalShow(self.HANDLER_DELETE_TRANSPONDER,"Info","This action will delete all programs on this transponder. Do you want to continue?",true,true);
				} else {
					$('#cancel').html("No");
					$('#ok').html("Yes");
					self.common_mgr.modalShow(self.HANDLER_DELETE_TRANSPONDER,"Warning","Do you want to delete the selected transponder?",true,true);
				}
			}
		}),
		
		$('#edit_tp').click(function (){
			self.fuc_state = 1;
			if(self.checkArray.length != 1)
			{
				self.common_mgr.modalShow("","Warning","Please select one Transponder to edit!",true,false);
				return;
			}
			
			self.checkTp();
			if (self.check_tp == 2) {
				setTimeout(() => {
					self.common_mgr.modalShow("","Info","There are some streams from the transponder in the module, please delete first.",true,false);
				}, 500);
			} else if (self.check_tp == 1) {
				setTimeout(() => {
					$('#cancel').html("No");
					$('#ok').html("Yes");
					self.common_mgr.modalShow(self.HANDLER_DELETE_PROGRAM,"Info","This action will delete all programs on this transponder. Do you want to continue?",true,true);
				}, 500);
			} else {
				self.create_add_tp_option(1);
				$('#tp_modal').modal('show');
			}
		})
		
		$('#btn_cancel').click(function() {	
			$('#tp_modal').modal('hide');
		})
		
		$('#btn_ok').click(function(){
			var json_data = null;
			if(self.fuc_state == 0)
			{
				var json_data =
				{
					"satId":self.sat_id,
					"freq":$('#tp_freq').val(),
					"symbolRate":$('#tp_symbol').val(),
					"polarization":$('#tp_pol').val(),
					"fec":$('#tp_fec').val()
				}
				var data = JSON.stringify(json_data);
				if ($('#tp_freq').val() == "") {
					$('#tp_freq').focus();
					self.common_mgr.modalShow("","Info","The frequency cannot be null.",true,false);
				} else if ($('#tp_symbol').val() == "") {
					$('#tp_symbol').focus();
					self.common_mgr.modalShow("","Info","The symbol rate cannot be null.",true,false);
				} else {
					self.save_tp_data(0,data);
					$('#tp_modal').modal('hide');
				}
			}else if(self.fuc_state == 1)
			{
				var json_data =
				{
					"id":$('#tp_id').val(),
					"satId":self.sat_id,
					"freq":$('#tp_freq').val(),
					"symbolRate":$('#tp_symbol').val(),
					"polarization":$('#tp_pol').val(),
					"fec":$('#tp_fec').val()
				}
				self.tp_data = JSON.stringify(json_data);
				if ($('#tp_freq').val() == "") {
					$('#tp_freq').focus();
					self.common_mgr.modalShow("","Info","The frequency cannot be null.",true,false);
				} else if ($('#tp_symbol').val() == "") {
					$('#tp_symbol').focus();
					self.common_mgr.modalShow("","Info","The symbol rate cannot be null.",true,false);
				} else {
					self.save_tp_data(1,self.tp_data);
					$('#tp_modal').modal('hide');
				}
			}
		})
	},
	DialogInit:function()
	{
		var self =this;
	    options={
	        OkClick:function (handler) {
	            if(handler == self.HANDLER_DELETE_TRANSPONDER)
	            {
	            	//此处删除数据并且重新加载数据
	            	self.deleteTransponder();
	            	self.updateLockSignalByTpList();
	            }
	            
	            if (handler == self.HANDLER_DELETE_PROGRAM) {
	            	self.deleteProByTpId();
				}
	            
	        },
	        CancelClick:function (handler) {
	        }
	    };
	    self.common_mgr.initDialog(options);
	},
	deleteProByTpId:function() {
		var self = this;
		$.ajax({
			url : "/programs/deleteProByTpId?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"tp_id" : self.checkArray.join(''),
				"board_id" : -1
			},
			async : false,
			success : function(data) {
				if (dataz > 0) {
					self.create_add_tp_option(1);
					$('#tp_modal').modal('show');
				}
			}
		})
	},
	addTransponder : function(tp_data) {
		var self = this;
		$.ajax({
			url : "/transponder/addTransponder?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : tp_data,
			success : function(data) {
				if (data.result > 0) {
					self.common_mgr.modalShow("","Info","Successful！",true,false);
					self.doPage();
				} else {
					self.common_mgr.modalShow("","Info","Failed,the Frequency already exists!",true,false);
				}
			}
		})
	},
	updateTransponder : function() {
		var self = this;
		$.ajax({
			url : "/transponder/updateTransponder?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : self.tp_data,
			success : function(data) {
				if (data.result > 0) {
					setTimeout(() => {
						self.common_mgr.modalShow("","Info","Successful！",true,false);
					}, 500);
					$('#tp_modal').modal('hide');
					self.doPage();
				}
			}
		})
	},
	checkTp:function() {
		var self = this;
		$.ajax({
			url : "/transponder/checkTpToDelete?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify(self.checkArray),
			async : false,
			success : function(data) {
				self.check_tp = data.result;
			}
		})
	},
	
	deleteTransponder : function() {
		var self = this;
		$.ajax({
			url : "/transponder/deleteTransponder?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify(self.checkArray),
			success : function(data) {
				if (data.result > 0) {
					setTimeout(() => {
						self.common_mgr.modalShow("","Info","Successful！",true,false);
					}, 500);
					self.doPage();
				}
			}
		})
	},
	
	updateLockSignalByTpList:function() {
		var self = this;
		$.ajax({
			url : "/search/updateLockSignalByTpList?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify(self.checkArray),
			success : function(data) {
			}
		})
	},
	
	create_add_tp_option:function(type){
		var self =this;
		$("#tp_pol").empty();
		for (var i = 0; i < self.pol_list.length; i++) {
			$("#tp_pol").append("<option value='" + i + "'>" + self.pol_list[i] + "</option>");
		}
		$("#tp_fec").empty();
		for (var i = 0; i < self.fec_list.length; i++) {
			$("#tp_fec").append("<option value='" + i + "'>" + self.fec_list[i] + "</option>");
		}
		if(type == 0)
		{
			$('#myModalLabel').html("Add Transponder");
		}else if(type == 1)
		{
			$('#myModalLabel').html("Edit Transponder");
			var obj = null;
			for(var i= 0; i< self.tp_list.length;i++)
			{
				if(self.checkArray[0] == self.tp_list[i].id){
					obj = self.tp_list[i];
					break;
				}
			}
			$('#tp_id').val(obj.id),
			$('#tp_freq').val(obj.freq),
			$('#tp_symbol').val(obj.symbolRate),
			$('#tp_pol').val(obj.polarization),
			$('#tp_fec').val(obj.fec),
			$('#tp_ts_id').val(obj.tsId),
			$('#tp_on_id').val(obj.onId)
		}
	},
	save_tp_data:function(type,tp_data){
		var self =this;
		if(type == 0) //add tp
		{
			self.addTransponder(tp_data);
		}else if(type == 1) //edit tp
		{
			self.updateTransponder();
		}
		
	},
	initTableHeight:function(){
		var self = this;
		var height = window.screen.height * 0.45 ;
		$(".table-body").css({"height": height});
	},
	getSatlliteInfo:function()
	{
		var self =this;
		$.ajax({
			url : "/search/getAllSatllite?"+new Date().getTime(),
			type : "get",
			dataType : "json",
			async : false,
			success : function(data) {
				console.log(data);
				self.createSatOption(data);
			}
		})
	},
	createSatOption:function(sat_info)
	{
		var self =this;
		$('#sat').empty();
		if (sat_info != null && sat_info.length > 0) {
			for (var i = 0; i < sat_info.length; i++) {
				var dispName = (i + 1) + "-" + sat_info[i].name + " ("
						+ Math.floor((sat_info[i].angle / 10)) + "."
						+ ((sat_info[i].angle % 10))
						+ ((sat_info[i].dir == 0) ? "E" : "W") + ")";
				$("#sat").append(
						"<option value='" + sat_info[i].id + "'>" + dispName
								+  "</option>");
			}
		} else {
			$("#sat").append("<option ></option>");
		}
	},
	doPage:function()
	{
		var self =this;
		var page_size = $('#page_size option:selected').val();
		var sat_id = $('#sat option:selected').val();
		if (sat_id == null) {
			sat_id = 1;
		}
		$('#sat').val(sat_id);
		self.sat_id = sat_id;
		var keyWord = $('#key_word').val();
		$('#check_all').prop('checked',false);
		self.checkArray.length=0;
		$.ajax({
			url : "/transponder/selectAllTp?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"page" : self.pageNumber,
				"size" : page_size,
				"keyWord" : keyWord,
				"sat_id" : self.sat_id
			},
			success : function(data) {
				self.tp_list = data.list;
				self.createTransponder(self.tp_list);
				$('#total').html(" " + data.total + " ");
				self.createPagination(data);
			}
		})
	},
	createTransponder:function(transponder_list)
	{
		var self =this;
		var page_size = $('#page_size option:selected').val();
		$('#transponder_list').empty();
		var list = '';
		var id = (self.pageNumber-1) * page_size;
		if (transponder_list != null && transponder_list.length > 0) {
			for (var i = 0; i < transponder_list.length; i++) {
				list += "<tr class=\"tr-link\">";
				list += "<td width=\"5%\"><input type=\"checkbox\" value=\""+transponder_list[i].id+"\" class=\"check_line\"/></td>"
				list += "<td width=\"10%\">" + (id + i + 1) + "</td>";
				list += "<td width=\"15%\">" + transponder_list[i].freq + "</td>";
				list += "<td width=\"20%\">" + transponder_list[i].symbolRate + "</td>";
				list += "<td width=\"20%\">"+self.pol_list[transponder_list[i].polarization]+"</td>";
				list += "<td width=\"10%\">" + self.fec_list[transponder_list[i].fec] + "</td>";
				list += "</tr>";
			}
			$('#transponder_list').append(list);
			$('#first').html(" " + (id + 1) + "");
			$('#last').html(" " + (id + transponder_list.length) + "");
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
			var page_size = $('#page_size option:selected').val();
			if(self.checkArray.length == page_size){
				$('#check_all').prop('checked',true);
			}else{
				$('#check_all').prop('checked',false);
			}
		});
		
		 $('.tr-link').unbind('click').click(function () {
			 $('.tr-link').removeClass("tr-in");
             $(this).addClass("tr-in");
             var tr_in = $(this).find("input[type=checkbox]");
             if(tr_in.prop('checked'))
             {
            	tr_in.prop('checked',false);
				for(var i=0;i<self.checkArray.length;i++)
				{
				    if(self.checkArray[i] == tr_in.val())
				    {
				        self.checkArray.splice(i,1);
				    }
				}
             }else{
            	 tr_in.prop('checked',true);
            	 self.checkArray.push(tr_in.val());
             }
            var page_size = $('#page_size option:selected').val();
 			if(self.checkArray.length == page_size){
 				$('#check_all').prop('checked',true);
 			}else{
 				$('#check_all').prop('checked',false);
 			}
		 });
	},
	createPagination:function(data)
	{
		var self =this;
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
		})
	},
	doPagination:function(obj) {
		var self =this;
		var page_size = $('#page_size option:selected').val();
		var sat_id = $('#sat option:selected').val();
		var keyWord = $('#key_word').val();
		$('#check_all').prop('checked',false);
		self.checkArray.length=0;
		$.ajax({
			url : "/transponder/selectAllTp?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"page" : obj.value,
				"size" : page_size,
				"keyWord" : keyWord,
				"sat_id" : sat_id
			},
			success : function(data) {
				if (data != null) {
					self.pageNumber = data.pageNum;
					self.tp_list = data.list;
					self.createTransponder(data.list);
					$('#total').html(" " + data.total + " ");
					self.createPagination(data);
				}
			}
		})
	}
}