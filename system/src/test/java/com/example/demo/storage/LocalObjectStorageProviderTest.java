package com.example.demo.storage;

import com.example.demo.common.ApiException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalObjectStorageProviderTest {
    @Test
    void uploadUsesGeneratedSafeKeyAndDeleteRemovesFile() throws Exception {
        var root = Files.createTempDirectory("object-storage-test");
        var provider = new LocalObjectStorageProvider(root, "http://localhost");
        String key = provider.upload(new UploadObject(new ByteArrayInputStream("ok".getBytes()), "a.txt", "text/plain", 2));
        assertTrue(key.matches("\\d{4}-\\d{2}-\\d{2}/[0-9a-f-]+\\.txt"));
        assertTrue(Files.exists(provider.resolve(key)));
        assertTrue(provider.getAccessUrl(key).contains("/retail-resource/oss/file/"));
        provider.delete(key);
        assertTrue(Files.notExists(provider.resolve(key)));
    }

    @Test
    void rejectsTraversalKey() throws Exception {
        var provider = new LocalObjectStorageProvider(Files.createTempDirectory("object-storage-test"));
        assertThrows(ApiException.class, () -> provider.delete("../outside.txt"));
    }
}
