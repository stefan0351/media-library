function imagePopup(name, img, width, height)
{
	var content="<img src=\""+img+"\"";
	if (width>0) content+=" width=\""+width+"\"";
	if (height>0) content+=" height=\""+height+"\"";
	content+=">";
	return overlib(content,CAPTION,name,FGCOLOR,"white",BGCOLOR,"#005500",CAPCOLOR,"white",VAUTO);
}

function newWindow(title, src, width, height)
{
	var wnd=window.open(src, title, "width="+width+",height="+height+",toolbar=no,menubar=no,scrollbars=yes,statusbar=yes,resizable=yes")
	wnd.focus();
}

function showErrors(errors)
{
	var text="";
	for (i=0;i<errors.length;i++)
	{
		if (i>0) text=text.concat("<br>");
		text=text.concat(errors[i]);
	}
	showError(text);
}

function showError(text)
{
	Dialog.alert(text,
	{
		windowParameters:
		{
			className: "alert",
			title:"Error",
			width:400, maxHeight:500,
			resizable:true, closable:true
		},
		okLabel: "Ok",
		ok: function()
		{
			return true;
		}
	});
}
