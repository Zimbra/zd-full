/*
    Background script. The script is executed in the background page at the start of application.
*/
if(typeof nw !== 'undefined') {
    console.log('NodeWebkitBackground.js is loaded');

    // include node utils file
    NodeWebkitUtils = require('./js/NodeWebkitUtils.js');

    // include node preference file, so we can use it to get local preference in ZD UI
    NodeWebkitPrefs = require('./js/NodeWebkitPrefs.js');

    // include locale handler file
    NodeWebkitI18n = require('./js/NodeWebkitI18n.js');

    NodeWebkitAutoUpdate = require('./js/NodeWebkitAutoUpdate.js');

    // include mailto handling file
    NodeWebkitMailto = require('./js/NodeWebkitMailto.js');
} else {
    //console.log('NodeWebkitBackground.js nw not defined');
}