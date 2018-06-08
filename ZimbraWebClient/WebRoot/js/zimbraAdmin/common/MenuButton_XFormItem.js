/*
 * 
 */
 
/**
* @class defines XFormItem type  _MENU_BUTTON_
* Adapts a DwtButton with a drop-down menu of choices to work with the XForm
* @constructor
* @author Greg Solovyev
**/
MenuButton_XFormItem = function() {}
XFormItemFactory.createItemType("_MENU_BUTTON_", "menu_button", MenuButton_XFormItem, Dwt_Button_XFormItem);
MenuButton_XFormItem.prototype.constructWidget = function () {
	var widget = Dwt_Button_XFormItem.prototype.constructWidget.call(this);
	var opList = this.getNormalizedValues();
	if (opList && opList.length) {
		var menu = new ZaPopupMenu(widget, null,null, opList);
		widget.setMenu(menu);
	}
	return widget;
}