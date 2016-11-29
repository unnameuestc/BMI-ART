package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.*;

import java.util.Date;

/**
 * Created by Keith on 2015/4/25.
 */
@Table("t_page")
@View("v_page")
public class Page {
    @Id
    private int id;

    @Column("title")
    private String title;

    @Column("userid")
    private int userId;

    @Column("content")
    private String content;

    @Column("createtime")
    private Date createTime;

    @Column("updatetime")
    private Date updateTime;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getClick() {
        return click;
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
