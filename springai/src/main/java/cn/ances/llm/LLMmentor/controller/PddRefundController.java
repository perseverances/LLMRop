package cn.ances.llm.LLMmentor.controller;

import cn.ances.llm.LLMmentor.model.ChatStatus;
import cn.ances.llm.LLMmentor.model.OrderChat;
import cn.ances.llm.LLMmentor.service.OrderTools;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/pdd/refund")
public class PddRefundController {

    @Autowired
    private DashScopeChatModel chatModel;

    private ChatClient chatClient;

    @Value("classpath:/templates/Pdd_refund_system_prompt.pt")
    private Resource resource;

    @PostConstruct
    public void init() {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10).build();

        chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(), new SimpleLoggerAdvisor())
                .defaultSystem(resource)        //提示词模板
                .defaultOptions(ChatOptions.builder().model("deepseek-v4-pro-0813").build())
                .build();
    }

    @GetMapping("/newChat")
    public OrderChat newChat(String userId, String orderId, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");

        //模拟数据库创建一个chat的记录，获取到他的唯一id。
        String chatId = UUID.randomUUID().toString();

        return chatClient.prompt()
                .user(String.format("我要咨询订单相关的售后问题，我的用户id是%s,我的订单号是: %s ,本地的对话Id是 %s，当前状态是 %s", userId, orderId, chatId, ChatStatus.CHAT_START.name()))
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId)
                )
                .call().entity(OrderChat.class);
    }

    @Autowired
    private OrderTools orderTools;

    @GetMapping("/ask")
    public Flux<String> ask(String question, String chatId, HttpServletResponse httpServletResponse) {
        httpServletResponse.setCharacterEncoding("UTF-8");

        return chatClient
                .prompt()
                .user(question).tools(orderTools)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param("chat_memory_retrieve_size", 100))
                .stream().content();
    }
}
