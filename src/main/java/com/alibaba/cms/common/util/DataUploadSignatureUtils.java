package com.alibaba.cms.common.util;

import com.google.common.io.BaseEncoding;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DataUploadSignatureUtils {
    public static final String CONST_MD5 = "MD5";
    public static final String CONST_CONTENT_TYPE = "Content-Type";
    public static final String CONST_CONTENT_LENGTH = "Content-Length";
    public static final String CONST_CONTENT_MD5 = "Content-MD5";
    public static final String CONST_AUTHORIZATION = "Authorization";
    public static final String CONST_USER_AGENT = "User-Agent";
    public static final String CONST_X_CMS_APIVERSION = "x-cms-api-version";
    public static final String CONST_X_CMS_SIGNATURE_METHOD = "x-cms-signature";
    public static final String CONST_X_CMS_IP = "x-cms-ip";
    public static final String CONST_HOST = "Host";
    public static final String CONST_DATE = "Date";
    public static final String CONST_X_ACS_PREFIX= "x-acs-";
    public static final String CONST_X_CMS_PREFIX= "x-cms-";
    public static final String CONST_X_CALLER_TYPE = "token";
    public static final String HEADER_X_STS_TOKEN = "x-cms-security-token";
    public static final String HEADER_X_CALLER_TYPE = "x-cms-caller-type";

    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String HMAC_SHA1_JAVA = "HmacSHA1";

    public static Map<String, String> generateRequestHeaders(byte[] body, String resourceUri, String httpMethod,
                                                             String accessKeyId, String accessKeySecret) {
        Map<String, String> headers = getDefaultHeaders();
        if (body.length > 0) {
            headers.put(CONST_CONTENT_MD5, md5(body));
        }
        addAuthFieldToHeader(httpMethod, headers, resourceUri, accessKeyId, accessKeySecret);
        return headers;
    }

    private static void addAuthFieldToHeader(String verb, Map<String, String> headers, String resourceUri,
                                             String accessKeyId, String accessSecret) {
        StringBuilder builder = new StringBuilder();
        builder.append(verb).append("\n")
            .append(getMapValue(headers, CONST_CONTENT_MD5)).append("\n")
            .append(getMapValue(headers, CONST_CONTENT_TYPE)).append("\n")
            .append(getMapValue(headers, CONST_DATE)).append("\n")
            .append(getCanonicalizedHeaders(headers)).append("\n")
            .append(resourceUri);

        String signature = calculateSignature(accessSecret, builder.toString());
        headers.put(CONST_AUTHORIZATION, accessKeyId + ":" + signature);
    }

    private static String calculateSignature(String accessKey, String data) {
        try {
            byte[] keyBytes = accessKey.getBytes(UTF_8_ENCODING);
            byte[] dataBytes = data.getBytes(UTF_8_ENCODING);
            Mac mac = Mac.getInstance(HMAC_SHA1_JAVA);
            mac.init(new SecretKeySpec(keyBytes, HMAC_SHA1_JAVA));
            return BaseEncoding.base16().encode((mac.doFinal(dataBytes)));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Not Supported encoding method " + UTF_8_ENCODING, e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Not Supported signature method hmac-sha1", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Failed to calculate the signature", e);
        }
    }

    private static String getCanonicalizedHeaders(Map<String, String> headers) {
        Map<String, String> treeMap = new TreeMap<>(headers);
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            if (!entry.getKey().startsWith(CONST_X_CMS_PREFIX)
                && !entry.getKey().startsWith(CONST_X_ACS_PREFIX)) {
                continue;
            }
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append("\n");
            }
            builder.append(entry.getKey()).append(":").append(entry.getValue());
        }
        return builder.toString();
    }

    private static String getMapValue(Map<String, String> map, String key) {
        return map.getOrDefault(key, "");
    }

    private static String md5(byte[] bytes){
        try {
            MessageDigest md =  MessageDigest.getInstance(CONST_MD5);
            return BaseEncoding.base16().encode(md.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Not Supported encoding Method", e);
        }
    }

    private static Map<String, String> getDefaultHeaders() {
        HashMap<String, String> headerParameter = new HashMap<>();
        headerParameter.put(CONST_DATE, TimeUtils.formatRfc822Date(new Date()));
        headerParameter.put(CONST_USER_AGENT, "cms-java-sdk-v-1.0");
        headerParameter.put(CONST_CONTENT_TYPE,"application/json");
        headerParameter.put(CONST_HOST, "metrichub-cms-cn-hangzhou.aliyuncs.com");
        headerParameter.put(CONST_X_CMS_APIVERSION, "1.0");
        headerParameter.put(CONST_X_CMS_SIGNATURE_METHOD, "hmac-sha1");
        headerParameter.put(CONST_X_CMS_IP, "127.0.0.1");

        return headerParameter;
    }
}