package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.DAO.*;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.utils.TextUtils;

import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;

import java.util.*;

/**
 * Created by Keith on 2015/6/16.
 */
public class OtherHandler extends BaseHandler {
    public void api_search() {
        String keyword = (String) this.getArgument("keyword");
        if (TextUtils.isEmpty(keyword)) {
            this.writeError("参数不足");
            return;
        }

        Map res = new HashMap();

        //用户昵称(过滤未认证用户)
        List<User> users = dao.query(User.class, Cnd.where("certifytypes", "<>", "").and("nickname", "LIKE", "%" + keyword + "%"));
        Map userMap = new HashMap();
        userMap.put("cnt", users.size());
        userMap.put("users", users);
        res.put("user", userMap);

        //视频
        List<Video> videos = dao.query(Video.class, Cnd.where("title", "LIKE", "%" + keyword + "%"));
        Map videoMap = new HashMap();
        videoMap.put("cnt", videos.size());
        videoMap.put("videos", videos);
        res.put("video", videoMap);

        //艺术品
        List<Art> arts = dao.query(Art.class, Cnd.where("title", "LIKE", "%" + keyword + "%"));
        for (Art a : arts) {
            //去掉简介
            a.setContent(null);
            //第一张图
            ArtImg img = dao.fetch(ArtImg.class, Cnd.where(Cnd.exps("artid", "=", a.getId())).desc("createtime"));
            if(img != null) {
                a.setTitleImg(img.getUrl());
            }
        }
        Map artMap = new HashMap();
        artMap.put("cnt", arts.size());
        artMap.put("arts", arts);
        res.put("art", artMap);

        //文章
        List<Page> pages = dao.query(Page.class, Cnd.where("title", "LIKE", "%" + keyword + "%"));
        Map pageMap = new HashMap();
        pageMap.put("cnt", pages.size());
        pageMap.put("pages", pages);
        res.put("page", pageMap);

        writeResult(res);
    }

    //艺术品搜索
    public void api_artSearch() {
    	String keyWord = (String) this.getArgument("keyword");
    	String typeId = (String) this.getArgument("typeid");
    	String pageSize = (String) this.getArgument("pagesize");
    	String pageNumber = (String) this.getArgument("pagenumber");
    	if (TextUtils.isEmpty(keyWord) || TextUtils.isEmpty(typeId)) {
            this.writeError("参数不足");
            return;
        }
    	
    	if(TextUtils.isEmpty(pageSize)){
    		pageSize = "10";
    	}
    	
    	if(TextUtils.isEmpty(pageNumber)){
    		pageNumber = "1";
    	}
    	
    	String onlyTradableStr = (String) this.getArgument("trade");
        if (TextUtils.isEmpty(onlyTradableStr)) {
            onlyTradableStr = "false";
        }
        boolean onlyTradable = Boolean.parseBoolean(onlyTradableStr);
    	
        List<Art> arts = new ArrayList<Art>();
        List<Art> _arts = new ArrayList<Art>();
        
    	if(onlyTradable){ //可交易作品
    		arts = dao.query(Art.class,
    				Cnd.where(Cnd.exps("usernickname", "Like", "%"+ keyWord + "%").and("tradestate", "=", DaoConst.TREAD_ENABLE).and("typeid", "=", typeId))
    					.or(Cnd.exps("title", "Like", "%"+ keyWord + "%").and("tradestate", "=", DaoConst.TREAD_ENABLE).and("typeid", "=", typeId)).desc("createtime"),
	    				dao.createPager(Integer.parseInt(pageNumber), Integer.parseInt(pageSize)));
	    	//统计总数	    	
	    	_arts = dao.query(Art.class,
	    			Cnd.where(Cnd.exps("usernickname", "Like", "%"+ keyWord + "%").and("tradestate", "=", DaoConst.TREAD_ENABLE).and("typeid", "=", typeId))
					.or(Cnd.exps("title", "Like", "%"+ keyWord + "%").and("tradestate", "=", DaoConst.TREAD_ENABLE).and("typeid", "=", typeId)));
	    	
    	}else{   	//可以交易和不可交易作品
    		arts = dao.query(Art.class,
    				Cnd.where(Cnd.exps("usernickname", "Like", "%"+ keyWord + "%").and("tradestate", "<=", DaoConst.TREAD_ENABLE).and("typeid", "=", typeId))
    					.or(Cnd.exps("title", "Like", "%"+ keyWord + "%").and("tradestate", "<=", DaoConst.TREAD_ENABLE).and("typeid", "=", typeId)).desc("createtime"),
	    				dao.createPager(Integer.parseInt(pageNumber), Integer.parseInt(pageSize)));
	    	//统计总数	    	
	    	_arts = dao.query(Art.class,
	    			Cnd.where(Cnd.exps("usernickname", "Like", "%"+ keyWord + "%").and("tradestate", "<=", DaoConst.TREAD_ENABLE).and("typeid", "=", typeId))
					.or(Cnd.exps("title", "Like", "%"+ keyWord + "%").and("tradestate", "<=", DaoConst.TREAD_ENABLE).and("typeid", "=", typeId)));
	    	
    	}
    	
    	for (Art a : arts) {
            //第一张图
            ArtImg img = dao.fetch(ArtImg.class, Cnd.where(Cnd.exps("artid", "=", a.getId())).desc("createtime"));
            if(img != null) {
                a.setTitleImg(img.getUrl());
            }
        }
    	
    	Map res = new HashMap();
    	res.put("allCnt", _arts.size());
    	res.put("page", pageNumber);
    	res.put("Cnt", arts.size());
    	res.put("arts", arts);
    	
    	this.writeResult(res);
    	
    }
    
    public void api_addSuggest() {
        String content = (String) this.getArgument("content");
        String contact = (String) this.getArgument("contact");
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(contact)) {
            this.writeError("参数不足");
            return;
        }

        Suggest suggest = new Suggest();
        suggest.setContent(content);
        suggest.setContact(contact);
        suggest.setCreateTime(new Date());

        if (dao.insert(suggest) == null) {
            this.writeError("数据库操作错误");
            return;
        }

        this.writeResult(null);
    }

    public void api_adminLogin() {
        String name = (String) this.getArgument("name");
        String pwd = (String) this.getArgument("pwd");

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            this.writeError("参数不足");
            return;
        }

        Admin u = dao.fetch(Admin.class,
                Cnd.where("name", "=", name).and("pwd", "=", pwd));

        if (u == null) {
            this.writeError("用户名或密码错误");
            return;
        }

        //生成authKey
        String seed = u.getName() + new Date().toString() + new Random().nextInt(1000);
        String md5 = TextUtils.MD5(seed);
        if (TextUtils.isEmpty(md5)) {
            this.writeError("服务器内部错误");
            return;
        }

        u.setAuthKey(md5);
        if (dao.update(u) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("authKey", u.getAuthKey());
        this.writeResult(res);
    }

    public void api_adminLogout() {
        String authKey = (String)getArgument("authkey");
        if (TextUtils.isEmpty(authKey)) {
            writeError("参数不足");
            return;
        }

        Admin u = dao.fetch(Admin.class, Cnd.where("authkey", "=", authKey));
        if (u == null) {
            writeError("管理员验证失败");
            return;
        }

        u.setAuthKey(null);

        if (dao.update(u) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        this.writeResult(null);
    }

    public void api_getSuggests() {
        String page = (String) this.getArgument("page");
        if (TextUtils.isEmpty(page)) {
            page = "1";
        }

        String pageSize = (String) this.getArgument("pagesize");
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }

        //建议总数
        int allCnt = dao.count(Suggest.class);

        //分页
        List<Suggest> suggests = dao.query(Suggest.class, Cnd.NEW().desc("createtime"),
                dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));

        Map res = new HashMap();
        res.put("allCnt", allCnt);
        res.put("page", page);
        res.put("cnt", suggests.size());
        res.put("suggests", suggests);

        this.writeResult(res);
    }

    public void api_getShareUrl(){
        String typeIdStr = (String) this.getArgument("typeid");
        String targetId = (String)this.getArgument("targetid");
        if (TextUtils.isEmpty(typeIdStr) || TextUtils.isEmpty(targetId)) {
            writeError("参数不足");
            return;
        }

        int typeId = Integer.parseInt(typeIdStr);

        String resUrl = "";
        switch (typeId){
            case DaoConst.CONTENT_VIDEO:
                resUrl = "/frame/show-video.html?share=1&videoid=" + targetId;
                break;
            case DaoConst.CONTENT_ART:
                resUrl = "/frame/show-art.html?share=1&artid=" + targetId;
                break;
            case DaoConst.CONTENT_PAGE:
                resUrl = "/frame/show-page.html?share=1&pageid=" + targetId;
                break;
        }

        String showLink = (String)this.getArgument("showlink");
        if(TextUtils.isEmpty(showLink) || showLink.equals("true")){
            resUrl += "&applink=1";
        }

        Map res = new HashMap();
        res.put("typeId", typeIdStr);
        res.put("targetId", targetId);
        res.put("resUrl", resUrl);

        this.writeResult(res);
    }
}
