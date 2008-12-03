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
