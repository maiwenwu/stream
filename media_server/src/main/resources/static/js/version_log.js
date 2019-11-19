var versionLogMgr;
$(function () {
    if (versionLogMgr == undefined)
    {
    	versionLogMgr = new VersionLogMgr();
    }
});


function VersionLogMgr()
{
    var self = this;
    self.init();

}

VersionLogMgr.prototype={
	constructor: VersionLogMgr,
	init:function() {
		var self = this;

		self.init_data();
		self.init_event();
	},
	
	init_data : function() {
		var self = this;
		self.getVersionLog();
	},
	
	init_event : function() {
		var self = this;
	},
	
	getVersionLog:function() {
		var self = this;
		$.ajax({
			type : "get",
			url : "/configuration/getChangeLog?"+new Date().getTime(),
			dataType : "json",
			success : function(data) {
				console.log(data);
				self.createOption(data);
			},
			error : function(textStatus) {
				console.log("textStatus:", textStatus);
			}
		})
	},
	
	createOption:function(data) {
		var self = this;
		var list = "";
		
		if (data != "" && data.length > 0) {
			for (var i = data.length-1; i >= 0; i--) {
				list += "<tr>";
				list += "<td>" + data[i].version + "</td>";
				list += "<td>" + data[i].date + "</td>";
				list += "<td>" + data[i].describe + "</td>";
				list += "</tr>";
			}
		}
		$('#version_list').append(list);
	},
}