/*
 * 
 */

ZmDocument = function(id, list) {
	ZmNotebookItem.call(this, ZmItem.DOCUMENT, id, list);
}
ZmDocument.prototype = new ZmNotebookItem;
ZmDocument.prototype.constructor = ZmDocument;

ZmDocument.prototype.toString = function() {
	return "ZmDocument";
};

// Data

ZmDocument.prototype.contentType;

// Static functions

ZmDocument.createFromDom = function(node, args) {
	var doc = new ZmDocument(node.id, args.list);
	doc.set(node);
	return doc;
};

// Public methods

ZmDocument.prototype.set = function(data) {
	ZmNotebookItem.prototype.set.call(this, data);

	// ZmDocument fields
	this.contentType = data.ct != null ? data.ct : this.contentType;
};
