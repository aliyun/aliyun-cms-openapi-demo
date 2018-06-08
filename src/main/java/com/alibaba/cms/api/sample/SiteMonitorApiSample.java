package com.alibaba.cms.api.sample;

import com.alibaba.cms.common.util.HttpClientUtils;
import com.alibaba.cms.common.util.SignatureUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.StringUtils;
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
        JSONObject createTaskResponse = createTask(ispCityMsg);
        String taskId = getTaskIdFromCreateTaskResponse(createTaskResponse);

        //3、获取站点监控详细信息
        getTaskDetail(taskId);

        //4、获取对应报警规则的报警历史
        //可用性报警监控
        String availabilityAlarmId = getAlarmIdFromCreateTaskResponse(createTaskResponse, "Availability");
        AlarmApiSample.describeAlarmHistory(availabilityAlarmId);
        //响应时间报警监控
        String responseTimeAlarmId = getAlarmIdFromCreateTaskResponse(createTaskResponse, "ResponseTime");
        AlarmApiSample.describeAlarmHistory(responseTimeAlarmId);
        //5、停止探测任务
        stopTask(taskId);
        //6、启动探测任务
        startTask(taskId);
        //7、修改站点监控探测任务
        modifyTask(taskId);
        //8、获取站点监控的任务列表
        getTasks();
        //9、获取站点监控的数据信息
        QueryMetricApiSample.queryMetricList(
            "acs_networkmonitor",   // 站点监控对应的project
            "Availability",         // 监控项名称: Availability:可用性/ResponseTime:平均响应时间
            "[{\"taskId\":\"task_A_id\"}]");  // Dimensions, json array 字符串格式，指定想要查询的taskId
        //10、获取站点监控的当前最新的数据信息
        QueryMetricApiSample.queryMetricLast(
            "acs_networkmonitor",   // 站点监控对应的project
            "Availability",         // 监控项名称: Availability:可用性/ResponseTime:平均响应时间
            "[{\"taskId\":\"task_A_id\"}]");  // Dimensions, json array 字符串格式，指定想要查询的taskId
        //11、删除站点监控探测任务
        deleteTask(taskId);
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
     */
    public static JSONObject createTask(String ispCities) {
        String httpMethod = "GET";
        String taskName = "your_site_monitor_task_name";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "CreateTask");
        params.put("Address", "http://www.your-site.com");
        params.put("TaskName", taskName);
        //TaskType:1 代表http
        params.put("TaskType", "1");
        //设置监测频率为5min, 必选
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
        //创建任务的时候同时设置报警规则(json Array 字符串格式)：alarmAction:报警联系组, metricName:  Availability(可用率), ResponseTime(响应时间), expression对应报警条件
        params.put("AlertRule", "["
            + "  {"
            + "    \"alarmActions\": ["
            + "      \"he_group\""          // 报警联系组
            + "    ],"
            + "    \"metricName\": \"Availability\","
            + "    \"expression\": \"$Availability<96\""    // 单位为%
            + "  },"
            + "  {"
            + "    \"alarmActions\": ["
            + "      \"he_group\""
            + "    ],"
            + "    \"metricName\": \"ResponseTime\","
            + "    \"expression\": \"$Average>5200\""      // 单位为ms
            + "  }"
            + "]");
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);

        String responseStr = HttpClientUtils.get(endpoint, params);
        return JSON.parseObject(responseStr);
    }


    /**
     * 创建站点监控，返回站点监控任务id
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
        //设置探针
        params.put("IspCity", "[{\"city\":\"546\",\"isp\":\"465\"},{\"city\":\"572\",\"isp\":\"465\"},"
            + "{\"city\":\"738\",\"isp\":\"465\"}]");
        //设置httpMethod、header、cookie等高级设置
        //match_rule=0 表示包含匹配内容则报警， match_rule=1表示不包含匹配内容则报警
        //response_content: 指定匹配内容来检查响应内容是否正确，为空则不做匹配检查。匹配内容仅支持英文
        params.put("Options",
            "{\"http_method\":\"post\",\"header\":\"key1:value1key2:value2\",\"cookie\":\"key1=value1;key2=value2\","
                + "\"username\":\"123\",\"password\":\"123\",\"time_out\":30000,\"request_content\":\"abc\","
                + "\"match_rule\":0,\"response_content\":\"abc\"}");
        //创建任务的时候同时设置报警规则(json Array 字符串格式)：alarmAction:报警联系组, metricName:  Availability(可用率), ResponseTime(响应时间), expression对应报警条件
        params.put("AlertRule", "["
            + "  {"
            + "    \"alarmActions\": ["
            + "      \"your_contact_group\""          // 报警联系组
            + "    ],"
            + "    \"metricName\": \"Availability\","
            + "    \"expression\": \"$Availability<96\""    // 单位为%
            + "  },"
            + "  {"
            + "    \"alarmActions\": ["
            + "      \"your_contact_group\""
            + "    ],"
            + "    \"metricName\": \"ResponseTime\","
            + "    \"expression\": \"$Average>5200\""      // 单位为ms
            + "  }"
            + "]");
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
        // 是否同时删除关联的报警规则, 1表示同时删除，0表示不删除
        params.put("IsDeleteAlarms", "1");
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

        return HttpClientUtils.get(endpoint, params);
    }


    private static String getTaskIdFromCreateTaskResponse(JSONObject response) {
        JSONObject obj = response.getJSONObject("Data");
        if (obj.keySet().size() == 1) {
            for (String taskName : obj.keySet()) {
                return obj.getString(taskName);
            }
        }
        return null;
    }

    private static String getAlarmIdFromCreateTaskResponse(JSONObject response, String metricName) {
        JSONArray rules = response.getJSONArray("AlertRule");

        for (int i = 0; i < rules.size(); i++) {
            JSONObject obj = rules.getJSONObject(i);
            if (obj.getString("metricName").equalsIgnoreCase(metricName)) {
                return obj.getString("name");
            }
        }
        return null;
    }


}
