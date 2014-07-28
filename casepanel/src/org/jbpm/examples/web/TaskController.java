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

package org.jbpm.examples.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jbpm.examples.ejb.ProcessBean;
import org.jbpm.examples.ejb.TaskBean;
import org.kie.api.definition.process.Node;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

@Model
public class TaskController {

	@EJB
	TaskBean taskBean;

	@Inject
	FacesContext facesContext;

	private String comment;
	private Map<String, Object> content;
	private Task task;
	private long taskId;
	private long processId;

	public long getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(long refundAmt) {
		this.refundAmt = refundAmt;
	}

	private long refundAmt;

	public long getProcessId() {
		return processId;
	}

	public void setProcessId(long processId) {
		this.processId = processId;
	}

	private List<TaskSummary> tasks;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Map<String, Object> getContent() {
		return content;
	}

	public void setContent(Map<String, Object> content) {
		this.content = content;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	@Produces
	public List<TaskSummary> getTasks() {
		return tasks;
	}

	public void retrieveTasks() {
		String user = FacesContext.getCurrentInstance().getExternalContext()
				.getRemoteUser();
		String message;
		try {
			tasks = taskBean.retrieveTaskList(user);
			message = "Retrieved " + tasks.size() + " task(s) for user " + user
					+ ".";
		} catch (Exception e) {
			message = "Cannot retrieve task list for user " + user + ".";
			facesContext.getExternalContext().getFlash().put("msg", message);
		}
	}

	public void queryTask() {
		String message;
		try {
			task = taskBean.getTask(taskId);
			content = taskBean.getContent();
			message = "Loaded task " + taskId + ".";

			nodes = taskBean.getNodes(this.processId);

		} catch (Exception e) {
			message = "Unable to query for task with id = " + taskId;
			facesContext.getExternalContext().getFlash().put("msg", message);
		}
	}

	private List<Activities> nodes;

	public List<Activities> getNodes() {
		return nodes;
	}

	public void setNodes(List<Activities> nodes) {
		this.nodes = nodes;
	}

	public String approveTask() {
		String message;
		try {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("out_comment", comment);
			result.put("refundamount", refundAmt);
			taskBean.approveTask(FacesContext.getCurrentInstance()
					.getExternalContext().getRemoteUser(), taskId, result);
			message = "The task " + taskId + " has been successfully ccompleted.";

		} catch (Exception e) {
			message = "Unable to approve the task " + taskId + ".";
		}
		facesContext.getExternalContext().getFlash().put("msg", message);
		return "dashboard.xhtml?faces-redirect=true";
	}

	public String logout() {
		String message = "User has logged out";

		facesContext.getExternalContext().getFlash().put("msg", message);

		facesContext.getExternalContext().invalidateSession();
		return "/login.jsp?faces-redirect=true";
	}
}
