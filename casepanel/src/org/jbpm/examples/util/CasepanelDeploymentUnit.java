package org.jbpm.examples.util;

import org.kie.internal.deployment.DeploymentUnit;

public class CasepanelDeploymentUnit implements DeploymentUnit{

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return "homeshop18";
	}

	@Override
	public RuntimeStrategy getStrategy() {
		// TODO Auto-generated method stub
		return  RuntimeStrategy.SINGLETON;
	}

}
