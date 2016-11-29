package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
 * Created by Keith on 2015/4/25.
 */
@Table("t_favor")
public class Favor {
    @Id
    private int id;

    @Column("typeid")
    private int typeId;

    @Column("userid")
    private int userId;

    @Column("targetid")
    private int targetId;

    @Column("createtime")
    private Date createTime;

    public int getId() {
        return id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
