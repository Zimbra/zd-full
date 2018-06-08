/*
 * 
 */

// This class is currently not being used and has been removed from the build
ZmEmoticonObjectHandler = function() {

	ZmObjectHandler.call(this, ZmEmoticonObjectHandler.TYPE);

	this._emoticons = [
	   { smiley: ">:)", image: "DevilEmoticon", tooltip: ZmMsg.devil },
       { smiley: ":)", image: "HappyEmoticon", tooltip: ZmMsg.happy },
	   { smiley: "=))", image: "RotflEmoticon", tooltip: ZmMsg.rotfl },
	   { smiley: "=((", image: "BrokenHeartEmoticon", tooltip: ZmMsg.brokenHeart },
	   { smiley: ":((", image: "CryingEmoticon", tooltip: ZmMsg.crying }, 
	   { smiley: "<:-P", image: "PartyEmoticon", tooltip: ZmMsg.party },
	   { smiley: ":O)", image: "ClownEmoticon", tooltip: ZmMsg.clown }
   ];

	this._smileyToSD = {};
		
	var regex = new Array(10);
	var idx = 0;
	var n = 0;
	// create regex to handle all the emoticons
	for (var i in this._emoticons) {
	    var emot = this._emoticons[i];
        	this._smileyToSD[emot.smiley] = emot;
		if (n++ > 0)
			regex[idx++] = "|";
		regex[idx++] ="(";
		if (emot.re != null)
			regex[idx++] = emot.re;
		else
			regex[idx++] = emot.smiley.replace(ZmEmoticonObjectHandler.RE_ESCAPE_RE, "\\$1");
		regex[idx++] =")";		
	}
	this._EMOTICONS_RE = new RegExp(regex.join(""), "g");

};

ZmEmoticonObjectHandler.prototype = new ZmObjectHandler;
ZmEmoticonObjectHandler.prototype.constructor = ZmEmoticonObjectHandler;

ZmEmoticonObjectHandler.TYPE = "emoticon";

ZmEmoticonObjectHandler.RE_ESCAPE_RE = /([\(\)\-\$])/g;

ZmEmoticonObjectHandler.prototype.match =
function(line, startIndex) {
    this._EMOTICONS_RE.lastIndex = startIndex;
    var m = this._EMOTICONS_RE.exec(line);
    if (m) m.context = {};
    return m;
};

ZmEmoticonObjectHandler.prototype._getHtmlContent =
function(html, idx, smiley, context) {
	context.sd = this._smileyToSD[smiley];
	if (context.sd) {
        	html[idx++] = AjxImg.getImageHtml(context.sd.image, null, null, true);
   	} else {
   	    return AjxStringUtil.htmlEncode(smiley);
   	}
	return idx;
};
	
ZmEmoticonObjectHandler.prototype.selected =
function(obj, span, ev, context) {
    if (context.isRaw) {
        span.innerHTML = context.html;
        context.isRaw = false;
    } else {
        context.html = span.innerHTML;
        context.isRaw = true;
        span.innerHTML = AjxStringUtil.htmlEncode(context.sd.smiley);
    }
};

ZmEmoticonObjectHandler.prototype.getToolTipText =
function(smiley, context) {
	return "<b>" + context.sd.tooltip + "</b>";
};

ZmEmoticonObjectHandler.prototype.getHoveredClassName =
function(object, context) {
    return this._className;
}

ZmEmoticonObjectHandler.prototype.getActionMenu =
function(obj) {
	return null;
};
