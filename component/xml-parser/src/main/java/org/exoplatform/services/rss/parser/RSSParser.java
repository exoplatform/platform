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
package org.exoplatform.services.rss.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.exoplatform.services.common.DataBuffer;
import org.exoplatform.services.common.HttpClientImpl;
import org.exoplatform.services.html.refs.RefsDecoder;
import org.exoplatform.services.token.attribute.AttributeParser;
import org.exoplatform.services.token.attribute.Attributes;
import org.exoplatform.services.xml.parser.XMLDocument;
import org.exoplatform.services.xml.parser.XMLNode;
import org.exoplatform.services.xml.parser.XMLParser;

/**
 * Created by The eXo Platform SARL .
 * 
 * @author nhuthuan Email: nhudinhthuan@yahoo.com
 */
public class RSSParser {

	private RefsDecoder decoder;

	public RSSParser() {
		decoder = new RefsDecoder();
	}

	public synchronized RSSDocument<DefaultRSSChannel, DefaultRSSItem> createDocument(
			XMLNode root) throws Exception {
		DefaultRSSChannel channel = createChannel(root);
		if (channel == null)
			return null;
		return new RSSDocument<DefaultRSSChannel, DefaultRSSItem>(channel,
				createItems(channel.getNode()));
	}

	public synchronized <T extends IRSSChannel, E extends IRSSItem> RSSDocument<T, E> createDocument(
			XMLNode root, Class<T> channelClazz, Class<E> itemClazz)
			throws Exception {
		T channel = createChannel(root, channelClazz);
		if (channel == null)
			return null;
		return new RSSDocument<T, E>(channel, createItems(channel.getNode(),
				itemClazz));
	}

	private DefaultRSSChannel createChannel(XMLNode root) throws Exception {
		return createChannel(root, DefaultRSSChannel.class);
	}

	private <T extends IRSSChannel> T createChannel(XMLNode root, Class<T> clazz)
			throws Exception {
		if (root.isNode("channel") || root.isNode("feed"))
			return createSingleChannel(root, clazz);
		List<XMLNode> list = root.getChildren();
		for (XMLNode ele : list) {
			T channel = createChannel(ele, clazz);
			if (channel != null)
				return channel;
		}
		return null;
	}

	private <T extends IRSSChannel> T createSingleChannel(XMLNode root,
			Class<T> clazz) throws Exception {
		T t = null;
		List<XMLNode> children = root.getChildren();
		t = createItem(root, clazz);
		for (XMLNode ele : children) {
			if (ele.getTotalChildren() < 1)
				continue;
			if (ele.isNode("generator"))
				t.setGenerator(ele.getChild(0).getNodeValue());
		}
		return t;
	}

	private List<DefaultRSSItem> createItems(XMLNode root) throws Exception {
		return createItems(root, DefaultRSSItem.class);
	}

	private <T extends IRSSItem> List<T> createItems(XMLNode root,
			Class<T> clazz) throws Exception {
		if (root == null)
			return new ArrayList<T>();
		List<XMLNode> list = root.getChildren();
		List<T> items = new ArrayList<T>();
		for (XMLNode ele : list) {
			if (ele.isNode("item") || ele.isNode("entry"))
				items.add(createItem(ele, clazz));
		}
		if (items.size() < 1)
			return createItems(root.getParent(), clazz);
		return items;
	}

	private <T extends IRSSItem> T createItem(XMLNode node, Class<T> clazz)
			throws Exception {
		T item = clazz.newInstance();
		item.setNode(node);
		List<XMLNode> children = node.getChildren();
		if (children.size() < 1)
			return null;
		for (XMLNode ele : children) {
			if (ele.isNode("title") && ele.getTotalChildren() > 0) {
				item
						.setTitle(removeCData(ele.getChild(0).getNodeValue()
								.trim()));
			} else if ((ele.isNode("description") || ele.isNode("summary") || ele
					.isNode("content"))
					&& ele.getTotalChildren() > 0) {
				if (item.getDesc() == null
						|| item.getDesc().trim().length() < 1) {
					item.setDesc(decoder.decode(removeCData(ele.getChild(0)
							.getNodeValue().trim())));
				}
			} else if (ele.isNode("link")) {
				if (ele.getTotalChildren() > 0) {
					// item.setLink(ele.getChild(0).getNodeValue());
					  item.setLink(removeCData(ele.getChild(0).getNodeValue()));
				} else {
					Attributes attributes = AttributeParser.getAttributes(ele);
					// item.setLink(attributes.getAttributeValue("href"));
					item.setLink(removeCData(attributes.getAttributeValue("href")));
				}
			} else if ((ele.isNode("pubDate") || ele.isNode("issued"))
					&& ele.getTotalChildren() > 0)
				item
						.setTime(removeCData(ele.getChild(0).getNodeValue()
								.trim()));
			else if (ele.isNode("image") && ele.getTotalChildren() > 0)
				item.setImage(ele.getChild(0).getNodeValue());
			// TODO: set creator of content
			else if (ele.isNode("dc:creator") && ele.getTotalChildren() > 0)
				item.setCreator((ele.getChild(0).getNodeValue()));
		}
		return item;
	}

	private String removeCData(String text) {
		int idx = text.indexOf("[CDATA[");
		if (idx != 1)
			return text;
		text = text.substring(idx + 7);
		idx = text.lastIndexOf("]]");
		if (idx != text.length() - 2)
			return text;
		return text.substring(0, text.length() - 2);
	}

	public synchronized <T extends IRSSChannel, E extends IRSSItem> RSSDocument<T, E> createDocument(
			String text, Class<T> channelClazz, Class<E> itemClazz)
			throws Exception {
		XMLDocument document = XMLParser.createDocument(text);
		if (document == null)
			return null;
		return createDocument(document.getRoot(), channelClazz, itemClazz);
	}

	public synchronized RSSDocument<DefaultRSSChannel, DefaultRSSItem> createDocument(
			String text) throws Exception {
		XMLDocument document = XMLParser.createDocument(text);
		if (document == null)
			return null;
		return createDocument(document.getRoot(), DefaultRSSChannel.class,
				DefaultRSSItem.class);
	}

	public synchronized RSSDocument<DefaultRSSChannel, DefaultRSSItem> createDocument(
			byte[] data, String charset) throws Exception {
		return createDocument(new String(data, charset),
				DefaultRSSChannel.class, DefaultRSSItem.class);
	}

	public synchronized <T extends IRSSChannel, E extends IRSSItem> RSSDocument<T, E> createDocument(
			byte[] data, String charset, Class<T> channelClazz,
			Class<E> itemClazz) throws Exception {
		return createDocument(new String(data, charset), channelClazz,
				itemClazz);
	}

	public synchronized <T extends IRSSChannel, E extends IRSSItem> RSSDocument<T, E> createDocument(
			InputStream input, String charset, Class<T> channelClazz,
			Class<E> itemClazz) throws Exception {
		XMLDocument document = XMLParser.createDocument(input, charset);
		if (document == null)
			return null;
		return createDocument(document.getRoot(), channelClazz, itemClazz);
	}

	public synchronized RSSDocument<DefaultRSSChannel, DefaultRSSItem> createDocument(
			InputStream input, String charset) throws Exception {
		XMLDocument document = XMLParser.createDocument(input, charset);
		if (document == null)
			return null;
		return createDocument(document.getRoot(), DefaultRSSChannel.class,
				DefaultRSSItem.class);
	}

	public synchronized <T extends IRSSChannel, E extends IRSSItem> RSSDocument<T, E> createDocument(
			File file, String charset, Class<T> channelClazz, Class<E> itemClazz)
			throws Exception {
		return createDocument(new FileInputStream(file), charset, channelClazz,
				itemClazz);
	}

	public synchronized RSSDocument<DefaultRSSChannel, DefaultRSSItem> createDocument(
			File file, String charset) throws Exception {
		if (!file.exists())
			return null;
		return createDocument(new FileInputStream(file), charset,
				DefaultRSSChannel.class, DefaultRSSItem.class);
	}

	public synchronized <T extends IRSSChannel, E extends IRSSItem> RSSDocument<T, E> createDocument(
			URL url, String charset, Class<T> channelClazz, Class<E> itemClazz)
			throws Exception {
		GetMethod get = null;
		try {
			HttpClientImpl httpClientService = new HttpClientImpl(url);
			get = httpClientService.getMethod(url.getFile());
			get.setFollowRedirects(true);
			int statusCode = httpClientService.getHttpClient().executeMethod(
					get);
			if (statusCode != HttpStatus.SC_OK) {
				throw new MalformedURLException("Server response code "
						+ statusCode);
			}
			InputStream input = get.getResponseBodyAsStream();
			DataBuffer buffer = new DataBuffer();
			byte[] data = buffer.loadInputStream(input).toByteArray();
			return createDocument(data, charset, channelClazz, itemClazz);
		} finally {
			if (get != null)
				get.releaseConnection();
		}
	}

	public synchronized <T extends IRSSChannel, E extends IRSSItem> RSSDocument<T, E> createDocument(
			URI uri, String charset, Class<T> channelClazz, Class<E> itemClazz)
			throws Exception {
		try {
			return createDocument(uri.toURL(), charset, channelClazz, itemClazz);
		} catch (Exception e) {
			try {
				File file = new File(uri);
				return createDocument(file, charset, channelClazz, itemClazz);
			} catch (Exception exp) {
				return null;
			}
		}
	}

	public synchronized RSSDocument<DefaultRSSChannel, DefaultRSSItem> createDocument(
			URI uri, String charset) throws Exception {
		return createDocument(uri, charset, DefaultRSSChannel.class,
				DefaultRSSItem.class);
	}

}