/**
 * This file is included from NodeWebkitBackground.js file.
 * This file is run in node context so don't try to access browser context objects in this file (like nw.Window)
 * for accessing window and other browser context objects you need to put methods in NodeWebkitInitStart or NodeWebkitInitEnd
 */
NodeWebkitUtils = {
    getSysTray: function() {
        if(NodeWebkitUtils.isWindows() && !NodeWebkitUtils.tray) {
            var menu = new nw.Menu();
            menu.append(new nw.MenuItem({
                type: 'normal',
                key: 'open',
                label: 'Open Zimbra Desktop'
            }));

            menu.append(new nw.MenuItem({
                type: 'normal',
                key: 'check_update',
                label: 'Check for Updates...',
                click: function() {
                    NodeWebkitAutoUpdate.checkForUpdate(true);
                }
            }));

            menu.append(new nw.MenuItem({
                type: 'normal',
                key: 'quit',
                label: 'Quit'
            }));

            var tray = new nw.Tray({
                icon: 'img/launcher_32x32.png',
                tooltip: 'Zimbra Desktop',
                menu: menu
            });

            NodeWebkitUtils.tray = tray;
        }

        return NodeWebkitUtils.tray;
    },

    // Check if we are working with sdk version or production
    // https://github.com/nwjs/nw.js/issues/5062
    isDevMode: function() {
        return (window.navigator.plugins.namedItem('Native Client') !== null);
    },

    getDebugLevelForProd: function() {
        // For production version, user can enable logging using localconfig.xml
        var debugLevel = NodeWebkitUtils.getLocalConfigData('zdesktop_ui_debug_level');
        debugLevel = parseInt(debugLevel, 10);

        return isNaN(debugLevel) ? 0 : debugLevel;
    },

    /**
     * Starts jetty server process and execute callback if passed as argument
     * callback will be only called when jetty server is successfully started
     * Currently this is only used in windows as mac and linux don't create jetty server from UI.
     */
    startJettyServer: function(callback) {
        var child_process = require('child_process'),
            path = require('path'),
            cmd;

        console.log('NodeWebkitUtils::startJettyServer');

        if (NodeWebkitUtils.isWindows()) {
            cmd = path.join(NodeWebkitUtils.getApplicationDataPath(), 'bin', 'zdctl.vbs');

            // Register callback so after jetty process is terminated we can proceed further
            console.log('Execute', ['cmd.exe', '/c', cmd, 'start'].join(' '));
            var terminal = child_process.execFile('cmd.exe', ['/c', cmd, 'start'], function(error, stdout, stderr) {
                if(error) {
                    console.log('Jetty server failed to start', error);
                    return;
                }

                if(callback) {
                    console.log('Jetty server started');

                    callback.call(NodeWebkitUtils);

                    //This will be called on every launch. Whenever locale is changed same function will be called from ZmSettings.js
                    NodeWebkitI18n.loadZdMessages();
                }
            });
        } /*else if(NodeWebkitUtils.isMac()) {
            cmd = path.join(NodeWebkitUtils.getApplicationDataPath(), 'bin', 'start-zdesktop');

            // Register callback so after jetty process is terminated we can proceed further
            var terminal = child_process.execFile(cmd, function(error, stdout, stderr) {
                if(error) {
                    console.log('Jetty server failed to start', error);
                    return;
                }

                if(callback) {
                    console.log('Jetty server started');
                    callback.call(NodeWebkitUtils);
                }
            });
        } else if(NodeWebkitUtils.isLinux()) {
            cmd = path.join(NodeWebkitUtils.getApplicationDataPath(), 'bin', 'zdesktop');

            // Register callback so after jetty process is terminated we can proceed further
            var terminal = child_process.execFile(cmd, ['start'], function(error, stdout, stderr) {
                if(error) {
                    console.log('Jetty server failed to start', error);
                    return;
                }

                if(callback) {
                    console.log('Jetty server started');
                    callback.call(NodeWebkitUtils);
                }
            });
        } */else {
            // fallback method for macos and linux
            callback.call(NodeWebkitUtils);
            NodeWebkitI18n.loadZdMessages();
        }
    },

    /**
     * Stops jetty server process and execute callback if passed as argument
     * callback will be only called when jetty server is successfully closed
     */
    stopJettyServer: function(callback) {
        var child_process = require('child_process'),
            path = require('path'),
            cmd;

        console.log('NodeWebkitUtils::stopJettyServer');
        NodeWebkitUtils.clearDownloadHistory();
        if (NodeWebkitUtils.isWindows()) {
            cmd = path.join(NodeWebkitUtils.getApplicationDataPath(), 'bin', 'zdctl.vbs');

            // Register callback so after jetty process is terminated we can proceed further
            console.log('Execute', ['cmd.exe', '/c', cmd, 'stop'].join(' '));
            var terminal = child_process.execFile('cmd.exe', ['/c', cmd, 'stop'], function(error, stdout, stderr) {
                if (error) {
                    console.log('Jetty server failed to stop', error);
                    return;
                }

                if (callback) {
                    console.log('Jetty server stopped');
                    callback.call(NodeWebkitUtils);
                }

                NodeWebkitUtils.destroyWriteStream();
            });
        } else if (NodeWebkitUtils.isMac()) {
            cmd = path.join(NodeWebkitUtils.getApplicationDataPath(), 'bin', 'stop-zdesktop');

            // Register callback so after jetty process is terminated we can proceed further
            console.log('Execute', cmd);
            var terminal = child_process.execFile(cmd, function(error, stdout, stderr) {
                if (error) {
                    console.log('Jetty server failed to stop', error);
                    return;
                }

                if (callback) {
                    console.log('Jetty server stopped');
                    callback.call(NodeWebkitUtils);
                }

                NodeWebkitUtils.destroyWriteStream();
            });
        } else if (NodeWebkitUtils.isLinux()) {
            cmd = path.join(NodeWebkitUtils.getApplicationDataPath(), 'bin', 'zdesktop');

            // Register callback so after jetty process is terminated we can proceed further
            console.log('Execute', [cmd, 'stop'].join(' '));
            var terminal = child_process.execFile(cmd, ['stop'], function(error, stdout, stderr) {
                if (error) {
                    console.log('Jetty server failed to stop', error);
                    return;
                }

                if (callback) {
                    console.log('Jetty server stopped');
                    callback.call(NodeWebkitUtils);
                }

                NodeWebkitUtils.destroyWriteStream();
            });
        }
    },

    /**
     * Get data of localconfig.xml file
     * it reads the file once and then caches the result (experimental, do we need to read it everytime or chaching would be good ?)
     */
    getLocalConfigData: function(propertyName) {
        var xmlTags, xmlDoc,
            path = require('path'),
            lcFile = path.join(NodeWebkitUtils.getApplicationDataPath(), 'conf', 'localconfig.xml'),
            fs = require('fs'), textData;

        var stats = fs.statSync(lcFile);
        var mtime = new Date(stats.mtime);

        console.log('NodeWebkitUtils::getLocalConfigData');
        console.log('Local config file last modified time', mtime);
        console.log('Local config file caching time', NodeWebkitUtils.localConfigLastUpdated);

        if (!NodeWebkitUtils.localConfigData || mtime > NodeWebkitUtils.localConfigLastUpdated) {
            NodeWebkitUtils.localConfigLastUpdated = new Date();

            var fileData, xmlDoc, xmlTags,
                parser = new DOMParser();

            fileData = fs.readFileSync(lcFile, 'utf8');
            xmlDoc = parser.parseFromString(fileData, 'text/xml');

            NodeWebkitUtils.localConfigData = xmlDoc;
        } else {
            console.log('Reading local config from cached data');
            xmlDoc = NodeWebkitUtils.localConfigData;
        }

        xmlTags = xmlDoc.getElementsByTagName('key');
        for (var i = 0, len = xmlTags.length; i < len; i++) {
            if (xmlTags[i].getAttribute('name') === propertyName) {
                textData = xmlTags[i].getElementsByTagName('value')[0].textContent;

                console.log('Reading local config', propertyName, textData);
                return textData;
            }
        }
    },

    /**
     * Gets application url which will be used to load ZD UI.
     */
    getApplicationUrl: function() {
        var localeId = NodeWebkitPrefs.getPreference('LOCALE_NAME'),
            url = '', debugLevel;

        console.log('NodeWebkitUtils::getApplicationUrl');

        if (NodeWebkitUtils.isWindows()) {
            var installationKey = NodeWebkitUtils.getLocalConfigData('zdesktop_installation_key'),
                jettyPort = NodeWebkitUtils.getLocalConfigData('zimbra_admin_service_port');

            url = 'http://127.0.0.1:' + jettyPort + '/desktop/login.jsp?at=' + installationKey;
        } else {
            // Read argument sent with nw executable (url)
            url = NodeWebkitUtils.getAppParam('url=', true);
        }

        if (localeId) {
            url += '&localeId=' + localeId;
        }

        // Check if we need to load debug version
        if (NodeWebkitUtils.isDevMode()) {
            url += '&dev=1&log=console';
        } else {
            debugLevel = NodeWebkitUtils.getDebugLevelForProd();
            if(debugLevel > 0) {
                url += '&log=console&debug=' + debugLevel;
            }
        }

        console.log('Application url', url);

        return url;
    },

    /**
     * Finds system dependent data path for zimbra desktop
     */
    getApplicationDataPath: function() {
        console.log('NodeWebkitUtils::getApplicationDataPath');

        var dataDir = NodeWebkitUtils.getAppParam('data-path=', true);

        // When installer has put mailto urls in registry, we will not be able to put data path in that registry
        // so for that particular scenario we have taken hardcoded path here
        if(typeof dataDir == 'undefined' && NodeWebkitUtils.isWindows()) {
            console.log('Get hardcoded data path for windows');
            dataDir = nw.process.env.LOCALAPPDATA + '\\Zimbra\\Zimbra Desktop';
        }

        console.log('Application data path', dataDir);

        return dataDir;
     },

    /**
     * Finds system dependent application path for zimbra desktop
     */
    getApplicationPath: function() {
        console.log('NodeWebkitUtils::getApplicationPath');

        var path = require('path'),
            appPath = NodeWebkitUtils.isWindows() ? path.dirname(process.execPath) : global.__dirname;

        console.log('Application path', appPath);

        return appPath;
    },

     /**
      * Finds particular parameter from list of passed parameters to application
      * It uses prefix to find out parameter like url=, data-path=
      */
    getAppParam: function(prefix, removePrefix) {
        console.log('NodeWebkitUtils::getAppParam');

        var params = nw.App.argv,
            param;

         // Default is false
        removePrefix = removePrefix || false;

        if(prefix) {
            param = params.find(function(p) {
                if(p.startsWith(prefix)) {
                    return true;
                }
            });

            if(typeof param != 'undefined') {
               if(removePrefix) {
                   param = param.replace(prefix, '');
               }
            }

            console.log('Application parameter', prefix, param);

            return param;
        } else {
            console.log('Application parameters', params);

            // if prefix is not specified then return all parameters
            return params;
        }
    },

    getMACMenuBar: function() {
        if(NodeWebkitUtils.isMac() && !NodeWebkitUtils.macMenuBar) {
            var nativeMenuBar = new nw.Menu({
                type : 'menubar'
            });

            nativeMenuBar.createMacBuiltin('Zimbra Desktop', {
                hideEdit: false,
                hideWindow: true
            });

            var updateCheckerMenu = new nw.MenuItem({
                label: 'Check for Updates...',
                click: function() {
                    NodeWebkitAutoUpdate.checkForUpdate(true);
                }
            });

            var zimbraDesktopSubmenu = nativeMenuBar.items[0].submenu;  
            zimbraDesktopSubmenu.insert(updateCheckerMenu, 1);

            NodeWebkitUtils.macMenuBar = nativeMenuBar;
        }

        return NodeWebkitUtils.macMenuBar;
    },

    addCopyLinkAddressContextMenu: function() {
        console.log('NodeWebkitUtils::addCopyLinkAddressContextMenu');

        chrome.contextMenus.create({
            'id': 'copy_link',
            'title': 'Copy Link Address',
            'contexts': ['link']
        });

        chrome.contextMenus.onClicked.addListener(function(info, tab) {
            console.log('Chrome native context menu clicked', info, tab);

            if (info.menuItemId === 'copy_link') {
                console.log('Copy link url to clipboard', info.linkUrl);

                var clipboard = nw.Clipboard.get();
                clipboard.set(info.linkUrl, 'text');
            }
        });
    },

    isWindows: function() {
        return (process.platform === 'win32');
    },

    isMac: function() {
        return (process.platform === 'darwin');
    },

    isLinux: function() {
        return (process.platform === 'linux');
    },

    /**
     * Returns md5 hash of string
     */
    getHashOfString: function(str) {
        var crypto = require('crypto'),
            hash = crypto.createHash('md5');

        hash.update(str);
        return hash.digest('hex');
    },

    // Array of opened file streams for foreground and background log files
    openedStreams: {},
    createWriteStream: function(fileName) {
        var fs = require('fs'),
            path = require('path'),
            util = require('util');

        if(!NodeWebkitUtils.openedStreams[fileName]) {
            var stdoutFile = path.join(NodeWebkitUtils.getApplicationDataPath(), 'log', fileName);

            var stdoutFS = fs.createWriteStream(stdoutFile, {
                encoding : 'utf8',
                flags : 'w'
            });

            // Store reference for future use
            NodeWebkitUtils.openedStreams[fileName] = stdoutFS;
        } else {
            // Use already created stream object
            stdoutFS = NodeWebkitUtils.openedStreams[fileName];
        }

        return function() {
            stdoutFS.write(new Date() + ' ' + util.format.apply(null, arguments) + '\n');
        };
    },

    destroyWriteStream: function() {
        // Close opened file descriptors for logs, if any
        if(Object.keys(NodeWebkitUtils.openedStreams).length > 0) {
            for(var strm in NodeWebkitUtils.openedStreams) {
                // Destroy stream
                NodeWebkitUtils.openedStreams[strm].destroy();
            }
        }
    },

    redirectLogsToFile: function() {
        if (NodeWebkitUtils.isDevMode() || NodeWebkitUtils.getDebugLevelForProd() <= 0) {
            // Dev mode has it's own logging mechanism
            // Only enable when user has enabled it in prod mode
            return;
        }

        console.log = NodeWebkitUtils.createWriteStream('zdesktop-ui-bg.log');
        console.error = console.log;
    },

    clearDownloadHistory: function() {
        chrome.downloads.erase({"limit" : 0}, 
            function(obj) { 
                console.log("Downloaded file history is cleared."); 
            });
    },

    updateSpellCheck: function(localeId) {
        if(NodeWebkitUtils.isWindows()) {
            localeId = localeId.replace('_', '-');
            chrome.settingsPrivate.setPref(
                    'spellcheck.dictionaries', [localeId], "null", function() { console.log("spell checking is updated."); }
                );
        }
    }
};

// start dumping logs to file system, this is only required for background process logs
NodeWebkitUtils.redirectLogsToFile();

module.exports = NodeWebkitUtils;