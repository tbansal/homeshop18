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

import org.jbpm.examples.ejb.ProcessBean;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import java.util.logging.Level;
import java.util.logging.Logger;

@Model
public class ProcessController {

    @EJB
    ProcessBean processBean;

    @Inject
    FacesContext facesContext;


    private String recipient;
    private int reward = 200;


    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public String startProcess() {
        String message;
        try {
            long processInstanceId = processBean.startProcess("parthog", reward);
            message = "Process instance " + processInstanceId + " has been successfully started." ;
        } catch (Exception e) {
            message = "Unable to start the business process.";
        }
        facesContext.getExternalContext().getFlash()
                .put("msg", message);
        return "dashboard.xhtml?faces-redirect=true";
    }
}
