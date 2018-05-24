package com.alibaba.cms.common.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class SignatureUtils {

    private static final Logger logger = LoggerFactory.getLogger(SignatureUtils.class);
    private final static String CHARSET_UTF8 = "utf8";
    private final static String ALGORITHM = "HmacSHA1";
    private final static String SEPARATOR = "&";

    private static String percentEncode(String value) {
        try {
            return value == null ? null : URLEncoder.encode(value, CHARSET_UTF8)
                .replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    private static String getHmacSHA1Signature(String params, String accessKeySecret) {
        String stringToSign = params;
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec signingKey = new SecretKeySpec(accessKeySecret.getBytes(CHARSET_UTF8), mac.getAlgorithm());
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(stringToSign.getBytes(CHARSET_UTF8));
            return new String(Base64.encodeBase64(rawHmac, false), CHARSET_UTF8);
        } catch (IllegalStateException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("getHmacSHA1Signature error:" + e.getMessage());
        }
        return null;
    }

    public static Map<String, String> appendPublicParams(Map<String, String> paramMap, String httpMethod,
                                                         String accessKeyId,
                                                         String accessKeySecret) {
        paramMap.put("Format", "JSON");
        paramMap.put("Version", "2018-03-08");
        paramMap.put("AccessKeyId", accessKeyId);
        paramMap.put("SignatureMethod", "HMAC-SHA1");
        paramMap.put("SignatureVersion", "1.0");
        paramMap.put("Timestamp", TimeUtils.getUTCTimeStr());
        int random = new Random().nextInt(100);
        String signatureNonce = String.valueOf(System.currentTimeMillis()) + "-" + String.valueOf(random);
        paramMap.put("SignatureNonce", signatureNonce);

        String[] keyArray = paramMap.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);

        // 将参数名称和值用"="进行连接,得到形如"key=value"的字符串
        // 将"="连接得到的参数组合按顺序依次用"&"进行连接,得到形如"key1=value1&key2=value2..."的字符串
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (String key : keyArray) {
            if (!isFirst) {
                stringBuilder.append(SEPARATOR);
            } else {
                isFirst = false;
            }
            stringBuilder.append(SignatureUtils.percentEncode(key)).append("=").append(
                SignatureUtils.percentEncode(paramMap.get(key)));
        }
        String stringToSign = String.format("%s%s%s%s%s", httpMethod,SEPARATOR, SignatureUtils.percentEncode("/"),SEPARATOR,
            SignatureUtils.percentEncode(stringBuilder.toString()));

        // 生成签名
        String signature = SignatureUtils.getHmacSHA1Signature(stringToSign, accessKeySecret + "&");
        paramMap.put("Signature", signature);
        return paramMap;
    }

}
