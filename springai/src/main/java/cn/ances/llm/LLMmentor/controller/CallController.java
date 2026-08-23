package cn.ances.llm.LLMmentor.controller;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/call")
public class CallController {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    @RequestMapping("/string")
    public String callString(String message){
        return dashScopeChatModel.call(message);
    }

    @RequestMapping("/messages")
    public String callMessage(String message){

        SystemMessage systemMessage = new SystemMessage("你是一个翻译工具，请把用户的内容翻译成英文");
        Message userMessage = new UserMessage(message);
        return dashScopeChatModel.call(systemMessage, userMessage);
    }

    @RequestMapping("/prompt")
    public String callPrompt(String message){

        SystemMessage systemMessage = new SystemMessage("请跟我说尊敬的主人你好后，再开始回答问题");
        Message userMessage = new UserMessage(message);

        ChatOptions chatOptions = ChatOptions.builder().model("deepseek-v4-pro-0813").build();
        Prompt prompt = Prompt.builder().messages(systemMessage,userMessage).chatOptions(chatOptions).build();
        return dashScopeChatModel.call(prompt).getResult().getOutput().getText();
    }

    //流式调用大模型
    @RequestMapping("/stream")
    public Flux<String> callStreamString(String message, HttpServletResponse response){
        //解决乱码问题
        response.setCharacterEncoding("UTF-8");
        return dashScopeChatModel.stream(message);
    }

}
