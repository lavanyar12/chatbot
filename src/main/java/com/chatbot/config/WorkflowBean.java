package com.chatbot.config;

import com.chatbot.ChatbotApplication;
import com.chatbot.model.Workflow;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Data
public class WorkflowBean implements ApplicationContextAware {

    @Value("${chatbot.filename}")
    private String filename;
    private String applicationId;

    private List<Workflow> workflowList;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {

        applicationId = applicationContext.getId();
        this.loadWorkflows();
    }

    // load data from the resources folder when app starts
    private void loadWorkflows() {

        // The class loader that loaded the class
        ClassLoader classLoader = ChatbotApplication.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filename);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + filename);
        } else {
            try (InputStreamReader streamReader =
                         new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(streamReader)) {
                String line;
                StringBuffer sb = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                this.workflowList = objectMapper.readValue(sb.toString(), new TypeReference<List<Workflow>>() {
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}