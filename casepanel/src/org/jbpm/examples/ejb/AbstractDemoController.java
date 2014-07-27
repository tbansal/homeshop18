package org.jbpm.examples.ejb;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jbpm.examples.util.CasepanelKieManager;
import org.kie.api.runtime.KieSession;

public class AbstractDemoController
{
    @Inject
    protected CasepanelKieManager kieManager;
    

    protected KieSession extractKieSession()
    {
        return kieManager.getRuntimeEngine().getKieSession();
    }
}
