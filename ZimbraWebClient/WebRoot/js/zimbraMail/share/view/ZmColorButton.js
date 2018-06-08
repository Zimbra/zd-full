/*
 * 
 */

ZmColorButton = function(params) {
    if (arguments.length == 0) return;
    DwtButton.call(this, params);
    var menu = new ZmColorMenu({parent:this,hideNone:params.hideNone});
    menu.addSelectionListener(new AjxListener(this, this._handleSelection));
    this.setMenu(menu);
    this._colorMenu = menu;
};
ZmColorButton.prototype = new DwtButton;
ZmColorButton.prototype.constructor = ZmColorButton;

ZmColorButton.prototype.toString = function() {
    return "ZmColorButton";
};

//
// Public methods
//

ZmColorButton.prototype.setImage = function(image, skipMenu) {
    DwtButton.prototype.setImage.apply(this, arguments);
    if (!skipMenu) {
        this._colorMenu.setImage(image);
    }
};

ZmColorButton.prototype.setValue = function(color) {
	var standardColorCode = ZmOrganizer.getStandardColorNumber(color);
	if(standardColorCode != -1) {
	 this._color = standardColorCode;
	} else {
    this._color = color;
	}
    var image = this.getImage();
    if (image) {
        image = image.replace(/,.*$/,"");
        this.setImage(this._color?[image,this._color].join(",color="):image, true);
    }
    this.setText(this._colorMenu.getTextForColor(this._color));
};


ZmColorButton.prototype.getValue = function() {
    return this._color;
};

//
// Protected methods
//

ZmColorButton.prototype._handleSelection = function(evt) {
    this.setValue(evt.item.getData(ZmOperation.MENUITEM_ID)); 
};