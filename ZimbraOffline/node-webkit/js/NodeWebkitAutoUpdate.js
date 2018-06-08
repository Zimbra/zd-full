/**
 * NodeWebkitAutoUpdate.js 
 * All functionality related to auto-update process.
 */

NodeWebkitAutoUpdate = {
        autoUpdate:null,
        schedulerJob:null,
        autoUpdateWindow:null,

        checkForUpdate:function(isManual) {
            if(isManual) {
                NodeWebkitAutoUpdate.handleManualUpdate();
            } else {
                NodeWebkitAutoUpdate.handleAutoUpdate();
            }
        },

        handleManualUpdate:function() {
            //open window
            nw.Window.open('auto-update/autoUpdate.html', { frame:false, height:210, width:630, show:true }, function(pWin) {
                NodeWebkitAutoUpdate.autoUpdateWindow = pWin;
                pWin.window.isChildWindow = true;

                pWin.on('loaded', function() {
                    NodeWebkitAutoUpdate.autoUpdate = pWin.window.autoUpdate;
                    var pBarTimer = NodeWebkitAutoUpdate.autoUpdate.showUpdateCheckerScreen();

                    NodeWebkitAutoUpdate.isUpdateAvailable(true, function(response) {
                        clearInterval(pBarTimer);

                        if(response.isUpdateAvailable) {
                            NodeWebkitAutoUpdate.autoUpdate.getHTMLElement('updateChecker').style.display = "none";
                            NodeWebkitAutoUpdate.autoUpdate.showUpdateDetailsScreen(response.updateResponse);
                        } else {
                            NodeWebkitAutoUpdate.autoUpdate.showAlreadyUptoDateScreen();
                        }
                    });
                });
            });
        },

        handleAutoUpdate:function() {
            NodeWebkitAutoUpdate.isUpdateAvailable(false, function(response){
                if(response.isUpdateAvailable) {
                    nw.Window.open('auto-update/autoUpdate.html', { frame:false, height:210, width:630, show:false }, function(pWin) {
                        NodeWebkitAutoUpdate.autoUpdateWindow = pWin;
                        pWin.window.isChildWindow = true;

                        pWin.on('loaded', function() {
                            NodeWebkitAutoUpdate.autoUpdate = pWin.window.autoUpdate;
                            NodeWebkitAutoUpdate.autoUpdate.showUpdateDetailsScreen(response.updateResponse);
                        });
                    });
                }
            });
        },

        isAlreadyUptoDate:function(response) {
            var parser = new DOMParser();
            var xml = parser.parseFromString(response, "text/xml");
            return xml.getElementsByTagName("update").length == 0;
        },

        isUpdateAvailable:function(isManual, cb) {
            NodeWebkitAutoUpdate.sendAutoUpdateCheckRequest(isManual, function(response) {
                var isUpdateAvailable = !NodeWebkitAutoUpdate.isAlreadyUptoDate(response);
                var updateRes = NodeWebkitAutoUpdate.parseAutoUpdateRespones(response);
                var res = {'isUpdateAvailable': isUpdateAvailable, 'updateResponse': updateRes};
                cb.call(this, res);
            });
        },

        parseAutoUpdateRespones:function(response) {
            var parser = new DOMParser();
            var xml = parser.parseFromString(response, "text/xml");
            var attributes = {};

            var updateTag = xml.getElementsByTagName('update')[0];
            if (updateTag === undefined) {
                return;
            }

            for (var i = 0; i < updateTag.attributes.length; i++) {
                var attr = updateTag.attributes[i];
                attributes[attr.name] = attr.value;
            }

            var patchTag = xml.getElementsByTagName('patch')[0];

            for (var i = 0; i < patchTag.attributes.length; i++) {
                var attr = patchTag.attributes[i];
                attributes[attr.name] = attr.value;
            }
            return attributes;
        },

        sendAutoUpdateCheckRequest:function(isManual,cb) {
            var channel = NodeWebkitPrefs.getPreference('AUTO_UPDATE_NOTIFICATION', 'release');//release or beta or doNotNotify
            if (channel === 'doNotNotify') {
                if (!isManual) {
                    return;
                }
            }

            var url = nw.App.manifest['autoUpdatePath'] + '?chn=' + channel + '&ver=' + nw.App.manifest['version'] + 
                '&bid=' + nw.App.manifest['build-id'] + '&bos=' + nw.App.manifest['platform'];

            //          updatePath =  updatePath + '?chn=release&ver=7.2.5&bid=12038&bos=macos';
            //          Zimbra website - www.zimbra.com
            //          Local site :   (no www)
            console.log("Auto Update URL : ", nw.App.manifest['autoUpdateHost'] + url);

            var options = {
                host: nw.App.manifest['autoUpdateHost'],
                path: url
            };

            var http = require('http');
            var req = http.get(options, function(res) {
                console.log("Auto update response status code" , res.statusCode);

                var updateResponse = '';
                res.on('data', function(d) {
                    updateResponse += d;
                });

                res.on('end', function() {
                    console.log("Auto update response data" , updateResponse);

                    cb.call(this, updateResponse);
                });
            });

            req.on('error', function(err) {
                console.log('Auto update request failed', err);

                if(isManual) {
                    NodeWebkitAutoUpdate.autoUpdate.showConnectionFailed();
                }

                return;
            }); 
        },

        scheduleAutoUpdate:function() {
            this.cancelAutoUpdateTimer();

            //First auto update check on startup (after 5 minutes)
            setTimeout(function(){
                NodeWebkitAutoUpdate.checkForUpdate(false);

                console.log('Auto update check on startup is triggered.');
            }, 5 * 60 * 1000);

            //schedule auto-update check after every 4 hours
            this.schedulerJob = setInterval(function() {
                NodeWebkitAutoUpdate.checkForUpdate(false);
            }, 240 * 60 * 1000);
        },

        cancelAutoUpdateTimer:function() {
            if(this.schedulerJob) {
                console.log('Auto-update timer is cleared.');

                clearInterval(this.schedulerJob);
            }
        }
};

module.exports = NodeWebkitAutoUpdate;
