/**
 * Init script
 */
var view;
function init()
{
	view = new ali.ThreadsViewer($( ".main-container" ));
}

function setThreads(threads)
{
	if(view == null)
	{
		init();
	}
	view.constructThreads(threads);
}

function getSelectedThreads()
{
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

