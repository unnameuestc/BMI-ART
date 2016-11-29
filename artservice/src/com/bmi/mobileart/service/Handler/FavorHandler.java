package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.DAO.*;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.utils.TextUtils;
import org.nutz.dao.Cnd;

import java.util.*;

/**
 * Created by Keith on 2015/4/30.
 */
public class FavorHandler extends BaseHandler {

    /**
     * 获取用户的收藏列表
     */
    public void api_getFavors() {
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

        List<Favor> favorList = dao.query(Favor.class,
                Cnd.where("userid", "=", user.getId()).desc("createtime"),
                dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));

        List<FavorContent> resList = new ArrayList<FavorContent>(favorList.size());
        for (Favor f : favorList) {
            switch (f.getTypeId()) {
                case DaoConst.CONTENT_VIDEO:
                    Video v = dao.fetch(Video.class, Cnd.where("id", "=", f.getTargetId()));
                    resList.add(new FavorContent(v, DaoConst.CONTENT_VIDEO));
                    break;
                case DaoConst.CONTENT_ART:
                    Art a = dao.fetch(Art.class, Cnd.where("id", "=", f.getTargetId()));
                    //去掉简介
                    //a.setContent(null);
                    //第一张图
                    ArtImg img = dao.fetch(ArtImg.class, Cnd.where(Cnd.exps("artid", "=", a.getId())).desc("createtime"));
                    a.setTitleImg(img.getUrl());
                    resList.add(new FavorContent(a, DaoConst.CONTENT_ART));
                    break;
                case DaoConst.CONTENT_PAGE:
                    Page p = dao.fetch(Page.class, Cnd.where("id", "=", f.getTargetId()));
                    resList.add(new FavorContent(p, DaoConst.CONTENT_PAGE));
                    break;
                default:
                    this.writeError("收藏类型不正确");
                    return;
            }
        }

        //总数
        int allCnt = dao.count(Favor.class, Cnd.where("userid", "=", user.getId()));

        Map res = new HashMap();
        res.put("cnt", resList.size());
        res.put("favors", resList);
        res.put("allCnt", allCnt);
        res.put("page", page);

        this.writeResult(res);
    }

    private class FavorContent {
        public Object obj;
        public int type;

        public FavorContent(Object obj, int type) {
            this.obj = obj;
            this.type = type;
        }
    }

    /**
     * 添加收藏
     */
    public void api_addFavor() {
        User user = UserHandler.checkUserAuth(this);
        if (user == null) {
            return;
        }

        String typeId = (String) this.getArgument("typeid");
        String targetId = (String) this.getArgument("targetid");
        if (TextUtils.isEmpty(typeId) || TextUtils.isEmpty(targetId)) {
            this.writeError("参数不足");
            return;
        }

        Favor f = dao.fetch(Favor.class, Cnd.where("typeid", "=", typeId)
                .and("userid", "=", user.getId())
                .and("targetid", "=", targetId));
        if (f != null) {
            this.writeError("已收藏");
            return;
        }

        int type = Integer.parseInt(typeId);
        switch (type) {
            case DaoConst.CONTENT_VIDEO:
                Video v = dao.fetch(Video.class, Cnd.where("id", "=", targetId));
                if (v == null) {
                    this.writeError("要收藏的视频不存在");
                    return;
                }
                break;
            case DaoConst.CONTENT_ART:
                Art a = dao.fetch(Art.class, Cnd.where("id", "=", targetId));
                if (a == null) {
                    this.writeError("要收藏的艺术品不存在");
                    return;
                }
                break;
            case DaoConst.CONTENT_PAGE:
                Page p = dao.fetch(Page.class, Cnd.where("id", "=", targetId));
                if (p == null) {
                    this.writeError("要收藏的文章不存在");
                    return;
                }
                break;
            default:
                this.writeError("收藏类型不正确");
                return;
        }

        Favor favor = new Favor();
        favor.setUserId(user.getId());
        favor.setTypeId(type);
        favor.setTargetId(Integer.parseInt(targetId));
        favor.setCreateTime(new Date());

        if (dao.insert(favor) == null) {
            this.writeError("数据库操作错误");
            return;
        }

        this.writeResult(null);
    }

    /**
     * 是否收藏
     */
    public void api_isFavor() {
        String userId = (String) this.getArgument("userid");
        String typeId = (String) this.getArgument("typeid");
        String targetId = (String) this.getArgument("targetid");
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(typeId) || TextUtils.isEmpty(targetId)) {
            this.writeError("参数不足");
            return;
        }

        String[] userIdList = userId.split(",");
        String[] typeIdList = typeId.split(",");
        String[] targetIdList = targetId.split(",");

        List resList = new ArrayList();
        int minLen = Math.min(Math.min(userIdList.length, typeIdList.length), targetIdList.length);
        for(int i = 0;i < minLen; i++){
            Favor f = dao.fetch(Favor.class,
                    Cnd.where("userid", "=", userIdList[i])
                            .and("typeid", "=", typeIdList[i])
                            .and("targetid", "=", targetIdList[i]));

            Map resPart = new HashMap();
            resPart.put("userid", userIdList[i]);
            resPart.put("typeid", typeIdList[i]);
            resPart.put("targetid", targetIdList[i]);
            resPart.put("favor", f != null);

            resList.add(resPart);
        }

        Map res = new HashMap();
        res.put("favors", resList);
        this.writeResult(res);
    }

    /**
     * 取消收藏
     */
    public void api_delFavor() {
        User user = UserHandler.checkUserAuth(this);
        if (user == null) {
            return;
        }

        String typeId = (String) this.getArgument("typeid");
        String targetId = (String) this.getArgument("targetid");
        if (TextUtils.isEmpty(typeId) || TextUtils.isEmpty(targetId)) {
            this.writeError("参数不足");
            return;
        }

        Favor f = dao.fetch(Favor.class, Cnd.where("typeid", "=", typeId)
                .and("userid", "=", user.getId())
                .and("targetid", "=", targetId));

        if (f == null) {
            this.writeError("尚未收藏");
            return;
        }

        if (dao.delete(f) <= 0) {
            this.writeError("数据库操作错误");
            return;
        }

        this.writeResult(null);
    }
}
