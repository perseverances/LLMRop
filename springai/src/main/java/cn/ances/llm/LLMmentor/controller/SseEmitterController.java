package cn.ances.llm.LLMmentor.controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/stream")
public class SseEmitterController {

    //http://localhost:8000/stream/see
    @GetMapping("/see")
    public SseEmitter sse(){
        SseEmitter emitter = new SseEmitter(60000L);
        Executors.newVirtualThreadPerTaskExecutor().submit(()->{
            try {
                for (int i = 0; i < 10; i++) {
                    emitter.send("Message"+ i);
                    Thread.sleep(2000);
                }
            }catch (IOException | InterruptedException e){
                emitter.completeWithError(e);
            }finally {
                emitter.complete();
            }
        });

        return emitter;
    }


    //http://localhost:8000/stream/see/streaming
    @GetMapping("/see/streaming")
    public ResponseEntity<StreamingResponseBody> chat(){
        StreamingResponseBody body = outputStream -> {
            for (int i = 0; i < 10; i++) {
                String data = "data chunk" + i+ "\n";
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();

                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    throw new RuntimeException();
                }
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE)
                .body(body);
    }




//    public Flux<String>

}
