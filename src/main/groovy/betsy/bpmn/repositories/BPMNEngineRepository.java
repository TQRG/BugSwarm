package betsy.bpmn.repositories;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.activiti.Activiti5170Engine;
import betsy.bpmn.engines.activiti.ActivitiEngine;
import betsy.bpmn.engines.camunda.Camunda710Engine;
import betsy.bpmn.engines.camunda.Camunda720Engine;
import betsy.bpmn.engines.camunda.Camunda730Engine;
import betsy.bpmn.engines.camunda.CamundaEngine;
import betsy.bpmn.engines.jbpm.JbpmEngine;
import betsy.bpmn.engines.jbpm.JbpmEngine610;
import betsy.bpmn.engines.jbpm.JbpmEngine620;
import betsy.common.repositories.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BPMNEngineRepository {
    public BPMNEngineRepository() {
        List<AbstractBPMNEngine> all = new ArrayList<>(Arrays.asList(new CamundaEngine(), new Camunda710Engine(),
                new Camunda720Engine(), new Camunda730Engine(), new ActivitiEngine(), new Activiti5170Engine(),
                new JbpmEngine(), new JbpmEngine610(), new JbpmEngine620()));
        repo.put("ALL", all);

        // insert every engine into the map
        for (AbstractBPMNEngine engine : repo.getByName("ALL")) {
            repo.put(engine.getName(), Collections.singletonList(engine));
        }

    }

    public List<AbstractBPMNEngine> getByName(String name) {
        return repo.getByName(name);
    }

    public List<AbstractBPMNEngine> getByNames(String... names) {
        return repo.getByNames(names);
    }

    public List<String> getNames() {
        return repo.getNames();
    }

    private Repository<AbstractBPMNEngine> repo = new Repository<>();
}
