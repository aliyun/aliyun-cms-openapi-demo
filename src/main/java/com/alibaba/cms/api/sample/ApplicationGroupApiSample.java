package com.alibaba.cms.api.sample;

import com.alibaba.cms.common.util.HttpClientUtils;
import com.alibaba.cms.common.util.SignatureUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author he.dong
 * @date 2018/5/22
 */
public class ApplicationGroupApiSample {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationGroupApiSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String endpoint = "http://metrics.aliyuncs.com/";

    /**
     * 创建应用分组
     * */
    public static String createMyGroups() {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "CreateMyGroups");
        params.put("GroupName", "your_group_name");
        // 分组的类型，有些分组是从其他系统同步而来，custom[为默认值，表示用户自己通过云监控控制台创建的],aone_group[表示这些分组从Aone同步而来], ehpc_cluster[表示这些分组从EHPC集群同步而来].
        params.put("Type", "custom");
        // 报警联系人分组列表, 多个用英文逗号分隔, 例如: dev,ops
        params.put("ContactGroups", "your_contact_groups");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        String response = HttpClientUtils.get(endpoint, params);
        return JSON.parseObject(response).getString("GroupId");
    }

    public static void listMyGroups() {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ListMyGroups");

        // 如果什么条件都不输入，默认返回所有应用分组
        params.put("GroupName", "your_updated_group_name");
        // 用于标识是否返回分组的联系人组信息: true/false
        params.put("SelectContactGroups", "true");
        // 匹配应用分组名称中包含的关键字
        params.put("Keyword", "key_word");
        // 根据分组下的关联实例id查询，主要用于查询实例是否归属当前分组
        params.put("InstanceId", "<instance_id>");
        params.put("PageSize", "3");
        params.put("PageNumber", "1");
        // custom[为默认值，表示用户自己通过云监控控制台创建的],aone_group[表示这些分组从Aone同步而来], ehpc_cluster[表示这些分组从EHPC集群同步而来]
        params.put("Type", "custom");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    /**
     * 该接口只返回唯一结果，如果输入条件匹配多个结果将返回错误，如果想匹配多个结果，请使用listMyGroups接口
     * */
    public static void getMyGroup(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "GetMyGroups");

        params.put("GroupId", groupId);
        params.put("GroupName", "your_group_name");
        // SelectContactGroups: true/false
        params.put("SelectContactGroups", "true");
        // 根据分组下的关联实例id查询，主要用于查询实例是否归属当前分组
        params.put("InstanceId", "<instance_id>");
        // custom[为默认值，表示用户自己通过云监控控制台创建的],aone_group[表示这些分组从Aone同步而来], ehpc_cluster[表示这些分组从EHPC集群同步而来]
        params.put("Type", "custom");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    public static void updateMyGroup(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "UpdateMyGroups");

        params.put("GroupId", groupId);
        params.put("GroupName", "your_group_name");
        // 报警联系人分组列表, 多个用英文逗号分隔, 例如: dev,ops
        params.put("ContactGroups", "your_contact_groups");
        // custom[为默认值，表示用户自己通过云监控控制台创建的],aone_group[表示这些分组从Aone同步而来], ehpc_cluster[表示这些分组从EHPC集群同步而来]
        params.put("Type", "custom");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    public static void deleteMyGroups(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DeleteMyGroups");

        params.put("GroupId", groupId);
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 设置动态分组规则
     * */
    public static void putGroupDynamicRule(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "PutGroupDynamicRule");

        params.put("GroupId", groupId);
        params.put("GroupRuleArrayJson", "["
            + "  {"
            + "    \"category\": \"ecs\","              //目前只支持ecs
            + "    \"filterRelation\": \"and\","        //匹配规则的关系  "and","or"
            + "    \"filters\": ["
            + "      {"
            + "        \"function\": \"contains\","     //匹配函数  startWith endWith contains
            + "        \"name\": \"hostName\","         //匹配的属性, 目前只支持主机的名称
            + "        \"value\": \"test\""             //匹配参数
            + "      }"
            + "    ]"
            + "  }"
            + "]");


        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 获取动态分组规则列表
     * */
    public static void listGroupDynamicRule(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ListGroupDynamicRule");

        params.put("GroupId", groupId);

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 删除动态分组规则
     * */
    public static void deleteGroupDynamicRule(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DeleteGroupDynamicRule");

        params.put("GroupId", groupId);
        //目前只支持ecs
        params.put("Category", "ecs");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    /**
     * 向应用分组内添加实例资源
     * */
    public static void addMyGroupInstances(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "AddMyGroupInstances");

        params.put("GroupId", groupId);
        /* 实例列表Json格式列表例如:[{ "instanceId": "<instance_id>", "category": "ECS", "regionId": "cn-shanghai" }].
         * category可选值: ECS(ECS服务器), RDS(RDS数据库), SLB(SLB负载均衡), KVSTORE(Redis缓存), MONGODB(MongoDB 数据库), CDN(CDN域名,CDN无regionId，不需要填), EIP(EIP 弹性公网IP), MEMCACHE(新版Memcache)
         * regionId可选值：https://help.aliyun.com/document_detail/40654.html?spm=a2c4g.11186623.6.689.UyUgZv&parentId=28572
         */
        params.put("Instances", "[{\"instanceId\":\"<instance_id>\",\"category\":\"ECS\",\"regionId\":\"cn-hangzhou\"}]");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 更新应用分组内实例资源，会覆盖分组内原有资源，相当于先删除全部资源在重新添加
     * */
    public static void updateMyGroupInstances(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "UpdateMyGroupInstances");

        params.put("GroupId", groupId);
        /* 实例列表Json格式列表例如:[{ "instanceId": "<instance_id>", "category": "ECS", "regionId": "cn-shanghai" }].
         * category可选值: ECS(ECS服务器), RDS(RDS数据库), SLB(SLB负载均衡), KVSTORE(Redis缓存), MONGODB(MongoDB 数据库), CDN(CDN域名,CDN无regionId，不需要填), EIP(EIP 弹性公网IP), MEMCACHE(新版Memcache)
         * regionId可选值：https://help.aliyun.com/document_detail/40654.html?spm=a2c4g.11186623.6.689.UyUgZv&parentId=28572
         */
        params.put("Instances", "[{\"instanceId\":\"<instance_id>\",\"category\":\"ECS\",\"regionId\":\"cn-hangzhou\"},"
            + "{\"instanceId\":\"<instance_id>\",\"category\":\"ECS\",\"regionId\":\"cn-qingdao\"}]");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 删除组内的资源实例
     * */
    public static void deleteMyGroupInstances(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "DeleteMyGroupInstances");

        params.put("GroupId", groupId);
        // 参数InstanceIds 和 InstanceIdList 指定一个即可

        // 这里的id是指ListMyGroupInstances接口返回的ID字段, 移除多个实例可以使用英文的逗号分隔
        //params.put("InstanceIds", "id1,id2");

        // 指实例id列表，如i-xxxxx格式，多个实例用逗号隔开
        params.put("InstanceIdList", "<instance_id>,<instance_id>");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    /**
     * 列出指定分组下的实例信息, 这个接口支持分页.
     * 为了保证性能, 这里只是列出了简单的实例id与regionId的信息,
     * 并不包含实例名称等更可读的信息, 如果需要一次返回这些信息可以调用ListMyGroupInstancesDetails接口.
     * */
    public static void listMyGroupInstances(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ListMyGroupInstances");

        params.put("GroupId", groupId);
        // 只返回指定类型的实例, category可选值: ECS(ECS服务器), RDS(RDS数据库), SLB(SLB负载均衡), KVSTORE(Redis缓存), MONGODB(MongoDB 数据库), CDN(CDN域名,CDN无regionId，不需要填), EIP(EIP 弹性公网IP), MEMCACHE(新版Memcache)
        params.put("Category", "ECS");
        // 分页参数, 默认值为true, 返回结果中包含了总记录数. 为了更好的性能, 可以将这个参数值为false, 不返回总记录数.
        params.put("Total", "true");
        params.put("PageNumber", "1");
        params.put("PageSize", "10");
        // 关键字, 模糊匹配instanceId和instanceName, 用来过滤结果
        params.put("Keyword", "your_key_word");


        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }


    public static void listMyGroupInstancesDetails(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ListMyGroupInstancesDetails");

        params.put("GroupId", groupId);
        // 只返回指定类型的实例, category可选值: ECS(ECS服务器), RDS(RDS数据库), SLB(SLB负载均衡), KVSTORE(Redis缓存), MONGODB(MongoDB 数据库), CDN(CDN域名,CDN无regionId，不需要填), EIP(EIP 弹性公网IP), MEMCACHE(新版Memcache)
        params.put("Category", "ECS");
        // 分页参数, 默认值为true, 返回结果中包含了总记录数. 为了更好的性能, 可以将这个参数值为false, 不返回总记录数.
        params.put("Total", "true");
        params.put("PageNumber", "1");
        params.put("PageSize", "3");
        // 关键字, 模糊匹配instanceId和instanceName, 用来过滤结果
        params.put("Keyword", "your_key_word");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    public static void listMyGroupCategories(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "ListMyGroupCategories");
        params.put("GroupId", groupId);

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 授权子账号应用分组的权限
     * */
    public static void updateMyGroupMembers(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "UpdateMyGroupMembers");
        params.put("GroupId", groupId);
        // 需要授权的子账号ID，可以在RAM控制台，子账号详情里看到，被授权的子账号将有管理分组内报警规则的管理权限。多个子账号使用英文逗号分隔，授权列表以最后一次调用为准，这个字段为空的时候将清空Master列表。
        params.put("Masters", "<sub_account_id>,<sub_account_id>");
        // 被授权的子账号只有指定分组的只读操作，不能修改组内任何资源。多个子账号使用英文逗号分隔，授权列表以最后一次调用为准，这个字段为空的时候将清空Reader列表。
        params.put("Readers", "<sub_account_id>,<sub_account_id>");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    public static String taskConfigCreate(String groupId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "TaskConfigCreate");

        params.put("GroupId", groupId);
        params.put("TaskName", "your_task_name");
        // TELNET—telnet监控任务、HTTP—http监控任务
        params.put("TaskType", "HTTP");
        // 任务的作用范围：GROUP—整个组上(组内所有实例)，GROUP_SPEC_INSTANCE—组内指定(1或多个)实例上
        params.put("TaskScope", "GROUP");
        // 探测目标，telnet的例子 {"uri":"telnet://127.0.0.1:22"}，下面是HTTP的例子
        params.put("JsonData", "{"
            + "\"uri\":\"http://localhost:8088/abc\","
            + "\"resposeCharset\":\"UTF-8\","
            + "\"keyword\":\"your_key_word\","          // 可选是否相应内容匹配keyword才报警
            + "\"negative\":true,"                      // true:包含keyword报警，false:不包含keyword才报警
            + "\"requestBody\":\"input_post_data\","    // 如果是method=POST, 可以选填requestBody
            + "\"method\":\"POST\"}");                  // method可以是HEAD, GET, POST
        params.put("AlertConfig", "{"
            + "\"notifyType\":\"0\","
            + "\"startTime\":0,"
            + "\"endTime\":24,"
            + "\"silenceTime\":\"300\","
            + "\"escalationList\":["
                + "{\"metric\":\"HttpStatus\",\"aggregate\":\"Value\",\"times\":3,\"operator\":\">\",\"value\":\"400\"},"
                + "{\"metric\":\"HttpLatency\",\"aggregate\":\"Average\",\"times\":3,\"operator\":\">\",\"value\":\"500\"}]}");

        // 如果taskScope!=GROUP,则需要填写对应的instanceList
        params.put("InstanceList", "<instance_id>,<instance_id>");

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        String response = HttpClientUtils.get(endpoint, params);
        return JSON.parseObject(response).getString("TaskId");
    }

    public static void taskConfigList(String groupId, String taskId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "TaskConfigList");
        //不输入任何条件返回所有数据，或者输入对应条件进行筛选
        params.put("GroupId", groupId);
        params.put("Id", taskId);
        params.put("TaskName", "your_task_name");
        params.put("PageNumber", "1");
        params.put("PageSize", "10");
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    public static void taskConfigDelete(List<String> taskIds) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "TaskConfigDelete");
        //可用性探测任务Id列表
        for (int i = 0; i < taskIds.size() ; i++) {
            params.put("IdList." + (i + 1), taskIds.get(i));
        }
        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    public static void taskConfigEnable(List<String> taskIds, boolean isEnable) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "TaskConfigEnable");
        params.put("Enabled", Boolean.toString(isEnable));
        //可用性探测任务Id列表
        for (int i = 0; i < taskIds.size() ; i++) {
            params.put("IdList." + (i + 1), taskIds.get(i));
        }

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    public static void taskConfigUnhealthy(List<String> taskIds) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "TaskConfigUnhealthy");
        //可用性探测任务Id列表
        for (int i = 0; i < taskIds.size() ; i++) {
            params.put("IdLTaskIdList." + (i + 1), taskIds.get(i));
        }

        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    /**
     * 参数设置同taskConfigCreate
     * */
    public static void taskConfigModify(String groupId, String taskId) {
        String httpMethod = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("Action", "TaskConfigModify");

        params.put("GroupId", groupId);
        params.put("Id", taskId);

        params.put("TaskName", "your_task_name");
        // TELNET—telnet监控任务、HTTP—http监控任务
        params.put("TaskType", "TELNET");
        // 任务的作用范围：GROUP—整个组上(组内所有实例)，GROUP_SPEC_INSTANCE—组内指定(1或多个)实例上
        params.put("TaskScope", "GROUP");
        // 探测目标，telnet的例子 {"uri":"telnet://127.0.0.1:22"}
        params.put("JsonData", "{\"uri\":\"telnet://127.0.0.1:22\"}");
        params.put("AlertConfig", "{"
            + "\"notifyType\":\"0\","
            + "\"startTime\":0,"
            + "\"endTime\":24,"
            + "\"silenceTime\":\"300\","
            + "\"escalationList\":["
            + "{\"metric\":\"HttpStatus\",\"aggregate\":\"Value\",\"times\":3,\"operator\":\">\",\"value\":\"400\"},"
            + "{\"metric\":\"HttpLatency\",\"aggregate\":\"Average\",\"times\":3,\"operator\":\">\",\"value\":\"500\"}]}");

        // 如果taskScope!=GROUP,则需要填写对应的instanceList
        params.put("InstanceList", "<instance_id>,<instance_id>");


        params = SignatureUtils.appendPublicParams(params, httpMethod, accessKeyId, accessKeySecret);
        HttpClientUtils.get(endpoint, params);
    }

    public static void main(String[] args) {
        /** 应用分组操作  */
        // 1. 创建应用分组
        String groupId = createMyGroups();
        // 2. 修改应用分组
        updateMyGroup(groupId);
        // 3. 为ECS类型资源设置动态分组
        putGroupDynamicRule(groupId);
        listGroupDynamicRule(groupId);
        deleteGroupDynamicRule(groupId);
        // 3. 添加实例到应用分组
        addMyGroupInstances(groupId);
        // 4. 更新应用分组内实例
        updateMyGroupInstances(groupId);
        // 5. 删除应用分组实例
        listMyGroupInstances(groupId);
        deleteMyGroupInstances(groupId);
        listMyGroupInstances(groupId);
        // 6. 查询所有分组
        listMyGroups();
        // 7. 查询应用分组详情
        listMyGroupCategories(groupId);
        listMyGroupInstances(groupId);
        listMyGroupInstancesDetails(groupId);

        /** 应用分组下的可用性监控  */
        // 1. 创建可用性监控
        String taskId = taskConfigCreate(groupId);
        // 2. 查询可用性监控
        taskConfigList(groupId, taskId);
        // 3. 修改可用性监控
        taskConfigModify(groupId, taskId);
        // 4. 可用性监控健康状态检查
        taskConfigUnhealthy(ImmutableList.of(taskId));
        // 5. 启用/禁用可用性监控
        taskConfigEnable(ImmutableList.of(taskId), true);
        // 6. 删除可用性监控
        taskConfigDelete(ImmutableList.of(taskId));


        /** 删除应用分组 */
        deleteMyGroups(groupId);
    }
}
