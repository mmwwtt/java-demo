package com.mmwwtt.demo.common.entity.多态;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * 多态序列化  @JsonTypeInfo和@JsonSubTypes  根据入参决定序列化成哪个子类对象
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        visible = true,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Dog.class, name = "dog"),
        @JsonSubTypes.Type(value = Brid.class, name = "brid")
})
@Data
public class Animal {
    public String type;
}
