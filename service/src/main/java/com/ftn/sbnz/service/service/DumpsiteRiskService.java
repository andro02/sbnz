package com.ftn.sbnz.service.service;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.Dumpsite;
import com.ftn.sbnz.model.Notification;

@Service
public class DumpsiteRiskService {
    
    private static final Logger logger = LoggerFactory.getLogger(DumpsiteRiskService.class);

    public Dumpsite evaluateRisk(Dumpsite dumpsite) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession session = kc.newKieSession("dumpsiteRiskSession");

        session.setGlobal("logger", logger);

        session.insert(dumpsite);
        int fired = session.fireAllRules();
        System.out.println("Pravila aktivirana: " + fired);

        // Prikupi notifikacije
        session.getObjects(obj -> obj instanceof Notification)
               .forEach(n -> System.out.println("OBAVESTENJE: " + ((Notification)n)));

        session.dispose();
        return dumpsite;
    }
}