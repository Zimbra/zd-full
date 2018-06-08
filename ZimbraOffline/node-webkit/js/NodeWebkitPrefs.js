/**
 * This file is included from NodeWebkitBackground.js file.
 * This file is run in node context so don't try to access browser context objects in this file (like nw.Window)
 * This file will read and write local preferences to file system.
 */
NodeWebkitPrefs = (function() {
    var fs = require('fs'),
        path = require('path'),
        localeId, data, stat,
        prefsFilePath = path.join(NodeWebkitUtils.getApplicationDataPath(), 'conf', 'local_prefs.json');

    try {
        // if file is not present then it will throw error,
        // so to handle that we need to wrap this code in try catch block
        stat = fs.statSync(prefsFilePath);
        if (stat.isFile()) {
            data = fs.readFileSync(prefsFilePath, 'utf8');
            if (data) {
                data = JSON.parse(data);
            }
        }
    } catch (e) {
        console.log('NodeWebkitPrefs Error reading local preferences', e);
    }

    return {
        getPreference: function(prefName, defaultValue) {
            if(data && data[prefName]) {
                console.log('NodeWebkitPrefs::getPreference', prefName, data[prefName]);

                return data[prefName];
            }

            console.log('NodeWebkitPrefs::getPreference use default value', prefName, defaultValue);

            return defaultValue;
        },

        addPreference: function(prefName, value) {
            data = data || {};

            console.log('NodeWebkitPrefs::addPreference', prefName, value);

            data[prefName] = value;
            fs.writeFileSync(prefsFilePath, JSON.stringify(data));
        }
    };
})();

module.exports = NodeWebkitPrefs;