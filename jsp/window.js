function NewWindow()
{
	var wnd=window.open("","Image","width=400,height=400,toolbar=no,menubar=no,scrollbars=yes,statusbar=yes,resizable=yes")
	wnd.focus();
}

function NewWindow2(name,width,height)
{
	var wnd=window.open("",name,"width="+width+",height="+height+",toolbar=no,menubar=no,scrollbars=yes,statusbar=yes,resizable=yes")
	wnd.focus();
}