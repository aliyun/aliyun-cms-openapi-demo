package com.alibaba.cms.common.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    public static final String DEFAULT_CHARSETNAME = "UTF-8";
    public static final int DEFAULT_TIMEOUT = 20 * 1000;
    public static final int CONNECT_TIMEOUT = 3 * 1000;

    /**
     * 根据params构造url
     *
     * @param url
     * @param params
     * @return url with params, like http://www.abc.com/?p=xyz&q=yyy
     * @throws URISyntaxException
     */
    public static String buildHttpURI(String url, Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return url;
        }
        URIBuilder builder = new URIBuilder();
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        try {
            return builder.setPath(url).setParameters(nameValuePairs).setCharset(Charset.forName(DEFAULT_CHARSETNAME))
                .build().toString();
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 简单的GET请求方法
     *
     * @param url request url
     * @param params parameters for get request
     * */
    public static String get(String url, Map<String, String> params) {
        try {
            url = buildHttpURI(url, params);
            logger.info("GET Request URL: {}", url);
            HttpResponse response = Request.Get(url).socketTimeout(DEFAULT_TIMEOUT).connectTimeout(CONNECT_TIMEOUT)
                .execute()
                .returnResponse();
            String responseMessage = EntityUtils.toString(response.getEntity());
            logger.info("response status: {}", response.getStatusLine());
            logger.info("response message: {}", JSON.toJSONString(JSON.parseObject(responseMessage), true));
            return responseMessage;
        } catch (Exception e) {
            logger.error("GET Request Failed with exception！{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 简单的POST请求方法
     * @param url request url
     * @param headers request headers
     * @param body post data
     * */
    public static String post(String url, Map<String, String> headers, byte[] body) {
        try {
            logger.info("POST Request URL: {}", url);
            logger.info("Header: \n{}", toStringForPrinting(headers));
            logger.info("Body: \n{}", new String(body));

            Request request = Request.Post(url);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request = request.addHeader(entry.getKey(), entry.getValue());
            }

            HttpResponse response = request.body(new ByteArrayEntity(body)).socketTimeout(DEFAULT_TIMEOUT)
                .connectTimeout(CONNECT_TIMEOUT)
                .execute()
                .returnResponse();
            String responseMessage = EntityUtils.toString(response.getEntity());
            logger.info("response status: {}", response.getStatusLine());
            logger.info("response message: {}", JSON.toJSONString(JSON.parseObject(responseMessage), true));
            return responseMessage;
        } catch (Exception e) {
            logger.error("POST Request Failed with exception！{}", e.getMessage(), e);
            return null;
        }
    }

    private static String toStringForPrinting(Map map) {
        StringBuffer str = new StringBuffer();
        for (Object key : map.keySet()) {
            str.append(key).append(":").append(map.get(key)).append("\n");
        }
        return str.toString();
    }
}
