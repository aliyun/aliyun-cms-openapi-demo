package com.alibaba.cms.sdk.sample;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.cms.CMSClient;
import com.aliyun.openservices.cms.exception.CMSException;
import com.aliyun.openservices.cms.model.impl.CustomEvent;
import com.aliyun.openservices.cms.request.CustomEventUploadRequest;
import com.aliyun.openservices.cms.response.CustomEventUploadResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.cms.model.v20180308.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author he.dong
 * @date 2018/5/28
 */
public class EventMonitorSdkSample {
    private static final Logger logger = LoggerFactory.getLogger(EventMonitorSdkSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static final String REGION_ID_BEIJING = "cn-beijing";

    /**
     * 上报自定义事件<br/> 自定义监控数据上报的api独立于云监控的标准api
     * <br/>
     * 详情参见：
     * <a href="https://help.aliyun.com/document_detail/60196.html?spm=a2c4g.11186623.6.583.4En6iU">https://help.aliyun.com</a>
     */
    public static void uploadEventData() {
        String endpoint = "https://metrichub-cms-cn-hangzhou.aliyuncs.com";
        //初始化client
        CMSClient cmsClient = new CMSClient(endpoint, accessKeyId, accessKeySecret);

        CustomEventUploadRequest request = CustomEventUploadRequest.builder()
            .append(CustomEvent.builder()
                //事件名称
                .setName("your_event_name")
                //设置定制的分组id
                .setGroupId(123456L)
                //事件详情
                .setContent("event content in string")
                .setTime(new Date())
                .build())
            .build();
        try {
            logger.info("sending CustomEventUploadRequest...");
            CustomEventUploadResponse response = cmsClient.putCustomEvent(request);
            logger.info("CustomEventUploadResponse:\n{}", JSON.toJSONString(response,true));
        } catch (CMSException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 查询系统事件分组数量，返回系统事件在指定时间范围的数量。<br/>
     * 支持的系统事件参见：<a href="https://help.aliyun.com/document_detail/66940.html?spm=a2c4g.11186623.6.587.86rVrm">https://help.aliyun.com</a>
     * */
    public static void querySystemEventCount() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        QuerySystemEventCountRequest request = new QuerySystemEventCountRequest();
        request.setAcceptFormat(FormatType.JSON);

        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        request.setQueryJson("{"
            + "\"product\":\"ECS\","                    // 产品名，ecs/slb/ess/...
            + "\"name\":\"system_event_name\","         // 系统事件名
            + "\"status\":\"system_event_status\","     // 系统事件状态，比如ECS的Executing/Executed/...
            + "\"level\":\"INFO\","                     // 事件级别, INFO/WARN/CRITICAL
            + "\"groupId\":\"your_group_id\","          // 应用分组id
            + "\"express\":\"sls query syntax\","       // 查询表达式，遵照sls的查询语法（分词符定义： , '";()[]{}?@&<>/:\n\t~!$%^+\\ )
            + "\"timeStart\":\"1527480816000\","        // 查询时间范围，unix时间戳，从1970-01-01 零点开始的毫秒数，时间跨度最大3天
            + "\"timeEnd\":\"1527497202000\""
            + "}");

        try {
            logger.info("sending QuerySystemEventCountRequest...");
            QuerySystemEventCountResponse response = client.getAcsResponse(request);
            logger.info("QuerySystemEventCountResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 查询系统事件在给定时间范围内的分布情况，分布的时间跨度会根据输入时间范围自适应<br/>
     * 支持的系统事件参见：<a href="https://help.aliyun.com/document_detail/66940.html?spm=a2c4g.11186623.6.587.86rVrm">https://help.aliyun.com</a>
     * */
    public static void querySystemEventHistogram() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        QuerySystemEventHistogramRequest request = new QuerySystemEventHistogramRequest();
        request.setAcceptFormat(FormatType.JSON);

        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        request.setQueryJson("{"
            + "\"product\":\"ECS\","                    // 产品名，ecs/slb/ess/...
            + "\"name\":\"system_event_name\","         // 系统事件名
            + "\"status\":\"system_event_status\","     // 系统事件状态，比如ECS的Executing/Executed/...
            + "\"level\":\"INFO\","                     // 事件级别, INFO/WARN/CRITICAL
            + "\"groupId\":\"your_group_id\","          // 应用分组id
            + "\"express\":\"sls query syntax\","       // 查询表达式，遵照sls的查询语法（分词符定义： , '";()[]{}?@&<>/:\n\t~!$%^+\\ )
            + "\"timeStart\":\"1527480816000\","        // 查询时间范围，unix时间戳，从1970-01-01 零点开始的毫秒数，时间跨度最大3天
            + "\"timeEnd\":\"1527497202000\""
            + "}");

        try {
            logger.info("sending QuerySystemEventHistogramRequest...");
            QuerySystemEventHistogramResponse response = client.getAcsResponse(request);
            logger.info("QuerySystemEventHistogramResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 查询系统事件在给定时间范围内的详细信息<br/>
     * 支持的系统事件参见：<a href="https://help.aliyun.com/document_detail/66940.html?spm=a2c4g.11186623.6.587.86rVrm">https://help.aliyun.com</a>
     * */
    public static void querySystemEventDetail() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        QuerySystemEventDetailRequest request = new QuerySystemEventDetailRequest();
        request.setAcceptFormat(FormatType.JSON);

        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        request.setQueryJson("{"
            + "\"product\":\"ECS\","                    // 产品名，ecs/slb/ess/...
            + "\"name\":\"system_event_name\","         // 系统事件名
            + "\"status\":\"system_event_status\","     // 系统事件状态，比如ECS的Executing/Executed/...
            + "\"level\":\"INFO\","                     // 事件级别, INFO/WARN/CRITICAL
            + "\"groupId\":\"your_group_id\","          // 应用分组id
            + "\"express\":\"sls query syntax\","       // 查询表达式，遵照sls的查询语法（分词符定义： , '";()[]{}?@&<>/:\n\t~!$%^+\\ )
            + "\"page\":\"1\","                         // 分页信息
            + "\"size\":\"10\","                        // 分页信息
            + "\"timeStart\":\"1527480816000\","        // 查询时间范围，unix时间戳，从1970-01-01 零点开始的毫秒数，时间跨度最大3天
            + "\"timeEnd\":\"1527497202000\""
            + "}");

        try {
            logger.info("sending QuerySystemEventDetailRequest...");
            QuerySystemEventDetailResponse response = client.getAcsResponse(request);
            logger.info("QuerySystemEventDetailResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }


    /**
     * 查询自定义事件分组数量，返回各个上报事件在指定时间范围的数量。
     * */
    public static void queryCustomEventCount() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        QueryCustomEventCountRequest request = new QueryCustomEventCountRequest();
        request.setAcceptFormat(FormatType.JSON);

        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        request.setQueryJson("{"
            + "\"name\":\"user_defined_event_name\","   // 自定义事件名称
            + "\"groupId\":\"your_group_id\","          // 应用分组id
            + "\"express\":\"sls query syntax\","       // 查询表达式，遵照sls的查询语法（分词符定义： '";=()[]{}?@&`~!#$%^*+<>/:\n\t.\\ )
            + "\"timeStart\":\"1527480816000\","        // 查询时间范围，unix时间戳，从1970-01-01 零点开始的毫秒数，时间跨度最大3天
            + "\"timeEnd\":\"1527494245000\""
            + "}");

        try {
            logger.info("sending QueryCustomEventCountRequest...");
            QueryCustomEventCountResponse response = client.getAcsResponse(request);
            logger.info("QueryCustomEventCountResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 查询自定义事件在给定时间范围内的分布情况，分布的时间跨度会根据输入时间范围自适应
     * */
    public static void queryCustomEventHistogram() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        QueryCustomEventHistogramRequest request = new QueryCustomEventHistogramRequest();
        request.setAcceptFormat(FormatType.JSON);

        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        request.setQueryJson("{"
            + "\"name\":\"user_defined_event_name\","   // 自定义事件名称
            + "\"groupId\":\"your_group_id\","          // 应用分组id
            + "\"express\":\"sls query syntax\","       // 查询表达式，遵照sls的查询语法（分词符定义： '";=()[]{}?@&`~!#$%^*+<>/:\n\t.\\ )
            + "\"timeStart\":\"1527480816000\","        // 查询时间范围，unix时间戳，从1970-01-01 零点开始的毫秒数，时间跨度最大3天
            + "\"timeEnd\":\"1527494245000\""
            + "}");

        try {
            logger.info("sending QueryCustomEventHistogramRequest...");
            QueryCustomEventHistogramResponse response = client.getAcsResponse(request);
            logger.info("QueryCustomEventHistogramResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 查询自定义事件在给定时间范围内的详细信息
     * */
    public static void queryCustomEventDetail() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        QueryCustomEventDetailRequest request = new QueryCustomEventDetailRequest();
        request.setAcceptFormat(FormatType.JSON);

        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        request.setQueryJson("{"
            + "\"name\":\"user_defined_event_name\","   // 自定义事件名称
            + "\"groupId\":\"your_group_id\","          // 应用分组id
            + "\"express\":\"sls query syntax\","       // 查询表达式，遵照sls的查询语法（分词符定义： '";=()[]{}?@&`~!#$%^*+<>/:\n\t.\\ )
            + "\"page\":\"1\","                         // 分页信息
            + "\"size\":\"10\","                        // 分页信息
            + "\"timeStart\":\"1528076189000\","        // 查询时间范围，unix时间戳，从1970-01-01 零点开始的毫秒数，时间跨度最大3天
            + "\"timeEnd\":\"1528097760000\""
            + "}");

        try {
            logger.info("sending QueryCustomEventDetailRequest...");
            QueryCustomEventDetailResponse response = client.getAcsResponse(request);
            logger.info("QueryCustomEventDetailResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    public static void main(String[] args) {
        /** 系统事件 */
        // 1. 系统事件会自动采集
        // 事件详情参见：https://help.aliyun.com/document_detail/66940.html?spm=a2c4g.11186623.6.587.86rVrm
        // 2. 查询系统事件数量
        querySystemEventCount();
        // 3. 查询系统事件分布
        querySystemEventHistogram();
        // 4. 查询系统事件详情
        querySystemEventDetail();

        /** 自定义事件 */
        // 1. 上报自定义事件数据
        uploadEventData();
        // 2. 查询自定义事件数量
        queryCustomEventCount();
        // 3. 查询自定义事件分布
        queryCustomEventHistogram();
        // 4. 查询自定义事件详情
        queryCustomEventDetail();
    }

}
