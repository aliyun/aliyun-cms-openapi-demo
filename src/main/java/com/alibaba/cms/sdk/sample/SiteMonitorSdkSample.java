package com.alibaba.cms.sdk.sample;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.cms.model.v20180308.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SiteMonitorSdkSample {
    private static final Logger logger = LoggerFactory.getLogger(SiteMonitorSdkSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static final String REGION_ID_BEIJING = "cn-beijing";

    public static void main(String[] args) {

        //1、获取探针信息
        String ispCityMsg = getIspAreaCity();

        //2、新建站点监控
        CreateTaskResponse createTaskResponse = createTask(ispCityMsg);
        String taskId = getTaskIdFromCreateTaskResponse(createTaskResponse);
        //3、获取站点监控详细信息
        getTaskDetail(taskId);

        //4、获取对应报警规则的报警历史
        String availabilityAlarmId = getAlarmIdFromCreateTaskResponse(createTaskResponse, "Availability");
        String responseTimeAlarmId = getAlarmIdFromCreateTaskResponse(createTaskResponse, "ResponseTime");
        describeAlarmHistory();
        //5、停止探测任务
        stopTask(taskId);
        //6、启动探测任务
        startTask(taskId);
        //7、修改站点监控探测任务
        modifyTask(taskId);
        //8、删除站点监控探测任务
        deleteTask(taskId);
        //9、获取站点监控的任务列表
        getTasks();
        //10、获取站点监控的数据信息
        queryMetricList();
        //11、获取站点监控的当前最新的数据信息
        queryMetricLast();
    }

    /**
     * 获取探针列表
     */
    public static String getIspAreaCity() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        DescribeISPAreaCityRequest request = new DescribeISPAreaCityRequest();
        // 不输入其他参数获得全量的探测点信息，或者也可以输入isp/ispCode或city/cityCode来获得具体的探测点信息（值必须完全匹配）
        request.setCity("北京市");
        request.setIsp("阿里巴巴");

        try {
            logger.info("sending DescribeISPAreaCityResponse...");
            DescribeISPAreaCityResponse response = client.getAcsResponse(request);
            logger.info("QueryCustomMetricListResponse0:\n{}", JSON.toJSONString(response,true));
            logger.info("response.getData():\n{}", JSON.toJSONString(JSON.parseArray(response.getData()), true));

            JSONArray data = JSON.parseArray(response.getData());
            JSONArray array = new JSONArray();

            for (int i = 0; i < data.size(); i++) {
                JSONObject isp = new JSONObject();
                isp.put("city", data.getJSONObject(i).getString("cityCode"));
                isp.put("isp", data.getJSONObject(i).getString("ispCode"));
                array.add(isp);
            }
            return array.toJSONString();
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    /**
     * 创建站点监控，返回站点监控任务id
     *
     */
    public static CreateTaskResponse createTask(String ispCities) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CreateTaskRequest request = new CreateTaskRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setAddress("http://www.aliyun.com");
        request.setTaskName("your_task_name");
        //TaskType:1 代表http
        request.setTaskType("1");
        //设置监测频率为5min
        request.setInterval("5");
        //设置探针
        request.setIspCity(ispCities);

        //设置httpMethod、header、cookie等高级设置
        //match_rule=0 表示包含匹配内容则报警， match_rule=1表示不包含匹配内容则报警
        //response_content: 指定匹配内容来检查响应内容是否正确，为空则不做匹配检查。匹配内容仅支持英文
        request.setOptions(
                "{\"http_method\":\"post\",\"header\":\"key1:value1key2:value2\",\"cookie\":\"key1=value1;key2=value2\","
                    + "\"username\":\"123\",\"password\":\"123\",\"time_out\":30000,\"request_content\":\"abc\","
                    + "\"match_rule\":0,\"response_content\":\"abc\"}");
        //创建任务的时候同时设置报警规则(json Array 字符串格式)：alarmAction:报警联系组, metricName:  Availability(可用率), ResponseTime(响应时间), expression对应报警条件
        request.setAlertRule("["
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

        try {
            logger.info("sending CreateTaskRequest...");
            CreateTaskResponse response = client.getAcsResponse(request);
            logger.info("CreateTaskResponse:\n{}", JSON.toJSONString(response,true));
            logger.info("response.getData():\n{}", JSON.toJSONString(JSON.parseObject(response.getData()), true));
            return response;
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    /**
     * 创建站点监控，返回站点监控任务id
     */
    public static void modifyTask(String taskId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        ModifyTaskRequest request = new ModifyTaskRequest();
        request.setTaskId(taskId);
        request.setAddress("http://www.aliyun.com");
        request.setTaskName("your_task_name");
        //设置监测频率为5min
        request.setInterval("5");
        //设置探针
        request.setIspCity("[{\"city\":\"546\",\"isp\":\"465\"},{\"city\":\"572\",\"isp\":\"465\"},"
            + "{\"city\":\"738\",\"isp\":\"465\"}]");
        //设置httpMethod、header、cookie等高级设置
        //match_rule=0 表示包含匹配内容则报警， match_rule=1表示不包含匹配内容则报警
        //response_content: 指定匹配内容来检查响应内容是否正确，为空则不做匹配检查。匹配内容仅支持英文
        request.setOptions(
            "{\"http_method\":\"post\",\"header\":\"key1:value1key2:value2\",\"cookie\":\"key1=value1;key2=value2\","
                + "\"username\":\"123\",\"password\":\"123\",\"time_out\":30000,\"request_content\":\"abc\","
                + "\"match_rule\":0,\"response_content\":\"abc\"}");
        //创建任务的时候同时设置报警规则(json Array 字符串格式)：alarmAction:报警联系组, metricName:  Availability(可用率), ResponseTime(响应时间), expression对应报警条件
        request.setAlertRule("["
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

        try {
            logger.info("sending ModifyTaskRequest...");
            ModifyTaskResponse response = client.getAcsResponse(request);
            logger.info("ModifyTaskResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 创建站点监控，返回站点监控任务id
     */
    public static void deleteTask(String taskId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        List<String> taskIdList = new ArrayList<>();
        taskIdList.add(taskId);

        DeleteTasksRequest request = new DeleteTasksRequest();
        request.setTaskIds(JSONObject.toJSONString(taskIdList));

        try {
            logger.info("sending DeleteTasksRequest...");
            DeleteTasksResponse response = client.getAcsResponse(request);
            logger.info("DeleteTasksResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }
    }


    /**
     * 创建站点监控，返回站点监控任务id
     */
    public static void describeAlarmHistory() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        DescribeAlarmHistoryRequest request = new DescribeAlarmHistoryRequest();
        // 应用分组ID
        request.setGroupId("group_id");
        // 创建报警规则时云监控自动产生的唯一标识
        request.setAlertName("alarm_id");
        // 创建报警规则时用户定义的规则名称
        request.setRuleName("your_task_name");
        // 报警规则监控的产品, 站点监控=acs_networkmonitor
        request.setNamespace("acs_networkmonitor");
        // 报警规则监控的指标, 例如Availability，代表可用性, ResponseTime代表响应时间
        request.setMetricName("Availability");
        //与EndTime最多间隔最长三天，可查询一年之内的数据，目前仅支持timestamp格式的时间
        request.setStartTime(String.valueOf(System.currentTimeMillis() - 1000 * 60 * 60 * 24));
        request.setEndTime(String.valueOf(System.currentTimeMillis()));
        // 查询结果是否只返回结果条数，默认是false
        request.setOnlyCount(false);
        // 查询结果是否正序返回，默认是false
        request.setAscending(true);
        // 报警状态,OK是恢复，ALARM是报警
        request.setState("ALARM");
        // 报警通知发送状态，0为已通知用户，1为不在生效期未通知，2为处于报警沉默期未通知
        request.setStatus("0");
        // 查询分页信息
        request.setPageSize(10);
        request.setPage(1);

        try {
            logger.info("sending DescribeAlarmHistoryRequest...");
            DescribeAlarmHistoryResponse response = client.getAcsResponse(request);
            logger.info("DescribeAlarmHistoryResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }
    }

    public static String getTaskDetail(String taskId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        DescribeTaskDetailRequest request = new DescribeTaskDetailRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setTaskId(taskId);

        try {
            logger.info("sending DescribeTaskDetailRequest...");
            DescribeTaskDetailResponse response = client.getAcsResponse(request);
            logger.info("DescribeTaskDetailResponse:\n{}", JSON.toJSONString(response,true));
            logger.info("response.getData():\n{}", JSON.toJSONString(JSON.parseObject(response.getData()), true));
            return response.getData();
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public static void stopTask(String taskId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        List<String> taskIdList = new ArrayList<>();
        taskIdList.add(taskId);

        StopTasksRequest request = new StopTasksRequest();
        request.setTaskIds(JSON.toJSONString(taskIdList));

        try {
            logger.info("sending StopTasksRequest...");
            StopTasksResponse response = client.getAcsResponse(request);
            logger.info("StopTasksResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }
    }

    public static void startTask(String taskId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        List<String> taskIdList = new ArrayList<>();
        taskIdList.add(taskId);

        StartTasksRequest request = new StartTasksRequest();
        request.setTaskIds(JSON.toJSONString(taskIdList));

        try {
            logger.info("sending StartTasksRequest...");
            StartTasksResponse response = client.getAcsResponse(request);
            logger.info("StartTasksResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }
    }

    public static void getTasks() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        DescribeTasksRequest request = new DescribeTasksRequest();
        //筛选指定的taskId
        request.setTaskId("your_task_id");
        //筛选任务类型，1：http
        request.setTaskType("1");
        //筛选监控任务名称或地址内容
        request.setKeyword("keyword");
        //获取第几页的任务
        request.setPage(1);
        //每页最多多少个任务
        request.setPageSize(5);

        try {
            logger.info("sending DescribeTasksRequest...");
            DescribeTasksResponse response = client.getAcsResponse(request);
            logger.info("DescribeTasksResponse:\n{}", JSON.toJSONString(response,true));
            logger.info("response.getData():\n{}", JSON.toJSONString(JSON.parseArray(response.getData()), true));
        } catch (ClientException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 查询站点监控的监控数据
     * */
    public static void queryMetricList() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        QueryMetricListRequest request = new QueryMetricListRequest();
        request.setAcceptFormat(FormatType.JSON);
        // 对应产品的project名称，站点监控为
        request.setProject("acs_networkmonitor");
        // 监控项名称: Availability:可用性/ResponseTime:平均响应时间
        request.setMetric("Availability");
        // Dimensions, json array 字符串格式，指定想要查询的taskId
        JSONArray dimensions = new JSONArray();
        JSONObject dimension = new JSONObject();
        dimension.put("taskId", "your_task_id");
        dimensions.add(dimension);
        request.setDimensions(dimension.toJSONString());
        // 时间间隔，统一用秒数来计算，例如 60, 300, 900。 如果不填写,则按照注册监控项时申明的上报周期来查询原始数据。如果填写统计周期，则查询对应的统计数据 。
        request.setPeriod("60");
        request.setStartTime(String.valueOf(System.currentTimeMillis() - 1000 * 60 * 24));
        request.setEndTime(String.valueOf(System.currentTimeMillis()));
        // 每次拉取的数据数量
        request.setLength("10");
        // 当返回的数据点大于一页的时候，会返回cursor值，可以传入该值继续查询，直到没有cursor返回表示所有数据都已经返回
        //request.setCursor("");

        try {
            logger.info("sending QueryMetricListRequest...");
            QueryMetricListResponse response = client.getAcsResponse(request);
            logger.info("QueryMetricListResponse:\n{}", JSON.toJSONString(response,true));
            logger.info("response.getDatapoints():\n{}", JSON.toJSONString(JSON.parseArray(response.getDatapoints()), true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 查询站点监控的最新的监控数据， 和queryMetricList的参数一致，但是只返回给定时间段内的最新的数据
     * */
    public static void queryMetricLast() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        QueryMetricLastRequest request = new QueryMetricLastRequest();
        request.setAcceptFormat(FormatType.JSON);
        // 对应产品的project名称，站点监控为
        request.setProject("acs_networkmonitor");
        // 监控项名称: Availability:可用性/ResponseTime:平均响应时间
        request.setMetric("Availability");
        // Dimensions, json array 字符串格式，指定想要查询的taskId
        JSONArray dimensions = new JSONArray();
        JSONObject dimension = new JSONObject();
        dimension.put("taskId", "your_task_id");
        dimensions.add(dimension);
        request.setDimensions(dimensions.toJSONString());
        // 时间间隔，统一用秒数来计算，例如 60, 300, 900。 如果不填写,则按照注册监控项时申明的上报周期来查询原始数据。如果填写统计周期，则查询对应的统计数据 。
        request.setPeriod("60");
        request.setStartTime(String.valueOf(System.currentTimeMillis() - 1000 * 60 * 24));
        request.setEndTime(String.valueOf(System.currentTimeMillis()));
        // 每次拉取的数据数量
        request.setLength("10");
        // 当返回的数据点大于一页的时候，会返回cursor值，可以传入该值继续查询，直到没有cursor返回表示所有数据都已经返回
        //request.setCursor("");

        try {
            logger.info("sending QueryMetricLastRequest...");
            QueryMetricLastResponse response = client.getAcsResponse(request);
            logger.info("QueryMetricLastResponse:\n{}", JSON.toJSONString(response,true));
            logger.info("response.getDatapoints():\n{}", JSON.toJSONString(JSON.parseArray(response.getDatapoints()), true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    private static String getTaskIdFromCreateTaskResponse(CreateTaskResponse response) {
        JSONObject obj = JSON.parseObject(response.getData());
        if (obj.keySet().size() == 1) {
            for (String taskName : obj.keySet()) {
                return obj.getString(taskName);
            }
        }
        return null;
    }

    private static String getAlarmIdFromCreateTaskResponse(CreateTaskResponse response, String metricName) {
        JSONArray rules = JSON.parseArray(response.getAlertRule());

        for (int i = 0; i < rules.size(); i++) {
            JSONObject obj = rules.getJSONObject(i);
            if (obj.getString("metricName").equalsIgnoreCase(metricName)) {
                return obj.getString("name");
            }
        }
        return null;
    }
}
