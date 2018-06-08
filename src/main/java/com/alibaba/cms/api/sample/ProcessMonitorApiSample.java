package com.alibaba.cms.api.sample;

import com.alibaba.cms.common.util.HttpClientUtils;
import com.alibaba.cms.common.util.SignatureUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author he.dong
 * @date 2018/5/29
 *
 */
public class ProcessMonitorApiSample {
    private static final Logger logger = LoggerFactory.getLogger(ProcessMonitorApiSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String endpoint = "http://metrics.aliyuncs.com/";


    /**
     * 添加进程监控关键字
     * */
    public static void nodeProcessCreate() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "NodeProcessCreate");
        // ECS实例id
        params.put("InstanceId", "instance_A_id");
        // 要监控的关键字,这个关键字可以是进程名称，也可以是启动的参数等
        params.put("Name", "java");
        // 进程名称, 对应ps命令中的comm field （only the executable name）
        params.put("ProcessName", "java");
        // 进程所有者
        params.put("ProcessUser", "");
        // 命令：number返回匹配条件的进程数，只支持number。
        params.put("Command", "number");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    /**
     * 删除进程监控关键字
     * */
    public static void nodeProcessDelete() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "NodeProcessDelete");
        // 必选：ECS实例id, name和id最少输入一个
        params.put("InstanceId", "instance_A_id");
        // 进程监控的id，可通过nodeProcesses接口查询，如果指定了id字段，会忽略name字段输入的值
        params.put("Id", "12345");
        // 创建进程时输入的Name值
        params.put("Name", "java");

        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 查询指定ECS的进程监控关键字列表
     * */
    public static void nodeProcesses() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "NodeProcesses");
        // ECS实例id
        params.put("InstanceId", "instance_A_id");

        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    public static void main(String[] args) {
        // 1. 给指定ecs创建进程关键字
        nodeProcessCreate();
        // 2. 查询指定ecs的进程监控关键字列表
        nodeProcesses();
        // 3. 查询指定ecs的进程监控关键字的监控信息
        QueryMetricApiSample.queryMetricList("acs_ecs_dashboard","vm.Process.number","[{\"instanceId\":\"instance_A_id\",\"processName\":\"java\"},{\"instanceId\":\"instance_A_id\",\"processName\":\"log\"}]");
        // 4. 查询指定ecs的全部进程总数监控信息
        QueryMetricApiSample.queryMetricList("acs_ecs_dashboard","vm.ProcessCount","[{\"instanceId\":\"instance_A_id\"}]");
        // 5. 创建进程监控报警规则: 只针对某个指定的进程
        String specifiedProcessAlarmId = AlarmApiSample.createAlarm(
            "your_alarm_name",
            "acs_ecs_dashboard",
            "vm.Process.number",
            "[{\"instanceId\":\"instance_A_id\",\"processName\":\"java\"},{\"instanceId\":\"instance_A_id\",\"processName\":\"log\"}]",
            "Average",
            ">",
            "10");
        // 6. 创建进程监控报警规则: 针对进程总数
        String totalProcessAlarmId = AlarmApiSample.createAlarm(
            "your_alarm_name",
            "acs_ecs_dashboard",
            "vm.ProcessCount",
            "[{\"instanceId\":\"instance_A_id\"}]",
            "Average",
            ">",
            "10");
        // 7. 查询报警历史
        AlarmApiSample.describeAlarmHistory(specifiedProcessAlarmId);
        AlarmApiSample.describeAlarmHistory(totalProcessAlarmId);
        // 8. 删除指定ecs的进程监控关键字
        nodeProcessDelete();

    }
}
