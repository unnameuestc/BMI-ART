package com.bmi.mobileart.service.Lib;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bmi.mobileart.service.Config;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

/**
 * Created by Keith on 2015/4/12.
 */
public class SmsApi {
    /**
     * 服务http地址
     */
    private static String BASE_URI = "http://yunpian.com";
    /**
     * 服务版本号
     */
    private static String VERSION = "v1";
    /**
     * 编码格式
     */
    private static String ENCODING = "UTF-8";
    /**
     * 查账户信息的http地址
     */
    private static String URI_GET_USER_INFO = BASE_URI + "/" + VERSION + "/user/get.json";
    /**
     * 通用发送接口的http地址
     */
    private static String URI_SEND_SMS = BASE_URI + "/" + VERSION + "/sms/send.json";
    /**
     * 模板发送接口的http地址
     */
    private static String URI_TPL_SEND_SMS = BASE_URI + "/" + VERSION + "/sms/tpl_send.json";

    /**
     * 取账户信息
     *
     * @return json格式字符串
     * @throws IOException
     */
    private static String getUserInfo(String apikey) throws IOException {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(URI_GET_USER_INFO + "?apikey=" + apikey);
        HttpMethodParams param = method.getParams();
        param.setContentCharset(ENCODING);
        client.executeMethod(method);
        return method.getResponseBodyAsString();
    }

    /**
     * 发短信
     *
     * @param apikey apikey
     * @param text   　短信内容
     * @param mobile 　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */
    private static String sendSms(String apikey, String text, String mobile) throws IOException {
        HttpClient client = new HttpClient();
        NameValuePair[] nameValuePairs = new NameValuePair[3];
        nameValuePairs[0] = new NameValuePair("apikey", apikey);
        nameValuePairs[1] = new NameValuePair("text", text);
        nameValuePairs[2] = new NameValuePair("mobile", mobile);
        PostMethod method = new PostMethod(URI_SEND_SMS);
        method.setRequestBody(nameValuePairs);
        HttpMethodParams param = method.getParams();
        param.setContentCharset(ENCODING);
        client.executeMethod(method);
        return method.getResponseBodyAsString();
    }

    /**
     * 通过模板发送短信
     *
     * @param apikey    apikey
     * @param tpl_id    　模板id
     * @param tpl_value 　模板变量值
     * @param mobile    　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */
    private static String tplSendSms(String apikey, long tpl_id, String tpl_value, String mobile) throws IOException {
        HttpClient client = new HttpClient();
        NameValuePair[] nameValuePairs = new NameValuePair[4];
        nameValuePairs[0] = new NameValuePair("apikey", apikey);
        nameValuePairs[1] = new NameValuePair("tpl_id", String.valueOf(tpl_id));
        nameValuePairs[2] = new NameValuePair("tpl_value", tpl_value);
        nameValuePairs[3] = new NameValuePair("mobile", mobile);
        PostMethod method = new PostMethod(URI_TPL_SEND_SMS);
        method.setRequestBody(nameValuePairs);
        HttpMethodParams param = method.getParams();
        param.setContentCharset(ENCODING);
        client.executeMethod(method);
        return method.getResponseBodyAsString();
    }

    public static SmsResult sendSmsCode(String phoneNum, String code) {
        SmsResult res = new SmsResult();
        res.phoneNum = phoneNum;
        res.code = code;
        try {
            String text = "【主尚文化馆】您的手机验证码是" + code + "，有效期为" + Config.SMS_Expired + "分钟，请尽快验证。";
            String resJsonStr = sendSms(Config.SMS_KEY, text, phoneNum);
            Map resJson = Json.fromJson(HashMap.class, Lang.inr(resJsonStr));
            res.isOk = (resJson.get("code").toString().equals("0"));
            res.msg = resJson.get("msg").toString();
            res.sendTime = new Date();
        } catch (Exception e) {
            if(Config.DEBUG) {
                e.printStackTrace();
            }
            res.isOk = false;
            res.msg = "服务器内部错误";
        }

        return res;
    }

    public static class SmsResult {
        public boolean isOk = false;    //是否发送成功
        public String msg;
        public String phoneNum;
        public String code;
        public Date sendTime;
    }
}
