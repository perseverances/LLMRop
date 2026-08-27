package cn.ances.llm.LLMmentor.controller;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/prompt/template")
public class PromptTemplateController implements InitializingBean {

    @Autowired
    private ChatModel dashScopeChatModel;

    private ChatClient chatClient;

    @Override
    public void afterPropertiesSet() throws Exception{
        chatClient = ChatClient.builder(dashScopeChatModel)
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .temperature(0.7)
                                .model("deepseek-v4-pro-0813")
                                .build()
                )
                .build();
    }

    @GetMapping("/call")
    public String call(String topic){
        String template = """
                请给我推荐几个关于{topic}的开源项目
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        promptTemplate.add("topic",topic);

        return chatClient.prompt(promptTemplate.create()).call().content();
    }

    @GetMapping("/stream")
    public Flux<String> callStream(String topic, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");

        String template = """
                请给我推荐几个关于{topic}的开源项目
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        promptTemplate.add("topic",topic);

        return chatClient.prompt(promptTemplate.create()).stream().content();
    }

    @GetMapping("/stream1")
    public Flux<String> callStream1(String topic, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");

        String template = """
                请给我推荐几个关于{topic}的开源项目
                """;

        return chatClient.prompt(new PromptTemplate(template).create(Map.of("topic",topic))).stream().content();
    }

    //提示词，通过Resource注入
    @Value("classpath:/templates/open-source-system-prompt.st")
    private Resource systemPrompt;

    @GetMapping("/file")
    public Flux<String> file(@RequestParam(value = "message") String message, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");

        HashMap variables = new HashMap();
        variables.put("language", "Java");
        variables.put("topic", message);
        //构建PromptTemplate
        PromptTemplate promptTemplate = PromptTemplate.builder().resource(systemPrompt).variables(variables).build();
//        PromptTemplate promptTemplate1 = new PromptTemplate(systemPrompt).create(variables);

        return chatClient.prompt(promptTemplate.create()).system("你是一个专业的的github项目收集人员").stream().content();
    }

}
