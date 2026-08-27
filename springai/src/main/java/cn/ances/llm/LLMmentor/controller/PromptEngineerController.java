package cn.ances.llm.LLMmentor.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/prompt/engineer")
public class PromptEngineerController implements InitializingBean {

    @Autowired
    private ChatModel dashScopeChatModel;

    private ChatClient chatClient;

    @GetMapping("/role")
    public String role(String message){
        return chatClient.prompt(message).call().content();
    }

    @GetMapping("/shot")
    public String shot(String message){
        return chatClient.prompt()
                .system("""
                        请根据用户输入的数字，给出结果，不需要思考过程，直接给出数字结果即可，推理过程参考：
                                        1 = 5
                                        2 = 10
                                        3 = 15
                                        ，如果用户给的不是个数字，请回复:无法回答，请输入数字
                        """)
                .user(message)
                .call().content();
    }

    @GetMapping("/shot1")
    public String shot1(String message){
        return chatClient.prompt()
                .system("""
                        请你根据用户输入的问题做改写，主要有以下改写策略：
                           1、改写其中的错别字。
                           2、做内容精简，帮用户的一堆废话精简成简单的一句话
                           可以参考以下实例：

                           Input：ni好
                           Output ：{"错别字改写":"你好","内容精简":""}

                           Input：我今天心情不错，我想知道今天是什么天气才让我心情这么好的？
                           Output ：{"错别字改写":"","内容精简":"今天是什么天气？"}
                        
                        """)
                .user(message)
                .call().content();
    }

    @GetMapping("/structureOutput")
    public String structureOutput(String message){
        return chatClient.prompt("请你用json格式输出内容")
                .system("你是一个有用的助手")      //覆盖
                .user(message)
//                .options(ChatOptions.builder().model("deepseek-v4-pro-0813").build())
                .call().content();
    }

    @GetMapping("/step")
    public Flux<String> chat4(@RequestParam(value = "message") String message, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");

        return chatClient.prompt("""
                执行以下操作：
                    step1-用一句话概括下面文本。
                    step2-将摘要翻译成英语。
                    step3-在英语摘要中列出每个人名。
                    step4-输出一个 JSON 对象，其中包含以下键：english_summary，num_names。
            
                    请用换行符分隔您的答案。
            """).system("你是个ai").user(message).stream().content();
    }

    @Override
    public void afterPropertiesSet() throws Exception{
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultSystem("你是一个毒舌博主，说话很噎人，请根据用户问题，怼他")
                .defaultOptions(ChatOptions.builder().model("deepseek-v4-pro-0813").build())
                .build();
    }
}
