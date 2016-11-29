package com.bmi.mobileart.service.DAO;

import com.bmi.mobileart.service.utils.TextUtils;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Keith on 2015/4/25.
 */
@Table("t_art")
@View("v_art")
public class Art {
    @Id
    private int id;

    @Column("typeid")
    private int typeId;

    @Column("title")
    private String title;

    @Column("userid")
    private int userId;

    @Column("content")
    private String content;

    @Column("createtime")
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

	@Column("tradestate")
    private int tradeState;

    @Column("tradedes")
    private String tradeDes;

    @Column("groupid")
    private int groupid;

    @Column("imgcnt")
    @Readonly
    private int imgCnt;

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

    private List imgList;
    private String titleImg;

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

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public int getTradeState() {
        return tradeState;
    }

    public void setTradeState(int tradeState) {
        this.tradeState = tradeState;
    }

    public String getTradeDes() {
        return tradeDes;
    }

    public void setTradeDes(String tradeDes) {
        this.tradeDes = tradeDes;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public int getImgCnt() {
        return imgCnt;
    }

    public int getCommentCnt() {
        return commentCnt;
    }

    public int getFavorCnt() {
        return favorCnt;
    }

    public List getImgList() {
        return imgList;
    }

    public void setImgList(List imgList) {
        this.imgList = imgList;
    }

    public String getTitleImg() {
        return titleImg;
    }

    public void setTitleImg(String titleImg) {
        this.titleImg = titleImg;
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
