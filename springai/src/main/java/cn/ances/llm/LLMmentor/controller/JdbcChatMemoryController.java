package cn.ances.llm.LLMmentor.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/jdbcMemory")
public class JdbcChatMemoryController implements InitializingBean {

    @Autowired
    private ChatModel dashScopeChatModel;

    private ChatClient chatClient;

    //使用jdbc做持久化
    @Autowired
    private ChatMemory jdbcChatMemory;

    @Override
    public void afterPropertiesSet() throws Exception {

        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(jdbcChatMemory).build(), new SimpleLoggerAdvisor())         //实现Logger 的 Advisor
                .defaultSystem("请用英文回答问题，然后另起一段，做出中文翻译的回答")
                .defaultOptions(DashScopeChatOptions.builder()
                        .temperature(0.7)
                        .model("deepseek-v4-pro-0813")
                        .build())
                .build();
    }

    @RequestMapping("/callJdbc")
    public Flux<String> callJdbc(String message, String chatId, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");

        return chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
}
