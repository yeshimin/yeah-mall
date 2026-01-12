package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.pojo.HttpResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.CloudAttachmentReq;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.PrintBaseResp;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import java.net.URLEncoder;
import java.util.Map;

/**
 * 附件云打印
 *
 * @Author: api.kuaidi100.com
 * @Date: 2020-07-20 9:26
 */
public class CloudPrintAttachment extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return null;
    }

    @Override
    public HttpResult execute(BaseRequest request) throws Exception{
        if (request instanceof CloudAttachmentReq){
            CloudAttachmentReq cloudPrintAttachmentReq = (CloudAttachmentReq)request;
            String url = String.format(ApiInfoConstant.CLOUD_PRINT_URL,
                    cloudPrintAttachmentReq.getMethod(),
                    cloudPrintAttachmentReq.getT(),
                    cloudPrintAttachmentReq.getKey(),
                    cloudPrintAttachmentReq.getSign(),
                    URLEncoder.encode(cloudPrintAttachmentReq.getParam(), "UTF-8"));
            return HttpUtils.doPostFile(url,cloudPrintAttachmentReq.getFile(),super.getConnectTimeout(),super.getSocketTimeout());
        }
        throw new ClassCastException();
    }

    public PrintBaseResp print(CloudAttachmentReq cloudPrintAttachmentReq) throws Exception{
        HttpResult httpResult = this.execute(cloudPrintAttachmentReq);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<PrintBaseResp<Map<String,String>>>(){}.getType());
        }
        return null;
    }
}
