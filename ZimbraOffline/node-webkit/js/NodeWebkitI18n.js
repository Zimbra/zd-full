/**
 * NodeWebkitI18n.js
 * This file is included from NodeWebkitBackground.js file.
 *
 * This file is used to get translation keys to use in NWJS related code.
 */
NodeWebkitI18n = {
    ZdMsg : null,

    loadZdMessages: function() {
        console.log('NodeWebkitI18n::loadZdMessages: load/reload translation related file');

        var jettyPort = NodeWebkitUtils.getLocalConfigData('zimbra_admin_service_port'),
            localId = NodeWebkitPrefs.getPreference('LOCALE_NAME');
            path = '/res/ZdMsg.js?locid=' + localId,
            options = {
                host: '127.0.0.1',
                port: NodeWebkitUtils.getLocalConfigData('zimbra_admin_service_port'),
                path: '/res/ZdMsg.js?locid=' + localId
            },
            vm = require('vm'),
            http = require('http');

        console.log('Get translation keys from file', options);
        var req = http.get(options, function(res) {
            console.log('Translation file download status code', res.statusCode);

            var response = '';
            res.on('data', function(d) {
                response += d;
            });

            res.on('end', function() {
                var context = {window: {}};
                var script = new vm.Script(response);
                script.runInNewContext(context);
                NodeWebkitI18n.ZdMsg = context.ZdMsg;
            });
        });

        req.on('error', function(err) {
            console.log('Translation file download failed', err);
        }); 
    },

    getMessage: function(key) {
        return this.ZdMsg[key];
    }
};

module.exports = NodeWebkitI18n;