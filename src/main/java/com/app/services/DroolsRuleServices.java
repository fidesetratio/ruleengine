package com.app.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.app.model.Rule;

@Service
public class DroolsRuleServices {
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	
	public static KieContainer kieContainer;
	private List<Rule>  loadRules(){
        List<Rule> rules= new ArrayList<Rule>();
//        System.out.println(rules.toString());
        try {
			File resource = resourceLoader.getResource("classpath:rules/offer.drl").getFile();
			 String content = new String(
				      Files.readAllBytes(resource.toPath()));
			 
		
			 Rule rule = new Rule();
			 rule.setId(new Long(1));
			 rule.setCreateTime("2012/20/20");
			 rule.setContent(content);
			 rule.setRuleKey("patar");
			 rule.setVersion("1.0");
			 rules.add(rule);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        return rules;
    }
	
	 public  void reload(){
	        KieContainer kieContainer=loadContainerFromString(loadRules());
	        this.kieContainer=kieContainer;
	 }


    private  KieContainer loadContainerFromString(List<Rule> rules) {
        long startTime = System.currentTimeMillis();
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();

        for (Rule rule:rules) {
            String  drl=rule.getContent();
            kfs.write("src/main/resources/" + drl.hashCode() + ".drl", drl);
        }

        KieBuilder kb = ks.newKieBuilder(kfs);

        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time to build rules : " + (endTime - startTime)  + " ms" );
        startTime = System.currentTimeMillis();
        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        endTime = System.currentTimeMillis();
        System.out.println("Time to load container: " + (endTime - startTime)  + " ms" );
        return kContainer;
    }

}
