package com.yeshimin.yeahboot.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

@AllArgsConstructor
public class StorageGetResult {

    private Object content;

    @Getter
    private boolean isRedirect;

    public InputStream getInputStream() {
        return this.isRedirect ? null : (InputStream) content;
    }

    public String getRedirectUrl() {
        return this.isRedirect ? (String) content : null;
    }
}
