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
 * QueryMetric系列接口用来查询具体的监控数据
 */
public class QueryMetricApiSample {
    private static final Logger logger = LoggerFactory.getLogger(QueryMetricApiSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String userId = "<userId>";
    private static String endpoint = "http://metrics.aliyuncs.com/";


    /**
     * 查询指定产品实例的监控的数据
     * */
    public static void queryMetricList(String project, String metric, String dimensions) {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "QueryMetricList");
        // 对应产品的project名称。 Project & Metric参见https://help.aliyun.com/document_detail/28619.html?spm=a2c4g.11186623.6.677.7FtWb7#h2--ecs-2
        params.put("Project", project);
        // 监控项名称
        params.put("Metric", metric);
        // 用于过滤监控数据的key-value集合，key可以使用注册监控项时申明的dimensionKeys中的一个或多个，value为该key对应的值。instanceId是必填项。dimension要求必须按顺序传入。可以是JSON Array格式，也可以是JSON Object格式
        params.put("Dimensions", dimensions);
        // 可以传入距离1970年1月1日0点的毫秒数，也可以传入format数据，如2015-10-20 00:00:00
        params.put("StartTime", "2018-05-27 00:00:00");
        params.put("EndTime", "2018-05-29 23:59:59");
        // 时间间隔，统一用秒数来计算，例如 60, 300, 900。 如果不填写,则按照注册监控项时申明的上报周期来查询原始数据。如果填写统计周期，则查询对应的统计数据 。
        params.put("Period", "60");
        params.put("Length", "5");
        // 当返回的数据点大于一页的时候，会返回cursor值，可以传入该值继续查询，直到没有cursor返回表示所有数据都已经返回
        //params.put("Cursor", "");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    /**
     * 查询指定产品实例的最新的监控数据， 和queryMetricList的参数一致，但是只返回给定时间段内的最新的数据
     * */
    public static void queryMetricLast(String project, String metric, String dimensions) {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "QueryMetricLast");
        // 对应产品的project名称。 Project & Metric参见https://help.aliyun.com/document_detail/28619.html?spm=a2c4g.11186623.6.677.7FtWb7#h2--ecs-2
        params.put("Project", project);
        // 监控项名称
        params.put("Metric", metric);
        // 用于过滤监控数据的key-value集合，key可以使用注册监控项时申明的dimensionKeys中的一个或多个，value为该key对应的值。instanceId是必填项。dimension要求必须按顺序传入。可以是JSON Array格式，也可以是JSON Object格式
        params.put("Dimensions", dimensions);
        // 如果不指定时间段，返回最新的数据, 可以传入距离1970年1月1日0点的毫秒数，也可以传入format数据，如2015-10-20 00:00:00
        //params.put("StartTime", "2018-05-27 00:00:00");
        //params.put("EndTime", "2018-05-29 23:59:59");
        // 时间间隔，统一用秒数来计算，例如 60, 300, 900。 如果不填写,则按照注册监控项时申明的上报周期来查询原始数据。如果填写统计周期，则查询对应的统计数据 。
        params.put("Period", "60");
        params.put("Length", "5");
        // 当返回的数据点大于一页的时候，会返回cursor值，可以传入该值继续查询，直到没有cursor返回表示所有数据都已经返回
        //params.put("Cursor", "");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    public static void main(String[] args) {
        // queryMetricLast的调用参数与queryMetricList一致，只是返回最新的数据，故只列出queryMetricList的调用。

        // 查询指定ECS的CPU使用率
        queryMetricList("acs_ecs_dashboard", "CPUUtilization",
            "[{\"instanceId\":\"instance_A_id\"},{\"instanceId\":\"instance_B_id\"}]");

        // 查询指定站点监控任务的可用性数据
        queryMetricList("acs_networkmonitor", "Availability",
            "[{\"taskId\":\"task_A_id\"}]");

        // 查询指定站点监控任务的平均响应时间
        queryMetricList("acs_networkmonitor", "ResponseTime",
            "[{\"taskId\":\"task_B_id\"}]");

        // 查询指定自定义监控的监控数据
            // 自定义监控的project值为acs_customMetric_父账号id
            // 自定义监控的metric值为用户自定义的上报metric值
            // 自定义监控的dimensions值为用户自定义的上报dimensions值, dimension的格式为key1=value1&key2=value2，并且按k进行字典升序进行组合的值
        queryMetricList("acs_customMetric_" + userId, "your_metric",
            "[{\"groupId\":\"your_group_id\",\"dimension\":\"key=value\"}]");

        // 查询指定ecs的进程监控关键字的监控信息
        queryMetricList("acs_ecs_dashboard","vm.Process.number","[{\"instanceId\":\"instance_A_id\",\"processName\":\"java\"},{\"instanceId\":\"instance_A_id\",\"processName\":\"log\"}]");

        // 查询指定ecs的全部进程总数监控信息
        queryMetricList("acs_ecs_dashboard","vm.ProcessCount","[{\"instanceId\":\"instance_A_id\"}]");

        // 此接口不支持查询事件监控的数据，请使用事件监控的查询api来查询
    }
}
