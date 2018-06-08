/*
 * 
 */
package com.zimbra.cs.offline.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.DocumentException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.common.httpclient.HttpClientUtil;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraHttpConnectionManager;
import com.zimbra.cs.offline.OfflineLog;

public class AutoUpdateTest {

    static final String BASE_UPDATE_URL = "http://localhost/update.php"; //for local testing
//    static final String BASE_UPDATE_URL = "https://www.zimbra.com/aus/zdesktop2/update.php"; //real update site; only updated once build is RTM

    static HttpClient httpClient = ZimbraHttpConnectionManager.getExternalHttpConnMgr().newHttpClient();

    static final String MEDIA_MAC = "_macos_intel.dmg";
    static final String MEDIA_WIN = "_win32.msi";
    static final String MEDIA_WIN_64 = "_win64.msi";
    static final String MEDIA_LINUX = "_linux_i686.tgz";


    //these values change with every build

    static class UpdateInfo {
        int expectedBuild;
        String expectedFileVersion;
        String expectedAttrVersion;
        String expectedType;

        public int getExpectedBuild() {
            return expectedBuild;
        }

        public String getExpectedFileVersion() {
            return expectedFileVersion;
        }

        public String getExpectedAttrVersion() {
            return expectedAttrVersion;
        }

        public String getExpectedType() {
            return expectedType;
        }

        public String getExpectedFilePrefix(String platform) {
            return "zdesktop_" + expectedFileVersion+"_b" + expectedBuild + "_" + platforms.get(platform).getTimestamp();
        }

        Map<String, PlatformInfo> platforms = new HashMap<String, PlatformInfo>();

        UpdateInfo(int expectedBuild, String expectedFileVersion, String expectedAttrVersion, String expectedType) {
            this.expectedBuild = expectedBuild;
            this.expectedFileVersion = expectedFileVersion;
            this.expectedAttrVersion = expectedAttrVersion + " build " + expectedBuild;
            this.expectedType = expectedType;
        }

        void addPlatform(PlatformInfo info) {
            platforms.put(info.getPlatform(), info);
        }

        PlatformInfo getPlatform(String platform) {
            return platforms.get(platform);
        }
    }

    static class PlatformInfo {
        String platform;
        String hash;
        int size;
        long timestamp;

        public String getPlatform() {
            return platform;
        }

        public String getHash() {
            return hash;
        }

        public int getSize() {
            return size;
        }

        public long getTimestamp() {
            return timestamp;
        }

        PlatformInfo(String platform, String hash, int size, long timestamp) {
            this.platform = platform;
            this.hash = hash;
            this.size = size;
            this.timestamp = timestamp;
        }

        public String getFileSuffix() {
            if (platform.equals("macos")) {
                return MEDIA_MAC;
            } else if (platform.equals("win32")) {
                return MEDIA_WIN;
            } else if (platform.equals("win64")) {
                return MEDIA_WIN_64;
            }
            else if (platform.equals("linux")) {
                return MEDIA_LINUX;
            }
            Assert.fail("Unexpected platform type");
            return null;
        }
    }

    private static Map<String, UpdateInfo> updateInfo = new HashMap<String, UpdateInfo>();
    private static String CHN_RELEASE = "release";
    private static String CHN_BETA = "beta";

    @BeforeClass
    public static void setUp()
    {
        UpdateInfo gaUpdateInfo = new UpdateInfo(12059, "7_2_7_ga", "7.2.7", "minor");
        gaUpdateInfo.addPlatform(new PlatformInfo("macos", "244065b4ed53dd52ba1871f6d4907287", 77805798, 20150629062100L));
        gaUpdateInfo.addPlatform(new PlatformInfo("win32", "0c8f0eea247365cd7404e416a91aea9e", 71799296, 20150629060421L));
        gaUpdateInfo.addPlatform(new PlatformInfo("win64", "af77874dcf01f1102fb1b70974c66cd4", 71732224, 20150629060421L));
        gaUpdateInfo.addPlatform(new PlatformInfo("linux", "b940cbcf66b908de7a383e4f0004d30b", 76407205, 20150629062326L));

        updateInfo.put(CHN_RELEASE, gaUpdateInfo);
    }

    static final String PARAM_CHN = "chn";
    static final String PARAM_VER = "ver";
    static final String PARAM_BID = "bid";
    static final String PARAM_BOS = "bos";

    static final String E_UPDATES = "updates";
    static final String E_UPDATE  = "update";
    static final String E_PATCH   = "patch";
    static final String A_URL     = "URL";
    static final String A_TYPE    = "type";
    static final String A_VERSION = "version";
    static final String A_HASH    = "hashValue";
    static final String A_SIZE    = "size";

    String send(NameValuePair[] params) throws HttpException, IOException {
        GetMethod httpMethod = new GetMethod(BASE_UPDATE_URL);
        httpMethod.setQueryString(params);
        int code = HttpClientUtil.executeMethod(httpClient, httpMethod);
        Assert.assertEquals(200, code);
        return httpMethod.getResponseBodyAsString();
    }

    void verifyExpectedUpdate(String response, String os, UpdateInfo updateInfo) throws DocumentException, ServiceException {
        String fileSuffix = updateInfo.getPlatform(os).getFileSuffix();
        String hash = updateInfo.getPlatform(os).getHash();
        int size = updateInfo.getPlatform(os).getSize();

        Element xml = Element.parseXML(response);
        Assert.assertEquals(E_UPDATES,xml.getName());
        Element update = xml.getElement(E_UPDATE);
        Assert.assertNotNull(update);
        Assert.assertEquals(updateInfo.getExpectedType(),update.getAttribute(A_TYPE));
        Assert.assertEquals(updateInfo.getExpectedAttrVersion(),update.getAttribute(A_VERSION));
        Element patch = update.getElement(E_PATCH);
        String url = patch.getAttribute(A_URL);
        String filename = url.substring(url.lastIndexOf("/")+1);
        Assert.assertEquals(updateInfo.getExpectedFilePrefix(os)+fileSuffix, filename);
        Assert.assertEquals(hash, patch.getAttribute(A_HASH));
        Assert.assertEquals(size, Integer.parseInt(patch.getAttribute(A_SIZE)));
    }

    void verifyNoUpdate(String response) throws DocumentException, ServiceException {
        Element xml = Element.parseXML(response);
        Assert.assertEquals(E_UPDATES,xml.getName());
        Assert.assertEquals(0, xml.listElements(E_UPDATE).size());
    }

    void sendAndVerify(String chn, String ver, int bid, String os) throws HttpException, IOException, DocumentException, ServiceException {
        NameValuePair[] nvp = new NameValuePair[4];
        nvp[0] = new NameValuePair(PARAM_CHN, chn);
        nvp[1] = new NameValuePair(PARAM_VER, ver);
        nvp[2] = new NameValuePair(PARAM_BID, bid+"");
        nvp[3] = new NameValuePair(PARAM_BOS, os);
        String response = send(nvp);
        OfflineLog.offline.info("\r\n"+response);
        UpdateInfo update = updateInfo.get(chn);
        if (chn.equalsIgnoreCase(CHN_BETA)) {
            //if GA is newer it should be published
            if (update == null || updateInfo.get(CHN_RELEASE).getExpectedBuild() > update.getExpectedBuild()) {
                update = updateInfo.get(CHN_RELEASE);
            }
        }


        if (bid < update.getExpectedBuild()) {
            verifyExpectedUpdate(response, os, update);
            OfflineLog.offline.info("Expected update received for %s %s %s build %d", os, ver, chn, bid);
        } else {
            verifyNoUpdate(response);
            OfflineLog.offline.info("No update expected for %s %s %s build %d", os, ver, chn, bid);
        }
    }

    String[] platforms = {"macos", "linux", "win32", "win64"};

    @Test
    public void ga201() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify(CHN_RELEASE, "2.0.1", 10659, platform);
        }
    }

    @Test
    public void ga701() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify(CHN_RELEASE, "7.0.1", 10791, platform);
        }
    }

    @Test
    public void beta711() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify(CHN_BETA, "7.1.1", 10867, platform);
        }
    }

    @Test
    public void ga711() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify(CHN_RELEASE, "7.1.1", 10917, platform);
        }
    }

    @Test
    public void ga712() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify(CHN_RELEASE, "7.1.2", 10978, platform);
        }
    }

    @Test
    public void beta713() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify(CHN_BETA, "7.1.3", 11139, platform);
        }
    }

    @Test
    public void beta714() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify(CHN_BETA, "7.1.4", 11234, platform);
        }
    }

    @Test
    public void ga725() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify(CHN_RELEASE, "7.2.5", 12038, platform);
        }
    }

    @Test
    public void alreadyUpdated() throws HttpException, IOException, DocumentException, ServiceException {
        for (String platform: platforms) {
            sendAndVerify(CHN_RELEASE, updateInfo.get(CHN_RELEASE).getExpectedAttrVersion(), updateInfo.get(CHN_RELEASE).getExpectedBuild(), platform);
        }
    }

}
