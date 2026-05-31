package com.ftn.sbnz.service.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.Dumpsite;
import com.ftn.sbnz.model.DumpsiteDetectionEvent;
import com.ftn.sbnz.model.FulfilledPrerequisite;
import com.ftn.sbnz.model.LogisticsOrder;
import com.ftn.sbnz.model.MissingPrerequisite;
import com.ftn.sbnz.model.Notification;
import com.ftn.sbnz.model.PrerequisiteResult;

@Service
public class DumpsiteRiskService {
    
    private static final Logger logger = LoggerFactory.getLogger(DumpsiteRiskService.class);

    private KieSession cepSession;

    @PostConstruct
    public void initCepSession() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        cepSession = kc.newKieSession("cepKsession");
    }

    @PreDestroy
    public void destroyCepSession() {
        if (cepSession != null) {
            cepSession.dispose();
        }
    }

    public void processDetectionEvent(DumpsiteDetectionEvent event) {
        cepSession.insert(event);
        cepSession.fireAllRules();
        cepSession.getObjects(obj -> obj instanceof Notification)
            .forEach(n -> System.out.println("CEP EVENT: " + n));
    }   

    public Dumpsite evaluateRisk(Dumpsite dumpsite) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession session = kc.newKieSession("forwardKsession");

        session.setGlobal("logger", logger);
        session.insert(dumpsite);

        int fired = session.fireAllRules();
        System.out.println("Pravila aktivirana: " + fired);

        session.getObjects(obj -> obj instanceof Notification)
            .forEach(n -> System.out.println("OBAVESTENJE: " + ((Notification) n)));

        session.dispose();

        // Nakon forward chaininga generiši logistički nalog
        LogisticsOrder order = generateLogisticsOrder(dumpsite);
        if (order != null) {
            dumpsite.setLogisticsOrder(order);
            System.out.println("LOGISTICKI NALOG: " + order);
        }

        // Backward - proveri preduslove za sanaciju
        PrerequisiteResult prerequisites = checkPrerequisites(dumpsite, new ArrayList<>());
        dumpsite.setPrerequisiteResult(prerequisites);
        System.out.println("PREDUSLOVI ZA SANACIJU: " + prerequisites);

        return dumpsite;
    }

    public PrerequisiteResult checkPrerequisites(Dumpsite dumpsite, List<String> fullfilledPrerequisites) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession session = kc.newKieSession("backwardKsession");

        LogisticsOrder order = dumpsite.getLogisticsOrder();

        session.insert(dumpsite);
        if (order != null) {
            session.insert(order);
        }
        // Insertuj ispunjene preduslove
        if (fullfilledPrerequisites != null) {
            fullfilledPrerequisites.forEach(name -> session.insert(new FulfilledPrerequisite(name)));
        }
        session.fireAllRules();

        List<String> allPrerequisites = new ArrayList<>();
        QueryResults all = session.getQueryResults("isPrerequisiteOf",
                                                Variable.v, "SanacijaDeponije");
        for (QueryResultsRow row : all) {
            String prereq = (String) row.get("x");
            if (prereq != null && !allPrerequisites.contains(prereq)) {
                allPrerequisites.add(prereq);
            }
        }

        List<String> missing = session.getObjects(obj -> obj instanceof MissingPrerequisite)
                .stream()
                .map(obj -> ((MissingPrerequisite) obj).getPrerequisiteName())
                .collect(java.util.stream.Collectors.toList());

        System.out.println("=== BACKWARD CHAINING ===");
        System.out.println("Nalog: " + (order != null ? order.getMehanization() : "nema"));
        System.out.println("Rok: " + (order != null ? order.getDeadlineDays() + " dana" : "nema"));
        System.out.println("Ispunjeni: " + (fullfilledPrerequisites != null ? fullfilledPrerequisites : "nema"));
        System.out.println("Svi preduslovi: " + allPrerequisites);
        System.out.println("Nedostajuci: " + missing);
        System.out.println("Spremnost za sanaciju: " + missing.isEmpty());

        session.dispose();
        return new PrerequisiteResult(allPrerequisites, missing);
    }

    public LogisticsOrder generateLogisticsOrder(Dumpsite dumpsite) {
        List<Map<String, Object>> rows = new ArrayList<>();

        Map<String, Object> r1 = new HashMap<>();
        r1.put("NivoRizika", "CRITICAL"); r1.put("Pristupacnost", "false");
        r1.put("Rok", "10"); r1.put("Mehanizacija", "Specijalna terenska vozila");
        r1.put("Budzet", "Prosiren");
        r1.put("Preduslovi", List.of("SpecijalnaVozilaDostupna", "PristupniPut", "ProsireniButzet", "DozvolaOpstine"));
        rows.add(r1);

        Map<String, Object> r2 = new HashMap<>();
        r2.put("NivoRizika", "CRITICAL"); r2.put("Pristupacnost", "true");
        r2.put("Rok", "3"); r2.put("Mehanizacija", "Bager + 3 kamiona");
        r2.put("Budzet", "Hitni fondovi");
        r2.put("Preduslovi", List.of("BagerDostupan", "HitniFondovi", "DozvolaOpstine"));
        rows.add(r2);

        Map<String, Object> r3 = new HashMap<>();
        r3.put("NivoRizika", "HIGH"); r3.put("Pristupacnost", "true");
        r3.put("Rok", "7"); r3.put("Mehanizacija", "Bager + 1 kamion");
        r3.put("Budzet", "Redovni");
        r3.put("Preduslovi", List.of("BagerDostupan", "RedovniBudzet", "DozvolaOpstine"));
        rows.add(r3);

        Map<String, Object> r4 = new HashMap<>();
        r4.put("NivoRizika", "HIGH"); r4.put("Pristupacnost", "false");
        r4.put("Rok", "15"); r4.put("Mehanizacija", "Kombinovana masina");
        r4.put("Budzet", "Redovni");
        r4.put("Preduslovi", List.of("KombinovanaDostavna", "PristupniPut", "RedovniBudzet", "DozvolaOpstine"));
        rows.add(r4);

        Map<String, Object> r5 = new HashMap<>();
        r5.put("NivoRizika", "MODERATE"); r5.put("Pristupacnost", "true");
        r5.put("Rok", "30"); r5.put("Mehanizacija", "Standardno komunalno vozilo");
        r5.put("Budzet", "Minimalni");
        r5.put("Preduslovi", List.of("KomunalnoVoziloDostupno", "MinimalniBudzet", "DozvolaOpstine"));
        rows.add(r5);

        Map<String, Object> r6 = new HashMap<>();
        r6.put("NivoRizika", "MODERATE"); r6.put("Pristupacnost", "false");
        r6.put("Rok", "45"); r6.put("Mehanizacija", "Traktor");
        r6.put("Budzet", "Minimalni");
        r6.put("Preduslovi", List.of("TraktordostUpan", "PristupniPut", "MinimalniBudzet", "DozvolaOpstine"));
        rows.add(r6);

        InputStream templateStream = getClass().getResourceAsStream(
                "/rules/template/logistics-template.drt");
        ObjectDataCompiler compiler = new ObjectDataCompiler();
        String drl = compiler.compile(rows, templateStream);

        KieHelper helper = new KieHelper();
        helper.addContent(drl, ResourceType.DRL);
        KieBase kieBase = helper.build();
        KieSession templateSession = kieBase.newKieSession();

        templateSession.insert(dumpsite);
        templateSession.fireAllRules();

        LogisticsOrder order = (LogisticsOrder) templateSession.getObjects(
                obj -> obj instanceof LogisticsOrder)
                .stream().findFirst().orElse(null);

        templateSession.dispose();

        // Postavi preduslove iz odgovarajuceg reda tabele
        if (order != null) {
            rows.stream()
                .filter(row -> row.get("NivoRizika").equals(
                            dumpsite.getRiskAssessment().getRiskLevel().toString()) &&
                            row.get("Pristupacnost").equals(
                            String.valueOf(dumpsite.isAccessPossible())))
                .findFirst()
                .ifPresent(row -> {
                    Object prerequisites = row.get("Preduslovi");
                    if (prerequisites instanceof List<?>) {
                        List<String> requiredPrerequisites = ((List<?>) prerequisites).stream()
                                .filter(String.class::isInstance)
                                .map(String.class::cast)
                                .collect(java.util.stream.Collectors.toList());
                        order.setRequiredPrerequisites(requiredPrerequisites);
                    }
                });
        }

        return order;
    }
}