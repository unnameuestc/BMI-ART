package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.*;

import java.util.Date;

/**
 * Created by Keith on 2015/4/25.
 */
@Table("t_video")
@View("v_video")
public class Video {
    @Id
    private int id;

    @Column("typeid")
    private int typeId;

    @Column("title")
    private String title;

    @Column("userid")
    private int userId;

    @Column("videourl")
    private String videoUrl;

    @Column("picurl")
    private String picUrl;

    @Column("createTime")
    private Date createTime;

    @Column("click")
    private int click;
    
    @Column("share")
    private int share;

    public int getShare() {
		return share;
	}

	public void setShare(int share) {
		this.share = share;
	}

	@Column("description")
    private String description;

    @Column("commentcnt")
    @Readonly
    private int commentCnt;

    @Column("favorcnt")
    @Readonly
    private int favorCnt;

    @Column("username")
    @Readonly
    private String userName;

    @Column("usernickname")
    @Readonly
    private String userNickName;

    @Column("useravatarurl")
    @Readonly
    private String userAvatarUrl;

    public int getId() {
        return id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getClick() {
        return click;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public int getCommentCnt() {
        return commentCnt;
    }

    public int getFavorCnt() {
        return favorCnt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }
}
