function imagePopup(name, img, width, height)
{
	return overlib("<img src=\""+img+"\">",CAPTION,name,FGCOLOR,"white",BGCOLOR,"#005500",CAPCOLOR,"white",WIDTH,width,HEIGHT,height,VAUTO);
}

function imagePopup(name, img)
{
	return overlib("<img src=\""+img+"\">",CAPTION,name,FGCOLOR,"white",BGCOLOR,"#005500",CAPCOLOR,"white",VAUTO);
}
