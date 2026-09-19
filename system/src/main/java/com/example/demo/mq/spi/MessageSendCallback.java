package com.example.demo.mq.spi;

@FunctionalInterface
public interface MessageSendCallback {

    void onSuccess(String messageId);

    default void onFailure(String messageId, String reason) {
        // Implementations may override this when failure must be persisted.
    }
}
