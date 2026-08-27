package cn.ances.llm.LLMmentor.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/client")
public class ChatClientController implements InitializingBean {

    @Autowired
    private ChatModel dashScopeChatModel;

//    DefaultChatClientBuilder  需要传可观测性参数

    private ChatClient chatClient;

    //有很多额外参数，可以给我们做初始化设置
    @GetMapping("/simpleCall")
    public String simpleCall(String message){
        return chatClient.prompt(message).call().content();
    }

    @GetMapping("/callOverwrite")
    public String callOverwrite(String message){
        //.system()方法，会不会覆盖掉前面的设置。
        // 会覆盖掉。返回ChatClientRequestSpec，里面也有各种设置
        return chatClient.prompt(message).system("请用韩文回答问题").call().content();
    }

    @GetMapping("/call")
    public String call(String message){
        //提示词是追加上去的
        return chatClient.prompt(new Prompt(new SystemMessage("请用韩文回答问题"),new UserMessage(message)))
                .call().content();
    }

    @GetMapping("/callUser")
    public String callUser(String message){
        //prompt中可以多轮对话，有记忆
        return chatClient.prompt().user(message).call().content();
    }

    @GetMapping("/stream")
    public Flux<String> stream(String message, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(message).stream().content();
    }

    @Override
    public void afterPropertiesSet() throws Exception{
        chatClient = ChatClient.builder(dashScopeChatModel)
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                ).defaultSystem("请用中文+英文回答问题（回答分为两段，第1段是中文，第2段为对应的英文翻译）")
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .temperature(0.7)
                                .build()
                )
                .build();
    }
}
