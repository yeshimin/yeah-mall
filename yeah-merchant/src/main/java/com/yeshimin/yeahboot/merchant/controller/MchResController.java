package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.merchant.service.MchResService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家资源表
 */
@RestController
@RequestMapping("/mchRes")
@RequiredArgsConstructor
public class MchResController {

    private final MchResService service;
}
