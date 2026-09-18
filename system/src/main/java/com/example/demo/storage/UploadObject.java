package com.example.demo.storage;

import java.io.InputStream;

public record UploadObject(InputStream content, String originalFilename, String contentType, long size) {
}
