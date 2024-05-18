package com.gdut.ai.common;


import kotlin.jvm.Synchronized;

//对大模型的问答结果进行收集
public class ResultCollector {

    public static final int STATE_RECEIVING = 0;

    public static final int STATE_FINISHED = 2;

    private boolean canDisplay = false;

    //大模型回答
    private String answer;

    //输入token
    private double inputToken;

    //输出token
    private double outputToken;

    //回答状态
    private int state;

    //模型版本
    private String version;

    public ResultCollector(){}


    public ResultCollector(String answer, double inputToken, double outputToken, String version) {
        this.answer = answer;
        this.inputToken = inputToken;
        this.outputToken = outputToken;
        this.state = STATE_RECEIVING;
        this.version = version;
    }

    public synchronized String getAnswer() {
        return answer==null?"":answer;
    }



    public double getInputToken() {
        return inputToken;
    }

    public synchronized void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getOutputToken() {
        return outputToken;
    }

    public void setCanDisplay(boolean canDisplay) {
        this.canDisplay = canDisplay;
    }
    public boolean getCanDisplay() {
        return canDisplay;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setInputToken(double inputToken) {
        this.inputToken = inputToken;
    }

    public void setOutputToken(double outputToken) {
        this.outputToken = outputToken;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

}
