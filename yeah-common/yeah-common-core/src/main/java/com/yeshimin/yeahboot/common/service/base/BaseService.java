package com.yeshimin.yeahboot.common.service.base;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;

/**
 * 基类Service
 */
public abstract class BaseService {

    @SneakyThrows
    private ResponseEntity<InputStreamResource> wrap(String filename, InputStream inputStream) {
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        // 使用 RFC 6266 编码文件名
        String encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
        headers.add("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }

    @SneakyThrows
    public ResponseEntity<InputStreamResource> wrap(String filename, InputStream inputStream, String redirectUrl) {
        if (StrUtil.isNotBlank(redirectUrl)) {
            return ResponseEntity.status(302).location(URI.create(redirectUrl)).build();
        } else {
            return this.wrap(filename, inputStream);
        }
    }

    @SneakyThrows
    private ResponseEntity<InputStreamResource> wrapForPreview(String filename, InputStream inputStream) {
        // 并不能支持所有的MIME类型，图片的可以；待优化
        String contentType = HttpUtil.getMimeType(filename);
        MediaType mediaType = MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream");

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.noCache())
                .body(new InputStreamResource(inputStream));
    }

    @SneakyThrows
    public ResponseEntity<InputStreamResource> wrapForPreview(
            String filename, InputStream inputStream, String redirectUrl) {
        if (StrUtil.isNotBlank(redirectUrl)) {
            return ResponseEntity.status(302).location(URI.create(redirectUrl)).build();
        } else {
            return this.wrapForPreview(filename, inputStream);
        }
    }
}
