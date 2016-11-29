package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Keith on 2015/4/12.
 */

@Table("t_smscode")
public class Smscode {
    @Id
    private int id;

    @Column("phone")
    private String phone;

    @Column("code")
    private String code;

    @Column("ischeck")
    private boolean isCheck = false;

    @Column("createtime")
    private Date createTime;

    public int getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
