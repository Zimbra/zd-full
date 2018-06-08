/*
 * 
 */

ZmImageAttachmentObjectHandler = function() {
	ZmObjectHandler.call(this, ZmImageAttachmentObjectHandler.TYPE);
	this._imageHash = {};
}

ZmImageAttachmentObjectHandler.prototype = new ZmObjectHandler;
ZmImageAttachmentObjectHandler.prototype.constructor = ZmImageAttachmentObjectHandler;

ZmImageAttachmentObjectHandler.TYPE = "imageAttachemnt";

ZmImageAttachmentObjectHandler.THUMB_SIZE = 'width="320" height="240"';
ZmImageAttachmentObjectHandler.THUMB_SIZE_MAX = 320;
	
// already htmlencoded!!
ZmImageAttachmentObjectHandler.prototype._getHtmlContent =
function(html, idx, obj, context) {
	html[idx++] = obj; //AjxStringUtil.htmlEncode(obj, true);
	return idx;
}

ZmImageAttachmentObjectHandler.prototype.getToolTipText =
function(url, context) {
	var image = this._imageHash[context.url];
	if (!image || !image.el || (image.el.src != context.url)) {
		image = {id:Dwt.getNextId()};
		this._imageHash[context.url] = image;
		this._preload(context.url, image.id);	
	}
	
	var el = document.getElementById(image.id);
	if (el && !image.el) {
		image.el = el;
	}
	if (image.el) {
		return image.el.xml || image.el.outerHTML;
	}
	return '<img id="'+ image.id +'" style="visibility:hidden;"/>';
};

ZmImageAttachmentObjectHandler.prototype.getActionMenu =
function(obj) {
	return null;
};

ZmImageAttachmentObjectHandler.prototype._preload =
function(url, id) {
	var tmpImage = new Image();
	tmpImage.onload = AjxCallback.simpleClosure(this._setSize, this, id, tmpImage);
	tmpImage.src = url;
}

ZmImageAttachmentObjectHandler.prototype._setSize =
function(id, tmpImage) {
	var elm = document.getElementById(id);
	if(elm) {
		var width = tmpImage.width;
		var height = tmpImage.height;
		if(width > ZmImageAttachmentObjectHandler.THUMB_SIZE_MAX && width >= height) {
			elm.width = ZmImageAttachmentObjectHandler.THUMB_SIZE_MAX;
		} else if (height > ZmImageAttachmentObjectHandler.THUMB_SIZE_MAX && height > width) {
			elm.height = ZmImageAttachmentObjectHandler.THUMB_SIZE_MAX;
		} else {
			elm.width = width;
			elm.width = height;
		}
		elm.src = tmpImage.src;
		elm.style.visibility = "visible";
	}
	tmpImage.onload = null;
	tmpImage = null;
}
