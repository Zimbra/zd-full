/**
 * All functions related to auto-update screen navigation.
 * Here is sample request-responst for auto-update.
 * Auto-update request :
 * https://www.zimbra.com/aus/universal/update.php?chn=release&ver=7.2.5&bid=12038&bos=macos
 * 
 * Auto-update response
 * <updates>
 * <update type="minor" version="7.2.7 build 12059" extensionVersion="2.0.1" detailsURL="http://www.zimbra.com/support/documentation/zd-documentation.html"  licenseURL="http://www.zimbra.com/license/zimbra-public-eula-2-3.html">
 * <patch type="complete" URL="http://files.zimbra.com/downloads/zdesktop/7.2.7/b12059/zdesktop_7_2_7_ga_b12059_20150629062100_macos_intel.dmg" hashFunction="md5" hashValue="380f369eaa984981486bb589b1f25508" size="77805788"/>
 * </update>
 * </updates>
 */

if(typeof global != 'undefined') {
    NodeWebkitUtils = global.NodeWebkitUtils;
    NodeWebkitAutoUpdate = global.NodeWebkitAutoUpdate;
    NodeWebkitI18n = global.NodeWebkitI18n;
}


var autoUpdate = {

        getHTMLElement:function(id) {
            return document.getElementById(id);
        },

        createHTML:function(attributes, innerHTML) {
            var bodyTag = document.getElementsByTagName('body')[0];
            var div = document.createElement("div");
            for(var att in attributes) {
                div.setAttribute(att, attributes[att]);
            }
            div.innerHTML = innerHTML;
            bodyTag.appendChild(div);
        },

        createFirstScreen:function() {
            if (document.getElementById('updateChecker')) { return; }

            var content = 
                "<div class='detailsSectionDiv'>" + 
                    "<p class='screenHeader'> " + NodeWebkitI18n.getMessage('AutoUpdateCheckForUpdate') + "</p>" +
                        "<div class='outerDiv'><div id='updateProgBar' class='progBarDiv'></div></div>" +
                    "</div>" + 
                  "<div class='rightDiv'>" + 
                    "<button id='cancelBtn' class='singleBtnMargin' >" + NodeWebkitI18n.getMessage('AutoUpdateCancel') + "</button>" +
                "</div>";
            var attributes = {'id':'updateChecker'};
            this.createHTML(attributes, content);
        },

        createAlreadyUptoDateScreen:function() {
            if (document.getElementById('alreadyUptoDate')) { return; }
            var content =
                "<div class='detailsSectionDiv'>" +
                    "<p class='screenHeader'>" + NodeWebkitI18n.getMessage('AutoUpdateUptoDate') + "</p>" +
                    "<p id='uptoDateInfo' class='detailsText'>" + NodeWebkitI18n.getMessage('AutoUpdateAlreadyOnLatestVersion') + "</p>" +
                "</div>" +
                "<div class='rightDiv'>" +
                    "<button id='alreadyUptoDateOk' class='highlightBtn singleBtnMargin'>" + NodeWebkitI18n.getMessage('AutoUpdateOk') + "</button>" +
                "</div>";

            var attributes = {'id':'alreadyUptoDate', 'class':'hiddenDiv'};
            this.createHTML(attributes, content);
        },

        createUpdateCheckerScreen:function() {
            if (document.getElementById('updateDetails')) { return; }
            var content = 
                "<div id='updateAvailable' class='detailsSectionDiv'>" + 
                    "<p id='newVersionAvailable' class='screenHeader'>" + NodeWebkitI18n.getMessage('AutoUpdateNewVersionAvailable') + "</p>" +
                    "<p id='updateDetailsText' class='detailsText'>" + NodeWebkitI18n.getMessage('AutoUpdateVersionInfo') + "</p>" +
                "</div>" + 
                "<div class='updateBtnDiv'>" +
                    "<button id='remindLaterBtn'>" + NodeWebkitI18n.getMessage('AutoUpdateRemindMeLaterText')  + "</button>" + 
                    "<button id='downloadBtn' disabled>" + NodeWebkitI18n.getMessage('AutoUpdateDownloadUpdate') +  "</button>" +
                "</div>";

            var attributes = {'id':'updateDetails', 'class':'hiddenDiv'};
            this.createHTML(attributes, content);
        },

        createDownloadScreen:function() {
            if (document.getElementById('downloadInProgress')) { return; }
            var content = 
                "<div class='detailsSectionDiv'>" + 
                    "<p id='downloadHeader' class='screenHeader'>" + NodeWebkitI18n.getMessage('AutoUpdateDownloading') + "</p>" +
                    "<div class='outerDiv'><div id='dowloadProgBar' class='progBarDiv'></div></div>" +
                    "<p id='downloadDetails' class='detailsText'></p> </div>" +
               "<div class='rightDiv'>" + 
                    "<button id='cancelDownloadBtn' style='margin:25px'>" +
                        NodeWebkitI18n.getMessage('AutoUpdateCancel') + 
                    "</button> </div>";

            var attributes = {'id':'downloadInProgress', 'class':'hiidenDiv'};
            this.createHTML(attributes, content);
        },

        createInstallUpdateScreen:function() {
            if (document.getElementById('installUpdate')) { return; }

            var content = 
                "<div class='detailsSectionDiv'>" +
                    "<p id='buildReadyToInstall' class='screenHeader'>" + NodeWebkitI18n.getMessage('AutoUpdateReadyToInstall') + "</p>" +
                    "<p id='restartNowOrRemindLater' class='detailsText'>" + NodeWebkitI18n.getMessage('AutoUpdateInstallOrRemindLater') + "</p>" +
                "</div>" +
                "<div class='updateBtnDiv'>" +
                    "<button id='remindMeLater' class='btn'>" + NodeWebkitI18n.getMessage('AutoUpdateRemindMeLaterText') + "</button>" +
                    "<button id='restartNow' class='highlightBtn rightBtn'>" + NodeWebkitI18n.getMessage('AutoUpdateRestartNow') + "</button>" +
                "</div>";

            var attributes = {'id':'installUpdate', 'class':'hiidenDiv'};
            this.createHTML(attributes, content);
        },

        createUpdateInProgressScreen:function() {
            if (document.getElementById('updateInProgress')) { return; }
            var content = 
                "<div class='detailsSectionDiv'>" +
                    "<p class='screenHeader'>" + NodeWebkitI18n.getMessage('AutoUpdateInstallingMsg') + "</p>" +
                    "<div class='outerDiv'><div id='updateInProgressBar' class='progBarDiv'></div></div>" +
                "</div>";

            var attributes = {'id':'updateInProgress', 'class':'hiidenDiv'};
            this.createHTML(attributes, content);
        },

        createConnectionFailedScreen:function() {
            if (document.getElementById('connectionFailed')) { return; }
            var content = 
                "<div class='detailsSectionDiv'>" +
                    "<p class='screenHeader'>" + NodeWebkitI18n.getMessage('AutoUpdateConnectionFailed') + "</p>" +
                    "<p class='detailsText'>" + NodeWebkitI18n.getMessage('AutoUpdateConnectionFailedMsg') + "</p>" +
                "</div>" + 
                "<div class='rightDiv'>" +
                    "<button id='connectionFailedOk' class='highlightBtn singleBtnMargin' >" + NodeWebkitI18n.getMessage('AutoUpdateOk') + "</button>" +
                "</div>";
            var attributes = {'id':'connectionFailed', 'class':'hiddenDiv'};
            this.createHTML(attributes, content);
        },

        showUpdateCheckerScreen:function() {
            this.getHTMLElement('softwareUpdateTitle').innerHTML = NodeWebkitI18n.getMessage('AutoUpdateSoftwareUpdate');
            this.createFirstScreen();
            var checkUpdateId = this.showProgressBar(this.getHTMLElement("updateProgBar"));
            var self = this;
            this.getHTMLElement('cancelBtn').addEventListener('click', function(event) {
                self.closeWindow();
                return;
            });
            return checkUpdateId;
        },

        showProgressBar:function(pBar) {
            var count = 0;
            var checkUpdateId = setInterval(function() {
                pBar.style.width = count + '%';
                count += 5;
                if(count >= 100) { 
                    count = 0; 
                }
            }, 1000);
            return checkUpdateId;
        },

        showUpdateDetailsScreen:function(updateResp) {
            this.createUpdateCheckerScreen();
            this.getHTMLElement('updateDetails').style.display = "block";

            this.getHTMLElement('softwareUpdateTitle').innerHTML = NodeWebkitI18n.getMessage('AutoUpdateSoftwareUpdate');
            var binaryAlreadyDownloaded = this.isBinaryAlreadyDownloaded(updateResp),
                path = require('path'),
                fileName = path.join(NodeWebkitUtils.getApplicationDataPath(), 'data', 'tmp', path.basename(updateResp['URL'])),
                version = updateResp['version'].split(" build ");
            this.getHTMLElement('updateDetailsText').innerHTML = this.getHTMLElement('updateDetailsText').innerHTML.replace('{0}', version[0]);
            this.getHTMLElement('updateDetailsText').innerHTML = this.getHTMLElement('updateDetailsText').innerHTML.replace('{1}', version[1]);

            var self = this;

            this.getHTMLElement('remindLaterBtn').addEventListener('click', function(event) {
                self.closeWindow();
            });
            console.log('binaryAlreadyDownloaded', binaryAlreadyDownloaded);
            if(binaryAlreadyDownloaded) {
                this.getHash(fileName, function(hash) {
                    self.getHTMLElement('downloadBtn').addEventListener('click', function(event) {
                        self.downloadUpdate(updateResp, hash);
                    });
                    self.enableDownloadButton();
                });
            } else {
                this.getHTMLElement('downloadBtn').addEventListener('click', function(event) {
                    self.downloadUpdate(updateResp, null);
                });
                self.enableDownloadButton();
            }
        },

        enableDownloadButton:function() {
            var downloadBtn = this.getHTMLElement('downloadBtn');
            downloadBtn.disabled = false;
            downloadBtn.className = "highlightBtn rightBtn";
        },

        showAlreadyUptoDateScreen:function() {
            this.createAlreadyUptoDateScreen();
            var self = this;
            this.getHTMLElement('alreadyUptoDateOk').addEventListener('click', function(event) {
                self.closeWindow();
            });

            this.getHTMLElement('updateChecker').style.display = "none";
            this.getHTMLElement('alreadyUptoDate').style.display = "block";
            this.getHTMLElement('uptoDateInfo').innerHTML = this.getHTMLElement('uptoDateInfo').innerHTML.replace('{0}', nw.App.manifest['version']);
            this.getHTMLElement('uptoDateInfo').innerHTML = this.getHTMLElement('uptoDateInfo').innerHTML.replace('{1}', nw.App.manifest['build-id']);
        },

        isBinaryAlreadyDownloaded:function(updateResp) {
            var fs = require('fs'),
                os = require('os'),
                path = require('path'),
                downloadURL = updateResp['URL'],
                fileName;

            fileName = path.join(NodeWebkitUtils.getApplicationDataPath(), 'data', 'tmp', path.basename(downloadURL));

            try {
                var fStats = fs.statSync(fileName);
                return true;
            } catch(e) {
                return false;
            }
        },

        downloadUpdate:function(updateResp, hashOfExistingFile) {
            var updatedVersion = updateResp['version'],
                checksum = updateResp['hashValue'],
                downloadURL = updateResp['URL'];
                path = require('path'),
                fileName = path.join(NodeWebkitUtils.getApplicationDataPath(), 'data', 'tmp', path.basename(downloadURL));

            console.log('Hash of existing package', hashOfExistingFile);
            console.log('Hash of updated package on server', checksum);

            if(hashOfExistingFile && hashOfExistingFile === checksum) {
                console.log('Package is already present at ' + fileName + ', skip downloading again');
                this.showInstallUpdateScreen(updatedVersion, fileName, true);
            } else {
                this.removeUnnecessaryFiles();
                this.startDownload(downloadURL, updatedVersion, fileName, checksum);
            }
        },

        removeUnnecessaryFiles:function() {
            var fs = require('fs'),
                path = require('path'),
                results = [],
                dir = path.join(NodeWebkitUtils.getApplicationDataPath(), 'data', 'tmp'),
                list = fs.readdirSync(dir);

            list.forEach(function(fileName) {
                fileName = dir + '/' + fileName;
                var fileExt = fileName.split('.').pop();

                if(fileExt === "dmg" || fileExt === "msi") {
                    fs.unlink(fileName, function(err) {
                        console.log('Found old package in download location, trying to delete it', fileName);

                        if(err) {
                            console.log('Error deleting old package');
                        } else {
                            console.log('Package deleted successfully');
                        }
                    });
                }
            });
        },

        startDownload:function(downloadURL, updatedVersion, fileName, checksum) {
            this.showDownloadScreen(updatedVersion);

            var fs = require('fs'),
                request = require('request'),
                fileSizeInBytes, downloadStartTime;

            console.log("File will be downloaded at", fileName);

            var downloader = request.get(downloadURL);
            console.log('Package download URL', downloadURL);

            // When user click on cancel, register event to cancel downloading process also
            this.getHTMLElement('cancelDownloadBtn').addEventListener('click', function(event) {
                downloader.abort();
            });

            downloader.on('response', function(response){
                console.log('Package download status code', response.statusCode);

                if(response && response.headers && response.headers['content-length']) {
                    fileSizeInBytes = response.headers['content-length'];
                    console.log('Package size in bytes', fileSizeInBytes);

                    downloadStartTime = (new Date).getTime();
                    console.log('Package download start time', downloadStartTime);
                }
            });

            fs.unlink(fileName, function() {
                downloader.pipe(fs.createWriteStream(fileName));
                downloader.resume();
            });

            downloader.on('error', function(error) {
                console.log('Package download failed', error);
            });

            var totalBytesReceived = 0;
            var self = this;
            downloader.on('data', function(data) {
                // start time will be present only when we have received proper response
                if(downloadStartTime) {
                    totalBytesReceived += parseInt(data.length, 10);
                    self.updateDownloadProgress(data, fileSizeInBytes, totalBytesReceived, downloadStartTime);
                } else {
                    console.log('Error: invalid response received from server');
                }
            });

            downloader.on('end', function() { 
                console.log('Package download completed');

                var checksumOfDownloaded = self.getHash(fileName, function(checksumOfDownloaded) {
                    console.log('Checksum of downloaded file', checksumOfDownloaded);
                    if(checksumOfDownloaded === checksum) {
                        self.showInstallUpdateScreen(updatedVersion, fileName, false);
                    } else {
                        console.log('Downloaded pacakge is corrupted');
                    }
                });
            });
        },

        updateDownloadProgress : function(data, fileSizeInBytes, totalBytesReceived, downloadStartTime) {
            var fileSizeInMb = parseFloat(fileSizeInBytes / 1048576).toFixed(1),
                currentTime = (new Date).getTime(),
                percentage = totalBytesReceived * 100 / fileSizeInBytes,
                elapsedTime = (currentTime - downloadStartTime) / 1000,
                remainingBytes = fileSizeInBytes - totalBytesReceived,
                remainingTime = (elapsedTime * remainingBytes / totalBytesReceived),
                downloadedSize = parseFloat(totalBytesReceived / 1048576).toFixed(1);

            this.getHTMLElement('dowloadProgBar').style.width = parseInt(percentage | 0, 10) + '%';
            this.getHTMLElement('downloadDetails').innerHTML = this.getDownloadStatusText(downloadedSize, fileSizeInMb, remainingTime);
        },

        getDownloadStatusText:function(downloadedSize, fileSizeInMb, remainingTime) {
            var info,
                index = 0,
                statusText,
                min = parseInt((remainingTime / 60) | 0, 10),
                sec = parseInt((remainingTime - (min * 60)) | 0, 10);

            if(min == 0) {
                statusText = NodeWebkitI18n.getMessage('AutoUpdateDownloadStatusInSeconds');
                info = [downloadedSize, fileSizeInMb, sec];
            } else {
                statusText = NodeWebkitI18n.getMessage('AutoUpdateDownloadStatus');
                info = [downloadedSize, fileSizeInMb, min, sec];
            }

            statusText = statusText.replace(/{\d}/g, function () { return info[index++];});
            return statusText;
        },

        showDownloadScreen:function(updatedVersion) {
            updatedVersion = updatedVersion.split(" build ");

            this.createDownloadScreen();
            this.getHTMLElement('updateDetails').style.display = "none";
            this.getHTMLElement('downloadInProgress').style.display = "block";
            this.getHTMLElement('downloadHeader').innerHTML = this.getHTMLElement('downloadHeader').innerHTML.replace('{0}', updatedVersion[0]);
            this.getHTMLElement('downloadHeader').innerHTML = this.getHTMLElement('downloadHeader').innerHTML.replace('{1}', updatedVersion[1]);

            var self = this;
            this.getHTMLElement('cancelDownloadBtn').addEventListener('click', function(event) {
                self.closeWindow();
            });
        },

        showInstallUpdateScreen:function(updatedVersion, fileName, isDownloaded) {
            updatedVersion = updatedVersion.split(" build ");

            this.createInstallUpdateScreen();
            if (isDownloaded) {
                this.getHTMLElement('updateDetails').style.display = "none";
            } else {
                this.getHTMLElement('downloadInProgress').style.display = "none";
            }
            this.getHTMLElement('installUpdate').style.display = "block";
            this.getHTMLElement('buildReadyToInstall').innerHTML = this.getHTMLElement('buildReadyToInstall').
                                                       innerHTML.replace('{0}', updatedVersion[0]);

            this.getHTMLElement('buildReadyToInstall').innerHTML = this.getHTMLElement('buildReadyToInstall').
                                                       innerHTML.replace('{1}', updatedVersion[1]);
            

            var self = this;
            this.getHTMLElement('remindMeLater').addEventListener('click', function(event) {
                self.closeWindow();
            });

            this.getHTMLElement('restartNow').addEventListener('click', function(event) {
                self.createUpdateInProgressScreen();
                self.getHTMLElement('installUpdate').style.display = "none";
                self.getHTMLElement('updateInProgress').style.display = "block";

                var timer = self.showProgressBar(self.getHTMLElement('updateInProgressBar')),
                    spawn = require('child_process').spawn,
                    childProcess;

                NodeWebkitUtils.stopJettyServer(function() {
                    clearInterval(timer);
                    self.closeWindow();

                    console.log('Start installing newly downloaded package', fileName);
                    if (NodeWebkitUtils.isMac()) {
                        childProcess = spawn('sh', ['auto-update/zd-mac-update.sh', fileName], {detached : true}).unref();
                    } else if(NodeWebkitUtils.isWindows()) {
                        childProcess = spawn('msiexec', ['/i', fileName], {detached : true}).unref();
                    }
                    //FIXME: add error handling if spawn fails
                    //FIXME: fix delay
                    nw.App.quit();
                });
            });
        },

        showConnectionFailed:function() {
            var self = this;
            this.createConnectionFailedScreen();
            this.getHTMLElement('updateChecker').style.display = "none";
            this.getHTMLElement('connectionFailed').style.display = "block";
            this.getHTMLElement('connectionFailedOk').addEventListener('click', function(event) {
                self.closeWindow();
            });
        },

        closeWindow:function() {
            NodeWebkitAutoUpdate.autoUpdateWindow = null;
            window.close();
        },

        //Calculate digest of file using MD5
        getHash:function(fileName, cb) {
            var res ;
            var crypto = require('crypto');
            var fs = require('fs');

            var fd = fs.createReadStream(fileName);
            var hash = crypto.createHash('md5');
            hash.setEncoding('hex');

            fd.on('end', function() {
                hash.end();
                res = hash.read();
                console.log(hash.read()); // the desired sha1sum
                cb.call(this, res);
            });

            // read all file and pipe it (write it) to the hash object
            fd.pipe(hash);
        }
};

