/**
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.examples.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.jbpm.examples.web.Activities;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.api.bpmn2.BPMN2DataService;
import org.jbpm.kie.services.impl.model.NodeInstanceDesc;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.InternalTaskService;

@javax.ejb.Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class TaskBean extends AbstractDemoController {

	@Resource
	private UserTransaction ut;

	@Inject
	TaskService taskService;

	@Inject
	private RuntimeDataService runtimeDataService;
	@Inject
	ProcessBean processService;

	private Map<String, Object> content;

	public List<TaskSummary> retrieveTaskList(String actorId) throws Exception {
		ut.begin();
		List<TaskSummary> list;
		try {
			taskService.getTaskById(1);
			list = taskService.getTasksAssignedAsPotentialOwner(actorId,
					"en-UK");
			ut.commit();
		} catch (RollbackException e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	@Inject
	BPMN2DataService serv;

	public List<Activities> getNodes(long processInstanceId) {

		List<Activities> activities = new ArrayList<Activities>();

		RuntimeEngine runtime = processService.getSingletonManager()
				.getRuntimeEngine(EmptyContext.get());
		KieSession ksession = extractKieSession();
		Node[] nodes = ((org.jbpm.ruleflow.core.RuleFlowProcess) runtime
				.getKieSession().getKieBase().getProcess("processrefundhs18"))
				.getNodes();
		// ProcessInstance processInstance = ksession
		// .getProcessInstance(processInstanceId);
		// runtimeDataService.getProcessesByDeploymentId("2");
		Collection<NodeInstanceDesc> activeNodes = runtimeDataService
				.getProcessInstanceFullHistory("2", 1);

		for (Node node : nodes) {
			Activities activity = new Activities();
			boolean isActive = false;
			for (NodeInstanceDesc activeNode : activeNodes) {
				if (activeNode.getName().equals(node.getName())) {
					isActive = true;
				}
			}
			activity.setActive(isActive);
			activity.setActivityName(node.getName());
			activities.add(activity);
		}
		// runtimeDataService.getProcessInstances();

		// runtimeDataService.getProcesses();
		// serv.getAllTasksDef("2");
		// serv.findProcessId(bpmn2Content, classLoader);
		// ProcessInstanceDesc processInstanceDesc = runtimeDataService
		// .getProcessInstanceById(5);

		return activities;
		// return ((org.jbpm.ruleflow.instance.RuleFlowProcessInstance)
		// processInstance)
		// .getWorkflowProcess().getNodes();
	}

	public void approveTask(String actorId, long taskId,
			Map<String, Object> content) throws Exception {
		ut.begin();
		try {
			taskService.start(taskId, actorId);
			content.put("refundamount", 50000);
			taskService.complete(taskId, actorId, content);
			ut.commit();
		} catch (RollbackException e) {
			Throwable cause = e.getCause();
			if (cause != null && cause instanceof OptimisticLockException) {
				// Concurrent access to the same process instance
				throw new ProcessOperationException(
						"The same process instance has likely been accessed concurrently",
						e);
			}
			throw new RuntimeException(e);
		} catch (PermissionDeniedException e) {
			// Transaction might be already rolled back by TaskServiceSession
			if (ut.getStatus() == Status.STATUS_ACTIVE) {
				ut.rollback();
			}
			// Probably the task has already been started by other users
			throw new ProcessOperationException("The task (id = " + taskId
					+ ") has likely been started by other users ", e);
		} catch (Exception e) {
			// Transaction might be already rolled back by TaskServiceSession
			if (ut.getStatus() == Status.STATUS_ACTIVE) {
				ut.rollback();
			}
			throw new RuntimeException(e);
		}
	}

	public Task getTask(long taskId) throws Exception {
		ut.begin();
		Task task;
		try {
			task = taskService.getTaskById(taskId);
			content = ((InternalTaskService) taskService)
					.getTaskContent(taskId);
			ut.commit();
		} catch (Exception e) {
			ut.rollback();
			throw new ProcessOperationException("Cannot get task " + taskId, e);
		}
		return task;
	}

	public Map<String, Object> getContent() {
		return content;
	}

}
