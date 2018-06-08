/*
 * 
 */

/**
 * Creates a radio button.
 * @constructor
 * @class
 * This class implements a radio button.
 * 
 * @param {hash}	params	a hash of parameters
 * @param  {DwtComposite}     params.parent	the parent widget
 * @param  {constant}     params.style 	the text style. May be one of: {@link DwtCheckbox.TEXT_LEFT} or
 * 									{@link DwtCheckbox.TEXT_RIGHT} arithimatically or'd (|) with one of:
 * 									{@link DwtCheckbox.ALIGN_LEFT}, {@link DwtCheckbox.ALIGN_CENTER}, or
 * 									{@link DwtCheckbox.ALIGN_LEFT}.
 * 									The first determines were in the checkbox the text will appear
 * 									(if set), the second determine how the content of the text will be
 * 									aligned. The default value for this parameter is: 
 * 									{@link DwtCheckbox.TEXT_LEFT} | {@link DwtCheckbox.ALIGN_CENTER}
 * @param  {string}     params.name		the input control name (required for IE)
 * @param  {string}     params.value     the input control value.
 * @param  {boolean}     params.checked	the input control checked status (required for IE)
 * @param  {string}     params.className	the CSS class
 * @param  {constant}     params.posStyle	the positioning style (see {@link DwtControl})
 * @param  {string}     params.id		an explicit ID to use for the control's HTML element
 * @param  {number}     params.index 	the index at which to add this control among parent's children
 * 
 * @extends	DwtCheckbox
 */
DwtRadioButton = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtRadioButton.PARAMS);
	params.className = params.className || "DwtRadioButton";
	DwtCheckbox.call(this, params);
}

DwtRadioButton.PARAMS = DwtCheckbox.PARAMS;

DwtRadioButton.prototype = new DwtCheckbox;
DwtRadioButton.prototype.constructor = DwtRadioButton;

DwtRadioButton.prototype.toString = function() {
	return "DwtRadioButton";
};

//
// Data
//

DwtRadioButton.prototype.TEMPLATE = "dwt.Widgets#DwtRadioButton";
