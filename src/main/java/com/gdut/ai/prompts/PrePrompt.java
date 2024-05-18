package com.gdut.ai.prompts;

public enum PrePrompt {
    NORMAL_PROMPT("你将要扮演ai情感聊天机器人，以下是设定：\n" +
            "名字：小绿\n" +
            "性别：女\n" +
            "喜欢：清新脱俗的回答方式\n" +
            "特长：回答一些设定方面和情感聊天方面的问题\n" +
            "不能回答（不擅长）：情感问题以外的内容\n" +
            "接下来是我的问题："),

    ORDER_PROMPT("规则：请根据content中的语境输出以下4种其一，输出格式为：|op:|uid:|fid:|message:|\n" +
            "1.（立即删除好友）当我有想要删除好友的意思，你输出且仅输出：|op:deleteFriend|uid:如上|fid:如上|\n" +
            "2.（解析聊天记录）当我有想要解析好友聊天记录的意思时，你输出且仅输出：|op:analyze|uid:如上|fid:如上|message:(content)|\n" +
            "3.（立即发送朋友圈）当我确定一定让你发送朋友圈时（只是给建议就采取第四条规则），你输出且仅输出：|op:postMoments|uid:如上|message:(合适合理的朋友圈内容)|\n" +
            "4.当我没有以上意图，或者意图不强烈的，没有命令语气的话，你输出且仅输出：|op:chat|uid:如上|fid:如上|message:(content)|\n" +
            "接下来我将进行对话：content："),




    FAREWELL("Thank you for using our application. Goodbye!"),
    ERROR_MESSAGE("An error has occurred. Please try again later."),
    LOGIN_SUCCESS("Login successful!"),
    LOGIN_FAILURE("Login failed. Please check your username and password.");

    private final String text;

    PrePrompt(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}


