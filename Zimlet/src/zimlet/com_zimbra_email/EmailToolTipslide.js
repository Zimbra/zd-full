/*
 * 
 */

function EmailToolTipSlide(html, visible, iconName, selectCallback, name) {
	this.html = html;
	this.visible = visible;
	this.iconName = iconName;
	this.id = Dwt.getNextId();
	this.iconDivId = Dwt.getNextId();
	this.selectCellId = Dwt.getNextId();
	this.slideShow = null;
	this.canvasElement = null;
	this._selectCallback = selectCallback;
	this.name = name;
};

EmailToolTipSlide.prototype.select =
function() {
	if(this.slideShow.currentSlideId) {
		document.getElementById(this.slideShow.currentSlideId).style.display = "none";
	}
	var offsetHeight = document.getElementById(EmailToolTipSlideShow.mainDivId).offsetHeight;
	if(offsetHeight != 0) {
		document.getElementById(this.id).style.height =offsetHeight;
	}

	document.getElementById(this.id).style.display = "block";

	this.slideShow.currentSlideId = this.id;
	if(this.slideShow.currentSelectCellId) {
		document.getElementById(this.slideShow.currentSelectCellId).style.background = "";
	}
	document.getElementById(this.selectCellId).style.background = "white";
	this.slideShow.currentSelectCellId = this.selectCellId;
	if(this._selectCallback) {
		this._selectCallback.run();
	}
};

/**
*Sets main div element that can be used to show info/error-msgs inline
*/
EmailToolTipSlide.prototype.setCanvasElement =
function(el) {
	this.canvasElement = el;
};

EmailToolTipSlide.prototype.setInfoMessage =
function(msg) {
	this._appendMsg2Slide(msg, "EmailToolTipSlideMsgColor");
};

EmailToolTipSlide.prototype.setErrorMessage =
function(msg) {
	this._appendMsg2Slide(msg, "EmailToolTipSlideErrorColor");
};

EmailToolTipSlide.prototype._appendMsg2Slide =
function(msg, colorClass) {
	var html = ["<div class='EmailToolTipSlideText ",colorClass,"'>",msg,"</div>"].join("");
	if(this.canvasElement) {
		this.canvasElement.innerHTML = html;
	}
};

EmailToolTipSlide.prototype.clearSlideMessage =
function(msg, colorClass) {
	if(this.canvasElement) {
		this.canvasElement.innerHTML = "";
	}
};
