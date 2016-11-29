package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.DAO.ArtType;
import com.bmi.mobileart.service.DAO.Follow;
import com.bmi.mobileart.service.DAO.User;
import com.bmi.mobileart.service.DAO.Video;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.utils.TextUtils;
import com.sun.xml.internal.fastinfoset.stax.events.Util;

import org.nutz.dao.Cnd;

import java.util.*;

/**
 * Created by Keith on 2015/4/29.
 */
public class VideoHandler extends BaseHandler {

    /**
     * 获取“演艺”中某个分类的视频列表
     */
    public void api_getVideos() {
        String typeId = (String) this.getArgument("typeid");
        if (TextUtils.isEmpty(typeId)) {
            this.writeError("参数不足");
            return;
        }

        String userNickName = (String) this.getArgument("usernick");
        if (TextUtils.isEmpty(userNickName)) {
            userNickName = "";
        }

        String page = (String) this.getArgument("page");
        if (TextUtils.isEmpty(page)) {
            page = "1";
        }

        String pageSize = (String) this.getArgument("pagesize");
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }

        //分页
        List<Video> videoList = dao.query(Video.class,
                Cnd.where("typeid", "=", typeId).and("usernickname", "LIKE", "%" + userNickName + "%").desc("createtime"),
                dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));

        Map res = new HashMap();
        res.put("page", page);
        res.put("cnt", videoList.size());
        res.put("videos", videoList);

        this.writeResult(res);
    }

    /**
     * 添加视频
     */
    public void api_addVideo() {
        String typeId = (String) this.getArgument("typeid");
        String userId = (String) this.getArgument("userid");

        if (TextUtils.isEmpty(typeId) || TextUtils.isEmpty(userId)) {
            this.writeError("参数不足");
            return;
        }

        ArtType type = dao.fetch(ArtType.class, Cnd.where("id", "=", typeId));
        if (type == null) {
            this.writeError("分类不存在");
            return;
        }

        User u = dao.fetch(User.class, Cnd.where("id", "=", userId));
        if (u == null) {
            this.writeError("用户不存在");
            return;
        }

        String title = (String) this.getArgument("title");
        String description = (String) this.getArgument("description");
//        String videoUrl = (String) this.getArgument("videourl");
        String picUrl = (String) this.getArgument("picurl");

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(picUrl)) {
            this.writeError("参数不足");
            return;
        }

        Video v = new Video();
        v.setTypeId(type.getId());
        v.setUserId(u.getId());
        v.setTitle(title);
        v.setDescription(description);
//        v.setVideoUrl(videoUrl);
        v.setPicUrl(picUrl);
        v.setCreateTime(new Date());
        v.setClick(0);

        if (dao.insert(v) == null) {
            this.writeError("数据库操作错误");
            return;
        }

        Map res = new HashMap();
        res.put("video", v);
        this.writeResult(res);
    }
    
    /**
     * 添加视频链接
     */
    public void api_addVideoing() {
    	String videoId = (String) this.getArgument("videoid");
    	String videoUrl = (String) this.getArgument("videourl");
        if (TextUtils.isEmpty(videoId) || TextUtils.isEmpty(videoUrl)) {
            this.writeError("参数不足");
            return;
        }

        Video v = dao.fetch(Video.class, Cnd.where(Cnd.exps("id", "=", videoId)));
        if (v == null) {
            this.writeError("视频不存在");
            return;
        }
        
        v.setVideoUrl(videoUrl);
        
        if (dao.update(v) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("video", v);
        this.writeResult(res);
    }

    /**
     * 获取视频详情
     */
    public void api_getVideoInfo(){
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Video v = dao.fetch(Video.class, Cnd.where("id", "=", id));
        if (v == null) {
            this.writeError("视频不存在");
            return;
        }

        Map res = new HashMap();
        res.put("video", v);
        this.writeResult(res);
    }

    /**
     * 删除视频
     */
    public void api_delVideo() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Video v = dao.fetch(Video.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (v == null) {
            this.writeError("视频不存在");
            return;
        }

        //TODO 暂时不删除关联收藏，评论等信息

        if (dao.delete(v) <= 0) {
            this.writeError("数据库操作错误");
            return;
        }

        this.writeResult(null);
    }

    /**
     * 修改视频
     */
    public void api_editVideo() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Video v = dao.fetch(Video.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (v == null) {
            this.writeError("视频不存在");
            return;
        }

        String typeId = (String) this.getArgument("typeid");
        if (!TextUtils.isEmpty(typeId)) {
            ArtType type = dao.fetch(ArtType.class, Cnd.where("id", "=", typeId));
            if (type == null) {
                this.writeError("分类不存在");
                return;
            }

            v.setTypeId(type.getId());
        }

        String userId = (String) this.getArgument("userid");
        if (!TextUtils.isEmpty(userId)) {
            User u = dao.fetch(User.class, Cnd.where("id", "=", userId));
            if (u == null) {
                this.writeError("用户不存在");
                return;
            }

            v.setUserId(u.getId());
        }

        String title = (String) this.getArgument("title");
        if (!TextUtils.isEmpty(title)) {
            v.setTitle(title);
        }

        String description = (String) this.getArgument("description");
        if (!TextUtils.isEmpty(description)) {
            v.setDescription(description);
        }

        String videoUrl = (String) this.getArgument("videourl");
        if (!TextUtils.isEmpty(videoUrl)) {
            v.setVideoUrl(videoUrl);
        }

        String picUrl = (String) this.getArgument("picurl");
        if (!TextUtils.isEmpty(picUrl)) {
            v.setPicUrl(picUrl);
        }

        if (dao.update(v) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("video", v);
        this.writeResult(res);
    }

    /**
     * 点击+1
     */
    public void api_addClick(){
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Video v = dao.fetch(Video.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (v == null) {
            this.writeError("视频不存在");
            return;
        }

        //点击量+1
        v.setClick(v.getClick() + 1);

        if (dao.update(v) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("video", v);
        this.writeResult(res);
    }
    /**
     * 分享数+1
     */
    public void api_addShare(){
    	String id = (String) this.getArgument("id");
    	if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }
    	
    	Video v = dao.fetch(Video.class, Cnd.where("id", "=", id));
    	if(v == null){
    		this.writeError("视频不存在");  		
    	}
    	
    	//分享量+1
    	v.setShare(v.getShare() + 1);
    	
    	if(dao.update(v) <= 0){
    		this.writeError("数据库错误");
    		return;
    	}
    	
    	Map res = new HashMap();
        res.put("video", v);
        this.writeResult(res);
    }
}
