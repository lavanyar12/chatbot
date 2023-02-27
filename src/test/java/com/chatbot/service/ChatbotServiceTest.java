package com.chatbot.service;

import com.chatbot.config.WorkflowBean;
import com.chatbot.model.ChatbotMessage;
import com.chatbot.model.IncomingMessage;
import com.chatbot.model.Workflow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatbotServiceTest {
    @Autowired
    ChatbotService chatbotService;

    private final String START = "ID_START";
    private final String INVALID = "ID_INVALID";

    private final String CONTENT = "Hi! I have a great shift opportunity for you! Are you Interested in hearing about it?<br><br>Please respond Yes or No";

    @Autowired
    private ApplicationContext applicationContext;

    private IncomingMessage incomingMessage;

    private WorkflowBean workflowBean;

    @BeforeAll
    void setUp() {
        workflowBean = this.applicationContext.getBean(WorkflowBean.class);
        incomingMessage = new IncomingMessage("", "");
    }

    @Test
    void testFirst() {
        ChatbotMessage response = chatbotService.getChatbotMessage(incomingMessage);
        System.out.println(response.toString());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(START, response.getWorkflowId());
        Assertions.assertEquals(CONTENT, response.getContent());
    }

    @Test
    void testValidOptionStopFlow() {
        incomingMessage.setWorkflowId(START);
        incomingMessage.setChatText("yes");
        ChatbotMessage response = chatbotService.getChatbotMessage(incomingMessage);
        System.out.println(response.toString());
        Assertions.assertNotNull(response);
        final String workflowId = "ID_YES";
        Workflow w = workflowBean.getWorkflowList().stream().filter(x->x.getWorkflowId().equals(workflowId)).findFirst().get();
        Assertions.assertNull(response.getWorkflowId());
        Assertions.assertEquals(w.getContent(), response.getContent());
    }

    @Test
    void testValidOptionMixedCaseStopFlow() {
        incomingMessage.setWorkflowId(START);
        incomingMessage.setChatText("yEs");
        ChatbotMessage response = chatbotService.getChatbotMessage(incomingMessage);
        System.out.println(response.toString());
        Assertions.assertNotNull(response);
        final String workflowId = "ID_YES";
        Workflow w = workflowBean.getWorkflowList().stream().filter(x->x.getWorkflowId().equals(workflowId)).findFirst().get();
        Assertions.assertNull(response.getWorkflowId());
        Assertions.assertEquals(w.getContent(), response.getContent());
    }
    @Test
    void testValidOptionContinueFlow() {
        incomingMessage.setWorkflowId(START);
        incomingMessage.setChatText("no");
        ChatbotMessage response = chatbotService.getChatbotMessage(incomingMessage);
        System.out.println(response.toString());
        Assertions.assertNotNull(response);
        final String workflowId = "ID_NO";
        Workflow w = workflowBean.getWorkflowList().stream().filter(x->x.getWorkflowId().equals(workflowId)).findFirst().get();
        Assertions.assertNotEquals(null, response.getWorkflowId());
        Assertions.assertEquals(workflowId, response.getWorkflowId());
        Assertions.assertEquals(w.getContent(), response.getContent());
    }
    @Test
    void testInvalidOption() {
        final String workflowId = "ID_NO";
        incomingMessage.setWorkflowId(workflowId);
        incomingMessage.setChatText("some random value");
        ChatbotMessage response = chatbotService.getChatbotMessage(incomingMessage);
        System.out.println(response.toString());
        Assertions.assertNotNull(response);
        StringBuffer sb = new StringBuffer();
        sb.append(workflowBean.getWorkflowList().stream().filter(y->y.getWorkflowId().equals(INVALID)).findFirst().get().getContent()).append("<br>");
        sb.append(workflowBean.getWorkflowList().stream().filter(y->y.getWorkflowId().equals(workflowId)).findFirst().get().getContent());
        Assertions.assertNotEquals(null, response.getWorkflowId());
        Assertions.assertEquals(workflowId, response.getWorkflowId());
        Assertions.assertEquals(sb.toString(), response.getContent());
    }

}