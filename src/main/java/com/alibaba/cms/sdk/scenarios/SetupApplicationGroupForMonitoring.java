package com.alibaba.cms.sdk.scenarios;

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
 * 本示例通过一个最小化的cms API调用来搭建一个围绕应用分组+报警模板的监控单元。 <br/><br/>
 *
 * 本示例将：<br/>
 * 1. 通过控制台手动创建联系人和联系人组<br/>
 * 2. 创建一个动态的应用分组，自动匹配实例名称包含"sample"的ECS实例<br/>
 * 3. 添加指定的RDS实例到应用分组<br/>
 * 4. 通过控制台手动创建报警模板<br/>
 * 5. 应用报警模板到应用分组<br/>
 *
 * @author he.dong
 * @date 2018/7/6
 */
public class SetupApplicationGroupForMonitoring {
    private static final Logger logger = LoggerFactory.getLogger(SetupApplicationGroupForMonitoring.class);
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static final String REGION_ID_BEIJING = "cn-beijing";

    public static void main(String[] args) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID_BEIJING, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        try {
            // 1. 通过控制台手动创建联系人和联系人组
            String contactGroups = "<your_contact_group_name>";
            logger.info("manually create contact groups on CMS console...");

            // 2-1. 创建应用分组
            CreateMyGroupsRequest request0 = new CreateMyGroupsRequest();
            request0.setAcceptFormat(FormatType.JSON);
            request0.setGroupName("demo-app-group");
            request0.setContactGroups(contactGroups);
            CreateMyGroupsResponse response0 = client.getAcsResponse(request0);
            long groupId = response0.getGroupId();
            logger.info("application group created, groupId = {}", groupId);

            // 2-2. 自动匹配实例名称包含"test"的ECS实例
            PutGroupDynamicRuleRequest request1 = new PutGroupDynamicRuleRequest();
            request1.setGroupId(groupId);
            request1.setGroupRuleArrayJson("["
                + "  {"
                + "    \"category\": \"ecs\","              // 暂时只支持ECS
                + "    \"filterRelation\": \"and\","        // 同时满足以下规则（或者使用or 来匹配以下任意规则）
                + "    \"filters\": ["
                + "      {"
                + "        \"function\": \"contains\","     // 额外还支持startWith, endWith
                + "        \"name\": \"hostName\","         // hostName代表ecs实例的自定义主机名称
                + "        \"value\": \"test\""             // 实际匹配的值，本例表示ecs主机名称中包含test的实例
                + "      }"
                + "    ]"
                + "  }"
                + "]");
            client.getAcsResponse(request1);
            logger.info("set dynamic rules for ECS instances");

            // 3. 添加指定的RDS实例到应用分组
            AddMyGroupInstancesRequest request2 = new AddMyGroupInstancesRequest();
            request2.setGroupId(groupId);
            request2.setInstances("["
                + "  {"
                + "    \"instanceId\": \"<your_instance_id>\","
                + "    \"category\": \"RDS\","
                + "    \"regionId\": \"cn-qingdao\""
                + "  }"
                + "]");
            client.getAcsResponse(request2);
            logger.info("added RDS instances");


            // 4. 通过控制台手动创建报警模板:
            String templateIds = "<your_template_id>";
            logger.info("manually create alarm templates, and get template ids");

            // 5. 应用报警模板到应用分组
            ApplyTemplateRequest request3 = new ApplyTemplateRequest();
            request3.setGroupId(groupId);
            request3.setTemplateIds(templateIds);
            request3.setApplyMode("ALARM_TEMPLATE_FIRST");
            client.getAcsResponse(request3);
            logger.info("applied templates to application groups");
        } catch (ClientException e) {
            logger.error("application group scenario creation failed.");
            logger.error(e.getMessage());
        }
    }
}
