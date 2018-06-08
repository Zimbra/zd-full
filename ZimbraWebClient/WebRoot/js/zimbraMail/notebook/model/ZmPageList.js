/*
 * 
 */

ZmPageList = function(search, type) {
	ZmList.call(this, type || ZmItem.PAGE, search);
}

ZmPageList.prototype = new ZmList;
ZmPageList.prototype.constructor = ZmPageList;

ZmPageList.prototype.toString = function() {
	return "ZmPageList";
};

// Public methods

/***
ZmPageList.prototype.addFromDom =
function(node, args) {
	this.type = node._type || this.type;
	return ZmList.prototype.addFromDom.call(this, node, args);
};
/***/
