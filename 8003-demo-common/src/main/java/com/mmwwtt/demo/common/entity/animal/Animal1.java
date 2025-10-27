package com.mmwwtt.demo.common.entity.animal;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "name"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Dog.class, name = "dog")
})
@Data
@Slf4j
public class Animal1 {
    private String sort  = "动物";

    public String name = "动物";
    public void say() {
        log.info("hello");
    }
    public String getSort() {
        return sort;
    }
}
