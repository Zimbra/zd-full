
/*
This JavaScript file will be executed for every page using NW.js framework.

JavaScript code will be executed after any files from css, but before any other DOM is constructed or any other script is run
*/
if(typeof nw !== 'undefined') {
    console.log('NodeWebkitInitStart.js is loaded');

    if(typeof global != 'undefined') {
        NodeWebkitUtils = global.NodeWebkitUtils;
        NodeWebkitI18n = global.NodeWebkitI18n;
        NodeWebkitAutoUpdate = global.NodeWebkitAutoUpdate;
        NodeWebkitMailto = global.NodeWebkitMailto;
        NodeWebkitPrefs = global.NodeWebkitPrefs;
        NodeWebkitWindowState = global.NodeWebkitWindowState;
        // include Window state handling file(Position of window)
        NodeWebkitWindowState = require('./js/NodeWebkitWindowState.js');
    }

    window.isNodeWebkit = true;
    document.documentElement.setAttribute('data-isnodewebkit', true);

    NodeWebkitInitStart = {
        init: function() {
            console.log('NodeWebkitInitStart.js::init');

            var nwGuiWindow = nw.Window.get();
            NodeWebkitWindowState.initWindowState(nwGuiWindow);

            NodeWebkitUtils.addCopyLinkAddressContextMenu();

            console.log('Auto update preference', NodeWebkitPrefs.getPreference('AUTO_UPDATE_NOTIFICATION'));
            if (NodeWebkitPrefs.getPreference('AUTO_UPDATE_NOTIFICATION', 'release') !== 'doNotNotify') {
                //Check periodically after every 4 hours from now
                console.log('scheduling auto update timer');
                NodeWebkitAutoUpdate.scheduleAutoUpdate();
            }

            // deciding development mode
            if (NodeWebkitUtils.isDevMode()) {
                // Clear the HTTP cache in memory and the one on disk. This method call is synchronized.
                nw.App.clearCache();

                // show the dev tools
                nwGuiWindow.showDevTools();

                // Dev tools for background page
                chrome.developerPrivate.openDevTools({
                    renderViewId: -1,
                    renderProcessId: -1,
                    extensionId: chrome.runtime.id
                });
            }
            NodeWebkitUtils.clearDownloadHistory();
        },

        redirectLogsToFile: function() {
            if (NodeWebkitUtils.isDevMode() || NodeWebkitUtils.getDebugLevelForProd() <= 0) {
                // Dev mode has it's own logging mechanism
                // Only enable when user has enabled it in prod mode
                return;
            }

            console.log = NodeWebkitUtils.createWriteStream('zdesktop-ui-fg.log');
            console.error = console.log;
        }
    };

    // Redirect all console logs to file system
    NodeWebkitInitStart.redirectLogsToFile();
} else {
    //console.log('NodeWebkitInitStart.js nw not defined');
}