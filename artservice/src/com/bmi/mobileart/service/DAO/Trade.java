package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.*;

import java.util.Date;

/**
 * Created by Keith on 2015/6/16.
 */
@Table("t_trade")
@View("v_trade")
public class Trade {
    @Id
    private int id;

    @Column("artid")
    private int artId;

    @Column("userid")
    private int userId;

    @Column("recipient")
    private String recipient;

    @Column("contact")
    private String contact;

    @Column("addr")
    private String addr;

    @Column("createtime")
    private Date createTime;

    @Column("finishtime")
    private Date finishTime;

    @Column("state")
    private int state = DaoConst.TREAD_ING;

    @Column("tradedes")
    @Readonly
    private String tradeDes;

    public int getId() {
        return id;
    }

    public int getArtId() {
        return artId;
    }

    public void setArtId(int artId) {
        this.artId = artId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTradeDes() {
        return tradeDes;
    }
}
