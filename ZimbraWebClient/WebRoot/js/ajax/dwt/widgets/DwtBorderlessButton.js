/*
 * 
 */

/**
 * Creates a border less button.
 * @constructor
 * @class
 * This class represents a button without a border.
 *
 * @param {hash}	params		a hash of parameters
 * @param {DwtComposite}      params.parent		the parent widget
 * @param {constant}      params.style			the button style (see {@link DwtButton})
 * @param {string}      params.className		the CSS class
 * @param {constant}      params.posStyle		the positioning style (see {@link Dwt})
 * @param {DwtButton.ACTION_MOUSEUP|DwtButton.ACTION_MOUSEDOWN}      params.actionTiming	if {@link DwtButton.ACTION_MOUSEUP}, then the button is triggered
 *											on mouseup events, else if {@link DwtButton.ACTION_MOUSEDOWN},
 * 											then the button is triggered on mousedown events
 * @param {string}      params.id			the ID to use for the control's HTML element
 * @param {number}      params.index 		the index at which to add this control among parent's children
 * 
 * @extends		DwtButton
 */
DwtBorderlessButton = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtBorderlessButton.PARAMS);

	DwtButton.call(this, params);
}

DwtBorderlessButton.PARAMS = ["parent", "style", "className", "posStyle", "actionTiming", "id", "index"];

DwtBorderlessButton.prototype = new DwtButton;
DwtBorderlessButton.prototype.constructor = DwtBorderlessButton;

DwtBorderlessButton.prototype.toString =
function() {
	return "DwtBorderlessButton";
}

//
// Data
//

DwtBorderlessButton.prototype.TEMPLATE = "dwt.Widgets#ZBorderlessButton"

