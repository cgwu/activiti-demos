package com.gg.helloactiviti;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by sam on 18-8-27.
 */
public class IdentifyServiceTest {
    // 使用默认的activiti.cfg.xml作为参数
    @Rule
//    public ActivitiRule activitiRule = new ActivitiRule();
    public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg.xml");

    @Test
    public void testUserAdd() throws Exception{
        IdentityService identityService = activitiRule.getIdentityService();
        User user = identityService.newUser("gg");
        user.setFirstName("三");
        user.setLastName("张");
        user.setEmail("gg@gmai.com");
        user.setPassword("gg");
        //保存到数据库
        identityService.saveUser(user);

        User userInDb = identityService.createUserQuery().userId("gg").singleResult();
        assertNotNull(userInDb);

    }

    @Test
    public void testUserDel() throws Exception {
        IdentityService identityService = activitiRule.getIdentityService();
        identityService.deleteUser("gg");
        User userInDb = identityService.createUserQuery().userId("gg").singleResult();
        assertNull(userInDb);
    }

    @Test
    public void testGroupAdd() throws Exception {
        IdentityService identityService = activitiRule.getIdentityService();
        Group group = identityService.newGroup("deptLeader");
        group.setName("部门领导");
        group.setType("assignment");
        identityService.saveGroup(group);

        List<Group> groupList = identityService.createGroupQuery().groupId("deptLeader").list();
        assertEquals(1,groupList.size());

    }
    @Test
    public void testGroupDel() throws Exception {
        IdentityService identityService = activitiRule.getIdentityService();
        identityService.deleteGroup("deptLeader");

        List<Group> groupList = identityService.createGroupQuery().groupId("deptLeader").list();
        assertEquals(0, groupList.size());
    }


    @Test
    public void testMembershipCreate() throws Exception {
        IdentityService identityService = activitiRule.getIdentityService();
        identityService.createMembership("gg","deptLeader");

        Group group = identityService.createGroupQuery().groupMember("gg").singleResult();
        assertEquals("deptLeader",group.getId());
    }
}
