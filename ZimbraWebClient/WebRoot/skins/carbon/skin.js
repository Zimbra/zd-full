/*
 * 
 */
//
// Skin class
//
function CarbonSkin() {
    BaseSkin.call(this);
    this.hints.toast = { location: "C", 
		transitions: [
				{ type: "fade-in", step: 5, duration: 50 },
                { type: "pause", duration: 5000 },
                { type: "fade-out", step: -10, duration: 500 }
			] 
		};
}
CarbonSkin.prototype = new BaseSkin;
CarbonSkin.prototype.constructor = CarbonSkin;

//
// Skin instance
//

window.skin = new CarbonSkin();
