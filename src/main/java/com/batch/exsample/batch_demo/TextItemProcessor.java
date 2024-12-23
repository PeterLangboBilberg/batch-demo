package com.batch.exsample.batch_demo;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TextItemProcessor implements ItemProcessor<String,String> {
    @Override
    public String process(String message) throws Exception {
       String resultText = message.replaceAll("line","row");
        return resultText;
    }
}
