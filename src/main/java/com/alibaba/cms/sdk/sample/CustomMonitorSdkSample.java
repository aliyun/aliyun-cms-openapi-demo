package com.alibaba.cms.sdk.sample;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.cms.CMSClient;
import com.aliyun.openservices.cms.exception.CMSException;
import com.aliyun.openservices.cms.metric.MetricAttribute;
import com.aliyun.openservices.cms.model.CustomMetric;
import com.aliyun.openservices.cms.request.CustomMetricUploadRequest;
import com.aliyun.openservices.cms.response.CustomMetricUploadResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.cms.model.v20180308.DeleteCustomMetricRequest;
import com.aliyuncs.cms.model.v20180308.DeleteCustomMetricResponse;
import com.aliyuncs.cms.model.v20180308.QueryCustomMetricListRequest;
import com.aliyuncs.cms.model.v20180308.QueryCustomMetricListResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author he.dong
 * @date 2018/5/15
 */
public class CustomMonitorSdkSample {
    private static final Logger logger = LoggerFactory.getLogger(CustomMonitorSdkSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String userId = "<your_user_id>";
    private static final String REGION_ID_BEIJING = "cn-beijing";

    /**
     * 上报自定义数据<br/>
     * 自定义监控数据上报的sdk独立于云监控的标准sdk
     */
    public static void uploadCustomData(){
        String endpoint = "https://metrichub-cms-cn-hangzhou.aliyuncs.com";
        //初始化client
        CMSClient cmsClient = new CMSClient(endpoint, accessKeyId, accessKeySecret);

        CustomMetricUploadRequest request = CustomMetricUploadRequest.builder()
            .append(CustomMetric.builder()
                //指标名
                .setMetricName("your_metric_name")
                //设置定制的分组id
                .setGroupId(123456L)
                .setTime(new Date())
                //TYPE_VALUE类型为原始值;TYPE_AGG类型为聚合值
                .setType(CustomMetric.TYPE_VALUE)
                //原始值，key只能为这个
                .appendValue(MetricAttribute.VALUE, 100)
                //添加维度
                .appendDimension("your_dimension_key", "your_dimension_value")
                .appendDimension("your_dimension_key2", "your_dimension_value")
                .build())
            // 上传多个数据点
            .append(CustomMetric.builder()
                //指标名
                .setMetricName("your_metric_name")
                //设置定制的分组id
                .setGroupId(123456L)
                .setTime(new Date(System.currentTimeMillis() - 1000 * 60))
                //TYPE_VALUE类型为原始值;TYPE_AGG类型为聚合值
                .setType(CustomMetric.TYPE_VALUE)
                //如果类型为原始值，值类型只能为VALUE; 如果类型为聚合值，值类型可以是其他类型。
                .appendValue(MetricAttribute.VALUE, 100)
                //添加维度
                .appendDimension("your_dimension_key", "your_dimension_value")
                .appendDimension("your_dimension_key2", "your_dimension_value")
                .build())
            .build();
        try {
            logger.info("sending CustomMetricUploadRequest...");
            CustomMetricUploadResponse response = cmsClient.putCustomMetric(request);
            logger.info("CustomMetricUploadResponse:\n{}", JSON.toJSONString(response,true));
        } catch (CMSException e) {
            logger.error(e.getMessage());
        }
    }


    /**
     * 查询自定义监控的监控项信息
     * */
    public static void queryCustomMetricList() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        // 输入groupId<0, 查询所有包含自定义监控项的分组和时间序列个数
        QueryCustomMetricListRequest request0 = new QueryCustomMetricListRequest();
        request0.setGroupId("-1");
        // 只输入groupId, 查询该分组下的所有监控项
        QueryCustomMetricListRequest request1 = new QueryCustomMetricListRequest();
        request1.setAcceptFormat(FormatType.JSON);
        request1.setGroupId("your_group_id");
        // 输入MetricName，Dimension等查询指定分组下的具体监控项
        QueryCustomMetricListRequest request2 = new QueryCustomMetricListRequest();
        request2.setGroupId("your_group_id");
        request2.setMetricName("your_metric_name");
        // 维度的部分或全部信息
        request2.setDimension("your_dimension_key");
        request2.setMd5("valid_md5_value");
        request2.setSize("10");
        request2.setPage("1");

        try {
            logger.info("sending QueryCustomMetricListRequest0...");
            QueryCustomMetricListResponse response = client.getAcsResponse(request0);
            logger.info("QueryCustomMetricListResponse0:\n{}", JSON.toJSONString(response,true));
            logger.info("response.getResult():\n{}", JSON.toJSONString(JSON.parseObject(response.getResult()), true));
            logger.info("sending QueryCustomMetricListRequest0...");
            response = client.getAcsResponse(request1);
            logger.info("QueryCustomMetricListResponse1:\n{}", JSON.toJSONString(response,true));
            logger.info("response.getResult():\n{}", JSON.toJSONString(JSON.parseObject(response.getResult()), true));
            logger.info("sending QueryCustomMetricListRequest2...");
            response = client.getAcsResponse(request2);
            logger.info("QueryCustomMetricListResponse2:\n{}", JSON.toJSONString(response,true));
            logger.info("response.getResult():\n{}", JSON.toJSONString(JSON.parseObject(response.getResult()), true));
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 删除自定义监控的监控项
     */
    public static void deleteCustomMetric() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        DeleteCustomMetricRequest request = new DeleteCustomMetricRequest();
        request.setAcceptFormat(FormatType.JSON);
        // GroupId 和 MetricName 必填，Md5和UUID最少选填一项
        request.setGroupId("your_group_id");
        request.setMetricName("your_metric_name");
        //request.setUUID("valid_uuid_value");
        request.setMd5("valid_md5_value");


        try {
            logger.info("sending DeleteCustomMetricRequest...");
            DeleteCustomMetricResponse response = client.getAcsResponse(request);
            logger.info("DeleteCustomMetricResponse:\n{}", JSON.toJSONString(response,true));
            // ret:true 删除成功, ret:false没有找到要删除的对应资源
            logger.info("response.getResult():\n{}", JSON.toJSONString(JSON.parseObject(response.getResult()), true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 1. 上报自定义监控数据
        uploadCustomData();
        // 2. 查询自定义监控的监控项
        queryCustomMetricList();
        // 3. 创建自定义监控报警
        AlarmSdkSample.createAlarm(
            "your_alarm_name",
            "acs_customMetric_" + userId,   // 自定义监控的project值为acs_customMetric_父账号id
            "your_customized_metric_name",
            "[{\"groupId\":\"your_group_id\",\"dimension\":\"key=value\"}]",    //dimension的值为用户定义的k-v信息, 格式为key1=value1&key2=value2，并且按K字典序升序排列组合的值
            "Average",
            ">=",
            "100");
        // 4. 查询自定义监控数据
        QueryMetricSdkSample.queryMetricList(
            "acs_customMetric_" + userId, // 自定义监控的project值为acs_customMetric_父账号id
            "your_metric_name",
            "[{\"groupId\":\"your_group_id\",\"dimension\":\"your_dimension_key=your_dimension_value&your_dimension_key2=your_dimension_value\"}]");    //dimension的值为用户定义的k-v信息, 格式为key1=value1&key2=value2，并且按K字典序升序排列组合的值
        // 5. 删除自定义监控项
        deleteCustomMetric();
    }


}
