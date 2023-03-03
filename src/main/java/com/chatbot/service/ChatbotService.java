package com.chatbot.service;

import com.chatbot.WorkflowBean;
import com.chatbot.model.ChatbotMessage;
import com.chatbot.model.IncomingMessage;
import com.chatbot.model.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class ChatbotService {

    @Autowired
    private WorkflowBean workflowBean;

    private final String START = "ID_START";
    private final String INVALID = "ID_INVALID";

    public ChatbotMessage getChatbotMessage(IncomingMessage message) {
        ChatbotMessage response = new ChatbotMessage();
        String workflowId = message != null && message.getWorkflowId().isEmpty() ? START : message.getWorkflowId();
        for (Workflow w : workflowBean.getWorkflowList()) {
            if (w.getWorkflowId().equals(workflowId)) {
                response.setContent(w.getContent());
                boolean validResponse = false;
                if (message.getWorkflowId().isEmpty()) { //first entry
                    log(message, response);
                    return new ChatbotMessage(w.getContent(), workflowId);
                } else if (CollectionUtils.isEmpty(w.getNext()) && !w.getWorkflowId().equals(INVALID)) {
                    log(message, response);
                    return response;
                }
                for (IncomingMessage i : w.getNext()) {
                    if (i.getChatText().equals(message.getChatText().toLowerCase())) { //valid response
                        final String searchId = i.getWorkflowId(); //set content for the next workflow
                        Workflow next = workflowBean.getWorkflowList().stream().filter(y->y.getWorkflowId().equals(searchId)).findFirst().get();
                        response.setWorkflowId(next.getNext().size() > 0 ? next.getWorkflowId() : null);
                        response.setContent(next.getContent());
                        validResponse = true;
                        break;
                    }
                }
                if (!validResponse) { //invalid response
                    final String searchId = message.getWorkflowId();
                    StringBuffer sb = new StringBuffer();
                    sb.append(workflowBean.getWorkflowList().stream().filter(y->y.getWorkflowId().equals(INVALID)).findFirst().get().getContent()).append("<br>");
                    sb.append(workflowBean.getWorkflowList().stream().filter(y->y.getWorkflowId().equals(searchId)).findFirst().get().getContent());
                    response.setContent(sb.toString());
                    response.setWorkflowId(searchId);
                    break;
                }
                break;
            }
        }
        log(message, response);
        return response;
    }

    private void log(IncomingMessage message, ChatbotMessage response) {
        System.out.println("Chat : " + message.toString() + " Bot Message : " + response.toString());
    }
}
