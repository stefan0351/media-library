function imagePopup(name, img, width, height)
{
	var content="<img src=\""+img+"\"";
	if (width>0) content+=" width=\""+width+"\"";
	if (height>0) content+=" height=\""+height+"\"";
	content+=">";
	return overlib(content,CAPTION,name,FGCOLOR,"white",BGCOLOR,"#005500",CAPCOLOR,"white",VAUTO);
}
