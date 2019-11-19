function ProgramMgr()
{
    var self = this;
    self.init();
}

$(function() {
	var program_mgr = new ProgramMgr();
	$('#program_modal').on("hide.bs.modal", function() {
		$(this).find('input').each(function () {
            $(this).val("")
       });
		$(this).find('select').each(function () {
            $(this).val("")
       });
	})
})

ProgramMgr.prototype =
{
	constructor:ProgramMgr,
	init:function()
	{
		var self = this;
		
		self.common_mgr = new CommonMgr();
		
		self.DELETE_PROGRAM_OK = 8;
		self.pageNumber = 1;
		self.fuc_state = 0;//0->>add,1-->edit,2-->delete
		self.board_list =[1,2,3,4,5]
		self.sat_id = 1;
		self.program_list = [];
		self.aStrUipol = [ 'H', 'V', 'L', 'R' ];
		self.checkArray = new Array();
		self.check_pro = true;
		
		self.init_view();
		self.init_data();
		self.init_event();
	},
	init_view:function()
	{
		var self = this;
		self.initTableHeight();
		self.DialogInit();
	},
	init_data:function()
	{
		var self = this;
		self.getSatlliteInfo();
		self.getTpInfo();
	},
	init_event:function()
	{
		var self = this;
		$('#page_size').change(function() {
			self.pageNumber = 1;
			self.doPage();
		})

		$('#key_word').on('input', function() {
			self.pageNumber = 1;
			self.doPage();
		})
		$('#program_sat').change(function() {
			self.pageNumber = 1;
			self.getTpInfo();
		})
		
		$('#program_tp').change(function() {
			self.pageNumber = 1;
			self.doPage();
		})
		
		$('#board_id').change(function() {
			self.pageNumber = 1;
			self.doPage();
		})
		
		$('#check_all').click(function(){ 
			var ch=$("#program_list input[type=checkbox]");
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
		
		$('#delete_program').click(function (){
			self.fuc_state = 2;
			if(self.checkArray.length < 1)
			{
				self.common_mgr.modalShow("","Warning","Please select Programs to delete",true,false);
				return;
			}else
			{
				$('#cancel').html("No");
				$('#ok').html("Yes");
				self.common_mgr.modalShow(self.DELETE_PROGRAM_OK,"Warning","Do you want to delete the selected program?",true,true);
			}
		}),
		
		$('#btn_cancel').click(function() {	
			$('#program_modal').modal('hide');
		})
		
		$('#btn_ok').click(function(){
			var json_data = null;
			if(self.fuc_state == 0)
			{
				var json_data =
				{
					"tpId":$('#tp_id').val(),
					"boardId":$('#board_id').val() -1,
					"serviceId":$('#service_id').val(),
					"serviceName":$('#service_name').val(),
					"pmtPid":$('#pmt_pid').val(),
					"pcrPid":$('#pcr_pid').val()
				}
				var data = JSON.stringify(json_data);
				//此处保存数据库
				self.save_program_data(0,data);
			}else if(self.fuc_state == 1)
			{
				var json_data =
				{
					"id":$('#program_id').val(),
					"tpId":$('#tp_id').val(),
					"boardId":$('#board_id').val() *1 -1,
					"serviceId":$('#service_id').val(),
					"serviceName":$('#service_name').val(),
					"pmtPid":$('#pmt_pid').val(),
					"pcrPid":$('#pcr_pid').val(),
					"vidPid":$('#vid_pid').val()
				}
				var data = JSON.stringify(json_data);
				self.save_program_data(1,data);
			}
			$('#program_modal').modal('hide');
		})
	},
	save_program_data:function(type,program_info)
	{
		var self =this;
		if(type == 0)
		{
			$.ajax({
				url : "/programs/addProgram?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				contentType : "application/json",
				data : program_info,
				success : function(data) {
					if (data > 0) {
						self.common_mgr.modalShow("","Info","Successful！",true,false);
					}
				}
			})
		}else if(type == 1)
		{
			$.ajax({
				url : "/programs/updateProgram?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				contentType : "application/json",
				data : program_info,
				success : function(data) {
					if (data > 0) {
						self.common_mgr.modalShow("","Info","Successful！",true,false);
					}
				}
			})
		}
		self.doPage();
	},
	create_add_program_option:function(type)
	{
		var self =this;
		$("#board_id").empty();
		for (var i = 0; i < self.board_list.length; i++) {
			$("#board_id").append("<option value='" + i + "'>" + self.board_list[i] + "</option>");
		}
		
		if(type == 0)
		{
			$('#myModalLabel').html("Add Program");
			$('#board_id').removeAttr("disabled");
			var tp_id = $('#program_tp option:selected').val();
			$('#tp_id').val(tp_id);
		}else if(type == 1)
		{
			$('#myModalLabel').html("Edit Program");
			$('#board_id').attr("disabled","disabled");
			var obj = null;
			for(var i= 0; i< self.program_list.length;i++)
			{
				if(self.checkArray[0] == self.program_list[i].id){
					obj = self.program_list[i];
					break;
				}
			}
			$('#program_id').val(obj.id);
			$('#tp_id').val(obj.tpId);
			$('#board_id').val(obj.boardId * 1 +1);
			$('#service_id').val(obj.serviceId);
			$('#service_name').val(obj.serviceName);
			$('#pmt_pid').val(obj.pmtPid);
			$('#pcr_pid').val(obj.pcrPid);
			$('#vid_pid').val(obj.vidPid);
		}
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
				self.createSatOption(data);
			}
		})
	},
	getTpInfo:function()
	{
		var self = this;
		var sat_id = $('#program_sat option:selected').val();
		self.sat_id = sat_id;
		$.ajax({
			url : "/search/getSatInfoById?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"id" : self.sat_id,
				"max_tp_id" : 0
			},
			async : false,
			success : function(data) {
				self.createTpOption(data.tp_info);
				self.doPage();
			}
		})
	},
	doPage:function()
	{
		var self =this;
		var page_size = $('#page_size option:selected').val();
		var tp_id = $('#program_tp option:selected').val();
		var sat_id = $('#program_sat option:selected').val();
		var board_id = $('#board_id option:selected').val();
		var keyWord = $('#key_word').val();
		self.checkArray.length = 0;
		$('#check_all').prop('checked',false);
		if (tp_id == "") {
			$("#program_list").empty();
			self.createPagination("");
		} else {
			$.ajax({
				url : "/programs/selectAllPro?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"page" : self.pageNumber,
					"size" : page_size,
					"keyWord" : keyWord,
					"tp_id" : tp_id,
					"sat_id" : sat_id,
					"board_id" : board_id
				},
				success : function(data) {
					self.program_list = data.list;
					self.createProgramOption(self.program_list);
					$('#total').html(" " + data.total + " ");
					self.createPagination(data);
				}
			})
		}
		
	},
	createPagination:function(data)
	{
		var self =this;
		$('.pagination').empty();
		var list = '';
		if (data == "") {
			list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"1\">First</button></li>";
			list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + 1
					+ "\" >Previous</button></li>";
			list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
					+ 1 + "\" >" + 1 + "</button></li>"
			list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
			list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + 1
					+ "\" >Last</button></li>";
		} else {
			if (data.pageNum == 0) {
				list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"1\">First</button></li>";
				list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + 1
						+ "\" >Previous</button></li>";
				list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
						+ 1 + "\" >" + 1 + "</button></li>"
				list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
				list += "<li ><button class=\"page-link\"value=\"" + 1
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
		var page_size = $('#page_size option:selected').val();
		var tp_id = $('#program_tp option:selected').val();
		var sat_id = $('#program_sat option:selected').val();
		var board_id = $('#board_id option:selected').val();
		var keyWord = $('#key_word').val();
		if (tp_id == "") {
			$("#program_list").empty();
		} else {
			$.ajax({
				url : "/programs/selectAllPro?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				data : {
					"page" : obj.value, 
					"size" : page_size,
					"keyWord" : keyWord,
					"tp_id" : tp_id,
					"sat_id" : sat_id,
					"board_id" : board_id
				},
				success : function(data) {
					if (data != null) {
						self.pageNumber = data.pageNum;
						self.programe_list = data.list;
						self.createProgramOption(self.programe_list);
						$('#total').html(" " + data.total + " ");
						self.createPagination(data);
					}
				}
			})
		}
		
	},
	createSatOption:function(sat_info)
	{
		var self =this;
		$('#program_sat').empty();
		if (sat_info != null && sat_info.length > 0) {
			for (var i = 0; i < sat_info.length; i++) {
				var dispName = (i + 1) + "-" + sat_info[i].name + " ("
						+ Math.floor((sat_info[i].angle / 10)) + "."
						+ ((sat_info[i].angle % 10))
						+ ((sat_info[i].dir == 0) ? "E" : "W") + ")";
				$("#program_sat").append(
						"<option value='" + sat_info[i].id + "'>" + dispName
								+ "</option>");
			}
		} else {
			$("#program_sat").append("<option ></option>");
		}
	},
	createTpOption:function(tp_info)
	{
		var self = this;
		$("#program_tp").empty();
		$("#program_tp").append("<option value='0'>All TP</option>");
		if (tp_info != null && tp_info.length > 0) {
			for (var i = 0; i < tp_info.length; i++) {
				var dispName = (i + 1) + "-" + tp_info[i].freq + "/"
						+ tp_info[i].symbolRate + "/"
						+ self.aStrUipol[tp_info[i].polarization];
				$("#program_tp").append(
						"<option value='" + tp_info[i].id + "' name='" + i + "'>"
								+ dispName + "</option>");
			}
		} else {
		}
	},
	createProgramOption:function(pro_list)
	{
		var self = this;
		var page_size = $('#page_size option:selected').val();
		$("#program_list").empty();
		var id = (self.pageNumber-1) * page_size;
		if (pro_list != null && pro_list.length > 0) {
			var list = '';
			for (var i = 0; i < pro_list.length; i++) {
				list += "<tr class=\"tr-link\">";
				list += "<td width=\"5%\"><input type=\"checkbox\" value=\""+pro_list[i].id+"\" class=\"check_line\"/></td>";
				list += "<td width=\"10%\">" + (id + i + 1) + "</td>";
				list += "<td width=\"10%\">" + ((pro_list[i].boardId)*1 +1) + "</td>";
				list += "<td width=\"10%\">"+  pro_list[i].serviceId+"</td>";
				if (pro_list[i].caType == 0) {
					list += "<td width=\"25%\">" + pro_list[i].serviceName + "</td>";
				} else {
					list += "<td width=\"25%\">" + pro_list[i].serviceName + " (" + " &#xF155 " + ")" + "</td>";
				}
				list += "<td width=\"10%\">" + pro_list[i].pmtPid + "</td>";
				list += "<td width=\"10%\">" + pro_list[i].pcrPid + "</td>";
				list += "<td width=\"10%\">" + pro_list[i].vidPid + "</td>";
				list += "</tr>";
			}
			$("#program_list").append(list);
			$('#first').html(" " + (id + 1) + "");
			$('#last').html(" " + (id + pro_list.length) + "");
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
	initTableHeight:function(){
		var self = this;
		var height = window.screen.height * 0.45 ;
		$(".table-body").css({"height": height});
	},
	deletePrograms : function() {
		var self = this;
		$.ajax({
			url : "/programs/deleteProgram?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify(self.checkArray),
			success : function(data) {
				if (data.result > 0) {
					self.doPage();
					setTimeout(() => {
						self.common_mgr.modalShow("","Info","Successful！",true,false);
					}, 500);
				}
			}
		})
	},

	checkPro:function() {
		var self = this;
		$.ajax({
			url : "/programs/checkProToDelete?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify(self.checkArray),
			async : false,
			success : function(data) {
				self.check_pro = data;
			}
		})
	},
	
	DialogInit:function()
	{
		var self =this;
	    options={
	        OkClick:function (handler) {
	            if(handler == self.DELETE_PROGRAM_OK)
	            {
	            	//此处删除数据并且重新加载数据
	            	self.checkPro();
					if (self.check_pro) {
						setTimeout(() => {
							self.common_mgr.modalShow("","Info","There are some streams from the program in the module, please delete first.",true,false);
						}, 1000);
					} else {
						self.deletePrograms();
					}
	            }
	        },
	        CancelClick:function (handler) {
	        }
	    };
	    self.common_mgr.initDialog(options);
	},
	
}