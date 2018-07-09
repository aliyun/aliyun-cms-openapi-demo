package com.alibaba.cms.api.sample;

import com.alibaba.cms.common.util.DataUploadSignatureUtils;
import com.alibaba.cms.common.util.HttpClientUtils;
import com.alibaba.cms.common.util.SignatureUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author he.dong
 * @date 2018/5/28
 */
public class EventMonitorApiSample {
    private static final Logger logger = LoggerFactory.getLogger(EventMonitorApiSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String endpoint = "http://metrics.aliyuncs.com/";

    /**
     * 上报自定义事件<br/> 自定义监控数据上报的api独立于云监控的标准api
     * <br/>
     * 详情参见：
     * <a href="https://help.aliyun.com/document_detail/60196.html?spm=a2c4g.11186623.6.583.4En6iU">https://help.aliyun.com</a>
     */
    public static void uploadEventData() {
        String endpoint = "https://metrichub-cms-cn-hangzhou.aliyuncs.com";
        String resourceURI = "/event/custom/upload";
        // 准备数据
        List<Object> data = new ArrayList<>();
        Map<String, Object> data1 = new HashMap<>();
        data1.put("groupId", 123456L);
        //事件名称
        data1.put("name", "event_name");
        //事件详情
        data1.put("content", "event content in string");
        data1.put("time", String.valueOf(System.currentTimeMillis()));
        data.add(data1);

        JSONArray array = new JSONArray(data);
        byte[] body = JSON.toJSONBytes(array, SerializerFeature.WriteDateUseDateFormat);

        // 上报数据
        Map<String, String> headers = DataUploadSignatureUtils.generateRequestHeaders(body, resourceURI, "POST",
            accessKeyId, accessKeySecret);
        HttpClientUtils.post(endpoint + resourceURI, headers, body);
    }

    /**
     * 查询系统事件分组数量，返回系统事件在指定时间范围的数量。<br/>
     * 支持的系统事件参见：<a href="https://help.aliyun.com/document_detail/66940.html?spm=a2c4g.11186623.6.587.86rVrm">https://help.aliyun.com</a>
     * */
    public static void querySystemEventCount() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "QuerySystemEventCount");
        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        params.put("QueryJson", "{"
            + "\"product\":\"ECS\","                    // 产品名，ecs/slb/ess/...
            + "\"name\":\"system_event_name\","         // 系统事件名
            + "\"status\":\"system_event_status\","     // 系统事件状态，比如ECS的Executing/Executed/...
            + "\"level\":\"INFO\","                     // 事件级别, INFO/WARN/CRITICAL
            + "\"groupId\":\"your_group_id\","          // 应用分组id
            + "\"express\":\"sls query syntax\","       // 查询表达式，遵照sls的查询语法（分词符定义： , '";()[]{}?@&<>/:\n\t~!$%^+\\ )
            + "\"timeStart\":\"1527480816000\","        // 查询时间范围，unix时间戳，从1970-01-01 零点开始的毫秒数，时间跨度最大3天
            + "\"timeEnd\":\"1527497202000\""
            + "}");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 查询系统事件在给定时间范围内的分布情况，分布的时间跨度会根据输入时间范围自适应<br/>
     * 支持的系统事件参见：<a href="https://help.aliyun.com/document_detail/66940.html?spm=a2c4g.11186623.6.587.86rVrm">https://help.aliyun.com</a>
     * */
    public static void querySystemEventHistogram() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "QuerySystemEventHistogram");
        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        params.put("QueryJson", "{"
            + "\"product\":\"ECS\","                    // 产品名，ecs/slb/ess/...
            + "\"name\":\"system_event_name\","         // 系统事件名
            + "\"status\":\"system_event_status\","     // 系统事件状态，比如ECS的Executing/Executed/...
            + "\"level\":\"INFO\","                     // 事件级别, INFO/WARN/CRITICAL
            + "\"groupId\":\"your_group_id\","          // 应用分组id
            + "\"express\":\"sls query syntax\","       // 查询表达式，遵照sls的查询语法（分词符定义： , '";()[]{}?@&<>/:\n\t~!$%^+\\ )
            + "\"timeStart\":\"1527480816000\","        // 查询时间范围，unix时间戳，从1970-01-01 零点开始的毫秒数，时间跨度最大3天
            + "\"timeEnd\":\"1527497202000\""
            + "}");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 查询系统事件在给定时间范围内的详细信息<br/>
     * 支持的系统事件参见：<a href="https://help.aliyun.com/document_detail/66940.html?spm=a2c4g.11186623.6.587.86rVrm">https://help.aliyun.com</a>
     * */
    public static void querySystemEventDetail() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "QuerySystemEventDetail");
        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        params.put("QueryJson", "{"
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
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    /**
     * 查询自定义事件分组数量，返回各个上报事件在指定时间范围的数量。
     * */
    public static void queryCustomEventCount() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "QueryCustomEventCount");
        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        params.put("QueryJson", "{"
            + "\"name\":\"user_defined_event_name\","   // 自定义事件名称
            + "\"groupId\":\"your_group_id\","          // 应用分组id
            + "\"express\":\"sls query syntax\","       // 查询表达式，遵照sls的查询语法（分词符定义： '";=()[]{}?@&`~!#$%^*+<>/:\n\t.\\ )
            + "\"timeStart\":\"1527480816000\","        // 查询时间范围，unix时间戳，从1970-01-01 零点开始的毫秒数，时间跨度最大3天
            + "\"timeEnd\":\"1527494245000\""
            + "}");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 查询自定义事件在给定时间范围内的分布情况，分布的时间跨度会根据输入时间范围自适应
     * */
    public static void queryCustomEventHistogram() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "QueryCustomEventHistogram");
        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        params.put("QueryJson", "{"
            + "\"name\":\"user_defined_event_name\","   // 自定义事件名称
            + "\"groupId\":\"your_group_id\","          // 应用分组id
            + "\"express\":\"sls query syntax\","       // 查询表达式，遵照sls的查询语法（分词符定义： '";=()[]{}?@&`~!#$%^*+<>/:\n\t.\\ )
            + "\"timeStart\":\"1527480816000\","        // 查询时间范围，unix时间戳，从1970-01-01 零点开始的毫秒数，时间跨度最大3天
            + "\"timeEnd\":\"1527494245000\""
            + "}");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 查询自定义事件在给定时间范围内的详细信息
     * */
    public static void queryCustomEventDetail() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "QueryCustomEventDetail");
        // 只有一个参数，json格式，只有查询时间范围是必选的，其他为过滤条件，描述如下
        params.put("QueryJson", "{"
            + "\"name\":\"user_defined_event_name\","   // 自定义事件名称
            + "\"groupId\":\"your_group_id\","          // 应用分组id
            + "\"express\":\"sls query syntax\","       // 查询表达式，遵照sls的查询语法（分词符定义： '";=()[]{}?@&`~!#$%^*+<>/:\n\t.\\ )
            + "\"page\":\"1\","                         // 分页信息
            + "\"size\":\"10\","                        // 分页信息
            + "\"timeStart\":\"1527480816000\","        // 查询时间范围，unix时间戳，从1970-01-01 零点开始的毫秒数，时间跨度最大3天
            + "\"timeEnd\":\"1527494245000\""
            + "}");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
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
