package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Keith on 2015/4/8.
 */

@Table("t_user")
@View("v_user")
public class User {

    @Id
    private int id;

    @Column("name")
    private String name;

    @Column("pwd")
    private String pwd;

    @Column("phone")
    private String phone;

    @Column("nickname")
    private String nickName;

    @Column("createtime")
    private Date createTime;

    @Column("avatarurl")
    private String avatarUrl;

    @Column("profile")
    private String profile;

    @Column("certifytypes")
    private String certifyTypes;

    @Column("certifytime")
    private Date certifyTime;

    @Column("authkey")
    private String authKey;

    @Column("favor")
    @Readonly
    private int favor;

    @Column("favorvideo")
    @Readonly
    private int favorVideo;

    @Column("favorart")
    @Readonly
    private int favorArt;

    @Column("favorpage")
    @Readonly
    private int favorPage;

    @Column("follow")
    @Readonly
    private int follow;

    @Column("fans")
    @Readonly
    private int fans;

    @Column("reply")
    @Readonly
    private int reply;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCertifyTypes() {
        return certifyTypes;
    }

    public void setCertifyTypes(String certifyTypes) {
        this.certifyTypes = certifyTypes;
    }

    public Date getCertifyTime() {
        return certifyTime;
    }

    public void setCertifyTime(Date certifyTime) {
        this.certifyTime = certifyTime;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public int getFavor() {
        return favor;
    }

    public int getFollow() {
        return follow;
    }

    public int getFans() {
        return fans;
    }

    public int getFavorVideo() {
        return favorVideo;
    }

    public int getFavorArt() {
        return favorArt;
    }

    public int getFavorPage() {
        return favorPage;
    }

    public int getReply() {
        return reply;
    }

    public Map toKVPair() {
        Map map = new HashMap();

        map.put("id", id);
        map.put("name", name);
        map.put("phone", phone);
        map.put("nickName", nickName);
        map.put("createTime", createTime);
        map.put("avatarUrl", avatarUrl);
        map.put("profile", profile);
        map.put("certifyTypes", certifyTypes);
        map.put("favor", favor);
        map.put("favorVideo", favorVideo);
        map.put("favorArt", favorArt);
        map.put("favorPage", favorPage);
        map.put("follow", follow);
        map.put("fans", fans);
        map.put("reply", reply);

        return map;
    }
}
