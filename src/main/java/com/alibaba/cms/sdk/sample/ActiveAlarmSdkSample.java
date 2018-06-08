package com.alibaba.cms.sdk.sample;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.cms.model.v20180308.*;
import com.aliyuncs.cms.model.v20180308.ListActiveAlertRuleResponse.Alarm;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author pingyue.liu
 */
public class ActiveAlarmSdkSample {
    private static final Logger logger = LoggerFactory.getLogger(ActiveAlarmSdkSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String userId = "<userId>";
    private static final String REGION_ID_BEIJING = "cn-beijing";

    /**
     * 查询已开启主动告警产品列表
     *
     * @param userId
     * @return
     */
    public static String[] listProductOfActiveAlert(String userId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        ListProductOfActiveAlertRequest request = new ListProductOfActiveAlertRequest();
        //选填,userId
        request.setUserId(userId);
        try {
            logger.info("sending ListProductOfActiveAlertRequest...");
            ListProductOfActiveAlertResponse response = client.getAcsResponse(request);
            logger.info("ListProductOfActiveAlertResponse:\n{}", JSON.toJSONString(response, true));
            return response.getDatapoints().split(",");
        } catch (ClientException e) {
            logger.error(e.getMessage());
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
    public static List<Alarm> listActiveAlertRule(String userId, String product) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        ListActiveAlertRuleRequest request = new ListActiveAlertRuleRequest();
        //选填,userId
        request.setUserId(userId);
        //必选，产品名,可选的值为：ecs,rds,slb,redisa,mongodb,mongodb_sharding,hbase,elasticsearch,opensearch
        request.setProduct(product);
        try {
            logger.info("sending ListActiveAlertRuleRequest...");
            ListActiveAlertRuleResponse response = client.getAcsResponse(request);
            logger.info("ListActiveAlertRuleResponse:\n{}", JSON.toJSONString(response, true));
            return response.getDatapoints();
        } catch (ClientException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * 为某个产品打开主动告警
     *
     * @param userId
     * @param product 产品名,可选的值为：ecs,rds,slb,redisa,mongodb,mongodb_sharding,hbase,elasticsearch,opensearch
     */
    public static void enableActiveAlert(String userId, String product) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        EnableActiveAlertRequest request = new EnableActiveAlertRequest();
        //选填,userId
        request.setUserId(userId);
        //必选，产品名,可选的值为：ecs,rds,slb,redisa,mongodb,mongodb_sharding,hbase,elasticsearch,opensearch
        request.setProduct(product);
        try {
            logger.info("sending EnableActiveAlertRequest...");
            EnableActiveAlertResponse response = client.getAcsResponse(request);
            logger.info("EnableActiveAlertResponse:\n{}", JSON.toJSONString(response, true));
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 为某个产品关闭主动告警
     *
     * @param userId
     * @param product 产品名,可选的值为：ecs,rds,slb,redisa,mongodb,mongodb_sharding,hbase,elasticsearch,opensearch
     */
    public static void disableActiveAlert(String userId, String product) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        DisableActiveAlertRequest request = new DisableActiveAlertRequest();
        //选填,userId
        request.setUserId(userId);
        //必选，产品名,可选的值为：ecs,rds,slb,redisa,mongodb,mongodb_sharding,hbase,elasticsearch,opensearch
        request.setProduct(product);
        try {
            logger.info("sending DisableActiveAlertRequest...");
            DisableActiveAlertResponse response = client.getAcsResponse(request);
            logger.info("DisableActiveAlertResponse:\n{}", JSON.toJSONString(response, true));
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        // 1. 查询某用户下已开启主动告警产品列表
        String[] products = listProductOfActiveAlert(userId);

        // 2、查询某个产品的主动告警规则列表
        String product;
        if (null != products && products.length > 0) {
            product = products[0];
        } else {
            product = "slb";
        }
        List<Alarm> alarmList = listActiveAlertRule(userId, product);

        // 3、关闭某个产品的主动告警
        disableActiveAlert(userId, product);

        // 4、开启某个产品的主动告警
        enableActiveAlert(userId, product);
    }

}
