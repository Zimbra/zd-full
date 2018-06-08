/*
 * 
 */

/**
 * @overview
 * This file defines the tab application button.
 *
 */

/**
 * @class
 * This class represents a button that behaves like a "tab" button, designed specifically for the row of
 * applications buttons at the top of the Zimbra Web Client interface.
 * <p>
 * Limitations:
 * <ul>
 * <li>cannot have a menu</li>
 * <li>does not support enabled/disabled</li>
 * </ul>
 * </p>
 * 
 * @author Conrad Damon
 * 
 * @param	{Hash}		params		a hash of parameters
 * 
 * @extends		DwtButton
 */
ZmAppButton = function(params) {

	if (arguments.length == 0) { return; }

    params.style = DwtLabel.IMAGE_LEFT;
	params.posStyle = DwtControl.RELATIVE_STYLE;
    DwtButton.call(this, params);

    this.setImage(params.image);
    this.setText(params.text);
};

ZmAppButton.prototype = new DwtButton;
ZmAppButton.prototype.constructor = ZmAppButton;

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmAppButton.prototype.toString =
function() {
	return "ZmAppButton";
};

//
// Data
//

ZmAppButton.prototype.TEMPLATE = "share.Widgets#ZmAppChooserButton";

//
// Public methods
//
ZmAppButton.prototype.setSelected =
function(selected) {
    this.isSelected = selected;
    this.setDisplayState(selected ? DwtControl.SELECTED : DwtControl.NORMAL);
};

/**
 * Sets the display state.
 * 
 * @param	{String}	state		the display state
 * @see		DwtControl
 */
ZmAppButton.prototype.setDisplayState =
function(state) {
    if (this.isSelected && state != DwtControl.SELECTED) {
        state = [DwtControl.SELECTED, state].join(" ");
    }
    DwtButton.prototype.setDisplayState.call(this, state);
};

ZmAppButton.prototype.getKeyMapName =
function() {
	return "ZmAppButton";
};

ZmAppButton.prototype.handleKeyAction =
function(actionCode, ev) {

	switch (actionCode) {

		case DwtKeyMap.SELECT:
			if (this.isListenerRegistered(DwtEvent.SELECTION)) {
				var selEv = DwtShell.selectionEvent;
				selEv.item = this;
				this.notifyListeners(DwtEvent.SELECTION, selEv);
			}
			break;

		default:
			return false;
	}
	return true;
};

/**
 * App toolbar buttons user ZHover instead of ZFocused
 * 
 * @private
 */
ZmAppButton.prototype._focus =
function() {
    this.setDisplayState(DwtControl.HOVER);
};
