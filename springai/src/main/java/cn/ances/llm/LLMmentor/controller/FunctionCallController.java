package cn.ances.llm.LLMmentor.controller;

import cn.ances.llm.LLMmentor.service.TimeTools;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/function")
public class FunctionCallController {

    @Autowired
    private DashScopeChatModel chatModel;

    private ChatClient chatClient;

    @GetMapping("/chat")
    public String chat(@RequestParam("query") String query) {
        System.out.println("chat request => " + query);

        return chatClient.prompt().toolNames("getTimeFunction").user(query).call().content();
    }

    @PostConstruct
    public void init() {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10).build();

        chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultOptions(DashScopeChatOptions.builder().model("deepseek-v4-pro-0813").build())
                .build();
    }

    @GetMapping("/chat1")
    public String chat1(@RequestParam("query") String query) {
        System.out.println("chat request => " + query);

        return chatClient.prompt().tools(new TimeTools()).user(query).call().content();
    }
}
