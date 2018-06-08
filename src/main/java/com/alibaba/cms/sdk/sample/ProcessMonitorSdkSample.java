package com.alibaba.cms.sdk.sample;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.cms.model.v20180308.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author he.dong
 * @date 2018/5/29
 *
 */
public class ProcessMonitorSdkSample {
    private static final Logger logger = LoggerFactory.getLogger(ProcessMonitorSdkSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static final String REGION_ID_BEIJING = "cn-beijing";


    /**
     * 添加进程监控关键字
     * */
    public static void nodeProcessCreate() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        NodeProcessCreateRequest request = new NodeProcessCreateRequest();
        request.setAcceptFormat(FormatType.JSON);

        // ECS实例id
        request.setInstanceId("instance_A_id");
        // 要监控的关键字,这个关键字可以是进程名称，也可以是启动的参数等
        request.setName("java");
        // 进程名称, 对应ps命令中的comm field （only the executable name）
        request.setProcessName("java");
        // 进程所有者
        request.setProcessUser("root");
        // 命令：number返回匹配条件的进程数，只支持number。
        request.setCommand("number");

        try {
            logger.info("sending NodeProcessCreateRequest...");
            NodeProcessCreateResponse response = client.getAcsResponse(request);
            logger.info("NodeProcessCreateResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }


    /**
     * 删除进程监控关键字
     * */
    public static void nodeProcessDelete() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        NodeProcessDeleteRequest request = new NodeProcessDeleteRequest();
        request.setAcceptFormat(FormatType.JSON);

        // 必选：ECS实例id, name和id最少输入一个
        request.setInstanceId("instance_A_id");
        // 进程监控的id，可通过nodeProcesses接口查询，如果指定了id字段，会忽略name字段输入的值
        request.setId("12345");
        // 创建进程时输入的Name值
        request.setName("java");

        try {
            logger.info("sending NodeProcessDeleteRequest...");
            NodeProcessDeleteResponse response = client.getAcsResponse(request);
            logger.info("NodeProcessDeleteResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 查询指定ECS的进程监控关键字列表
     * */
    public static void nodeProcesses() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        NodeProcessesRequest request = new NodeProcessesRequest();
        request.setAcceptFormat(FormatType.JSON);

        // ECS实例id
        request.setInstanceId("instance_A_id");

        try {
            logger.info("sending NodeProcessesRequest...");
            NodeProcessesResponse response = client.getAcsResponse(request);
            logger.info("NodeProcessesResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }


    public static void main(String[] args) {
        // 1. 给指定ecs创建进程关键字
        nodeProcessCreate();
        // 2. 查询指定ecs的进程监控关键字列表
        nodeProcesses();
        // 3. 查询指定ecs的进程监控关键字的监控信息
        QueryMetricSdkSample.queryMetricList("acs_ecs_dashboard","vm.Process.number","[{\"instanceId\":\"instance_A_id\",\"processName\":\"java\"},{\"instanceId\":\"instance_A_id\",\"processName\":\"log\"}]");
        // 4. 查询指定ecs的全部进程总数监控信息
        QueryMetricSdkSample.queryMetricList("acs_ecs_dashboard","vm.ProcessCount","[{\"instanceId\":\"instance_A_id\"}]");

        // 5. 创建进程监控报警规则: 只针对某个指定的进程
        String specifiedProcessAlarmId = AlarmSdkSample.createAlarm("your_alarm_name", "acs_ecs_dashboard", "vm.Process.number",
            "[{\"instanceId\":\"instance_A_id\",\"processName\":\"java\"},"
                + "{\"instanceId\":\"instance_A_id\",\"processName\":\"log\"}]",
            "Average", ">", "10");

        // 6. 创建进程监控报警规则: 针对进程总数
        String totalProcessAlarmId = AlarmSdkSample.createAlarm("your_alarm_name", "acs_ecs_dashboard", "vm.ProcessCount",
            "[{\"instanceId\":\"instance_A_id\"}]", "Average", ">", "10");

        // 7. 查询报警历史
        AlarmSdkSample.describeAlarmHistory(specifiedProcessAlarmId);
        AlarmSdkSample.describeAlarmHistory(totalProcessAlarmId);

        // 8. 删除指定ecs的进程监控关键字
        nodeProcessDelete();

    }
}
