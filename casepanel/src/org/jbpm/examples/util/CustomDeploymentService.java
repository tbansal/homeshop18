package org.jbpm.examples.util;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.kie.services.impl.AbstractDeploymentService;
import org.jbpm.kie.services.impl.DeployedUnitImpl;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.internal.deployment.DeploymentUnit;
import org.kie.internal.io.ResourceFactory;

// plain CustomDeploymentService solves:
// Caused by: org.jboss.weld.exceptions.DeploymentException: WELD-001408 Unsatisfied dependencies for type [DeploymentService]
// with qualifiers [@Default] at injection point [[field] @Inject private org.jbpm.kie.services.impl.form.FormProviderServiceImpl.deploymentService]
@ApplicationScoped
public class CustomDeploymentService extends AbstractDeploymentService {
	@PersistenceUnit(unitName = "org.jbpm.domain")
	private EntityManagerFactory emf;

	@Override
	public void deploy(DeploymentUnit unit) {
		// TODO Auto-generated method stub
		super.deploy(unit);

		RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory
				.get()
				.newDefaultBuilder()
				.entityManagerFactory(emf)
				.addAsset(
						ResourceFactory
								.newClassPathResource("processrefund.bpmn"),
						ResourceType.BPMN2).get();
		DeployedUnitImpl deployedUnit = new DeployedUnitImpl(unit);
		commonDeploy(unit, deployedUnit, environment);
	}

}
