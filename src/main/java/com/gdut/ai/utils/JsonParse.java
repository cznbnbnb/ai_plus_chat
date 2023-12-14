package com.gdut.ai.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class JsonParse {
    public Header header;
    public Payload payload;

    public static class Header {
        public int code;
        public String message;
        public String sid;
        public int status;
    }

    public static class Payload {
        public Choices choices;
        public Usage usage;
    }

    public static class Choices {
        public int status;
        public int seq;
        public List<Text> text;
    }

    public static class Usage {
        @JSONField(name = "text")
        public TextUsage textUsage;
    }

    public static class TextUsage {
        public int prompt_tokens;
        public int completion_tokens;
    }

    public static class Text {
        public String content;
        public String role;
        public int index;
    }

    public static JsonParse fromJsonString(String jsonStr) {
        return JSON.parseObject(jsonStr, JsonParse.class);
    }

}
