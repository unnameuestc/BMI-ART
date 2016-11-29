package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.*;

import java.util.Date;

/**
 * Created by Keith on 2015/6/16.
 */
@Table("t_artgroup")
@View("v_artgroup")
public class ArtGroup {
    @Id
    private int id;

    @Column("userid")
    private String userId;

    @Column("name")
    private String name;

    @Column("des")
    private String des;

    @Column("createtime")
    private Date createTime;

    @Column("username")
    @Readonly
    private String userName;

    @Column("usernickname")
    @Readonly
    private String userNickName;

    @Column("useravatarurl")
    @Readonly
    private String userAvatarUrl;

    @Column("artcnt")
    @Readonly
    private String artCnt;

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public String getArtCnt() {
        return artCnt;
    }
}
