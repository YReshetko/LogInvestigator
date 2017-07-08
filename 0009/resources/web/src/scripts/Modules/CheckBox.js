// ali - analise log interface
if(typeof(ali) == 'undefined') ali = function(){};
ali.CheckBox = function(size)
{
	this._isSelect = false;
    var boxStyleDeSelected =
    {
	    display: "inline-block",
	    'margin-left' : "5px",
	    'margin-top' : "1px",
	    'content' : "url(img/checkBoxDeSelected.png)"
    }
	var boxStyleSelected =
	{
		display: "inline-block",
		'margin-left' : "5px",
		'margin-top' : "1px",
		'content' : "url(img/checkBoxSelected.png)"
	}
	this._checkBox = $("<div/>").css(boxStyleDeSelected);
	Object.defineProperties(this,{
		box : {
			get : function(value)
			{
				return this._checkBox;
			}
		},
		select : {
			get : function()
			{
				return this._isSelect;
			},
			set : function(value)
			{
				this._isSelect = value;
				if(value)
				{
					this._checkBox.css(boxStyleSelected);
				}
				else
				{
					this._checkBox.css(boxStyleDeSelected);
				}
			}
		}
	});

	var self = this;
	this._checkBox.on("mousedown", function(event){
		self.select = !self.select;
	})
}