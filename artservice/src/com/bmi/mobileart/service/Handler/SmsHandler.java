package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.Config;
import com.bmi.mobileart.service.DAO.Smscode;
import com.bmi.mobileart.service.Lib.SmsApi;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.utils.DateTimeUtils;
import com.bmi.mobileart.service.utils.TextUtils;

import org.nutz.dao.Cnd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信验证码
 * Created by Keith on 2015/4/12.
 */
public class SmsHandler extends BaseHandler {

    /**
     * 发送短信验证码
     */
    public void api_sendCode() {
        String phone = (String) this.getArgument("phone");
        if (TextUtils.isEmpty(phone) || !TextUtils.isPhoneNum(phone)) {
            this.writeError("手机号为空或有误");
            return;
        }

        //是不是间隔内
        Smscode sms = dao.fetch(Smscode.class, Cnd.where("phone", "=", phone).desc("createtime"));
        if (sms != null && DateTimeUtils.diffSecond(sms.getCreateTime(), new Date()) < Config.SMS_Interval) {
            this.writeError("距离上次发送不足" + Config.SMS_Interval + "秒");
            return;
        }

        //生成验证码
        String code = TextUtils.getRandomCode(Config.SMS_CODE_LEN);

        //发送
        SmsApi.SmsResult result = SmsApi.sendSmsCode(phone, code);
        if (!result.isOk) {
            this.writeError(result.msg);
            return;
        }

        //存入数据库
        sms = new Smscode();
        sms.setPhone(phone);
        sms.setCode(code);
        sms.setCheck(false);
        sms.setCreateTime(new Date());

        if (dao.insert(sms) == null) {
            this.writeError("数据库错误");
            return;
        }

        this.writeResult(null);
    }

    /**
     * 验证短信验证码
     */
    public void api_verifyCode(){
        String phone = (String) this.getArgument("phone");
        String code = (String) this.getArgument("code");

        if(TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)){
            this.writeError("参数不足");
            return;
        }

        Map res = new HashMap();

        Smscode sms = dao.fetch(Smscode.class, Cnd.where("phone", "=", phone).and("code", "=", code).desc("createtime"));
        if (sms != null) {
            if(sms.isCheck()){
                res.put("valid", false);
                res.put("msg", "验证码已被使用");
            }else if (DateTimeUtils.diffSecond(sms.getCreateTime(), new Date()) >= Config.SMS_Expired * 60){
                res.put("valid", false);
                res.put("msg", "验证码已过期");
            }else {
                sms.setCheck(true);
                if (dao.update(sms) <= 0) {
                    this.writeError("数据库错误");
                    return;
                }

                res.put("valid", true);
            }
        }else {
            res.put("valid", false);
            res.put("msg", "验证码不存在");
        }

        this.writeResult(res);
    }
}
