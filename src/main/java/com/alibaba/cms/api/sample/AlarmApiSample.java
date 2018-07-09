package com.alibaba.cms.api.sample;

import com.alibaba.cms.common.util.HttpClientUtils;
import com.alibaba.cms.common.util.SignatureUtils;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author he.dong
 * @date 2018/5/28
 */
public class AlarmApiSample {
    private static final Logger logger = LoggerFactory.getLogger(AlarmApiSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String endpoint = "http://metrics.aliyuncs.com/";

    /**
     * 创建报警规则,<br/>
     * 为了简化调用，只暴露了部分参数可配置。<br/>
     * 具体参数设置见 https://help.aliyun.com/document_detail/28619.html?spm=a2c4g.11186623.6.677.7FtWb7
     */
    public static String createAlarm(String alarmName, String namespace, String metricName, String dimensions,
                                   String statistics, String comparisonOperator, String threshold) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "CreateAlarm");
        //必选
        params.put("Name", alarmName);
        //必选，Namespace
        params.put("Namespace", namespace);
        //必选
        params.put("MetricName", metricName);
        //必选, 报警规则对应实例列表，为json array对应的string，例如[{"instanceId":"name1"},{"instanceId":"name2"}]
        params.put("Dimensions", dimensions);
        //必选，设置联系组，必须在控制台上已创建, 为json array对应的string，例如 ["联系组1","联系组2"]
        params.put("ContactGroups", "[\"your_contact_group\"]");
        //必选，设置统计方法，必须与定义的metric一致，例如Average
        params.put("Statistics", statistics);
        //可选，设置查询周期，单位为s,只能设置成：60, 300, 900,60*N
        params.put("Period", "60");
        //必选，设置报警比较符，只能为以下几种<=,<,>,>=,==,!=
        params.put("ComparisonOperator", comparisonOperator);
        //必选，设置报警阈值,目前只开放数值类型功能
        params.put("Threshold", threshold);
        //可选，设置连续探测几次都满足阈值条件时报警，默认3次
        params.put("EvaluationCount", "3");
        //可选，报警生效时间的开始时间，默认0，代表0点
        params.put("StartTime", "1");
        //可选，报警生效时间的结束时间，默认24，代表24点
        params.put("EndTime", "22");
        //可选，通道沉默周期,默认86400，单位s，只能选5min，10min，15min，30min，60min，3h，6h，12h，24h
        params.put("SilenceTime", "300");
        //可选，为0是旺旺+邮件，为1是旺旺+邮件+短信
        params.put("NotifyType", "0");
        //可选，回调url.
        params.put("Webhook", "{\"url\":\"https://www.abc.com/xxx/\",\"method\":\"get\",\"params\":{}}");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        String response = HttpClientUtils.get(endpoint, params);

        return JSON.parseObject(response).getString("Data");
    }

    /**
     * 修改报警规则<br/>
     * 具体参数设置见 https://help.aliyun.com/document_detail/28619.html?spm=a2c4g.11186623.6.677.7FtWb7
     */
    public static void updateAlarm(String alarmId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "UpdateAlarm");
        //必选
        params.put("Id", alarmId);
        //可选，报警规则名称
        params.put("Name", "alarm-name-update");
        //可选，设置联系组，为json array对应的string
        params.put("ContactGroups", "[\"your_contact_group\"]");
        //可选，设置统计方式
        params.put("Statistics", "Minimum");
        //可选，设置查询周期，单位为s,只能设置成：60, 300, 900,60*N
        params.put("Period", "60");
        //可选，设置比较符
        params.put("ComparisonOperator", ">=");
        //可选，设置报警阈值
        params.put("Threshold", "20");
        //可选，设置连续探测几次都满足阈值条件时报警，默认3次
        params.put("EvaluationCount", "3");
        //可选，报警生效时间的开始时间，默认0，代表0点
        params.put("StartTime", "5");
        //可选，报警生效时间的结束时间，默认24，代表24点
        params.put("EndTime", "22");
        //可选，通道沉默周期,默认86400，单位s，只能选5min，10min，15min，30min，60min，3h，6h，12h，24h
        params.put("SilenceTime", "86400");
        //可选，为0是旺旺+邮件，为1是旺旺+邮件+短信
        params.put("NotifyType", "1");
        //可选，回调url.
        params.put("Webhook", "{\"url\":\"https://www.abc.com/xxx/\",\"method\":\"get\",\"params\":{}}");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 禁用报警规则。报警规则停止后，将停止探测关联实例的监控项数据
     */
    public static void disableAlarm(String alarmId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DisableAlarm");

        params.put("Id", alarmId);

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 启动报警规则，当您的报警规则处于停止状态时，可以使用此接口启用报警规则
     */
    public static void enableAlarm(String alarmId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "EnableAlarm");

        params.put("Id", alarmId);

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 获取报警规则列表
     * */
    public static void listAlarm(String alarmId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ListAlarm");
        //所有参数均为可选参数，根据输入的参数进行结果过滤

        //报警规则的id
        params.put("Id", alarmId);
        //报警规则名称，支持模糊查询
        params.put("Name", "your_alarm_name");
        // 对应产品的project名称 https://help.aliyun.com/document_detail/28619.html?spm=a2c4g.11186623.6.677.7FtWb7#h2--ecs-2
        params.put("Namespace", "acs_ecs_dashboard");
        //规则关联的实例信息，为json object对应的字符串，可以查询到关联该实例的所有规则，应用该字段时必须指定namespace.例如{"instanceId":"name1"}
        params.put("Dimension", "{\"instanceId\":\"your_instance_id\"}");
        //报警规则状态：ALARM(报警)、INSUFFICIENT_DATA（数据不足）、OK（正常）
        params.put("State", "OK");
        //true为启用，false为禁用
        params.put("IsEnable", "true");
        params.put("PageNumber", "1");
        params.put("PageSize", "5");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    /**
     * 删除已创建的报警规则
     */
    public static void deleteAlarm(String alarmId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DeleteAlarm");

        params.put("Id", alarmId);

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    /**
     * 查询联系人组
     */
    public static void listContactGroup() {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ListContactGroup");

        params.put("PageNumber", "1");
        params.put("PageSize", "10");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 查询联系人组
     */
    public static void getContacts() {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "GetContacts");
        params.put("GroupName", "your_contact_group_name");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 查询联系人组
     */
    public static void describeContact() {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DescribeContact");
        params.put("ContactName", "your_contact_name");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 获取报警历史
     * 可根据需要输入对应字段做过滤
     */
    public static void describeAlarmHistory(String alarmId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DescribeAlarmHistory");
        //所有参数均为可选参数，根据输入的参数进行结果过滤


        // 应用分组ID
        //params.put("GroupId", "group_id");
        // 创建报警规则时云监控自动产生的唯一标识
        params.put("AlertName", alarmId);
        // 创建报警规则时用户定义的规则名称
        //params.put("RuleName", "");
        // 报警规则监控的产品
        //params.put("Namespace", "");
        // 报警规则监控的指标
        //params.put("MetricName", "");
        //与EndTime最多间隔最长三天，可查询一年之内的数据，目前仅支持timestamp格式的时间
        //params.put("StartTime", "1527055096000");
        //params.put("EndTime", "1527057096000");
        // 查询结果是否只返回结果条数，默认是false
        //params.put("OnlyCount", "true");
        // 查询结果是否正序返回，默认是false
        //params.put("Ascending", "false");
        // 报警状态,OK是恢复，ALARM是报警
        //params.put("State", "ALARM");
        // 报警通知发送状态，0为已通知用户，1为不在生效期未通知，2为处于报警沉默期未通知
        //params.put("Status", "0");
        params.put("PageSize", "10");
        params.put("Page", "1");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 获取报警规则列表
     *
     * @param applyMode  GROUP_INSTANCE_FIRST or ALARM_TEMPLATE_FIRST
     * @param templateIds separate by comma: 1234,3456
     * */
    public static void applyTemplate(String groupId, String templateIds, String applyMode) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ApplyTemplate");
        // 应用分组Id
        params.put("GroupId", groupId);
        // 报警模板Id：多个英文逗号分隔
        params.put("TemplateIds", templateIds);
        // GROUP_INSTANCE_FIRST：分组实例优先模式，应用报警模板的时候，以分组实例优先，如果分组中不存在这种实例则忽略模板中的规则；ALARM_TEMPLATE_FIRST：模板实例优先模式，应用报警模板的时候，不管分组中是否存在这种实例，都创建出这种规则
        params.put("ApplyMode", applyMode);
        // Warning (手机+邮箱+旺旺+钉钉机器人)--对应level=3；Info(邮箱+旺旺+钉钉机器人)--对应level=4
        params.put("NotifyLevel", "3");
        // 开始生效时间，值为0-23之间的整数，默认为0
        params.put("EnableStartTime", "0");
        // 结束生效时间，值为0-23之间的整数，默认为23
        params.put("EnableEndTime", "23");
        // 通道沉默时间，单位为秒，默认为86400，即24小时
        params.put("SilenceTime", "86400");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    public static void main(String[] args) {
        // 1. 查看联系人祖
        listContactGroup();
        getContacts();
        describeContact();
        // 2. 创建报警
        String alarmId = createAlarm("your_alarm_name",
            "acs_ecs_dashboard",
            "CPUUtilization",
            "[{\"instanceId\":\"your_instance_id\"}]",
            "Average",
            ">=",
            "10");
        // 3. 修改报警
        updateAlarm(alarmId);
        // 4. 查看报警
        listAlarm(alarmId);
        // 5. 禁用报警
        disableAlarm(alarmId);
        // 6. 启用报警
        enableAlarm(alarmId);
        // 7. 查看报警历史
        describeAlarmHistory(alarmId);
        // 8. 删除报警
        deleteAlarm(alarmId);
        // 9. 应用报警模板
        applyTemplate("<your_group_id>","<your_template_ids>","ALARM_TEMPLATE_FIRST");
    }

}
