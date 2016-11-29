package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.DAO.Art;
import com.bmi.mobileart.service.DAO.Page;
import com.bmi.mobileart.service.DAO.User;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.utils.TextUtils;

import org.nutz.dao.Cnd;

import java.util.*;

/**
 * Created by Keith on 2015/4/30.
 */
public class PageHandler extends BaseHandler {

    /**
     * 获取文章列表
     */
    public void api_getPages() {
        String page = (String) this.getArgument("page");
        if (TextUtils.isEmpty(page)) {
            page = "1";
        }

        String pageSize = (String) this.getArgument("pagesize");
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }

        //文章总数
        int allPageCnt = dao.count(Page.class);

        String userId = (String) this.getArgument("userid");

        List<Page> pageList;
        if (TextUtils.isEmpty(userId)) {
            pageList = dao.query(Page.class, Cnd.NEW().desc("createtime"),
                    dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));
        } else {
            pageList = dao.query(Page.class, Cnd.where("userid", "=", userId).desc("createtime"),
                    dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));
        }

        Map res = new HashMap();
        res.put("allCnt", allPageCnt);
        res.put("page", page);
        res.put("cnt", pageList.size());
        res.put("pages", pageList);

        this.writeResult(res);
    }

    /**
     * 添加文章
     */
    public void api_addPage() {
        String title = (String) this.getArgument("title");
        String userId = (String) this.getArgument("userid");
        String content = (String) this.getArgument("content");

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(userId) || TextUtils.isEmpty(content)) {
            this.writeError("参数不足");
            return;
        }

        User u = dao.fetch(User.class, Cnd.where("id", "=", userId));
        if (u == null) {
            this.writeError("用户不存在");
            return;
        }

        Page page = new Page();
        page.setTitle(title);
        page.setUserId(u.getId());
        page.setContent(content);
        page.setCreateTime(new Date());
        page.setUpdateTime(new Date());
        page.setClick(0);

        if (dao.insert(page) == null) {
            this.writeError("数据库操作错误");
            return;
        }

        Map res = new HashMap();
        res.put("page", page);
        this.writeResult(res);
    }

    /**
     * 获取文章详情
     */
    public void api_getPageInfo(){
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Page page = dao.fetch(Page.class, Cnd.where("id", "=", id));
        if (page == null) {
            this.writeError("文章不存在");
            return;
        }

        Map res = new HashMap();
        res.put("page", page);
        this.writeResult(res);
    }

    /**
     * 删除文章
     */
    public void api_delPage() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Page page = dao.fetch(Page.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (page == null) {
            this.writeError("文章不存在");
            return;
        }

        //TODO 暂时不删除关联收藏，评论等信息

        if (dao.delete(page) <= 0) {
            this.writeError("数据库操作错误");
            return;
        }

        this.writeResult(null);
    }

    /**
     * 修改文章
     */
    public void api_editPage() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Page page = dao.fetch(Page.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (page == null) {
            this.writeError("文章不存在");
            return;
        }

        String userId = (String) this.getArgument("userid");
        if (!TextUtils.isEmpty(userId)) {
            User u = dao.fetch(User.class, Cnd.where("id", "=", userId));
            if (u == null) {
                this.writeError("用户不存在");
                return;
            }

            page.setUserId(u.getId());
        }

        String title = (String) this.getArgument("title");
        if (!TextUtils.isEmpty(title)) {
            page.setTitle(title);
        }

        String content = (String) this.getArgument("content");
        if (!TextUtils.isEmpty(content)) {
            page.setContent(content);
        }

        if (dao.update(page) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("page", page);
        this.writeResult(res);
    }

    /**
     * 文章点击量+1
     */
    public void api_addClick() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Page page = dao.fetch(Page.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (page == null) {
            this.writeError("文章不存在");
            return;
        }

        //点击量+1
        page.setClick(page.getClick() + 1);

        if (dao.update(page) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("page", page);
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
    	
    	Page p = dao.fetch(Page.class, Cnd.where("id", "=", id));
    	if(p == null){
    		this.writeError("视频不存在");  		
    	}
    	
    	//分享量+1
    	p.setShare(p.getShare() + 1);
    	
    	if(dao.update(p) <= 0){
    		this.writeError("数据库错误");
    		return;
    	}
    	
    	Map res = new HashMap();
        res.put("page", p);
        this.writeResult(res);
    }
}
