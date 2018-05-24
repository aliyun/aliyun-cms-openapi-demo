package com.alibaba.cms.api.sample;

import com.alibaba.cms.common.util.HttpClientUtils;
import com.alibaba.cms.common.util.SignatureUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiteMonitorApiSample {
    private static final Logger logger = LoggerFactory.getLogger(SiteMonitorApiSample.class);

    private static String endpoint = "http://metrics.aliyuncs.com/";
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";

    private static String cityHangzhou = "杭州市";
    private static String cityBeijing = "北京市";
    private static String ispAli = "阿里巴巴";
    private static String ispChinaTelecom = "电信";
    private static String ispChinaUnion = "联通";
    private static String ispChinaMobile = "移动";

    public static void main(String[] args) {
        //1、获取探针信息
        Map<String, List<String>> ispCityMap = new HashMap<>();
        List<String> ispAliCity = new ArrayList<>();
        ispAliCity.add(cityBeijing);
        ispAliCity.add(cityHangzhou);
        ispCityMap.put(ispAli, ispAliCity);
        List<String> ispChinaTelecomCity = new ArrayList<>();
        ispChinaTelecomCity.add(cityBeijing);
        ispCityMap.put(ispChinaTelecom, ispChinaTelecomCity);

        String ispCityMsg = getIspAreaCity(ispCityMap);

        //2、新建站点监控
        String taskId = createTask(ispCityMsg);

        //3、获取站点监控详细信息
        String taskDetail = getTaskDetail(taskId);

        //4、新建站点监控的报警规则
        List<String> contactGroups = new ArrayList<>();
        contactGroups.add("your_contact_group_name");
        //可用性报警监控
        String metricName = "Availability";
        String statistics = "Availability";
        String availabilityAlarm = createAlarm(taskId, metricName, ">=", "50", statistics, contactGroups);
        //响应时间报警监控
        metricName = "ResponseTime";
        statistics = "Average";
        String responseTimeAlarm = createAlarm(taskId, metricName, "<", "500", statistics, contactGroups);

        //5、获取对应报警规则的报警历史
        getAlarmHistory(availabilityAlarm);
        getAlarmHistory(responseTimeAlarm);

        //6、停止探测任务
        stopTask(taskId);
        //7、启动探测任务
        startTask(taskId);
        //8、修改站点监控探测任务
        //modifyTask(taskId);
        //9、删除站点监控探测任务
        deleteTask(taskId);
        //10、获取站点监控的任务列表
        getTasks();
        //11、获取站点监控的数据信息
        queryMetricList();
        //12、获取站点监控的当前最新的数据信息
        queryMetricLast();
    }

    /**
     * 获取探针列表
     */
    public static String getIspAreaCity(Map<String, List<String>> ispCityMap) {
        StringBuilder stringBuilder = new StringBuilder();
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DescribeISPAreaCity");
        // 不输入其他参数获得全量的探测点信息，或者也可以输入isp/ispCode或city/cityCode来获得具体的探测点信息（值必须完全匹配）
        //params.put("Isp","电信");
        //params.put("City","北京市");
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);

        String responseStr = HttpClientUtils.get(endpoint, params);
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        String dataStr = jsonObject.getString("Data");
        JSONArray dataArr = JSONArray.parseArray(dataStr);
        for (int i = 0; i < dataArr.size(); i++) {
            JSONObject item = dataArr.getJSONObject(i);
            String isp = item.getString("isp");
            String city = item.getString("city");
            if (ispCityMap.containsKey(isp) && ispCityMap.get(isp).contains(city)) {
                stringBuilder.append(String
                    .format(",{\"city\":\"%s\",\"isp\":\"%s\"}", item.getString("cityCode"),
                        item.getString("ispCode")));
                ispCityMap.get(isp).remove(city);
            }
        }
        stringBuilder.deleteCharAt(0).insert(0, "[");
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    /**
     * 创建站点监控，返回站点监控任务id
     *
     */
    public static String createTask(String ispCities) {
        String httpMethod = "GET";
        String taskName = "your_site_monitor_task_name";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "CreateTask");
        params.put("Address", "http://www.your-site.com");
        params.put("TaskName", taskName);
        //TaskType:1 代表http
        params.put("TaskType", "1");
        //设置监测频率为5min
        params.put("Interval", "5");
        //设置探针
        params.put("IspCity", ispCities);
        //设置httpMethod、header、cookie等高级设置
        //match_rule=0 表示包含匹配内容则报警， match_rule=1表示不包含匹配内容则报警
        //response_content: 指定匹配内容来检查响应内容是否正确，为空则不做匹配检查。匹配内容仅支持英文
        params.put("Options",
            "{\"http_method\":\"post\",\"header\":\"key1:value1\nkey2:value2\",\"cookie\":\"key1=value1;key2=value2\","
                + "\"username\":\"123\",\"password\":\"123\",\"time_out\":30000,\"request_content\":\"abc\","
                + "\"match_rule\":0,\"response_content\":\"onlyEnglish\"}");
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);

        String responseStr = HttpClientUtils.get(endpoint, params);
        JSONObject dataJsonObj = JSONObject.parseObject(responseStr).getJSONObject("Data");
        return dataJsonObj.getString(taskName);
    }

    /**
     * 新建站点监控的报警规则
     */
    public static String createAlarm(String taskId, String metricName, String comparisonOperator,
                                     String threshold, String statistics, List<String> contactGroups) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "CreateAlarm");
        //必选，可用性报警规则名称
        params.put("Name", "your_alarm_name");
        //必选，Namespace:acs_networkmonitor，代表站点监控
        params.put("Namespace", "acs_networkmonitor");
        //必选，监控项名称，例如Availability，代表可用性
        params.put("MetricName", metricName);
        //必选，报警规则对应实例列表,为json array对应的string
        params.put("Dimensions", String.format("[{\"taskId\":\"%s\"}]", taskId));
        //可选，设置查询周期，单位为s,只能设置成：60, 300, 900
        params.put("Period", "60");
        //必选，设置统计方式
        params.put("Statistics", statistics);
        //必选，设置比较符
        params.put("ComparisonOperator", comparisonOperator);
        //必选，设置报警阈值
        params.put("Threshold", threshold);
        //可选，设置连续探测几次都满足阈值条件时报警，默认3次
        params.put("EvaluationCount", "2");
        //必选，设置联系组，为json array对应的string
        params.put("ContactGroups", JSONObject.toJSONString(contactGroups));
        //可选，报警生效时间的开始时间，默认0，代表0点
        params.put("StartTime", "1");
        //可选，报警生效时间的结束时间，默认24，代表24点
        params.put("EndTime", "22");
        //可选，通道沉默周期,默认86400，单位s，只能选5min，10min，15min，30min，60min，3h，6h，12h，24h
        params.put("SilenceTime", "300");
        //可选，为0是旺旺+邮件，为1是旺旺+邮件+短信
        params.put("NotifyType", "0");
        //可选，回调url.
        params.put("Webhook", "{\"url\":\"http://www.abcd.com/xxx/yyyy.html\",\"method\":\"get\",\"params\":{}}");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);

        String responseStr = HttpClientUtils.get(endpoint, params);
        String alarmRuleId = JSONObject.parseObject(responseStr).getString("Data");
        return alarmRuleId;
    }

    /**
     * 获取报警历史
     */
    public static String getAlarmHistory(String alarmRuleId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ListAlarmHistory");
        //可选，报警规则的id
        params.put("Id", alarmRuleId);
        //可选，每页记录数，默认值：100
        params.put("Size", "50");
        //可选，查询数据开始时间，默认24小时前，可以输入long型时间，也可以输入yyyy-MM-dd HH:mm:ss类型时间
        params.put("StartTime", "2018-05-15 00:00:00");
        //可选，查询数据结束时间，默认24小时前，可以输入long型时间，也可以输入yyyy-MM-dd HH:mm:ss类型时间
        params.put("EndTime", "2018-05-15 16:30:00");
        //可选，查询数据的起始位置，为空则按时间查询前100条
        params.put("Cursor", "2");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);

        String responseStr = HttpClientUtils.get(endpoint, params);
        return responseStr;
    }

    /**
     * 修改站点监控
     */
    public static void modifyTask(String taskId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ModifyTask");
        params.put("TaskId", taskId);
        params.put("Address", "http://www.your-site.com");
        params.put("TaskName", "your_task_name");
        //设置监测频率为1min
        params.put("Interval", "1");
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);

        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 创建站点监控，返回站点监控任务id
     */
    public static void deleteTask(String taskId) {
        List<String> taskIdList = new ArrayList<>();
        taskIdList.add(taskId);
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DeleteTasks");
        params.put("TaskIds", JSONObject.toJSONString(taskIdList));
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);

        HttpClientUtils.get(endpoint, params);
    }

    public static String getTaskDetail(String taskId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DescribeTaskDetail");
        params.put("TaskId", taskId);
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        String responseStr = HttpClientUtils.get(endpoint, params);

        return responseStr;
    }

    public static void stopTask(String taskId) {
        List<String> taskIdList = new ArrayList<>();
        taskIdList.add(taskId);
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "StopTasks");
        params.put("TaskIds", JSONObject.toJSONString(taskIdList));
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    public static void startTask(String taskId) {
        List<String> taskIdList = new ArrayList<>();
        taskIdList.add(taskId);
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "StartTasks");
        params.put("TaskIds", JSONObject.toJSONString(taskIdList));
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);

        HttpClientUtils.get(endpoint, params);
    }

    public static String getTasks() {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DescribeTasks");
        //获取第几页的任务
        params.put("Page", "1");
        //每页最多多少个任务
        params.put("PageSize", "20");
        //筛选监控任务名称或地址内容
        params.put("Keyword", "your_key_word");
        //筛选任务类型，1：http
        params.put("TaskType", "1");
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        String responseStr = HttpClientUtils.get(endpoint, params);

        return responseStr;
    }

    /**
     * 查询站点监控的监控数据
     * */
    public static void queryMetricList() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "QueryMetricList");
        // 对应产品的project名称，站点监控为
        params.put("Project", "acs_networkmonitor");
        // 监控项名称: Availability:可用性/ResponseTime:平均响应时间
        params.put("Metric", "Availability");
        // Dimensions, json array 字符串格式，指定想要查询的taskId
        params.put("Dimensions", "[{\"taskId\":\"35c59cfd-dbaf-405a-93ba-b76239fb24d3\"}]");
        params.put("StartTime", "2018-05-23 14:00:00");
        params.put("EndTime", "2018-05-23 23:59:59");
        // 时间间隔，统一用秒数来计算，例如 60, 300, 900。 如果不填写,则按照注册监控项时申明的上报周期来查询原始数据。如果填写统计周期，则查询对应的统计数据 。
        params.put("Period", "60");
        params.put("Length", "5");
        // 当返回的数据点大于一页的时候，会返回cursor值，可以传入该值继续查询，直到没有cursor返回表示所有数据都已经返回
        //params.put("Cursor", "");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 查询站点监控的最新的监控数据， 和queryMetricList的参数一致，但是只返回给定时间段内的最新的数据
     * */
    public static void queryMetricLast() {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "QueryMetricLast");
        // 对应产品的project名称，站点监控为
        params.put("Project", "acs_networkmonitor");
        // 监控项名称: Availability:可用性/ResponseTime:平均响应时间
        params.put("Metric", "Availability");
        // Dimensions, json array 字符串格式，指定想要查询的taskId
        params.put("Dimensions", "[{\"taskId\":\"06760da9-f1e7-471f-b828-214a2c1b95f8\"}]");
        // 如果不指定时间段，返回最新的数据
        //params.put("StartTime", "2018-05-23 14:00:00");
        //params.put("EndTime", "2018-05-23 23:59:59");
        // 时间间隔，统一用秒数来计算，例如 60, 300, 900。 如果不填写,则按照注册监控项时申明的上报周期来查询原始数据。如果填写统计周期，则查询对应的统计数据 。
        params.put("Period", "60");
        params.put("Length", "5");
        // 当返回的数据点大于一页的时候，会返回cursor值，可以传入该值继续查询，直到没有cursor返回表示所有数据都已经返回
        //params.put("Cursor", "");
        params = SignatureUtils.appendPublicParams(params, "GET", accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

}
