/**
 * Init script
 */
var view;
function init()
{
	if(view == null)
	{
		view = new ali.ThreadsViewer($( ".main-container" ));
	}
}

function setThreads(threads)
{
	init();
	view.constructThreads(threads);
}

function getSelectedThreads()
{
	init();
	var selectedThreads = view.getSelectedThreads();
	if(app != null)
	{
		var i;
		for (i = 0; i< selectedThreads.length; i++)
		{
			//  Call back to java application to set new thread
		  	app.addSelectedThread(selectedThreads[i]);
		}
	}
}

