package com.yeshimin.yeahboot.basic.service.storage;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 可自动删除文件的InputStream
 */
@Slf4j
public class TempFileInputStream extends FileInputStream {

    private final File tempFile;

    public TempFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.tempFile = file;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                log.info("delete temp file: {}, result: {}", tempFile.getAbsolutePath(), deleted);
            }
        }
    }
}
