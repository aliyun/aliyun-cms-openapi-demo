package com.alibaba.cms.api.sample;

import com.alibaba.cms.common.util.DataUploadSignatureUtils;
import com.alibaba.cms.common.util.HttpClientUtils;
import com.alibaba.cms.common.util.SignatureUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author he.dong
 * @date 2018/5/15
 */
public class CustomMonitorApiSample {
    private static final Logger logger = LoggerFactory.getLogger(CustomMonitorApiSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String userId = "<userId>";
    private static String endpoint = "http://metrics.aliyuncs.com/";


    /**
     * 上报自定义数据<br/> 自定义监控数据上报的api独立于云监控的标准api
     * <br/>
     * 详情参见：
     * <a href="https://help.aliyun.com/document_detail/63275.html?spm=a2c4g.11186623.6.633.0hi5Dh">https://help.aliyun.com</a>
     */
    public static void uploadCustomData() {
        String endpoint = "https://metrichub-cms-cn-hangzhou.aliyuncs.com";
        String resourceURI = "/metric/custom/upload";
        // 准备数据
        List<Object> data = new ArrayList<>();
        Map<String, Object> data1 = new HashMap<>();
        data1.put("groupId", 1234567L);
        data1.put("metricName", "your_metric_name");
        data1.put("dimensions", "{\"your_dimension_key\":\"your_dimension_value\",\"your_dimension_key2\":\"your_dimension_value2\"}");
        // type=0 表示上传原始数据，type=1表示上传聚合数据
        data1.put("type", 0);
        // type=0 时,传入的key只能是value，表示原始值。如果type=1，传入的key可以是Average,Sum,P10等，具体参见官网帮助文档
        data1.put("values", "{\"value\":100}");
        data1.put("time", String.valueOf(System.currentTimeMillis()));

        Map<String, Object> data2 = new HashMap<>(data1);
        data2.put("time", String.valueOf(System.currentTimeMillis() - 1000L * 60));
        Map<String, Object> data3 = new HashMap<>(data1);
        data3.put("time", String.valueOf(System.currentTimeMillis() - 1000L * 60 * 2));
        Map<String, Object> data4 = new HashMap<>(data1);
        data4.put("time", String.valueOf(System.currentTimeMillis() - 1000L * 60 * 3));

        data.add(data1);
        data.add(data2);
        data.add(data3);
        data.add(data4);

        JSONArray array = new JSONArray(data);
        byte[] body = JSON.toJSONBytes(array, SerializerFeature.WriteDateUseDateFormat);

        // 上报数据
        Map<String, String> headers = DataUploadSignatureUtils.generateRequestHeaders(body, resourceURI, "POST",
            accessKeyId, accessKeySecret);
        HttpClientUtils.post(endpoint + resourceURI, headers, body);
    }


    /**
     * 查询自定义监控的监控项信息
     * */
    public static void queryCustomMetricList() {
        // 只输入groupId,且为空或者<0, 查询所有具有自定义监控的分组和时间序列个数
        Map<String, String> params = new HashMap<>();
        params.put("Action", "QueryCustomMetricList");
        params.put("GroupId", "");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);

        // 只输入groupId, 查询该分组下的所有监控项
        params = new HashMap<>();
        params.put("Action", "QueryCustomMetricList");
        params.put("GroupId", "your_groupId");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);

        // 输入MetricName，Dimension等查询指定分组下的具体监控项
        params = new HashMap<>();
        params.put("Action", "QueryCustomMetricList");
        params.put("GroupId", "your_groupId");
        params.put("MetricName", "your_metric_name");
        // 维度的部分或全部信息,
        params.put("Dimension", "your_dimension_key or your_dimension_value or part of the string");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 删除自定义监控的监控项
     */
    public static void deleteCustomMetric() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DeleteCustomMetric");
        // GroupId 和 MetricName 必填，Md5和UUID最少选填一项
        params.put("GroupId", "your_group_id");
        params.put("MetricName", "your_metric_name");
        params.put("Md5", "md5_value");
        params.put("UUID", "uuid_value");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // 1. 上报自定义监控数据
        uploadCustomData();
        // 2. 查询自定义监控的监控项
        queryCustomMetricList();
        // 3. 创建自定义监控报警
        AlarmApiSample.createAlarm("custom_alarm",
            "acs_customMetric_" + userId,   // 自定义监控的project值为acs_customMetric_父账号id
            "your_metric_name",
            "[{\"groupId\":\"your_group_id\",\"dimension\":\"key=value\"}]",  //dimension的值为用户定义的k-v信息, 格式为key1=value1&key2=value2，并且按K字典序升序排列组合的值
            "Average",
            ">=",
            "100");
        // 4. 查询自定义监控数据
        QueryMetricApiSample.queryMetricList(
            "acs_customMetric_" + userId, // 自定义监控的project值为acs_customMetric_父账号id
            "your_metric_name",
            "[{\"groupId\":\"your_group_id\",\"dimension\":\"your_dimension_key=your_dimension_value&your_dimension_key2=your_dimension_value\"}]"); //dimension的值为用户定义的k-v信息, 格式为key1=value1&key2=value2，并且按K字典序升序排列组合的值
        // 5. 删除自定义监控项
        deleteCustomMetric();
    }


}
