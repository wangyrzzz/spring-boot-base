package com.example.demo.storage;

public interface ObjectStorageProvider {
    String upload(UploadObject object);

    void delete(String objectKey);

    String getAccessUrl(String objectKey);
}
