package cn.ances.llm.LLMmentor.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.math.BigDecimal;

//以JsonPropertyDescription做解释
public record BookRecord (@JsonPropertyDescription("书名，以中文展示") String title,

                          @JsonPropertyDescription("作者，以中文展示") String author,

                          @JsonPropertyDescription("简介，以中文展示") String desc,

                          @JsonPropertyDescription("价格，人民币，以分为单位") BigDecimal price,

                          @JsonPropertyDescription("出版社，以中文展示") String publisher){
}
