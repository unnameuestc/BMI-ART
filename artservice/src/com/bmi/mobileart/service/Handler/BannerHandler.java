package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.DAO.Art;
import com.bmi.mobileart.service.DAO.ArtType;
import com.bmi.mobileart.service.DAO.Banner;
import com.bmi.mobileart.service.DAO.DaoConst;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.utils.TextUtils;
import org.nutz.dao.Cnd;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Keith on 2015/5/14.
 */
public class BannerHandler extends BaseHandler {

    public final static String HomeBannerName = "home";
    public final static String TradeBannerName = "trade";

    public void api_getHomeBanner() {
        baseGetBanner(HomeBannerName);
    }

    //后台网站接口
    public void api_getBannerById(){
    	String banner_id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(banner_id)) {
            this.writeError("参数不足");
            return;
        }

        Banner banner = (Banner) dao.fetch(Banner.class, Cnd.where(Cnd.exps("id", "=", banner_id)));
        Map res = new HashMap();
        res.put("banner", banner);
        this.writeResult(res);
    }
    
    public void api_editBanner() {
    	String bannerId = (String) this.getArgument("bannerid");
    	String description = (String) this.getArgument("description");
        String picUrl = (String) this.getArgument("picurl");
    	if (TextUtils.isEmpty(bannerId)) {
            this.writeError("参数不足1111");
            return;
        }
    	
    	Banner b = dao.fetch(Banner.class, Cnd.where("id", "=", bannerId));
    	if(b == null){
    		this.writeError("图片不存在");
    	}
    	
    	if(!TextUtils.isEmpty(description)){
    		b.setDescription(description);
    	}
    	
    	if(!TextUtils.isEmpty(picUrl)){
    		b.setPicUrl(picUrl);
    	}
    	     	
    	if (dao.update(b) <= 0){
    		this.writeError("数据库操作错误");
            return;
    	}
    	
    	Map res = new HashMap();
        res.put("banner", b);
        this.writeResult(res);
    }
    
    public void api_addHomeBanner() {
        String typeId = (String) this.getArgument("typeid");
        String targetId = (String) this.getArgument("targetid");
        String description = (String) this.getArgument("description");
        String picUrl = (String) this.getArgument("picurl");

        if (TextUtils.isEmpty(typeId) || TextUtils.isEmpty(targetId)
                || TextUtils.isEmpty(description) || TextUtils.isEmpty(picUrl)) {
            this.writeError("参数不足");
            return;
        }

        Banner banner = new Banner();
        banner.setName(HomeBannerName);
        banner.setTypeId(Integer.parseInt(typeId));
        banner.setTargetId(Integer.parseInt(targetId));
        banner.setDescription(description);
        banner.setPicUrl(picUrl);
        banner.setCreateTime(new Date());

        if (dao.insert(banner) == null) {
            this.writeError("数据库操作错误");
            return;
        }

        Map res = new HashMap();
        res.put("banner", banner);
        this.writeResult(res);
    }

    public void api_getTradeBanner() {
        baseGetBanner(TradeBannerName);
    }

    public void api_addTradeBanner() {
        String targetId = (String) this.getArgument("targetid");
        String description = (String) this.getArgument("description");
        String picUrl = (String) this.getArgument("picurl");

        if (TextUtils.isEmpty(targetId) || TextUtils.isEmpty(description) || TextUtils.isEmpty(picUrl)) {
            this.writeError("参数不足");
            return;
        }

        //检查一下
        Art art = dao.fetch(Art.class, Cnd.where("id", "=", targetId));
        if (art == null || art.getTradeState() == DaoConst.TREAD_DISABLE) {
            this.writeError("艺术品不存在或不可交易");
            return;
        }

        Banner banner = new Banner();
        banner.setName(TradeBannerName);
        banner.setTypeId(DaoConst.CONTENT_ART);
        banner.setTargetId(Integer.parseInt(targetId));
        banner.setDescription(description);
        banner.setPicUrl(picUrl);
        banner.setCreateTime(new Date());

        if (dao.insert(banner) == null) {
            this.writeError("数据库操作错误");
            return;
        }

        Map res = new HashMap();
        res.put("banner", banner);
        this.writeResult(res);
    }

    public void api_delBanner() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Banner banner = (Banner) dao.fetch(Banner.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (banner == null) {
            this.writeError("轮播图片不存在");
            return;
        }

        if (dao.delete(banner) <= 0) {
            this.writeError("数据库操作错误");
            return;
        }

        this.writeResult(null);
    }


    private void baseGetBanner(String bannerName) {
        List<Banner> banners = dao.query(Banner.class, Cnd.where("name", "=", bannerName));
        int cnt = 0;
        if (banners != null) {
            cnt = banners.size();
        }

        Map res = new HashMap();
        res.put("cnt", cnt);
        if (cnt > 0) {
            res.put("banners", banners);
        }
        this.writeResult(res);
    }
}
