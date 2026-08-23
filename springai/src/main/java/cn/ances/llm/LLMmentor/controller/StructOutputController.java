package cn.ances.llm.LLMmentor.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/streamOutput")
public class StructOutputController implements InitializingBean {

    @Autowired
    private ChatModel chatModel;

    private ChatClient chatClient;

    @Override
    public void afterPropertiesSet() throws Exception{
        chatClient = ChatClient.builder(chatModel)
                .defaultOptions(DashScopeChatOptions.builder()
                        .temperature(0.7)
                        .model("deepseek-v4-pro-0813")
                        .build())
                .defaultAdvisors(new SimpleLoggerAdvisor())              //日志输出，debug输出入参和出参
                .build();
    }

    @RequestMapping("/")
    public String call(String message){
        return chatClient.prompt(message).call().content();
    }
}
