package com.gdut.ai.exception;

public class AskException extends Exception{

        public static final long serialVersionUID = 1L;

        public AskException(){
            super();
        }

        public AskException(String msg){
            super(msg);
        }

        public AskException(Throwable cause) {
            super(cause);
        }

        public AskException(String msg, Throwable e){
            super(msg,e);
        }
}
