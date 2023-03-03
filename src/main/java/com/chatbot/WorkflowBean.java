package com.chatbot;

import com.chatbot.model.Workflow;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Data
public class WorkflowBean {
    private List<Workflow> workflowList; //holds the workflows in memory

    @Bean
    public List<Workflow> getWorkflows(@Value("${chatbot.filename}") String filename) {

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
            return this.workflowList;
        }
    }

}