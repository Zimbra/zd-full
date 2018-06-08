/*
 * 
 */

/**
 * Constructor.
 * 
 * @author Raja Rao DV (rrao@zimbra.com)
 */
function ZmAboutZimlet() {
}

ZmAboutZimlet.prototype = new ZmZimletBase();
ZmAboutZimlet.prototype.constructor = ZmAboutZimlet;

ZmAboutZimlet.prototype._getContent = function() {
	var subs = {
			version : appCtxt.getSettings().getInfoResponse.version,
			userAgent : [this.getMessage("userAgent"), " ", navigator.userAgent].join(""),
			copyright: this.getMessage("copyright")
		};
		return AjxTemplate.expand(
				"com_zimbra_about.templates.About#DialogView", subs);

};

/**
 * Called when user single-clicks on the panel
 */
ZmAboutZimlet.prototype.singleClicked = function() {
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();
	var content = this._getContent();
	dlg.setTitle(this.getMessage("label"));
	dlg.setContent(content);
	dlg.popup();
};

/**
 * Called when user double-clicks on the panel
 */
ZmAboutZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};
