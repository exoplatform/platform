/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.services.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

/**
 * @by thuannd (nhudinhthuan@yahoo.com) Jun 13, 2005
 */
public class HttpClientImpl {

	private HttpClient http;

	private DataBuffer buffer;

	public static int HTTP_TIMEOUT = 25000;

	public static int METHOD_TIMEOUT = 25000;

	public HttpClientImpl() throws Exception {
	}

	public HttpClientImpl(URL url) throws Exception {
		setURL(url);
	}

	public void setURL(URL url) throws Exception {
		System.setProperty("HTTPClient.cookies.save", "true");
		System.getProperties().put("HTTPClient.dontChunkRequests", "true");
		setHost(url.getProtocol(), url.getHost(), url.getPort());
	}

	private void setHost(String protocol, String host, int port) throws Exception {
		MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams para = new HttpConnectionManagerParams();
		para.setConnectionTimeout(HTTP_TIMEOUT);
		para.setDefaultMaxConnectionsPerHost(10);
		para.setMaxTotalConnections(20);
		para.setStaleCheckingEnabled(true);
		manager.setParams(para);
		http = new HttpClient(manager);
		http.getParams()
				.setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
		http.getParams().setParameter("http.socket.timeout",
				new Integer(HTTP_TIMEOUT));
		http.getParams().setParameter("http.protocol.content-charset", "UTF-8");
		http.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		http.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		if (port < 0)
			port = 80;
		HostConfiguration hostConfig = http.getHostConfiguration();
		hostConfig.setHost(host, port, protocol);

		String proxyHost = System.getProperty("httpclient.proxy.host");
		if (proxyHost == null || proxyHost.trim().length() < 1)
			return;
		String proxyPort = System.getProperty("httpclient.proxy.port");
		hostConfig.setProxy(proxyHost, Integer.parseInt(proxyPort));

		String username = System.getProperty("httpclient.proxy.username");
		String password = System.getProperty("httpclient.proxy.password");
		String ntlmHost = System.getProperty("httpclient.proxy.ntlm.host");
		String ntlmDomain = System.getProperty("httpclient.proxy.ntlm.domain");

		Credentials ntCredentials;
		if (ntlmHost == null || ntlmDomain == null) {
			ntCredentials = new UsernamePasswordCredentials(username, password);
		} else {
			ntCredentials = new NTCredentials(username, password, ntlmHost,
					ntlmDomain);
		}
		http.getState().setProxyCredentials(AuthScope.ANY, ntCredentials);
	}

	public HttpClient getHttpClient() {
		return this.http;
	}

	public byte[] loadContent(String link) throws Exception {
		GetMethod get = getMethod(link);
		if (get == null)
			return new byte[0];
		get.setFollowRedirects(true);
		get.getParams().setParameter("http.socket.timeout",
				new Integer(METHOD_TIMEOUT));
		int status = http.executeMethod(get);
		if (status != HttpStatus.SC_OK)
			return new byte[0];
		InputStream in = get.getResponseBodyAsStream();
		BufferedInputStream input = new BufferedInputStream(in);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte b[] = new byte[1024];
		int read = 0;
		while ((read = input.read(b)) > -1) {
			output.write(b, 0, read);
		}
		in.close();
		input.close();
		get.releaseConnection();
		b = output.toByteArray();
		output.flush();
		output.close();
		return b;
	}

	public InputStream loadContentByInput(String link) throws Exception {
		GetMethod get = getMethod(link);
		if (get == null)
			return null;
		int status = http.executeMethod(get);
		if (status == HttpStatus.SC_OK)
			return get.getResponseBodyAsStream();
		return null;
	}

	public String loadResource(String link, File file) throws Exception {
		GetMethod get = getMethod(link);
		if (get == null)
			return link;
		int status = http.executeMethod(get);
		if (status != HttpStatus.SC_OK)
			return link;
		if (buffer == null)
			buffer = new DataBuffer();
		buffer.save(file, get.getResponseBody());
		get.releaseConnection();
		return link;
	}

	public GetMethod getMethod(String link) throws Exception {
		if (link.toLowerCase().startsWith("ftp://"))
			return null;
		if (link.startsWith("http://") || link.startsWith("shttp://")) {
			URL url = new URL(link);
			if (!url.getHost().toLowerCase().equals(
					http.getHostConfiguration().getHost().toLowerCase())) {
				return null;
			}
		}
		if (link.startsWith("www.")) {
			if (!link.toLowerCase().equals(
					http.getHostConfiguration().getHost().toLowerCase())) {
				return null;
			}
		}
		// System.out.println("da tra link "+link+" va "+
		// http.getHostConfiguration().getHost());
		GetMethod get = new GetMethod(link);
		get.setFollowRedirects(true);
		get.getParams().setParameter("http.socket.timeout",
				new Integer(METHOD_TIMEOUT));
		return get;
	}

}
