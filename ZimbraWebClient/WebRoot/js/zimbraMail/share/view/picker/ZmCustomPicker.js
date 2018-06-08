/*
 * 
 */

/**
 * @overview
 */

/**
 * Creates a custom picker control.
 * @class
 * This class represents a custom picker control.
 * 
 * @param		{DwtControl}	parent		the parent
 * 
 * @extends		ZmPicker
 * @see			ZmPicker.CUSTOM
 */
ZmCustomPicker = function(parent) {

	ZmPicker.call(this, parent);

    this.setTitle(ZmMsg.search);
    this.setImage("Search");
}

ZmCustomPicker.prototype = new ZmPicker;
ZmCustomPicker.prototype.constructor = ZmCustomPicker;

ZmPicker.CTOR[ZmPicker.CUSTOM] = ZmCustomPicker;

ZmCustomPicker.prototype.toString = 
function() {
	return "ZmCustomPicker";
}

ZmCustomPicker.prototype._setupPicker =
function(parent) {
	var picker = new DwtComposite(parent);
	var size = 24;
	var fieldId = Dwt.getNextId();
	var html = new Array(20);
	var i = 0;
	html[i++] = "<p>" + ZmMsg.addSearch + "</p>";
	html[i++] = "<center><table cellpadding='2' cellspacing='0' border='0'>";
	html[i++] = "<tr valign='middle'>";
	html[i++] = "<td align='right' nowrap>" + ZmMsg.searchLabel + "</td>";
	html[i++] = "<td align='left' nowrap><input type='text' autocomplete='off' nowrap size='" + size + "' id='" + fieldId + "'/></td>";
	html[i++] = "</tr>";
	html[i++] = "</table></center>";
	picker.getHtmlElement().innerHTML = html.join("");

	var field = this._field = document.getElementById(fieldId);
	Dwt.setHandler(field, DwtEvent.ONCHANGE, ZmCustomPicker._onChange);
	field._picker = this;
}

ZmCustomPicker._onChange = 
function(ev) {
	var element = DwtUiEvent.getTarget(ev);
	var picker = element._picker;
	var charCode = DwtKeyEvent.getCharCode(ev);
	if (charCode == 13 || charCode == 3) {
		picker.execute();
	    return false;
	} else {
		picker._updateQuery();
		return true;
	}	
}

ZmCustomPicker.prototype._updateQuery = 
function() {
	var val = this._field.value;
	if (val)
		this.setQuery("(" + this._field.value + ")");
	else
		this.setQuery("");
}
