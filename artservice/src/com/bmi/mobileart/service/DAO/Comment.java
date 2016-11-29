package com.bmi.mobileart.service.DAO;

import com.bmi.mobileart.service.utils.TextUtils;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Keith on 2015/4/25.
 * modified by xiaoyu on 2015/6/4.
 */
@Table("t_comment")
@View("v_comment")
public class Comment {
    @Id
    private int id;

    @Column("typeid")
    private int typeId;

    @Column("userid")
    private int userId;

    @Column("replyuserid")
    private int replyUserId;

    @Column("targetid")
    private int targetId;

    @Column("fatherid")
    private int fatherId;

    @Column("content")
    private String content;

    @Column("createtime")
    private Date createTime;

    @Column("valid")
    private boolean valid;

    @Column("isread")
    private boolean isRead;

    @Column("username")
    @Readonly
    private String userName;

    @Column("usernickname")
    @Readonly
    private String userNickName;

    @Column("useravatarurl")
    @Readonly
    private String userAvatarUrl;
    
    @Column("replyusername")
    @Readonly
    private String replyUserName;

    @Column("replynickname")
    @Readonly
    private String replyNickName;

    @Column("replyavatarurl")
    @Readonly
    private String replyAvatarUrl;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getReplyUserId() {
		return replyUserId;
	}

	public void setReplyUserId(int replyUserId) {
		this.replyUserId = replyUserId;
	}

	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public int getFatherId() {
		return fatherId;
	}

	public void setFatherId(int fatherId) {
		this.fatherId = fatherId;
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

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
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

	public String getReplyUserName() {
		return replyUserName;
	}

	public void setReplyUserName(String replyUserName) {
		this.replyUserName = replyUserName;
	}

	public String getReplyNickName() {
		return replyNickName;
	}

	public void setReplyNickName(String replyNickName) {
		this.replyNickName = replyNickName;
	}

	public String getReplyAvatarUrl() {
		return replyAvatarUrl;
	}

	public void setReplyAvatarUrl(String replyAvatarUrl) {
		this.replyAvatarUrl = replyAvatarUrl;
	}
 
}

