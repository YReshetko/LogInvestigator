// ali - analise log interface
if(typeof(ali) == 'undefined') ali = function(){};

ali.ThreadsViewer = function(container)
{
	this._container = container;
	this._threads = new Array();
	this._systemThreads;
	this._path;
	this._isRegularOrder = true;
	this.constructThreads = function(value)
	{
		this.clear();
		var threads = value.threads;
		var minTime = 0;
		var maxTime = 0;
		var i,l;
		var colorFactory = new ali.ColorsFactory();
		l = threads.length;
		for (i=0;i<l;i++)
		{
			if(!threads[i].isDelete)
			{
				var color = colorFactory.next();
			    var oneThread = new ali.SampleThread(threads[i], color.background, color.border, this._path);
				//this._container.append(oneThread.sample);
				oneThread.width = Math.round(oneThread.timeSize/1000);
				//oneThread.x = i*52;
				if(oneThread.startTime < minTime || minTime == 0)
				{
					minTime = oneThread.startTime;
				}
				this._threads.push(oneThread);
			}
		}
		this._systemThreads = new ali.ThreadsSystem(minTime);
		l = this._threads.length;
		for(i=0;i<l;i++)
		{
			this._systemThreads.addThread(this._threads[i]);
		}
		this._systemThreads.addLines();
		this._container.append(this._systemThreads.system);

		this._isRegularOrder = !this._isRegularOrder;
		//this.changeThreadView();
	}
	this.changeThreadView = function()
	{
		this._isRegularOrder = !this._isRegularOrder;
		this._systemThreads.setWrappType(!this._isRegularOrder);
		var threadPosition;
		var wrapper;
		if(!this._isRegularOrder)
		{
			threadPosition = "absolute";
			wrapper = "nowrap";
		}
		else
		{
			threadPosition = "relative";
			wrapper = "normal";
		}
		var i,l;
		l = this._threads.length;
		for(i=0;i<l;i++)
		{
		   this._threads[i].sample.css("position", threadPosition);
		   if(this._isRegularOrder)
		   {
			   this._threads[i].sample.css("width", "100%");
			   this._threads[i].sample.css("left", "0");
			   this._threads[i].sample.css("top", "");
		   }
			else
		   {
			   this._threads[i].width = this._threads[i].width/100;
			   this._threads[i].x = this._threads[i].x;
			   this._threads[i].sample.css("top", "0px");
		   }
		}

	}
	this.selectAll = function(value)
	{
		var i,l;
		l = this._threads.length;
		for(i=0;i<l;i++)
		{
			this._threads[i].isSelected = value;
		}
	}
/*	this.getThreadsSet = function()
	{
		var threads = this._getSelectedThreads();
		var data = {
			path : this._path,
			names : threads
		};
		return data;
	}*/
	this.getSelectedThreads = function()
	{
		var out = new Array();
		var i,l;
		l = this._threads.length;
		for(i=0;i<l;i++)
		{
			if(this._threads[i].isSelected)
			{
				out.push(this._threads[i].threadName);
			}
		}
		return out;
	}
	this.clear = function()
	{
		while (this._threads.length > 0)
		{
			this._threads[0].sample.remove();
			this._threads.shift();
		}
		if(this._systemThreads)
		{
			this._systemThreads.clear();
			this._systemThreads = null;
		}
	}
}

ali.SampleThread = function(thread, backgroundColor, borderColor, path)
{
	var threadName = thread["threadName"];
	this._threadName = thread["threadName"];
	this._minWidth = 220;
	var threadStyle =
	{
		position: "absolute",
		'border-style': "solid",
		'border-width': "1px",
		'margin-bottom' : "2px",
		'background-color' : backgroundColor,
		'border-color' : borderColor,
		color : "white",
		'border-radius' : "6px",
		height : 23,
		'font-size' : "10pt",
		"white-space" : "nowrap",
		'text-overflow' : "ellipsis",
		overflow: "hidden"
	}
	var downloadStyle =
	{
		'margin-left' : "5px",
		'margin-top' : "1px",
		display: "inline-block",
		'content' : "url(img/downloaThread.png)"
	}

	var labelStyle =
	{
		'margin-left' : "15px",
		display: "inline-block"
	}
	this._thread = $("<div/>").css(threadStyle).addClass("thread-one-sample");

	this._label = $("<div/>").css(labelStyle);
	this._checkBox = new ali.CheckBox(10);

	this._download = $("<span/>").css(downloadStyle);
	this._download.on("mousedown", function(){
		if(app != null)
		{
			// Call back into java application
			app.download(threadName);
		}
	});
	this._thread.append(this._checkBox.box);
	this._thread.append(this._download);
	this._label.append(this._threadName);
	this._thread.append(this._label);
	this._thread.attr('title',this._threadName);
	this._width = 0;
	this._startTime = parseInt(thread["startTime"]);
	this._endTime = parseInt(thread["endTime"]);

	Object.defineProperties(this,{
		x : {
			set : function(value)
			{
				this._x = value;
				this._thread.css("left", value);
			},
			get : function()
			{
				return this._x;
			}
		},
		y : {
			set : function(value)
			{
				this._y = value;
				this._thread.css("top", value);
			},
			get : function()
			{
				return this._y;
			}
		},
		width : {
			get : function()
			{
				if(this._width < this._minWidth)
				{
					return this._minWidth;
				}
				return this._width;
			},
			set : function(value)
			{
				this._width = value*100;
				if (this._width < this._minWidth)
				{
					this._thread.css("width", this._minWidth);
				}
				else
				{
					this._thread.css("width", this._width);
				}
			}
		},
		startTime : {
			get : function()
			{
				return this._startTime;
			}
		},
		endTime : {
			get : function()
			{
				return this._endTime;
			}
		},
		timeSize : {
			get : function()
			{
			 	return this._endTime - this._startTime;
			}
		},
		sample : {
			get : function()
			{
				return this._thread;
			}
		},
		isSelected : {
			get : function()
			{
				return this._checkBox.select;
			},
			set : function(value)
			{
				this._checkBox.select = value;
			}
		},
		threadName : {
			get : function()
			{
				return this._threadName;
			}
		}
	});
}

ali.ThreadsSystem = function(startTime)
{
	this._startTime = startTime;
  	this._lines = new Array();
	this._container = $("<div/>").addClass("threads-system");

	this.addThread = function(thread)
	{
	  	var i,l;
		var wasAdded = false;
		l = this._lines.length;
		var atPosition = thread.startTime - this._startTime;
		atPosition = Math.round(atPosition/10);
		for (i=0;i<l;i++)
		{
			if (this._lines[i].canAdd(atPosition))
			{
				this._lines[i].addThread(thread, atPosition);
				wasAdded = true;
				break;
			}
		}
		if(!wasAdded)
		{
			var newLine = new ali.ThreadsLine();
			newLine.addThread(thread, atPosition);
			this._lines.push(newLine);
		}
	};
	this.addLines = function()
	{
		var i,l;
		l = this._lines.length;
		for (i=l-1;i>=0;i--)
		{
			this._container.append(this._lines[i].line);
		}
	};
	this.clear = function()
	{
		while(this._lines.length)
		{
			this._lines[0].line.remove();
			this._lines.shift();
		}
	}
	this.setWrappType = function(isNoWrapp)
	{
		var i,l;
		l = this._lines.length;
		for(i=0;i<l;i++)
		{
			this._lines[i].isWrapp = isNoWrapp;
		}
	}
	Object.defineProperties(this,{
		system : {
			get : function()
			{
				return this._container;
			}
		}
	});
}
ali.ThreadsLine = function()
{
	var lineStyle =
	{
		position : "relative",
		height : "auto",
		'min-height': "25px",
		'max-height': "25px"
	}
	this._line = $("<div/>").css(lineStyle).addClass("threads-line");
	this._startPosition = 0;
	this.canAdd = function(position)
	{
		if (position >= this._startPosition)
		{
			return true;
		}
		return false;
	}
	this.addThread = function(thread, atPosition)
	{
		this._line.append(thread.sample);
		thread.x = atPosition;
		thread.y = 0;
		this._startPosition = atPosition + thread.width;
	}
	Object.defineProperties(this,{
		line : {
			get : function()
			{
				return this._line;
			}
		},
		isWrapp : {
			set : function(value)
			{
				if(value)
				{
					this._line.css('min-height', "25px");
					this._line.css('max-height', "25px");
				}
				else
				{
					this._line.css('min-height', "");
					this._line.css('max-height', "");
				}
			}
		}
	});
}

ali.ColorsFactory = function()
{
	this.ind1 = 0;
	this.ind2 = 0;
	this.ind3 = 0;

	this.hasNext = function()
	{
		var flag = true;
		flag = flag && this.ind3<ali.CONST.THREADS_COLORS.length;
		flag = flag && this.ind2<ali.CONST.THREADS_COLORS.length;
		flag = flag && this.ind1<ali.CONST.THREADS_COLORS.length;
		return flag;
	}
	this.next = function()
	{
		var isCorrect = false;
		if(this.ind3<ali.CONST.THREADS_COLORS.length-1)
		{
			++this.ind3;
		}
		else
		{
			isCorrect = true;
			this.ind3 = 0;
		}
		if (isCorrect){
			if(this.ind2<ali.CONST.THREADS_COLORS.length-1)
			{
				isCorrect = false;
				++this.ind2;
			}
			else
			{
				isCorrect = true;
				this.ind2 = 0;
			}
			if (isCorrect){
				if(this.ind1<ali.CONST.THREADS_COLORS.length-1)
				{
					++this.ind1;
				}
				else
				{
					this.ind1 = 0;
				}

			}
		}
		var background = "rgba(";
		var border = "rgba(";
		background += ali.CONST.THREADS_COLORS[this.ind1] +  ", ";
		border += ali.CONST.THREADS_COLORS[this.ind1] +  ", ";
		background += ali.CONST.THREADS_COLORS[this.ind2] +  ", ";
		border += ali.CONST.THREADS_COLORS[this.ind2] +  ", ";
		background += ali.CONST.THREADS_COLORS[this.ind3] +  ", 0.7)";
		border += ali.CONST.THREADS_COLORS[this.ind3] +  ", 1)";

		var out =
		{
			background : background,
			border : border
		}
		return out;
	}
}
/**
 * Class for consrants
 * @constructor
 */
ali.CONST = function(){};
/**
 * Array of thread colors to construct it
 * @type {Array} - array of colors
 */
ali.CONST.THREADS_COLORS =
	[
		17, 51, 102, 153, 204, 238
	]