package cn.ances.llm.LLMmentor.config;

import cn.ances.llm.LLMmentor.service.FunctionCallService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class FunctionCallConfiguration {

    @Bean
    @Description("根据用户输入的时区获取该时区的当前时间")
    public Function<FunctionCallService.Request, FunctionCallService.Response> getTimeFunction(FunctionCallService functionCallService){
        //返回Function,接口的参数T是输入，参数R是result
        return functionCallService::getTimeByZoneId;
    }
}
