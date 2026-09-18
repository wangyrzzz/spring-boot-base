package com.example.demo.system;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentServiceTest {
    @Test
    void releasedCodeIsBoundedAndKeepsDeleteMarker() {
        String result = DocumentService.releasedCode("x".repeat(128), 12345L);
        assertTrue(result.length() <= 128);
        assertTrue(result.contains("_delete_"));
    }
}
