package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.View;

import java.util.Date;

/**
 * Created by Keith on 2015/6/23.
 */
@Table("t_admin")
public class Admin {
    @Id
    private int id;

    @Column("name")
    private String name;

    @Column("pwd")
    private String pwd;

    @Column("createtime")
    private Date createTime;

    @Column("authkey")
    private String authKey;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }
}
