package cn.ances.llm.LLMmentor.controller;

import cn.ances.llm.LLMmentor.model.Book;
import cn.ances.llm.LLMmentor.model.BookRecord;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/streamOutput")
public class StructOutputController implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(StructOutputController.class);
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

    @RequestMapping("/call")
    public String call(String message){

        PromptTemplate promptTemplate = PromptTemplate.builder().template("请给我推荐基本心理学有关的书，输出格式：{format}").build();

        BeanOutputConverter<BookRecord> converter = new BeanOutputConverter<>(BookRecord.class);

        //.content()
        //.chatResponse().getResult().getOutput().getText()
        String resp = chatClient.prompt(promptTemplate.create(Map.of("format",converter.getFormat())))
                .call().chatResponse().getResult().getOutput().getText();

        BookRecord book = converter.convert(resp);

        log.info(book.toString());
        return book.title() + " " + book.author() + " " + book.desc() + " " + book.publisher() + " " + book.price();
    }

    @RequestMapping("/convert")
    public String convert(){
        //提示词 + 格式，非常简洁。
        // entity方法
        BookRecord book = chatClient.prompt("请给我推荐基本心理学有关的书")
                .call().entity(BookRecord.class);
        log.info(book.toString());

        return book.title() + " - " + book.author() + " - " + book.desc() + " - " + book.publisher() + " - " + book.price();
    }
}
