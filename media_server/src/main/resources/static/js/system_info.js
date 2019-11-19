function SystemMgr()
{
    var self = this;
}

$(function() {
	var system_mgr = new SystemMgr();
		system_mgr.init();
});

SystemMgr.prototype =
{
	constructor:SystemMgr,
	init:function()
	{
		var self = this;
		self.chart_cpu;
		self.chart_memory;
		self.chart_net;
		self.chart_stream;
		
		self.init_view();
		self.init_data();
		self.init_event();
		
	},
	init_view:function()
	{
		var self = this;
		self.draw_memory();
		self.draw_cpu();
		self.draw_net();
	},
	init_data:function()
	{
		var self = this;
	},
	init_event:function()
	{
		var self = this;
		var pro = setInterval(() => {
			$.ajax({
				url : "/os/getSystemInfo?"+new Date().getTime(),
				type : "get",
				dataType : "json",
				success : function(data) {
					self.update_cpu(data);
					self.update_memory(data);
					self.update_net(data);
				}
			 });
		}, 1000);
	},
	draw_memory:function()
	{
		var self = this;
		var ctx = document.getElementById('chart_memory').getContext('2d');
		self.chart_memory = new Chart(ctx, {
		   	type: 'pie',
		    data:{
				labels: ['used','buffer','free'],
				  datasets: [{
				      data: [0,0,100],
				      backgroundColor: [
		                 '#ff6384',
		                 '#00FF7F',
		                 '#36a2eb',
				       ]
				  }],
		    	},
		});
		var info = "used:" + 0 +"M" +" / "+ "buffer:" + 0 + "M"+ " / "+"free:"+ 100 +"M";
		$("#mem_info").html(info);
	},
	draw_cpu:function(data)
	{
		var self = this;
		var ctx = document.getElementById('chart_cpu').getContext('2d');
		self.chart_cpu = new Chart(ctx, {
		    type: 'doughnut',
		    data:{
				labels: ['used','free'],
				  datasets: [{
				      data: [0,100],
				      borderAlign:'left',
				      backgroundColor: [
						'#ff6384',
						'#36a2eb'
				       ],
				  }],
		    	},
		});
		var info = "used:" + 0 +"%" +" / "+ "free:"+100+"%";
		$("#cpu_info").html(info);
	},
	draw_net:function()
	{
		var self = this;
		var labels = [];
		var datas = [];
		for(var i= 120 ; i >= 1;i--)
		{
			if(i%60 == 0 || i == 1)
			{
				labels.push(i+"s");
			}
			else
			{
				labels.push(" ");
			}
			datas.push(0);
		}
		var ctx = document.getElementById('chart_net').getContext('2d');
		self.chart_net = new Chart(ctx, {
		    type: 'line',
		    data: {
		        labels: labels,
		        datasets: [{
		            label: 'tx',
		            backgroundColor: 'rgb(255, 99, 132)',
		            borderColor: 'rgb(255, 99, 132)',
		            borderWidth:1,
		            fill:false,
		            data:datas,
		            radius:1
		        },
		        {
		            label: 'rx',
		            backgroundColor: '#36a2eb',
		            borderColor: '#36a2eb',
		            data:datas,
		            fill:false,
		            borderWidth:1,
		            radius:1
		        },
		       ]
		    },
		    options: {
		    	layout: {
		            padding: {
		                left: 10,
		                right: 0,
		                top: 0,
		                bottom: 0
		            }
		        },
		        fill:false,
		    	animation: {
		            duration: 0 // general animation time
		        },
		        hover: {
		            animationDuration: 0 // duration of animations when hovering an item
		        },
		        responsiveAnimationDuration: 0 // animation duration after a resize
		    }
		});
		$("#net_info").html("tx:"+0+" Mbps / rx:"+0+" Mbps");
	},
	draw_stream:function()
	{
		var self = this;
		var ctx = document.getElementById('chart_stream').getContext('2d');
		self.chart_stream = new Chart(ctx, {
		    type: 'line',
		    data: {
		        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
		        datasets: [{
		            label: 'My First dataset',
		            backgroundColor: 'rgb(255, 99, 132)',
		            borderColor: 'rgb(255, 99, 132)',
		            data: [10, 10, 5, 2, 20, 30, 20]
		        }]
		    },
		    options: {}
		});
	},
	update_memory:function(data)
	{
		var self = this;
		self.chart_memory.data.datasets[0].data[0] = data.memused;
		self.chart_memory.data.datasets[0].data[1] = data.membuffer;
		self.chart_memory.data.datasets[0].data[2] = data.memfree;
		self.chart_memory.update();
		var info = "used:" + data.memused +"M" +" / "+ "buffer:" + data.membuffer + "M"+ " / "+"free:"+ data.memfree +"M";
		$("#mem_info").html(info);
	},
	update_cpu:function(data)
	{
		var self = this;
		self.chart_cpu.data.datasets[0].data[0] = data.cpuused;
		self.chart_cpu.data.datasets[0].data[1] = data.cpufree;
		self.chart_cpu.update(data)
		var info = "used:" + data.cpuused+"%" +" / "+ "free:"+data.cpufree+"%";
		$("#cpu_info").html(info);
	},
	update_stream:function(data)
	{
		self = this;
	},
	update_net:function(data)
	{
		var self = this;
		self.chart_net.data.datasets[0].data = data.tx_data;
		self.chart_net.data.datasets[1].data = data.rx_data;
		self.chart_net.update();
		
		var tx = data.tx_data[data.tx_data.length-1] +" Mbps";
		var rx = data.rx_data[data.rx_data.length-1] +" Mbps";
		$("#net_info").html("tx:"+tx+" / rx:"+rx);
	},
}