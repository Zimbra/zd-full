/*
 * 
 */

/**
 * Creates a phone call.
 * @constructor
 * @class
 * This class represents a phone call.
 *
 * @param id		[int]			unique ID
 * @param list		[ZmVoiceList]	list that contains this item 
 */
ZmCall = function(id, list) {
	ZmVoiceItem.call(this, ZmItem.VOICEMAIL, id, list);
}

ZmCall.prototype = new ZmVoiceItem;
ZmCall.prototype.constructor = ZmCall;

ZmCall.prototype.toString = 
function() {
	return "ZmCall";
}

/**
* Fills in the voicemail from the given message node.
*
* @param node		a message node
* @param args		hash of input args
*/
ZmCall.createFromDom =
function(node, args) {
	var result = new ZmCall(node.id, args.list);
	result._loadFromDom(node);
	return result;
};

ZmCall.prototype._loadFromDom =
function(node) {
	ZmVoiceItem.prototype._loadFromDom.call(this, node);
};

