package cn.ances.llm.LLMmentor.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class DailyDemoController {

    public static void main(String[] args) {
        test1();
        test2();
    }

    /**
     * completableFuture链式回调（非阻塞）
     */
    static void test1(){
        CompletableFuture.supplyAsync(() -> "hello")
                .thenApply(s -> s + ",world1")           //转换结果
                .thenAccept(System.out::println)        //消费结果
                .thenRun(() -> System.out.println("done"));     //后续动作
    }

    /**
     * completableFuture异步执行与结果获取
     */
    static void test2(){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(()->{
            try {
                System.out.println(System.currentTimeMillis());
                Thread.sleep(3000);
                System.out.println(System.currentTimeMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return "result";
        });
        System.out.println(future.join());
    }
}
