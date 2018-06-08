/**
 * This file is included from NodeWebkitBackground.js file.
 * Used for tracking window's state(Position of window) 
 */

NodeWebkitWindowState = {
    winState: null,
    currWinMode: null,
    isMaximizationEvent: false,
    resizeTimeout: null,

    initWindowState: function(win) {
        console.log("localStorage.windowState" + localStorage.windowState);
        NodeWebkitWindowState.registerEventsForWindowState(win);

        winState = JSON.parse(localStorage.windowState || 'null');

        if (winState) {
            currWinMode = winState.mode;
            if (currWinMode === 'maximized') {
                win.maximize();
            } else {
                NodeWebkitWindowState.restoreWindowState(win);
            }
        } else {
            currWinMode = 'normal';
            NodeWebkitWindowState.dumpWindowState(win);
        }
        win.show();
    },

    dumpWindowState: function(win) {
        if (!winState) {
            winState = {};
        }
        //we don't want to save minimized state, only maximized or normal
        if (currWinMode === 'maximized') {
            winState.mode = 'maximized';
        } else {
            winState.mode = 'normal';
        }

        // when window is maximized you want to preserve normal
        // window dimensions to restore them later (even between sessions)
        if (currWinMode === 'normal') {
            winState.x = win.x;
            winState.y = win.y;
            winState.width = win.width;
            winState.height = win.height;
        }
    },

    restoreWindowState: function (win, showWindow) {
        win.resizeTo(winState.width, winState.height);
        win.moveTo(winState.x, winState.y);
        showWindow && win.show();
    },

    saveWindowState: function(win) {
        if(winState) {
            NodeWebkitWindowState.dumpWindowState(win);
            localStorage.windowState = JSON.stringify(winState);
        }
        localStorage.zoomLevel = win.zoomLevel;
    },

    registerEventsForWindowState: function(win) {
        win.on('maximize', function () {
            isMaximizationEvent = true;
            currWinMode = 'maximized';
        });

        win.on('unmaximize', function () {
            currWinMode = 'normal';
            NodeWebkitWindowState.restoreWindowState(win);
        });

        win.on('minimize', function () {
            currWinMode = 'minimized';
        });

        win.on('restore', function () {
            currWinMode = 'normal';
        });

        win.window.addEventListener('resize', function () {
            // resize event is fired many times on one resize action,
            // this hack with setTiemout forces it to fire only once
            if(NodeWebkitWindowState.resizeTimeout) {
                clearTimeout(NodeWebkitWindowState.resizeTimeout);
            }

            NodeWebkitWindowState.resizeTimeout = setTimeout(function () {
                // on MacOS you can resize maximized window, so it's no longer maximized
                if (NodeWebkitWindowState.isMaximizationEvent) {
                    // first resize after maximization event should be ignored
                    NodeWebkitWindowState.isMaximizationEvent = false;
                } else {
                    if (currWinMode === 'maximized') {
                        currWinMode = 'normal';
                    }
                }
                NodeWebkitWindowState.dumpWindowState(win);
            }, 500);
        }, false);
    },

    getLastZoomLevel: function() { 
        return (localStorage.zoomLevel && parseFloat(localStorage.zoomLevel)) || 0;
    }
};

module.exports = NodeWebkitWindowState;
