package cn.ances.llm.LLMmentor.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeTools {

    /**
     * 没有现成的工具，重新定义一个工具给LLM使用
     * @param zoneId
     * @return
     */
    @Tool(description = "Get time by zone id")
    public String getTimeByZoneId(@ToolParam(description = "Time zone id,such as Aisa/Shanghai") String zoneId){
        System.out.println("getTimeZoneId,zoneId=" + zoneId);

        //ZoneId、ZonedDateTime是Java.time的方法
        ZoneId zid = ZoneId.of(zoneId);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zid);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

        return zonedDateTime.format(formatter);
    }
}
