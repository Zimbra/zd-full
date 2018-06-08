/**
 * This file is included from NodeWebkitBackground.js file.
 * This file is run in node context so don't try to access browser context objects in this file (like nw.Window)
 * for accessing window and other browser context objects you need to put methods in NodeWebkitInitStart or NodeWebkitInitEnd
 *
 * This file is used to manage mailto registrations for ZD, as there are lots of variations how to register mailto in windows and mac,
 * we have created common wrapper functions so we can encapsulate native logic
 */
NodeWebkitMailto = {
    // Update registry value for mailto
    updateRegistry: function(remove) {
        console.log('NodeWebkitMailto::updateRegistry', 'Update mailto preference');

        if (NodeWebkitUtils.isWindows()) {
            NodeWebkitMailto.updateMailtoSettingWin(remove);
        } else if(NodeWebkitUtils.isMac()) {
            NodeWebkitMailto.updateMailtoSettingMac(remove);
        }
    },

    // Check if mailto is already registered for ZD
    isRegistered: function() {
        var regValue = '',
            isRegistered = false;

        console.log('NodeWebkitMailto::isRegistered');

        if (NodeWebkitUtils.isWindows()) {
            isRegistered = NodeWebkitMailto.readMailtoSettingWin();
        } else if(NodeWebkitUtils.isMac()) {
            regValue = NodeWebkitMailto.readMailtoSettingMac();

            isRegistered = regValue && regValue.indexOf('Zimbra Desktop') !== -1;
        }

        console.log('is Zimbra Desktop registered as default mail client?', isRegistered);
        return isRegistered;
    },

    // Application which is used to register for mailto protocol
    getRegistrationApp: function() {
        var path = require('path');

        if (NodeWebkitUtils.isWindows()) {
            return path.join(NodeWebkitUtils.getApplicationPath(), 'extensions', 'zimbramapi', 'ZimbraDesktopHelper.exe');
        } else if(NodeWebkitUtils.isMac()) {
            return path.join(NodeWebkitUtils.getApplicationDataPath(), 'bin', 'lstool');
        }
    },

    /************ Windows ************/

    updateMailtoSettingWin: function(remove) {
        if(!remove) {
            // Windows doesn't have functionality to unset any application as default mail client, you can only change it
            // so we are ignoring parameter passed by calling function
            NodeWebkitMailto.executeZimbraHelper('/SetAsDefault');
        }
    },

    readMailtoSettingWin: function() {
        return NodeWebkitMailto.executeZimbraHelper('/IsDefault');
    },

    executeZimbraHelper: function(param) {
        var child_process = require('child_process'),
            path = require('path'),
            helperExe = NodeWebkitMailto.getRegistrationApp();

        console.log('Execute', ['cmd.exe', '/c', helperExe, param].join(' '));
        var retValue = child_process.execFileSync('cmd.exe', ['/c', helperExe, param], {
            encoding: 'utf8'
        });

        return (retValue === 'true');
    },

    /************ Mac ************/

    updateMailtoSettingMac: function (isRemove) {
        var path = require('path'),
            child_process = require('child_process'),
            mailToApp,
            appPath = NodeWebkitMailto.getRegistrationApp();

        mailToApp = isRemove ? path.join('/Applications', 'Mail.app') : path.join(NodeWebkitUtils.getApplicationPath(), 'Zimbra Desktop.app');

        console.log('Execute ', [appPath, 'write', 'url', 'mailto', mailToApp].join(' '));
        child_process.execFile(appPath, ['write', 'url', 'mailto', mailToApp], function(error, stdout, stderr) {
            if (error) {
                console.error('mailto registration failed with error code: ', error, query);
                return;
            }
        });
    },

    readMailtoSettingMac: function () {
        var child_process = require('child_process'),
            appPath = NodeWebkitMailto.getRegistrationApp();

        try {
            console.log('Execute', [appPath, 'read', 'url', 'mailto'].join(' '));
            return child_process.execFileSync(appPath, ['read', 'url', 'mailto'], {
                encoding: 'utf8'
            });
        } catch (e) {
            console.log('error getting current mailto handling application', e);
        }
    }
};

module.exports = NodeWebkitMailto;