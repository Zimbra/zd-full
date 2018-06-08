/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.zimbra.common.httpclient.HttpClientUtil;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.BigByteBuffer;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.ZimbraHttpConnectionManager;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;

/**
 * @author JJ Zhuang
 */
public class Request {
	private static final String URL_PATH = "/Microsoft-Server-ActiveSync";
	private static final String DEVICE_TYPE = "Zimbra";
	
	private static final String BASIC_AUTH_HEADER = "Authorization";
	private static final String USER_AGENT_HEADER = "User-Agent";
	private static final String ASPROTOCOL_HEADER = "MS-ASProtocolVersion";
	private static final String POLICY_KEY_HEADER = "X-MS-PolicyKey";
	
	private static final String CONTENT_TYPE = "application/vnd.ms-sync.wbxml";
	
	private SyncSettings syncSettings;
	private String policyKey;
	
	public Request(SyncSettings syncSettings, String policyKey) {
		this.syncSettings = syncSettings;
		this.policyKey = policyKey;
	}
	
	public void doRequest(Command cmd, boolean isDebugTraceOn) throws ServiceException, CommandCallbackException, HttpStatusException, ResponseStatusException, BinaryCodecException, IOException {
		BigByteBuffer bbb = new BigByteBuffer();
		try {
			cmd.encodeRequest(bbb, isDebugTraceOn);
			bbb.doneWriting();
			InputStream reqInput = bbb.getInputStream();
			InputStream respInput = null;
			try {
			    Pair<InputStream, Long> resp = doRequest(getURI() + "&Cmd=" + cmd.getName(), reqInput, bbb.length(), CONTENT_TYPE);
			    if (resp != null) {
    			    respInput = resp.getFirst();
        			cmd.processResponse(respInput, resp.getSecond().intValue(), isDebugTraceOn);
			    }
			} finally {
				reqInput.close();
				if (respInput != null)
				    respInput.close();
			}
		} finally {
			bbb.destroy();
		}
	}
	
	public void doSendMail(InputStream mimeInput, long size, boolean saveInSent) throws HttpStatusException, ServiceException, IOException {
	    doRequest(getURI() + "&Cmd=SendMail" + (saveInSent ? "&SaveInSent=T" : ""), mimeInput, size, "message/rfc822");
	}
	
	private Pair<InputStream, Long> doRequest(String uri, InputStream in, long contentLength, String contentType) throws HttpStatusException, ServiceException, IOException {
        HttpClient client = ZimbraHttpConnectionManager.getInternalHttpConnMgr().getDefaultHttpClient();
        PostMethod method = new PostMethod(uri);
        setRequestHeaders(method, true);
        try {
            ZimbraLog.sync.debug("POST %s", uri);
            method.setRequestEntity(new InputStreamRequestEntity(in, contentLength, contentType));
            HttpClientUtil.executeMethod(client, method);
            ZimbraLog.sync.debug(method.getStatusLine());
            if (method.getStatusCode() != 200)
                throw HttpStatusException.newException(method.getStatusCode());
            long respSize = method.getResponseContentLength();
            return (respSize > 0) ? new Pair<InputStream, Long>(method.getResponseBodyAsStream(), respSize) : null;
        } catch (HttpException x) {
            if (method.getStatusCode() != 200)
                throw HttpStatusException.newException(method.getStatusCode());
            else
                throw ServiceException.FAILURE("Unexpected HttpException", x);
        }
	}
	
	private String getURI() throws UnsupportedEncodingException {
	    return syncSettings.getHostUri() + URL_PATH + "?User=" + URLEncoder.encode(syncSettings.username, "utf-8") +
	           "&DeviceId=" + syncSettings.deviceId + "&DeviceType=" + DEVICE_TYPE;
	}
	
	public void doOptions() throws HttpStatusException, ServiceException, IOException {
        HttpClient client = ZimbraHttpConnectionManager.getInternalHttpConnMgr().getDefaultHttpClient();
        String uri = getURI();
        OptionsMethod method = new OptionsMethod(uri);
        setRequestHeaders(method, false);
        try {
            ZimbraLog.sync.debug("OPTIONS %s", uri);
            HttpClientUtil.executeMethod(client, method);
            ZimbraLog.sync.debug(method.getStatusLine());
            if (method.getStatusCode() != 200)
                throw HttpStatusException.newException(method.getStatusCode());
            StringBuilder sb = new StringBuilder();
            sb.append("OPTIONS response headers:\n");
            for (Header header : method.getResponseHeaders())
                sb.append(header.getName()).append(":").append(header.getValue()).append("\n");
            ZimbraLog.sync.debug(sb.toString());
        } catch (HttpException x) {
            if (method.getStatusCode() != 200)
                throw HttpStatusException.newException(method.getStatusCode());
            else
                throw ServiceException.FAILURE("Unexpected HttpException", x);
        }
	}

	//	POST /Microsoft-Server-ActiveSync?User=jj&DeviceId=BAD73E6E02156460E800185977C03182&DeviceType=PocketPC&Cmd=GetItemEstimate HTTP/1.1
	//	Accept-Language: en-us
	//	MS-ASProtocolVersion: 2.5
	//	Content-Type: application/vnd.ms-sync.wbxml
	//	X-MS-PolicyKey: 666416194
	//	User-Agent: MSFT-PPC/5.2.200
	//	Host: 192.168.0.203
	//	Content-Length: 39
	//	Connection: Keep-Alive
	//	Cache-Control: no-cache
	//	Authorization: Basic ampob21lLmxvY2FsXGpqOndhcDEh
	private void setRequestHeaders(HttpMethod method, boolean includePolicyKey) {
	    //method.addRequestHeader("Accept", "*/*");
	    //method.addRequestHeader("Accept-Language", "en-us");
	    //method.addRequestHeader("Accept-Encoding", "gzip, deflate");
		method.addRequestHeader(ASPROTOCOL_HEADER, "2.5");
		if (includePolicyKey)
		    method.addRequestHeader(POLICY_KEY_HEADER, policyKey);
		method.addRequestHeader(USER_AGENT_HEADER, syncSettings.userAgent);
		method.addRequestHeader(BASIC_AUTH_HEADER, syncSettings.getBasicAuthString());
	}
}
