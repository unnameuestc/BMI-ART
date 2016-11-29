package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.DAO.*;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.utils.TextUtils;

import org.nutz.dao.Cnd;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Readonly;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Keith on 2015/4/30.
 * modified by xiaoyu on 2015/6/4.
 */
public class CommentHandler extends BaseHandler {

    /**
     * 获取所有评论
     */
    public void api_getAllComments() {
        String typeId = (String) this.getArgument("typeid");
        if (TextUtils.isEmpty(typeId)) {
            this.writeError("参数不足");
            return;
        }

        String page = (String) this.getArgument("page");
        if (TextUtils.isEmpty(page)) {
            page = "1";
        }

        String pageSize = (String) this.getArgument("pagesize");
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }

        List<Comment> comments = dao.query(Comment.class,
                Cnd.where("typeid", "=", typeId).desc("createtime"),
                dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));

        Map res = new HashMap();

        //评论总数
        int allCommentCnt = dao.count(Comment.class, Cnd.where("typeid", "=", typeId));
        res.put("allCnt", allCommentCnt);
        res.put("cnt", comments.size());
        res.put("comments", comments);
        res.put("page", page);

        this.writeResult(res);
    }

    /**
     * 获取评论列表
     */
    public void api_getComments() {

        String typeId = (String) this.getArgument("typeid");
        String targetId = (String) this.getArgument("targetid");
        if (TextUtils.isEmpty(typeId) || TextUtils.isEmpty(targetId)) {
            this.writeError("参数不足");
            return;
        }

        String page = (String) this.getArgument("page");
        if (TextUtils.isEmpty(page)) {
            page = "1";
        }

        String pageSize = (String) this.getArgument("pagesize");
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }

        List<Comment> comments = dao.query(Comment.class,
                Cnd.where("typeid", "=", typeId).and("targetid", "=", targetId).and("valid", "=", "true").desc("createtime"),
                dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));

        Map res = new HashMap();

        //评论总数
        int allCommentCnt = dao.count(Comment.class, Cnd.where("typeid", "=", typeId)
                .and("targetid", "=", targetId).and("valid", "=", "true"));
        res.put("allCnt", allCommentCnt);
        res.put("cnt", comments.size());
        res.put("comments", comments);
        res.put("page", page);

        this.writeResult(res);
    }

    /**
     * 获取当前登录用户的评论/回复列表
     */
    public void api_getReplies() {
        User user = UserHandler.checkUserAuth(this);
        if (user == null) {
            return;
        }

        String page = (String) this.getArgument("page");
        if (TextUtils.isEmpty(page)) {
            page = "1";
        }

        String pageSize = (String) this.getArgument("pagesize");
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }

        List<Comment> comments = dao.query(Comment.class,
                Cnd.where("replyuserid", "=", user.getId()).and("valid", "=", "true").desc("createtime"),
                dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));
        
        //得到未读评论列表
        List<Comment> unreadComments = dao.query(Comment.class,
                Cnd.where("replyuserid", "=", user.getId()).and("valid", "=", "true").and("isread", "=", "false"));
        
        List<CommentContent> resList = new ArrayList<CommentContent>(comments.size());
        
        for(Comment c : comments){
        	
//        	得到c中数据
//        	int id = c.getId();
//            int typeId = c.getTypeId();
//            int userId = c.getUserId();
//            int replyUserId = c.getReplyUserId();
//            int targetId = c.getTargetId();
//            int fatherId = c.getFatherId();
//            String content = c.getContent();
//            Date createTime = c.getCreateTime();
//            boolean valid = c.isValid();
//            boolean isRead = c.isRead();
//            String userName = c.getUserName();
//            String userNickName = c.getUserNickName();
//            String userAvatarUrl = c.getUserAvatarUrl();
//            String replyUserName = c.getReplyUserName();
//            String replyNickName = c.getReplyNickName();
//            String replyAvatarUrl = c.getReplyAvatarUrl();
        	
        	switch( c.getTypeId()){
	        	case DaoConst.CONTENT_VIDEO:
	                Video v = dao.fetch(Video.class, Cnd.where("id", "=", c.getTargetId()));
//	                resList.add(new CommentContent(v, id, typeId, userId, replyUserId,
//	                		targetId, fatherId, content, createTime, valid, isRead, userName, userNickName, userAvatarUrl,
//	                		replyUserName, replyNickName, replyAvatarUrl));
	                resList.add(new CommentContent(v, c));
	                break;
	            case DaoConst.CONTENT_ART:
	                Art a = dao.fetch(Art.class, Cnd.where("id", "=", c.getTargetId()));
	                resList.add(new CommentContent(a, c));
	                break;
	            case DaoConst.CONTENT_PAGE:
	                Page p = dao.fetch(Page.class, Cnd.where("id", "=", c.getTargetId()));
	                resList.add(new CommentContent(p, c));
	                break;
	            default:
	                this.writeError("收藏类型不正确");
	                return;
	        	}
        }
        
        Map res = new HashMap();

        res.put("cnt", comments.size());
        res.put("unreadCnt", unreadComments.size());
        res.put("replies", resList);
//        res.put("replies", comments);
        res.put("page", page);
        
        this.writeResult(res);
    }
    /*
    * 获取当前登录用户的未读评论总数
    */
   public void api_unReadCnt() {
       User user = UserHandler.checkUserAuth(this);
       if (user == null) {
           return;
       }  
       //得到未读评论列表
       List<Comment> unreadComments = dao.query(Comment.class,
               Cnd.where("replyuserid", "=", user.getId()).and("valid", "=", "true").and("isread", "=", "false"));
           
       Map res = new HashMap();
       res.put("unreadCnt", unreadComments.size());      
       this.writeResult(res);
   }
    
    private class CommentContent {
    	public Object obj;
    	public Comment comment;
    	public CommentContent(Object obj, Comment comment){
    		this.obj = obj;
    		this.comment = comment;
    	}
    	
    	
//        private int id;
//        private int typeId;
//        private int userId;
//        private int replyUserId;
//        private int targetId;
//        private int fatherId;
//        private String content;
//        private Date createTime;
//        private boolean valid;
//        private boolean isRead;
//        private String userName;
//        private String userNickName;
//        private String userAvatarUrl;
//        private String replyUserName;
//        private String replyNickName;
//        private String replyAvatarUrl;
//    	
//        public CommentContent(Object obj, int id, int typeId, int userId, int replyUserId,
//        		int targetId, int fatherId, String content, Date createTime, boolean valid, 
//        		boolean isRead, String userName, String userNickName, String userAvatarUrl,
//        		String replyUserName, String replyNickName, String replyAvatarUrl){
//        	this.obj = obj;
//        	this.id =id;
//        	this.typeId = typeId;
//        	this.userId = userId;
//        	this.replyUserId = replyUserId;
//        	this.targetId = targetId;
//        	this.fatherId = fatherId;
//        	this.content = content;
//        	this.createTime = createTime;
//        	this.valid = valid;
//        	this.isRead = isRead;
//        	this.userName = userName;
//        	this.userNickName = userNickName;
//        	this.userAvatarUrl = userAvatarUrl;
//        	this.replyUserName = replyUserName;
//        	this.replyNickName = replyNickName;
//        	this.replyAvatarUrl = replyAvatarUrl;
//        }
    	
    }

    //获得作品详细信息（typeId和targetId）
	public void api_getWork() {
		User user = UserHandler.checkUserAuth(this);
		if (user == null) {
			return;
		}

		String _typeId = (String) this.getArgument("typeid");
		int typeId = Integer.parseInt(_typeId);

		String targetId = (String) this.getArgument("targetid");

		Map res = new HashMap();
		
		switch(typeId){
			case DaoConst.CONTENT_VIDEO:
				Video video = dao.fetch(Video.class, Cnd.where("id", "=", targetId));	
				res.put("video", video);
				break;
				
			case DaoConst.CONTENT_ART:
				Art art = dao.fetch(Art.class, Cnd.where("id", "=", targetId));
				res.put("art", art);
				break;
				
			case DaoConst.CONTENT_PAGE:
				Page page_work = dao.fetch(Page.class,Cnd.where("id", "=", targetId));
				res.put("page_work", page_work);
				break;
				
			default:
                this.writeError("作品不存在！");
                break;
		}
		
		this.writeResult(res);
	}
    
    
    /**
     * 添加评论
     */
    public void api_addComment() {
        User user = UserHandler.checkUserAuth(this);
        if (user == null) {
            return;
        }

        String typeId = (String) this.getArgument("typeid");
        String targetId = (String) this.getArgument("targetid");
        String content = (String) this.getArgument("content");

        if (TextUtils.isEmpty(typeId) || TextUtils.isEmpty(targetId) || TextUtils.isEmpty(content)) {
            this.writeError("参数不足");
            return;
        }

        Comment comment = new Comment();
        comment.setTypeId(Integer.parseInt(typeId));
        comment.setTargetId(Integer.parseInt(targetId));
        comment.setUserId(user.getId());
        comment.setContent(content);

        String fatherId = (String) this.getArgument("fatherid");
        if (!TextUtils.isEmpty(fatherId)) {		//fatherId非空，即是回复评论
            Comment fatherComment = dao.fetch(Comment.class, Cnd.where("id", "=", fatherId));
            if (fatherComment != null) {
                comment.setFatherId(Integer.parseInt(fatherId));
                comment.setReplyUserId(fatherComment.getUserId());
            }
        }else {  //不是评论的回复，但如果是艺术品或视频的评论，
            int type = Integer.parseInt(typeId);
                       
            switch(type){
            	case DaoConst.CONTENT_VIDEO :
            		
            		Video v = dao.fetch(Video.class, Cnd.where("id", "=", targetId));
                    if (v != null) {
                        comment.setReplyUserId(v.getUserId());
                    }
                    break;
                    
            	case DaoConst.CONTENT_ART :
            		
            		Art a = dao.fetch(Art.class, Cnd.where("id", "=", targetId));
                    if (a != null) {
                        comment.setReplyUserId(a.getUserId());
                    }
                    break;
                                        
            	case DaoConst.CONTENT_PAGE :
            		
            		Page p = dao.fetch(Page.class, Cnd.where("id", "=", targetId));
                	if(p != null){
                		comment.setReplyUserId(p.getUserId());
                	}                	
                	break;
                	
            	default:
                    this.writeError("评论作品不存在！");
                    break;
            }
            
        }

        comment.setCreateTime(new Date());
        comment.setValid(true);
        comment.setRead(false);

        if (dao.insert(comment) == null) {
            this.writeError("数据库操作错误");
            return;
        }        
        
        Comment c = dao.fetch(Comment.class, Cnd.where("id", "=", comment.getId()));
        
        Map res = new HashMap();
        res.put("comment", c);
        this.writeResult(res);
    }

    /**
     * 删除评论
     */
    public void api_delComment() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        List<Comment> comments = dao.query(Comment.class, Cnd.where("id", "=", id).or("fatherid", "=", id));
        if (comments == null) {
            this.writeError("评论不存在");
            return;
        }

        if (dao.delete(comments) <= 0) {
            this.writeError("数据库操作错误");
            return;
        }

        //TODO 删除评论以及该评论的所有回复

        this.writeResult(null);
    }

    /**
     * 评论已读
     */
    public void api_markRead() {
        String id = (String) this.getArgument("id");
        String read = (String) this.getArgument("read");
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(read)) {
            this.writeError("参数不足");
            return;
        }

        Comment comment = dao.fetch(Comment.class, Cnd.where("id", "=", id));
        if (comment == null) {
            this.writeError("评论不存在");
            return;
        }

        comment.setRead(Boolean.parseBoolean(read));

        if (dao.update(comment) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        this.writeResult(null);
    }

    /**
     * 评论审核
     */
    public void api_markValid() {
        String id = (String) this.getArgument("id");
        String valid = (String) this.getArgument("valid");
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(valid)) {
            this.writeError("参数不足");
            return;
        }

        Comment comment = dao.fetch(Comment.class, Cnd.where("id", "=", id));
        if (comment == null) {
            this.writeError("评论不存在");
            return;
        }

        comment.setValid(Boolean.parseBoolean(valid));

        if (dao.update(comment) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        this.writeResult(null);
    }
}
