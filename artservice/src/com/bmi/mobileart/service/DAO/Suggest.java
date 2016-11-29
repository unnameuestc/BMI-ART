package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
 * Created by Keith on 2015/6/16.
 */

@Table("t_suggest")
public class Suggest {
    @Id
    private int id;

    @Column("content")
    private String content;

    @Column("contact")
    private String contact;

    @Column("createtime")
    private Date createTime;

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
