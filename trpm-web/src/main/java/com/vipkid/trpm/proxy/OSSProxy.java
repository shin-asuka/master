package com.vipkid.trpm.proxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.vipkid.trpm.constant.ApplicationConstant.OSS;

public class OSSProxy {

	private static Logger logger = LoggerFactory.getLogger(OSSProxy.class);

	private static final Pattern pattern = Pattern.compile("(http://.*)/(.*)/(.*)\\.(.*)");
	private static final Pattern patterns = Pattern.compile("(https://.*)/(.*)/(.*)\\.(.*)");

	public static boolean shrink(final String url, final String height, final String width,
			final String type) {
		if (!checkUrl(url)) {
			throw new IllegalStateException("url of you image is invalidate.");
		}

		if (height == null) {
			throw new IllegalStateException("height can not be null.");
		}

		if (width == null) {
			throw new IllegalStateException("width can not be null.");
		}

		if (type == null) {
			throw new IllegalStateException("type can not be null.");
		}

		boolean result = false;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url + "@" + width + "w_" + height + "h_" + "." + type);

		try {
			result = httpClient.execute(httpGet, new ShrinkResponseHandler(url));
			httpClient.close();
		} catch (Exception e) {
			logger.error("exception when processing image: %s", e.getMessage());
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("exception when processing image: %s", e.getMessage());
			}
		}

		return result;
	}

	public static boolean shrink(final String url, final String style) throws Exception {
		if (!checkUrl(url)) {
			throw new IllegalStateException("url of you image is invalidate.");
		}

		if (style == null) {
			throw new IllegalStateException("style can not be null.");
		}

		boolean result = false;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url + "@!" + style);

		try {
			result = httpClient.execute(httpGet, new ShrinkResponseHandler(url));
			httpClient.close();
		} catch (Exception e) {
			logger.error("exception when processing image: %s", e.getMessage());
			throw new IOException("exception when processing image:" + e.getMessage());
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("exception when processing image: %s", e.getMessage());
			}
		}

		return result;
	}

	static class ShrinkResponseHandler implements ResponseHandler<Boolean> {

		private String url;

		public ShrinkResponseHandler(String url) {
			this.url = url;
		}

		@Override
		public Boolean handleResponse(HttpResponse httpResponse) throws ClientProtocolException,
				IOException {
			StatusLine statusLine = httpResponse.getStatusLine();

			if (HttpURLConnection.HTTP_OK != statusLine.getStatusCode()) {
				return false;
			}

			HttpEntity httpEntity = httpResponse.getEntity();
			OSSClient client = new OSSClient(OSS.ENDPOINT, OSS.KEY_ID, OSS.KEY_SECRET);
			ObjectMetadata metadata = new ObjectMetadata();

			if (null != httpEntity) {
				metadata.setContentLength(httpEntity.getContentLength());
				Matcher matcher;
				if (url.contains("https:")){
					matcher = patterns.matcher(url);
				} else {
					matcher = pattern.matcher(url);
				}

				if (matcher.matches()) {
					String dir = matcher.group(2);
					String name = matcher.group(3);
					String type = matcher.group(4);

					String key = dir + "/" + name + "." + type;
					client.putObject(OSS.BUCKET, key, httpEntity.getContent(), metadata);
				}

				logger.info("Image process response status {}", statusLine.getStatusCode());
			}

			return true;
		}

	}

	/**
	 * 检查URL合法性
	 * 
	 * @param url
	 * @return boolean
	 */
	private static boolean checkUrl(String url) {
		if (null == url) {
			return false;
		} else {
			Matcher matcher;
			if (url.contains("https:")){
				matcher = patterns.matcher(url);
			} else {
				matcher = pattern.matcher(url);
			}
			return matcher.matches();
		}
	}

}
