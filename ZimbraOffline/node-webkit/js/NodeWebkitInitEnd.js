/*
This JavaScript file will be executed for every page using NW.js framework.

JavaScript will be executed after the document object is loaded, before onload event is fired.
 */
if(typeof nw !== 'undefined') {
    console.log('NodeWebkitInitEnd.js is loaded');

    NodeWebkitInitEnd = {
        init: function() {
            console.log('NodeWebkitInitEnd::init');

            NodeWebkitInitEnd.createMACMenuBar();

            NodeWebkitInitEnd.sysTrayEvents();

            NodeWebkitInitEnd.windowEvents();
        },

        // OSX only. Creates the default menus (App, Edit and Window).
        createMACMenuBar: function() {
            var win = nw.Window.get(),
                menu;

            menu = NodeWebkitUtils.getMACMenuBar();

            if(menu) {
                console.log('NodeWebkitInitEnd.js::createMACMenuBar');
                win.menu = menu;
            }
        },

        // Windows only, register events for system tray
        sysTrayEvents: function() {
            var tray = NodeWebkitUtils.getSysTray();

            if(tray) {
                console.log('NodeWebkitInitEnd.js::sysTrayEvents');
                tray.on('click', NodeWebkitInitEnd.openHandler);

                var menu,
                    menuItems = tray.menu.items;

                for(var i = 0, len = menuItems.length; i < len; i++) {
                    menu = menuItems[i];

                    switch(menu.key) {
                        case 'open':
                            menu.on('click', NodeWebkitInitEnd.openHandler);
                            break;
                        case 'quit':
                            menu.on('click', NodeWebkitInitEnd.quitApplication);
                            break;
                    }
                }
             }
        },

        openHandler: function(url) {
            var win = nw.Window.get();
            console.log('NodeWebkitInitEnd.js::openHandler');
            NodeWebkitWindowState.restoreWindowState(win, true);

            if(window && typeof window['appCtxt'] != 'undefined' && typeof url == 'string') {
                console.log('Processing mailto url', url);

                // Check if url is mailto url
                if(url.indexOf('mailto:') !== -1) {
                    var ac = appCtxt.getAppController();
                    ac.handleOfflineMailTo(url);
                 }
            }
        },

        quitApplication: function() {
            var win = nw.Window.get();
            NodeWebkitWindowState.saveWindowState(win);

            console.log('NodeWebkitInitEnd.js::quitApplication');

            // Till we kill application, hide the window so user will not be obstructed
            // as killing jetty process will take sometime,
            // and we don't want to kill UI process before killing jetty process
            win.hide();

            NodeWebkitUtils.stopJettyServer(function() {
                nw.App.quit();
             });
        },

        reopenHandler: function() {
            var win = nw.Window.get();

            console.log('NodeWebkitInitEnd.js::reopenHandler');

            win.show();
        },

        /**
         * Function is used to update badge with unread mail count and will flash the badge icon
         * so application will get user attention.
         * It additionally will also update title and icon of system tray icon in windows.
         */
        updateBadgeOnNotifications: function(count, title) {
            var win = nw.Window.get();

            // Function sets text on top of application icon in the dock for OSX
            var label = (count > 0) ? count.toString() : '';
            win.setBadgeLabel(label);

            // win.requestAttention is used to flash dock icon, to request user attention
            // for linux, number is casted to boolean value
            // for windows, dock icon will flash 2 times
            // for mac, will fire NSInformationalRequest event(by passing -ve value)
            if (NodeWebkitUtils.isMac()) {
                win.requestAttention(-1);
            } else {
                win.requestAttention((count > 0) ? 2 : false);
            }
            // also update tray icon for new mail
            var tray = NodeWebkitUtils.getSysTray();
            if (tray) {
                tray.icon = (count > 0) ? 'img/newEmail_16x16.png' : 'img/launcher_32x32.png';
                tray.tooltip = title ? title : 'Zimbra Desktop';
            }
        },

        windowEvents: function() {
            var win = nw.Window.get();

            console.log('NodeWebkitInitEnd.js::windowEvents');

            if(NodeWebkitUtils.isMac()) {
                // Close window for Menu, Dock or Shortcut(command+Q) close; hide it for Window close
                win.on('close', function(event) {
                    if (event == 'quit') {
                        NodeWebkitInitEnd.quitApplication();
                    } else {
                        // event is `undefined`
                        win.hide();
                    }
                });

                // Register a reopen event for OSX
                nw.App.on('reopen', NodeWebkitInitEnd.reopenHandler);
                nw.App.on('open', NodeWebkitInitEnd.openHandler);
            } else if(NodeWebkitUtils.isWindows()) {
                win.on('close', function() { 
                    var quitOnClose = 
                        NodeWebkitUtils.getLocalConfigData('zdesktop_quit_on_close') == 'true';

                    if(quitOnClose) {
                        NodeWebkitInitEnd.quitApplication();
                    } else {
                        win.hide();//keep ZD service running in background
                    }
                });
                nw.App.on('open', NodeWebkitInitEnd.openHandler);
            } else if(NodeWebkitUtils.isLinux()) {
                win.on('close', NodeWebkitInitEnd.quitApplication);
            }

            win.on('focus', function() {
                // Cancel user attention
                win.requestAttention(false);

                if(NodeWebkitAutoUpdate.autoUpdateWindow) {
                    NodeWebkitAutoUpdate.autoUpdateWindow.show();
                    NodeWebkitAutoUpdate.autoUpdateWindow.focus();
                }
            });
        },

        newWindowPolicyHandler: function(frame, url, policy) {
            console.log('NodeWebkitInitEnd.js::newWindowPolicyHandler');

            if(typeof nw.Shell === 'undefined') {
                return;
            }

            // allow all external links to be opened in system default browser except zd 'open in web browser' link
            if ((url.substr(0, 4) === 'http' && url.indexOf('127.0.0.1') === -1) || (url.substr(0, 4) === 'http' && url.indexOf('127.0.0.1:') > -1 && url.indexOf('/?at=') > -1)) {
                nw.Shell.openExternal(url);
                policy.ignore();

                return;
            }

            // Set Window size for all new windows
            // Remove this when nwjs fixes https://github.com/nwjs/nw.js/issues/5517
            policy.setNewWindowManifest({
                width: 764,
                height: 627/*,
                title: 'Zimbra Desktop'*/
            });
        },

        registerZoomEvent: function(e) {
            var win = nw.Window.get();
            if (e.type == 'keydown' && ((NodeWebkitUtils.isWindows() && e.ctrlKey) || (NodeWebkitUtils.isMac() && e.metaKey))) {
                switch(e.keyCode) {
                    case 48 : win.zoomLevel = 0; break;//Ctrl+0 - default zoom level
                    case 187 : win.zoomLevel += 0.2; break;//Ctrl ++ zoom in
                    case 189 : win.zoomLevel -= 0.2; break;//Ctrl-- zoom out
                }
            }
            win.zoomLevel = parseFloat(win.zoomLevel.toFixed(1));
        }
    };

    // Register event for all windows
    // This file is injected in iframe also, so check if nw exists or not
    var win = nw.Window.get();
    win.on('new-win-policy', NodeWebkitInitEnd.newWindowPolicyHandler);
    win.zoomLevel = NodeWebkitWindowState.getLastZoomLevel();
    document.addEventListener('keydown', NodeWebkitInitEnd.registerZoomEvent, false);
    document.addEventListener("wheel", NodeWebkitInitEnd.registerZoomEvent, false);

    // Call function only for parent function
    // This flag is set in launchZD.jsp
    if(window.mainWindow) {
        NodeWebkitInitEnd.init();
    } else {
        // Set title of all child windows
        win.title = 'Zimbra Desktop';
    }
} else {
    //console.log('NodeWebkitInitEnd.js nw not defined');
}