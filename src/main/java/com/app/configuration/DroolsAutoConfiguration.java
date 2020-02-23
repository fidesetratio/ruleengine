package com.app.configuration;

import java.io.IOException;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.KModuleBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.app.rest.RuleEngineRestService;
import com.app.services.DroolsRuleServices;


@Configuration
public class DroolsAutoConfiguration {
    
    private static final String RULES_PATH = "rules/";
    
   
    public KieFileSystem kieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
        System.out.println("patar timotius kieFileSystem");
		/*
		 * for (Resource file : getRuleFiles()) {
		 * System.out.println(file.getFilename());
		 * kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_PATH +
		 * file.getFilename(), "UTF-8")); }
		 */
        return kieFileSystem;
    }

    private Resource[] getRuleFiles() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        return resourcePatternResolver.getResources("classpath*:" + RULES_PATH + "**/*.*");
    }
    
    @Bean
     public KieContainer kieContainer() throws IOException {
       
    	System.out.println("kie container");
    	final KieRepository kieRepository = getKieServices().getRepository();
        
        kieRepository.addKieModule(new KieModule() {
            public ReleaseId getReleaseId() {
                return kieRepository.getDefaultReleaseId();
            }
        });
        
        KieBuilder kieBuilder = getKieServices().newKieBuilder(kieFileSystem());
        kieBuilder.buildAll();

        KieContainer kieContainer=getKieServices().newKieContainer(kieRepository.getDefaultReleaseId());

        DroolsRuleServices.kieContainer=kieContainer;
        
        return kieContainer;
    }
    
    private KieServices getKieServices() {
        return KieServices.Factory.get();
    }
    
    @Bean

    public KieBase kieBase() throws IOException {
        return kieContainer().getKieBase();
    }
    
    @Bean
    public KieSession kieSession() throws IOException {
    	System.out.println("session is created");
        return kieContainer().newKieSession();
    }

    @Bean
    @ConditionalOnMissingBean(KModuleBeanFactoryPostProcessor.class)
    public KModuleBeanFactoryPostProcessor kiePostProcessor() {
        return new KModuleBeanFactoryPostProcessor();
    }
}

