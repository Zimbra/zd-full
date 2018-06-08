/*
 * 
 */

/**
 * Creates a voicemail.
 * @constructor
 * @class
 * This class represents a voiemail.
 *
 * @param id		[int]			unique ID
 * @param list		[ZmVoiceList]	list that contains this item
 */
ZmVoicemail = function(id, list) {

	ZmVoiceItem.call(this, ZmItem.VOICEMAIL, id, list);

	this.isUnheard = false;
	this.soundUrl = null;
}

ZmVoicemail.prototype = new ZmVoiceItem;
ZmVoicemail.prototype.constructor = ZmVoicemail;

ZmVoicemail.prototype.toString = 
function() {
	return "ZmVoicemail";
}

/**
* Fills in the voicemail from the given message node.
*
* @param node		a message node
* @param args		hash of input args
*/
ZmVoicemail.createFromDom =
function(node, args) {
	var result = new ZmVoicemail(node.id, args.list);
	result._loadFromDom(node);
	return result;
};

ZmVoicemail.prototype._loadFromDom =
function(node) {
	ZmVoiceItem.prototype._loadFromDom.call(this, node);
	if (node.f) {
		this.isUnheard = node.f.indexOf("u") >= 0;
		this.isHighPriority = node.f.indexOf("!") >= 0;
		this.isPrivate = node.f.indexOf("p") >= 0;
	}
	if (node.content) this.soundUrl = node.content[0].url;
};

