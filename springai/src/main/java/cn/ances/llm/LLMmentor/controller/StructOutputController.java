package cn.ances.llm.LLMmentor.controller;

import cn.ances.llm.LLMmentor.model.BookRecord;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/streamOutput")
public class StructOutputController implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(StructOutputController.class);
    @Autowired
    private ChatModel chatModel;

    private ChatClient chatClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        chatClient = ChatClient.builder(chatModel)
                .defaultOptions(DashScopeChatOptions.builder()
                        .temperature(0.7)
                        .model("deepseek-v4-pro-0813")
                        .build())
                .defaultAdvisors(new SimpleLoggerAdvisor())              //日志输出，debug输出入参和出参
                .build();
    }

    @RequestMapping("/call")
    public String call() {

        PromptTemplate promptTemplate = PromptTemplate.builder().template("请给我推荐基本心理学有关的书，输出格式：{format}").build();

        BeanOutputConverter<BookRecord> converter = new BeanOutputConverter<>(BookRecord.class);

        //.content()
        //.chatResponse().getResult().getOutput().getText()
        String resp = chatClient.prompt(promptTemplate.create(Map.of("format", converter.getFormat())))
                .call().chatResponse().getResult().getOutput().getText();

        BookRecord book = converter.convert(resp);

        log.info(book.toString());
        return book.title() + " " + book.author() + " " + book.desc() + " " + book.publisher() + " " + book.price();
    }

    @RequestMapping("/convert")
    public String convert() {
        //提示词 + 格式，非常简洁。
        // entity方法
        BookRecord book = chatClient.prompt("请给我推荐基本心理学有关的书")
                .call().entity(BookRecord.class);
        log.info(book.toString());

        return book.title() + " - " + book.author() + " - " + book.desc() + " - " + book.publisher() + " - " + book.price();
    }

    //实际上还是BeanOutputConverter
    @RequestMapping("/convertList")
    public String convertList() {
        // entity方法
        List<BookRecord> bookList = chatClient.prompt("请给我推荐基本心理学有关的书")
                .call().entity(new ParameterizedTypeReference<List<BookRecord>>() {
                });
        log.info(bookList.toString());

        return bookList.toString();
    }

    //只能返回Map，但是没有按照要求返回--通过提示词语言描述来达到目的
    //一般都是拿list通过stream转换为map
    @RequestMapping("/convertMap")
    public String convertMap() {
        // entity方法
        Map<String, Object> bookRecordMap = chatClient.prompt("请给我推荐几本心理学有关的书，书的内容包括书名、作者、价格、上市时间等信息，以书名作为key，书的信息作为value")
                .call().entity(new MapOutputConverter());
        log.info(bookRecordMap.toString());

        return bookRecordMap.toString();
    }
}
