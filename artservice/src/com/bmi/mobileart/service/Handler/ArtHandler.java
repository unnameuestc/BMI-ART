package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.Config;
import com.bmi.mobileart.service.DAO.*;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.utils.TextUtils;

import org.nutz.dao.Cnd;

import javax.xml.soap.Text;

import java.util.*;

/**
 * Created by Keith on 2015/4/29.
 */
public class ArtHandler extends BaseHandler {

    /**
     * 获取艺术品分类
     */
    public void api_getArtTypes() {
        List<ArtType> types = dao.query(ArtType.class, null);
        int cnt = 0;
        if (types != null) {
            cnt = types.size();
        }

        Map res = new HashMap();
        res.put("cnt", cnt);
        if (cnt > 0) {
            res.put("types", types);
        }
        this.writeResult(res);
    }

    /**
     * 获取某个分类的艺术品列表(简洁信息)
     */
    public void api_getArtList() {
        String typeId = (String) this.getArgument("typeid");
        if (TextUtils.isEmpty(typeId)) {
            this.writeError("参数不足");
            return;
        }

        String userNickName = (String) this.getArgument("usernick");
        if (TextUtils.isEmpty(userNickName)) {
            userNickName = "";
        }

        String onlyTradableStr = (String) this.getArgument("trade");
        if (TextUtils.isEmpty(onlyTradableStr)) {
            onlyTradableStr = "false";
        }
        boolean onlyTradable = Boolean.parseBoolean(onlyTradableStr);

        String page = (String) this.getArgument("page");
        if (TextUtils.isEmpty(page)) {
            page = "1";
        }

        String pageSize = (String) this.getArgument("pagesize");
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }

        List<Art> artList = new ArrayList<Art>();
        if(onlyTradable){
            artList = dao.query(Art.class,
                    Cnd.where("typeid", "=", typeId).and("tradestate", "=", DaoConst.TREAD_ENABLE).and("usernickname", "LIKE", "%" + userNickName + "%").desc("createtime"),
                    dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));
        }else {
            artList = dao.query(Art.class,
                    Cnd.where("typeid", "=", typeId).and("usernickname", "LIKE", "%" + userNickName + "%").desc("createtime"),
                    dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));
        }

        for (Art a : artList) {
            //去掉简介
            a.setContent(null);
            //第一张图
            ArtImg img = dao.fetch(ArtImg.class, Cnd.where(Cnd.exps("artid", "=", a.getId())).desc("createtime"));
            if(img != null) {
                a.setTitleImg(img.getUrl());
            }
        }

        Map res = new HashMap();
        res.put("page", page);
        res.put("cnt", artList.size());
        res.put("arts", artList);

        this.writeResult(res);
    }

    /**
     * 获取用户详情页中的分组的艺术品列表
     */
    public void api_getUserArtGroup(){
        String userId = (String) this.getArgument("userid");
        if(TextUtils.isEmpty(userId)){
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
        List<Map> resList = new ArrayList<Map>();
        
        List<Art> artList = dao.query(Art.class, Cnd.where("userid", "=", userId).and("groupid", "<>", "").desc("groupid"),
        		dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));
        for (Art a : artList) {
            //去掉简介
            //a.setContent(null);
            //第一张图
            ArtImg img = dao.fetch(ArtImg.class, Cnd.where(Cnd.exps("artid", "=", a.getId())).desc("createtime"));
            if(img != null) {
                a.setTitleImg(img.getUrl());
            }
        }

        int i = 0;
        while(i < artList.size()) {
            Art art = artList.get(i);
            int currGroupId = art.getGroupid();

            //创建新组
            ArtGroup group = dao.fetch(ArtGroup.class, Cnd.where("id", "=", currGroupId));
            if (group == null) {
                i++;
                continue;
            }

            Map groupMap = new HashMap();
            groupMap.put("group", group);

            List<Art> groupList = new ArrayList<Art>();
            groupList.add(art);

            i++;
            while (i < artList.size() && artList.get(i).getGroupid() == currGroupId) {
                art = artList.get(i);
                groupList.add(art);
                i++;
            }

            groupMap.put("cnt", groupList.size());
            groupMap.put("artList", groupList);
            resList.add(groupMap);
        }

        Map resMap = new HashMap();
        resMap.put("cnt", resList.size());
        resMap.put("group", resList);

        this.writeResult(resMap);
    }

    /**
     * 获取艺术品详情
     */
    public void api_getArtInfo(){
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Art art = dao.fetch(Art.class, Cnd.where("id", "=", id));
        if (art == null) {
            this.writeError("艺术品不存在");
            return;
        }

        art.setImgList(getImgList(art.getId()));

        Map res = new HashMap();
        res.put("art", art);
        this.writeResult(res);
    }

    /**
     * 添加艺术品
     */
    public void api_addArt(){
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
        String content = (String) this.getArgument("content");

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            this.writeError("参数不足");
            return;
        }

        Art art = new Art();
        art.setTypeId(type.getId());
        art.setUserId(u.getId());
        art.setTitle(title);
        art.setContent(content);
        art.setCreateTime(new Date());
        art.setClick(0);
        art.setTradeState(DaoConst.TREAD_DISABLE);
        art.setTradeDes("");

        String tradable = (String) this.getArgument("tradable");
        if (!TextUtils.isEmpty(tradable)) {
            art.setTradeState(tradable.equalsIgnoreCase("true") ? DaoConst.TREAD_ENABLE : DaoConst.TREAD_DISABLE);
        }

        String tradeDes = (String) this.getArgument("tradedes");
        if (!TextUtils.isEmpty(tradeDes)) {
            art.setTradeDes(tradeDes);
        }

        String groupId = (String) this.getArgument("groupid");
        if (!TextUtils.isEmpty(groupId)) {
            ArtGroup g = dao.fetch(ArtGroup.class, Cnd.where("id", "=", groupId));
            if (g == null) {
                this.writeError("分组不存在");
                return;
            }
            art.setGroupid(g.getId());
        }

        if (dao.insert(art) == null) {
            this.writeError("数据库操作错误");
            return;
        }

        Map res = new HashMap();
        res.put("art", art);
        this.writeResult(res);
    }

    /**
     * 删除艺术品
     */
    public void api_delArt() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Art art = (Art) dao.fetch(Art.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (art == null) {
            this.writeError("艺术品不存在");
            return;
        }

        //TODO 暂时不删除关联图片，收藏，评论等信息

        if (dao.delete(art) <= 0) {
            this.writeError("数据库操作错误");
            return;
        }

        this.writeResult(null);
    }

    /**
     * 修改艺术品
     */
    public void api_editArt() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Art art = (Art) dao.fetch(Art.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (art == null) {
            this.writeError("艺术品不存在");
            return;
        }

        String typeId = (String) this.getArgument("typeid");
        if (!TextUtils.isEmpty(typeId)) {
            ArtType type = dao.fetch(ArtType.class, Cnd.where("id", "=", typeId));
            if (type == null) {
                this.writeError("分类不存在");
                return;
            }

            art.setTypeId(type.getId());
        }

        String userId = (String) this.getArgument("userid");
        if (!TextUtils.isEmpty(userId)) {
            User u = dao.fetch(User.class, Cnd.where("id", "=", userId));
            if (u == null) {
                this.writeError("用户不存在");
                return;
            }

            art.setUserId(u.getId());
        }

        String title = (String) this.getArgument("title");
        if (!TextUtils.isEmpty(title)) {
            art.setTitle(title);
        }

        String content = (String) this.getArgument("content");
        if (!TextUtils.isEmpty(content)) {
            art.setContent(content);
        }

        String tradeState = (String) this.getArgument("tradestate");
        if (!TextUtils.isEmpty(tradeState)) {
            art.setTradeState(Integer.parseInt(tradeState));
        }

        String tradeDes = (String) this.getArgument("tradedes");
        if (!TextUtils.isEmpty(tradeDes)) {
            art.setTradeDes(tradeDes);
        }

        String groupId = (String) this.getArgument("groupid");
        if (!TextUtils.isEmpty(groupId)) {
            ArtGroup g = dao.fetch(ArtGroup.class, Cnd.where("id", "=", groupId));
            if (g == null) {
                this.writeError("分组不存在");
                return;
            }
            art.setGroupid(g.getId());
        }

        if (dao.update(art) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("art", art);
        this.writeResult(res);
    }

    /**
     * 添加艺术品图片
     */
    public void api_addArtImg(){
        String artId = (String) this.getArgument("artid");
        String url = (String) this.getArgument("url");
        if (TextUtils.isEmpty(artId) || TextUtils.isEmpty(url)) {
            this.writeError("参数不足");
            return;
        }

        Art art = dao.fetch(Art.class, Cnd.where("id", "=", artId));
        if (art == null) {
            this.writeError("艺术品不存在");
            return;
        }

        ArtImg img = new ArtImg();
        img.setUrl(url);
        img.setArtId(art.getId());
        img.setCreateTime(new Date());

        if (dao.insert(img) == null) {
            this.writeError("数据库操作错误");
            return;
        }

        art.setImgList(getImgList(art.getId()));

        Map res = new HashMap();
        res.put("art", art);
        this.writeResult(res);
    }

    /**
     * 删除艺术品图片
     */
    public void api_delArtImg() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        ArtImg img = dao.fetch(ArtImg.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (img == null) {
            this.writeError("图片不存在");
            return;
        }

        if (dao.delete(img) <= 0) {
            this.writeError("数据库操作错误");
            return;
        }

        this.writeResult(null);
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

        Art art = dao.fetch(Art.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (art == null) {
            this.writeError("艺术品不存在");
            return;
        }

        //点击量+1
        art.setClick(art.getClick() + 1);

        if (dao.update(art) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("art", art);
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
    	
    	Art a = dao.fetch(Art.class, Cnd.where("id", "=", id));
    	if(a == null){
    		this.writeError("视频不存在");  		
    	}
    	
    	//分享量+1
    	a.setShare(a.getShare() + 1);
    	
    	if(dao.update(a) <= 0){
    		this.writeError("数据库错误");
    		return;
    	}
    	
    	Map res = new HashMap();
        res.put("art", a);
        this.writeResult(res);
    }
    
    public void api_addGroup(){
        String userId = (String) this.getArgument("userid");
        String name = (String) this.getArgument("name");
        String des = (String) this.getArgument("des");

        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(name) || TextUtils.isEmpty(des)) {
            this.writeError("参数不足");
            return;
        }

        User u = dao.fetch(User.class, Cnd.where("id", "=", userId));
        if (u == null) {
            this.writeError("用户不存在");
            return;
        }

        ArtGroup g = dao.fetch(ArtGroup.class, Cnd.where("name", "=", name).and("userid", "=", userId));
        if(g != null){
            this.writeError("分组名已存在");
            return;
        }

        ArtGroup group = new ArtGroup();
        group.setUserId(userId);
        group.setName(name);
        group.setDes(des);
        group.setCreateTime(new Date());

        if (dao.insert(group) == null) {
            this.writeError("数据库操作错误");
            return;
        }

        Map res = new HashMap();
        res.put("group", group);
        this.writeResult(res);
    }

    public void api_delGroup(){
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        ArtGroup group = dao.fetch(ArtGroup.class, Cnd.where("id", "=", id));
        if (group == null) {
            this.writeError("分组不存在");
            return;
        }

        if (dao.delete(group) <= 0) {
            this.writeError("数据库操作错误");
            return;
        }

        this.writeResult(null);
    }

    public void api_editGroup(){
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        ArtGroup group = dao.fetch(ArtGroup.class, Cnd.where("id", "=", id));
        if (group == null) {
            this.writeError("分组不存在");
            return;
        }

        String name = (String) this.getArgument("name");
        if(!TextUtils.isEmpty(name) && !name.equals(group.getName())){
            ArtGroup g = dao.fetch(ArtGroup.class, Cnd.where("name", "=", name).and("userid", "=", group.getUserId()));
            if(g != null){
                this.writeError("分组名已存在");
                return;
            }
            group.setName(name);
        }

        String des = (String) this.getArgument("des");
        if(!TextUtils.isEmpty(des)){
            group.setDes(des);
        }

        if (dao.update(group) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("group", group);
        this.writeResult(res);
    }

    public void api_getGroup(){
        String userId = (String) this.getArgument("userid");
        if (!TextUtils.isEmpty(userId)) {
            List<ArtGroup> groups = dao.query(ArtGroup.class, Cnd.where("userid", "=", userId));

            Map res = new HashMap();
            res.put("cnt", groups.size());
            res.put("groups", groups);
            this.writeResult(res);

            return;
        }

        String id = (String) this.getArgument("id");
        if(TextUtils.isEmpty(id)){
            this.writeError("参数不足");
            return;
        }

        ArtGroup g = dao.fetch(ArtGroup.class, Cnd.where("id", "=", id));
        if(g == null){
            this.writeError("分组不存在");
            return;
        }

        Map res = new HashMap();
        res.put("group", g);
        this.writeResult(res);
    }

    private List getImgList(int artId){
        return dao.query(ArtImg.class, Cnd.where("artid", "=", artId).desc("createtime"));
    }
}
