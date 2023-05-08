package com.lianxi.activiti.controller;


import com.lianxi.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * activiti的流程
 * https://juejin.cn/post/6997652315373043749
 * https://zhuanlan.zhihu.com/p/517122312
 *
 * @author ruoyi
 */
@RestController
@Slf4j
public class ActivitiController {
    //得到RepositoryService实例
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /**
     * 流程部署
     *
     * @return
     */
    @GetMapping("/deployment")
    public R deployment() {
        //使用RepositoryService进行部署
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource("processes/Process1.bpmn20.xml");
        builder.addClasspathResource("processes/Process1.png");
        builder.name("first_activiti_process");
        Deployment deployment = builder.deploy();
        //输出部署信息
        log.info("流程部署id：{}", deployment.getId());
        log.info("流程部署名称：{}", deployment.getName());
        return R.ok();
    }

    /**
     * 流程实例启动
     *
     * @return
     */
    @GetMapping("/testStartProcess")
    public R testStartProcess() {
        Map<String, Object> map = new HashMap<>();

        //使用UEL 表达式设置

        // 学生填写申请单    Assignee：${student}
        map.put("student", "lucy");

        // 班主任审批    Assignee：${teacher}
        map.put("teacher", "jack");
        //根据流程定义Id启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess_1", map);

        //输出实例信息
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("当前活动Id：" + processInstance.getActivityId());

        //流程定义id：myProcess_1:1:12702ed4-ffd9-11eb-8847-02004c4f4f50
        //流程实例id：a9b162aa-ffda-11eb-bad1-02004c4f4f50
        //当前活动Id：null
        return R.ok();
    }

    /**
     * 任务查询
     *
     * @return
     */
    @GetMapping("/testFindPersonalTaskList")
    public R testFindPersonalTaskList() {
        //任务负责人
        String assignee = "lucy";

        //根据流程key 和 任务负责人 查询任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("myProcess_1")
//                .taskAssignee(assignee)
                .list();

        for (Task task : list) {

            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());

            System.out.println("任务名称：" + task.getName());

        }

        //流程实例id：a9b162aa-ffda-11eb-bad1-02004c4f4f50
        //任务id：a9b5815e-ffda-11eb-bad1-02004c4f4f50
        //任务负责人：liuky
        //任务名称：提交申请
        return R.ok();
    }

    /**
     * 完成任务
     *
     * @return
     */
    @GetMapping("/completTask")
    public R completTask() {

        //根据流程key和任务的负责人查询任务并选择其中的一个任务处理,这里用的
        //是singleResult返回一条，真实环境中是通过步骤5中查询出所有的任务，然后在页面上选择一个任务进行处理.
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("myProcess_1") //流程Key
                .taskAssignee("lucy")  //要查询的负责人
                .singleResult();

        //完成任务,参数：任务id
        taskService.complete(task.getId());
        return R.ok();
    }

    /**
     * 流程结束，或流程流转过程中的历史信息查询
     *
     * @return
     */
    @GetMapping("/findHistoryInfo")
    public R findHistoryInfo() {

        //获取 actinst表的查询对象
        HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
        //查询 actinst表，条件：根据 InstanceId 查询
        instanceQuery.processInstanceId("35080cff-ed50-11ed-ad85-0a0027000004");
        //增加排序操作,orderByHistoricActivityInstanceStartTime 根据开始时间排序 asc 升序
        instanceQuery.orderByHistoricActivityInstanceStartTime().asc();
        //查询所有内容
        List<HistoricActivityInstance> activityInstanceList = instanceQuery.list();
        //输出结果
        for (HistoricActivityInstance hi : activityInstanceList) {

            System.out.println("");
            System.out.println("===================-===============");
            System.out.println(hi.getStartTime());
            System.out.println(hi.getAssignee());
            System.out.println(hi.getActivityId());
            System.out.println(hi.getActivityName());
            System.out.println(hi.getProcessDefinitionId());
            System.out.println(hi.getProcessInstanceId());
            System.out.println("===================-===============");
            System.out.println("");

        }
        return R.ok();
    }

    /**
     * 流程定义信息查询
     */
    public void queryProcessDefinition() {

        //得到ProcessDefinitionQuery对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //查询出当前所有的流程定义
        List<ProcessDefinition> definitionList = processDefinitionQuery.processDefinitionKey("myProcess_1")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();

        //打印结果
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println("流程定义 id=" + processDefinition.getId());
            System.out.println("流程定义 name=" + processDefinition.getName());
            System.out.println("流程定义 key=" + processDefinition.getKey());
            System.out.println("流程定义 Version=" + processDefinition.getVersion());
            System.out.println("流程部署ID =" + processDefinition.getDeploymentId());
        }

    }

    //删除流程
    public void deleteDeployment() {

        String deploymentId = "125098e1-ffd9-11eb-8847-02004c4f4f50";

        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        repositoryService.deleteDeployment(deploymentId);

        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式，如果流程
        //repositoryService.deleteDeployment(deploymentId, true);
    }
}