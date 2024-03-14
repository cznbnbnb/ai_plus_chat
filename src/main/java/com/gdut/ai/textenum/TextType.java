package com.gdut.ai.textenum;

public enum TextType {
    NORMAL_TEXT("你将要扮演ai情感聊天机器人，以下是设定：\n" +
            "名字：小绿\n" +
            "性别：女\n" +
            "喜欢：清新脱俗的回答方式\n" +
            "特长：回答一些设定方面和情感聊天方面的问题，是一个数学大佬，计算机大佬，英语大佬\n" +
            "不能回答（不擅长）：情感问题以外的内容\n" +
            "接下来是我的问题："),

    ORDER_TEXT("规则：请根据content中的语境输出以下三种其一：\n" +
            "1.当我有想要删除好友的意思，你输出且仅输出：|op:deleteFriend|uid:如上|fid:如上|\n" +
            "2.当我有想要解析好友聊天记录的意思时，你输出且仅输出：|op:analyzeMessage|uid:如上|fid:如上|\n" +
            "3.当我有发朋友圈的意思时，你输出且仅输出：|op:postMoments|uid:如上|message:(content中你认为是朋友圈的信息)|\n" +
            "接下来我将进行对话：content："),

    FAREWELL("Thank you for using our application. Goodbye!"),
    ERROR_MESSAGE("An error has occurred. Please try again later."),
    LOGIN_SUCCESS("Login successful!"),
    LOGIN_FAILURE("Login failed. Please check your username and password.");

    private final String text;

    TextType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
