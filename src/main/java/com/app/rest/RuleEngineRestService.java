package com.app.rest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.data.Order;
import com.app.services.DroolsRuleServices;

@RestController
@RequestMapping("/rule")
public class RuleEngineRestService {
	@Autowired
	private DroolsRuleServices services;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private KieSession session;

	
	
	@RequestMapping(value="/reload", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> reload( ) throws ParseException{
		services.reload();
		ResponseEntity<String> entity = new ResponseEntity<>("ok",HttpStatus.OK);
		return entity;
	}
	
	@PostMapping("/order")
	public Order orderNow(@RequestBody Order order) {
		 KieSession session = DroolsRuleServices.kieContainer.newKieSession();

		 
		session.insert(order);
		
		int count = session.fireAllRules();
		session.destroy();
		System.out.println("count for fireAllRules:"+count);
		return order;
	}
		
	
	@PostMapping("/order3")
	public Order orderNow3(@RequestBody Order order) {
		

		 
		session.insert(order);
		
		int count = session.fireAllRules();
		System.out.println("count for fireAllRules:"+count);
		return order;
	}
		
	
	@PostMapping("/order2")
	public Order orderNow2(@RequestBody Order order) {
		 KieSession session = DroolsRuleServices.kieContainer.newKieSession();
		 
		 
				File resource;
				String ruleContent = null;
				try {
					resource = resourceLoader.getResource("classpath:rules/offer.drl").getFile();
				
					ruleContent = new String(
						      Files.readAllBytes(resource.toPath()));
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 
				 
		 try {
		        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
		        kb.add(ResourceFactory.newByteArrayResource(ruleContent.getBytes("utf-8")), ResourceType.DRL);
		        KnowledgeBuilderErrors errors = kb.getErrors();
		        for (KnowledgeBuilderError error : errors) {
		            System.out.println(error);
		        }
		        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
		        kBase.addPackages(kb.getKnowledgePackages());
		        session = kBase.newKieSession();
		       session.insert(order);
		    	int count = session.fireAllRules();
		    	System.out.println("count:"+count);
		    	session.destroy();
	 } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	 }
		 finally {
		        if (session != null)
		        	session.dispose();
		    }
		 
	
		
		return order;
	}
		
	

}
