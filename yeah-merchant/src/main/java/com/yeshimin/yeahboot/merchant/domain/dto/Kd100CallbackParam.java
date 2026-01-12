package com.yeshimin.yeahboot.merchant.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class Kd100CallbackParam {

    private String kuaidicom;
    private String kuaidinum;
    private String status;
    private String message;
    private DataObj data;

    @Data
    public static class DataObj {
        private String orderId;
        private Integer status;
        private String courierName;
        private String courierMobile;
        private String weight;
        private String freight;
        private List<FeeDetail> feeDetails;
    }

    @Data
    public static class FeeDetail {
        private String feeType;
        private String feeDesc;
        private String amount;
        private Integer payStatus;
    }
}
