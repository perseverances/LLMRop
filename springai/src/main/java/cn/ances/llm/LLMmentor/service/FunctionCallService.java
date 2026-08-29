package cn.ances.llm.LLMmentor.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FunctionCallService {

    public Response getTimeByZoneId(Request request) {
        System.out.println("getTimeZoneId,zoneId=" + request.zoneId);

        //ZoneId、ZonedDateTime是Java.time的方法
        ZoneId zid = ZoneId.of(request.zoneId);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zid);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

        return new Response(zonedDateTime.format(formatter));
    }

    public record Request(@JsonProperty(required = true, value = "zoneId")
                          @JsonPropertyDescription("时区，比如Aisa/Shanghai") String zoneId) {
    }

    public record Response(String time) {
    }
}
