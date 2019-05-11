package com.uetty.rule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Locale;

@RestController
@SpringCloudApplication
public class RuleEngineApplication {

    @Autowired
    private MessageSource messageSource;

    public static void main(String[] args) {
        SpringApplication.run(RuleEngineApplication.class, args);
//        String path = "D:/idea/work/cloud-learn-parent/rule-engine/target/classes/script/hget.lua";
//        File file = new File(path);
//        System.out.println(file.exists());

    }

    @GetMapping("/")
    public Mono<String> index() {
        Locale locale = LocaleContextHolder.getLocale();
        String name = messageSource.getMessage("name", null, locale);
        return Mono.just(name);
    }

}
