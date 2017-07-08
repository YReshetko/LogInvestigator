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
	return view._getSelectedThreads();
}

