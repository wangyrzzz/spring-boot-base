package com.example.demo.mq.api;

@FunctionalInterface
public interface MessageHandler {

    void handle(ReceivedMessage message) throws Exception;
}
