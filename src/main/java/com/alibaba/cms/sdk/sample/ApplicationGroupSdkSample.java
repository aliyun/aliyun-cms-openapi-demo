package com.alibaba.cms.sdk.sample;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.cms.model.v20180308.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author he.dong
 * @date 2018/5/22
 */
public class ApplicationGroupSdkSample {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationGroupSdkSample.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static final String REGION_ID_BEIJING = "cn-beijing";


    /**
     * 创建应用分组
     * */
    public static long createMyGroups() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        CreateMyGroupsRequest request = new CreateMyGroupsRequest();
        request.setAcceptFormat(FormatType.JSON);
        request.setGroupName("your_group_name");
        // 分组的类型，有些分组是从其他系统同步而来，custom[为默认值，表示用户自己通过云监控控制台创建的],aone_group[表示这些分组从Aone同步而来], ehpc_cluster[表示这些分组从EHPC集群同步而来].
        request.setType("custom");
        // 报警联系人分组列表, 多个用英文逗号分隔, 例如: dev,ops
        request.setContactGroups("your_contact_groups");

        try {
            logger.info("sending CreateMyGroupsRequest...");
            CreateMyGroupsResponse response = client.getAcsResponse(request);
            logger.info("CreateMyGroupsResponse:\n{}", JSON.toJSONString(response,true));
            return response.getGroupId();
        } catch (ClientException e) {
            logger.info(e.getMessage());
            return -1;
        }
    }

    public static void listMyGroups() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        ListMyGroupsRequest request = new ListMyGroupsRequest();
        request.setAcceptFormat(FormatType.JSON);

        // 如果什么条件都不输入，默认返回所有应用分组
        request.setGroupName("your_updated_group_name");
        // 用于标识是否返回分组的联系人组信息: true/false
        request.setSelectContactGroups(true);
        // 匹配应用分组名称中包含的关键字
        request.setKeyword("test");
        // 根据分组下的关联实例id查询，主要用于查询实例是否归属当前分组
        request.setInstanceId("<instance_id>");

        request.setPageNumber(1);
        request.setPageSize(10);
        // custom[为默认值，表示用户自己通过云监控控制台创建的],aone_group[表示这些分组从Aone同步而来], ehpc_cluster[表示这些分组从EHPC集群同步而来]
        request.setType("custom");

        try {
            logger.info("sending ListMyGroupsRequest...");
            ListMyGroupsResponse response = client.getAcsResponse(request);
            logger.info("ListMyGroupsResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }


    /**
     * 该接口只返回唯一结果，如果输入条件匹配多个结果将返回错误，如果想匹配多个结果，请使用listMyGroups接口
     * */
    public static void getMyGroup(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        GetMyGroupsRequest request = new GetMyGroupsRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        // 如果什么条件都不输入，默认返回所有应用分组
        request.setGroupName("your_group_name");
        // 用于标识是否返回分组的联系人组信息: true/false
        request.setSelectContactGroups(true);
        // 根据分组下的关联实例id查询，主要用于查询实例是否归属当前分组
        request.setInstanceId("<instance_id>");
        // custom[为默认值，表示用户自己通过云监控控制台创建的],aone_group[表示这些分组从Aone同步而来], ehpc_cluster[表示这些分组从EHPC集群同步而来]
        request.setType("custom");

        try {
            logger.info("sending GetMyGroupsRequest...");
            GetMyGroupsResponse response = client.getAcsResponse(request);
            logger.info("GetMyGroupsResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    public static void updateMyGroup(String groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        UpdateMyGroupsRequest request = new UpdateMyGroupsRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        request.setGroupName("your_group_name");
        // 报警联系人分组列表, 多个用英文逗号分隔, 例如: dev,ops
        request.setContactGroups("your_contact_groups");
        // custom[为默认值，表示用户自己通过云监控控制台创建的],aone_group[表示这些分组从Aone同步而来], ehpc_cluster[表示这些分组从EHPC集群同步而来]
        request.setType("custom");

        try {
            logger.info("sending UpdateMyGroupsRequest...");
            UpdateMyGroupsResponse response = client.getAcsResponse(request);
            logger.info("UpdateMyGroupsResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }


    public static void deleteMyGroups(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        DeleteMyGroupsRequest request = new DeleteMyGroupsRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);

        try {
            logger.info("sending DeleteMyGroupsRequest...");
            DeleteMyGroupsResponse response = client.getAcsResponse(request);
            logger.info("DeleteMyGroupsResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }


    /**
     * 设置动态分组规则
     * */
    public static void putGroupDynamicRule(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        PutGroupDynamicRuleRequest request = new PutGroupDynamicRuleRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        request.setGroupRuleArrayJson("["
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


        try {
            logger.info("sending PutGroupDynamicRuleRequest...");
            PutGroupDynamicRuleResponse response = client.getAcsResponse(request);
            logger.info("PutGroupDynamicRuleResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 获取动态分组规则列表
     * */
    public static void listGroupDynamicRule(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        ListGroupDynamicRuleRequest request = new ListGroupDynamicRuleRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);


        try {
            logger.info("sending ListGroupDynamicRuleRequest...");
            ListGroupDynamicRuleResponse response = client.getAcsResponse(request);
            logger.info("ListGroupDynamicRuleResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 删除动态分组规则
     * */
    public static void deleteGroupDynamicRule(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        DeleteGroupDynamicRuleRequest request = new DeleteGroupDynamicRuleRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        //目前只支持ecs
        request.setCategory("ecs");

        try {
            logger.info("sending DeleteGroupDynamicRuleRequest...");
            DeleteGroupDynamicRuleResponse response = client.getAcsResponse(request);
            logger.info("DeleteGroupDynamicRuleResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }


    /**
     * 向应用分组内添加实例资源
     * */
    public static void addMyGroupInstances(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        AddMyGroupInstancesRequest request = new AddMyGroupInstancesRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        /* 实例列表Json格式列表例如:[{ "instanceId": "<instance_id>", "category": "ECS", "regionId": "cn-shanghai" }].
         * category可选值: ECS(ECS服务器), RDS(RDS数据库), SLB(SLB负载均衡), KVSTORE(Redis缓存), MONGODB(MongoDB 数据库), CDN(CDN域名,CDN无regionId，不需要填), EIP(EIP 弹性公网IP), MEMCACHE(新版Memcache)
         * regionId可选值：https://help.aliyun.com/document_detail/40654.html?spm=a2c4g.11186623.6.689.UyUgZv&parentId=28572
         */
        request.setInstances("[{\"instanceId\":\"<instance_id>\",\"category\":\"ECS\",\"regionId\":\"cn-hangzhou\"}]");
        try {
            logger.info("sending AddMyGroupInstancesRequest...");
            AddMyGroupInstancesResponse response = client.getAcsResponse(request);
            logger.info("AddMyGroupInstancesResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 更新应用分组内实例资源，会覆盖分组内原有资源，相当于先删除全部资源在重新添加
     * */
    public static void updateMyGroupInstances(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        UpdateMyGroupInstancesRequest request = new UpdateMyGroupInstancesRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        /* 实例列表Json格式列表例如:[{ "instanceId": "<instance_id>", "category": "ECS", "regionId": "cn-shanghai" }].
         * category可选值: ECS(ECS服务器), RDS(RDS数据库), SLB(SLB负载均衡), KVSTORE(Redis缓存), MONGODB(MongoDB 数据库), CDN(CDN域名,CDN无regionId，不需要填), EIP(EIP 弹性公网IP), MEMCACHE(新版Memcache)
         * regionId可选值：https://help.aliyun.com/document_detail/40654.html?spm=a2c4g.11186623.6.689.UyUgZv&parentId=28572
         */
        request.setInstances(
            "[{\"instanceId\":\"<instance_id>\",\"category\":\"ECS\",\"regionId\":\"cn-hangzhou\"},"
                + "{\"instanceId\":\"<instance_id>\",\"category\":\"ECS\",\"regionId\":\"cn-qingdao\"}]");

        try {
            logger.info("sending UpdateMyGroupInstancesRequest...");
            UpdateMyGroupInstancesResponse response = client.getAcsResponse(request);
            logger.info("UpdateMyGroupInstancesResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 删除组内的资源实例
     * */
    public static void deleteMyGroupInstances(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        DeleteMyGroupInstancesRequest request = new DeleteMyGroupInstancesRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        // 参数InstanceIds 和 InstanceIdList 指定一个即可

        // 这里的id是指ListMyGroupInstances接口返回的ID字段, 移除多个实例可以使用英文的逗号分隔
        //request.setInstanceIds("id1,id2");

        // 指实例id列表，如i-xxxxx格式，多个实例用逗号隔开
        request.setInstanceIdList("<instance_id>,<instance_id>");

        try {
            logger.info("sending DeleteMyGroupInstancesRequest...");
            DeleteMyGroupInstancesResponse response = client.getAcsResponse(request);
            logger.info("DeleteMyGroupInstancesResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }


    /**
     * 列出指定分组下的实例信息, 这个接口支持分页.
     * 为了保证性能, 这里只是列出了简单的实例id与regionId的信息,
     * 并不包含实例名称等更可读的信息, 如果需要一次返回这些信息可以调用ListMyGroupInstancesDetails接口.
     * */
    public static void listMyGroupInstances(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        ListMyGroupInstancesRequest request = new ListMyGroupInstancesRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        // 只返回指定类型的实例, category可选值: ECS(ECS服务器), RDS(RDS数据库), SLB(SLB负载均衡), KVSTORE(Redis缓存), MONGODB(MongoDB 数据库), CDN(CDN域名,CDN无regionId，不需要填), EIP(EIP 弹性公网IP), MEMCACHE(新版Memcache)
        request.setCategory("ECS");
        // 分页参数, 默认值为true, 返回结果中包含了总记录数. 为了更好的性能, 可以将这个参数值为false, 不返回总记录数.
        request.setTotal(true);
        request.setPageNumber(1);
        request.setPageSize(10);
        // 关键字, 模糊匹配instanceId和instanceName, 用来过滤结果
        request.setKeyword("your_key_word");

        try {
            logger.info("sending ListMyGroupInstancesRequest...");
            ListMyGroupInstancesResponse response = client.getAcsResponse(request);
            logger.info("ListMyGroupInstancesResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }


    public static void listMyGroupInstancesDetails(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        ListMyGroupInstancesDetailsRequest request = new ListMyGroupInstancesDetailsRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        // 只返回指定类型的实例, category可选值: ECS(ECS服务器), RDS(RDS数据库), SLB(SLB负载均衡), KVSTORE(Redis缓存), MONGODB(MongoDB 数据库), CDN(CDN域名,CDN无regionId，不需要填), EIP(EIP 弹性公网IP), MEMCACHE(新版Memcache)
        request.setCategory("ECS");
        // 分页参数, 默认值为true, 返回结果中包含了总记录数. 为了更好的性能, 可以将这个参数值为false, 不返回总记录数.
        request.setTotal(true);
        request.setPageNumber(1);
        request.setPageSize(10);
        // 关键字, 模糊匹配instanceId和instanceName, 用来过滤结果
        request.setKeyword("your_key_word");

        try {
            logger.info("sending ListMyGroupInstancesDetailsRequest...");
            ListMyGroupInstancesDetailsResponse response = client.getAcsResponse(request);
            logger.info("ListMyGroupInstancesDetailsResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    public static void listMyGroupCategories(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        ListMyGroupCategoriesRequest request = new ListMyGroupCategoriesRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);

        try {
            logger.info("sending ListMyGroupCategoriesRequest...");
            ListMyGroupCategoriesResponse response = client.getAcsResponse(request);
            logger.info("ListMyGroupCategoriesResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 授权子账号应用分组的权限
     * */
    public static void updateMyGroupMembers(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        UpdateMyGroupMembersRequest request = new UpdateMyGroupMembersRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        // 需要授权的子账号ID，可以在RAM控制台，子账号详情里看到，被授权的子账号将有管理分组内报警规则的管理权限。多个子账号使用英文逗号分隔，授权列表以最后一次调用为准，这个字段为空的时候将清空Master列表。
        request.setMasters("<sub_account_id>,<sub_account_id>");
        // 被授权的子账号只有指定分组的只读操作，不能修改组内任何资源。多个子账号使用英文逗号分隔，授权列表以最后一次调用为准，这个字段为空的时候将清空Reader列表。
        request.setReaders("<sub_account_id>,<sub_account_id>");

        try {
            logger.info("sending UpdateMyGroupMembersRequest...");
            UpdateMyGroupMembersResponse response = client.getAcsResponse(request);
            logger.info("UpdateMyGroupMembersResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    public static long taskConfigCreate(long groupId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        TaskConfigCreateRequest request = new TaskConfigCreateRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        request.setTaskName("your_task_name");
        // TELNET—telnet监控任务、HTTP—http监控任务
        request.setTaskType("HTTP");
        // 任务的作用范围：GROUP—整个组上(组内所有实例)，GROUP_SPEC_INSTANCE—组内指定(1或多个)实例上
        request.setTaskScope("GROUP");
        // 探测目标，telnet的例子 {"uri":"telnet://127.0.0.1:22"}，下面是HTTP的例子
        request.setJsonData("{"
            + "\"uri\":\"http://localhost:8088/abc\","
            + "\"resposeCharset\":\"UTF-8\","
            + "\"keyword\":\"your_key_word\","          // 可选是否相应内容匹配keyword才报警
            + "\"negative\":true,"                      // true:包含keyword报警，false:不包含keyword才报警
            + "\"requestBody\":\"input_post_data\","    // 如果是method=POST, 可以选填requestBody
            + "\"method\":\"POST\"}");                  // method可以是HEAD, GET, POST
        request.setAlertConfig("{"
            + "\"notifyType\":\"0\","
            + "\"startTime\":0,"
            + "\"endTime\":24,"
            + "\"silenceTime\":\"300\","
            + "\"escalationList\":["
            + "{\"metric\":\"HttpStatus\",\"aggregate\":\"Value\",\"times\":3,\"operator\":\">\",\"value\":\"400\"},"
            + "{\"metric\":\"HttpLatency\",\"aggregate\":\"Average\",\"times\":3,\"operator\":\">\",\"value\":\"500\"}]}");
        // 如果taskScope!=GROUP,则需要填写对应的instanceList
        request.setInstanceLists(ImmutableList.of("<instance_id>,<instance_id>"));

        try {
            logger.info("sending TaskConfigCreateRequest...");
            TaskConfigCreateResponse response = client.getAcsResponse(request);
            logger.info("TaskConfigCreateResponse:\n{}", JSON.toJSONString(response,true));
            return response.getTaskId();
        } catch (ClientException e) {
            logger.info(e.getMessage());
            return -1;
        }
    }

    public static void taskConfigList(long groupId, long taskId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        TaskConfigListRequest request = new TaskConfigListRequest();
        request.setAcceptFormat(FormatType.JSON);

        //不输入任何条件返回所有数据，或者输入对应条件进行筛选
        request.setGroupId(groupId);
        request.setId(taskId);
        request.setTaskName("your_task_name");
        request.setPageNumber(1);
        request.setPageSize(10);

        try {
            logger.info("sending TaskConfigListRequest...");
            TaskConfigListResponse response = client.getAcsResponse(request);
            logger.info("TaskConfigListResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    public static void taskConfigDelete(List<Long> taskIds) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        TaskConfigDeleteRequest request = new TaskConfigDeleteRequest();
        request.setAcceptFormat(FormatType.JSON);

        //可用性探测任务Id列表
        request.setIdLists(taskIds);

        try {
            logger.info("sending TaskConfigDeleteRequest...");
            TaskConfigDeleteResponse response = client.getAcsResponse(request);
            logger.info("TaskConfigDeleteResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    public static void taskConfigEnable(List<Long> taskIds, boolean isEnabled) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        TaskConfigEnableRequest request = new TaskConfigEnableRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setIdLists(taskIds);
        request.setEnabled(isEnabled);

        try {
            logger.info("sending TaskConfigEnableRequest...");
            TaskConfigEnableResponse response = client.getAcsResponse(request);
            logger.info("TaskConfigEnableResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    public static void taskConfigUnhealthy(List<Long> taskIds) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        TaskConfigUnhealthyRequest request = new TaskConfigUnhealthyRequest();
        request.setAcceptFormat(FormatType.JSON);

        //可用性探测任务Id列表
        request.setTaskIdLists(taskIds);

        try {
            logger.info("sending TaskConfigUnhealthyRequest...");
            TaskConfigUnhealthyResponse response = client.getAcsResponse(request);
            logger.info("TaskConfigUnhealthyResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 参数设置同taskConfigCreate
     * */
    public static void taskConfigModify(long groupId, long taskId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        TaskConfigModifyRequest request = new TaskConfigModifyRequest();
        request.setAcceptFormat(FormatType.JSON);

        request.setGroupId(groupId);
        request.setId(taskId);

        request.setTaskName("your_task_name");
        // TELNET—telnet监控任务、HTTP—http监控任务
        request.setTaskType("HTTP");
        // 任务的作用范围：GROUP—整个组上(组内所有实例)，GROUP_SPEC_INSTANCE—组内指定(1或多个)实例上
        request.setTaskScope("GROUP");
        // 探测目标，telnet的例子 {"uri":"telnet://127.0.0.1:22"}，下面是HTTP的例子
        request.setJsonData("{"
            + "\"uri\":\"http://localhost:8088/abc\","
            + "\"resposeCharset\":\"UTF-8\","
            + "\"keyword\":\"your_key_word\","          // 可选是否相应内容匹配keyword才报警
            + "\"negative\":true,"                      // true:包含keyword报警，false:不包含keyword才报警
            + "\"requestBody\":\"input_post_data\","    // 如果是method=POST, 可以选填requestBody
            + "\"method\":\"POST\"}");                  // method可以是HEAD, GET, POST
        request.setAlertConfig("{"
            + "\"notifyType\":\"0\","
            + "\"startTime\":0,"
            + "\"endTime\":24,"
            + "\"silenceTime\":\"300\","
            + "\"escalationList\":["
            + "{\"metric\":\"HttpStatus\",\"aggregate\":\"Value\",\"times\":3,\"operator\":\">\",\"value\":\"400\"},"
            + "{\"metric\":\"HttpLatency\",\"aggregate\":\"Average\",\"times\":3,\"operator\":\">\",\"value\":\"500\"}]}");
        // 如果taskScope!=GROUP,则需要填写对应的instanceList
        request.setInstanceLists(ImmutableList.of("<instance_id>,<instance_id>"));

        try {
            logger.info("sending TaskConfigModifyRequest...");
            TaskConfigModifyResponse response = client.getAcsResponse(request);
            logger.info("TaskConfigModifyResponse:\n{}", JSON.toJSONString(response,true));
        } catch (ClientException e) {
            logger.info(e.getMessage());
        }
    }

    public static void main(String[] args) {
        /** 应用分组操作  */
        // 1. 创建应用分组
        long groupId = createMyGroups();
        // 2. 修改应用分组
        updateMyGroup(String.valueOf(groupId));
        // 3. 为ECS类型资源设置动态分组
        putGroupDynamicRule(groupId);
        listGroupDynamicRule(groupId);
        deleteGroupDynamicRule(groupId);
        // 4. 添加指定实例到应用分组
        addMyGroupInstances(groupId);
        // 5. 更新应用分组内实例
        updateMyGroupInstances(groupId);
        // 6. 删除应用分组实例
        listMyGroupInstances(groupId);
        deleteMyGroupInstances(groupId);
        listMyGroupInstances(groupId);
        // 7. 查询所有分组
        listMyGroups();
        // 8. 查询应用分组详情
        listMyGroupCategories(groupId);
        listMyGroupInstances(groupId);
        listMyGroupInstancesDetails(groupId);

        /** 应用分组下的可用性监控  */
        // 1. 创建可用性监控
        long taskId = taskConfigCreate(groupId);
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
