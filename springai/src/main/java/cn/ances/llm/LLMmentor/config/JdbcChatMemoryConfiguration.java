package cn.ances.llm.LLMmentor.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 这个类在此处有点鸡肋，ChatMemoryAutoConfiguration注入的时候，把jdbc注入进来了
 * 去掉注释，然后JdbcChatMemoryRepository报错，注释掉该文件，才能不使用持久化记忆
 */
@Configuration
public class JdbcChatMemoryConfiguration {

    @Bean
    public ChatMemory jdbcChatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository){
        return MessageWindowChatMemory.builder().chatMemoryRepository(jdbcChatMemoryRepository).maxMessages(20).build();
    }
}
