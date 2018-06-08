package com.alibaba.cms.api.sample;

import com.alibaba.cms.common.util.HttpClientUtils;
import com.alibaba.cms.common.util.SignatureUtils;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pingyue.liu
 */
public class ActiveAlarmApiSample {
    private static final Logger logger = LoggerFactory.getLogger(ActiveAlarmApiSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String userId = "<userId>";
    private static String endpoint = "http://metrics.aliyuncs.com/";

    /**
     * 查询已开启主动告警产品列表
     *
     * @param userId
     * @return
     */
    public static String[] listProductOfActiveAlert(String userId) {
        //请求类型 POST|GET
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ListProductOfActiveAlert");
        //必选,userId
        params.put("UserId", userId);
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        String response = HttpClientUtils.get(endpoint, params);
        try {
            return JSON.parseObject(response).getString("Datapoints").split(",");
        } catch (Exception e) {
            logger.error("ListProductOfActiveAlert's failed,response:" + response);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询某个产品下的主动告警规则列表
     *
     * @param userId
     * @param product 产品名,可选的值为：ecs,rds,slb,redisa,mongodb,mongodb_sharding,hbase,elasticsearch,opensearch
     * @return
     */
    public static String listActiveAlertRule(String userId, String product) {
        //请求类型 POST|GET
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ListActiveAlertRule");
        //选填,userId
        params.put("UserId", userId);
        //必选，产品名,可选的值为：ecs,rds,slb,redisa,mongodb,mongodb_sharding,hbase,elasticsearch,opensearch
        params.put("Product", product);
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        String response = HttpClientUtils.get(endpoint, params);
        try {
            return JSON.parseObject(response).getString("Datapoints");
        } catch (Exception e) {
            logger.error("ListActiveAlertRule's failed,response:" + response);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 为某个产品打开主动告警
     *
     * @param userId
     * @param product 产品名,可选的值为：ecs,rds,slb,redisa,mongodb,mongodb_sharding,hbase,elasticsearch,opensearch
     */
    public static String enableActiveAlert(String userId, String product) {
        //请求类型 POST|GET
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "EnableActiveAlert");
        //选填,userId
        params.put("UserId", userId);
        //必选，产品名,可选的值为：ecs,rds,slb,redisa,mongodb,mongodb_sharding,hbase,elasticsearch,opensearch
        params.put("Product", product);
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        return HttpClientUtils.get(endpoint, params);
    }

    /**
     * 为某个产品关闭主动告警
     *
     * @param userId
     * @param product 产品名,可选的值为：ecs,rds,slb,redisa,mongodb,mongodb_sharding,hbase,elasticsearch,opensearch
     */
    public static String disableActiveAlert(String userId, String product) {
        //请求类型 POST|GET
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DisableActiveAlert");
        //选填,userId
        params.put("UserId", userId);
        //必选，产品名,可选的值为：ecs,rds,slb,redisa,mongodb,mongodb_sharding,hbase,elasticsearch,opensearch
        params.put("Product", product);
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        return HttpClientUtils.get(endpoint, params);
    }

    public static void main(String[] args) {
        // 1. 查询某用户下已开启主动告警产品列表
        String[] products = listProductOfActiveAlert(userId);

        // 2、查询某个产品的主动告警规则列表
        String product;
        if (null != products && products.length > 0) {
            product = products[0];
        } else {
            product = "ecs";
        }
        String alarmStr = listActiveAlertRule(userId, product);

        // 3、关闭某个产品的主动告警
        disableActiveAlert(userId, product);

        // 4、开启某个产品的主动告警
        enableActiveAlert(userId, product);
    }

}
