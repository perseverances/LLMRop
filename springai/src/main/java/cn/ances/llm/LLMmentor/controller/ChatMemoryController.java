package cn.ances.llm.LLMmentor.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chatMemory")
public class ChatMemoryController implements InitializingBean {

    @Autowired
    private ChatModel chatModel;

    private ChatClient chatClient;

    @Autowired
    private ChatMemory chatMemory;

    @Override
    public void afterPropertiesSet() throws Exception {
        //chat_memory_retrieve_size_key，已经取消，语义不明确
        //maxMessage，非常明确。用户、system都是message
//        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(7).build();

        chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(), new SimpleLoggerAdvisor())         //实现Logger 的 Advisor
                .defaultSystem("请用英文回答问题，然后另起一段，做出中文翻译的回答")
                .defaultOptions(DashScopeChatOptions.builder()
                        .temperature(0.7)
                        .model("deepseek-v4-pro-0813")
                        .build())
                .build();
    }

    @RequestMapping("/call")
    public String call() {

        List<Message> messages = new ArrayList<>();

        //第一轮对话
        messages.add(new SystemMessage("你是一个游戏设计师"));
        messages.add(new UserMessage("我想设计一个回合制游戏"));
        ChatResponse chatResponse = chatModel.call(Prompt.builder()
                .messages(messages)
                .chatOptions(ChatOptions.builder().model("deepseek-v4-pro-0813").build())
                .build()
        );
        String content = chatResponse.getResult().getOutput().getText();
        System.out.println(content);
        System.out.println("======");

        messages.add(new AssistantMessage(content));

        //第二轮对话
        messages.add(new UserMessage("能帮我结合一些二次元的元素吗?"));
        chatResponse = chatModel.call(Prompt.builder()
                .messages(messages)
                .chatOptions(ChatOptions.builder().model("deepseek-v4-pro-0813").build())
                .build()
        );
        content = chatResponse.getResult().getOutput().getText();
        System.out.println(content);
        System.out.println("======");

        messages.add(new AssistantMessage(content));

        //第三轮对话
        messages.add(new UserMessage("那如果主要是针对女性玩家的游戏呢?有什么需要改进的？"));

        Prompt prompt = Prompt.builder()
                .messages(messages)
                .chatOptions(ChatOptions.builder().model("deepseek-v4-pro-0813").build())
                .build();
        chatModel.call(Prompt.builder()
                .messages(messages)
                .chatOptions(ChatOptions.builder().model("deepseek-v4-pro-0813").build())
                .build()
        );

        return chatModel.call(prompt).getResult().getOutput().getText();
    }


    //通过AssistantMessage传入历史回答
    @RequestMapping("/call1")
    public String call1() {
        List<Message> messages = new ArrayList<>();

        //第一轮对话
        messages.add(new SystemMessage("你是一个旅行推荐师"));

        messages.add(new UserMessage("我想去新疆玩"));
        messages.add(new AssistantMessage("好的，我知道了，你要去新疆，请问你准备什么时候去"));

        messages.add(new UserMessage("我准备元旦的时候去玩"));
        messages.add(new AssistantMessage("好的，请问你想玩那些内容？"));

        messages.add(new UserMessage("我喜欢自然风光"));

        Prompt prompt = Prompt.builder().messages(messages)
                .chatOptions(ChatOptions.builder().model("deepseek-v4-pro-0813").build())
                .build();
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    //通过chat_memory_conversation_id
    //会把回答的问题带给大模型
    @RequestMapping("/callConversation")
    public Flux<String> callConversation(String message, String chatId, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");

        return chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

}
