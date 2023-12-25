package com.memory.search.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章类型枚举
 */
public enum ArticleTypeEnum {

    POST("后端", "0"),
    PICTURE("前端", "1"),
    ARTICLE("Android", "2"),
    IOS("ios", "3"),
    AI("人工智能", "4"),
    DEV("开发工具", "5"),
    LIFE("代码人生", "6"),
    READ("阅读", "7");

    private final String text;

    private final String value;

    ArticleTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ArticleTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ArticleTypeEnum anEnum : ArticleTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
