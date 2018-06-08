/*
 * 
*/


/**
 * Object that deals with Preferences and Preferences dialog
 * @param zimlet  Email Zimlet
 */
function EmailToolTipPrefDialog(zimlet) {
	this.zimlet = zimlet;
	this.emailZimlet_tooltipArea = this.zimlet.getUserProperty("emailZimlet_tooltipArea");
	if(!this.emailZimlet_tooltipArea) {
		this.emailZimlet_tooltipArea = EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_MEDIUM];
	}
	this.updateEmailTooltipSize();
}

EmailToolTipPrefDialog.SIZE_VERYSMALL = "VERYSMALL";
EmailToolTipPrefDialog.SIZE_SMALL = "SMALL";
EmailToolTipPrefDialog.SIZE_MEDIUM = "MEDIUM";
EmailToolTipPrefDialog.SIZE_LARGE = "LARGE";
EmailToolTipPrefDialog.SIZE_XL = "XL";

EmailToolTipPrefDialog.DIMENSIONS = [];
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_VERYSMALL]  = "220px x 130px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_SMALL]  = "230px x 140px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_MEDIUM]  = "280px x 150px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_LARGE]  = "260px x 200px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_XL]  = "270px x 210px";

/**
 * Updates Email Tooltip's tooltipWidth and tooltipHeight
 */
EmailToolTipPrefDialog.prototype.updateEmailTooltipSize =
function() {
	var size = this.emailZimlet_tooltipArea.replace(/px/ig, "");
	var arry = size.split(" x ");
	EmailTooltipZimlet.tooltipWidth = arry[0];
	EmailTooltipZimlet.tooltipHeight = arry[1];
};