package com.bmi.mobileart.service;

import com.bmi.mobileart.service.Config;
import com.bmi.mobileart.service.Handler.*;
import com.bmi.mobileart.service.Server.*;

import java.io.IOException;

/**
 * @author hoganliu
 * modified by xiaoyu
 */
public class Main {

    private ApiMonitor monitor = ApiMonitor.get();

    public static void main(String[] args) throws IOException {

        //初始化配置
        if(!Config.init()){
            System.out.println("Error init configuration!");
            return;
        }

        BmiServer server = new BmiServer(Config.PortNum, Config.ThreadCnt);

        //首页
        server.addHandler("/", new IndexHandler());

        //用户
        server.addHandler("/user/", new UserHandler());

        //短信
        server.addHandler("/sms/", new SmsHandler());

        //关注
        server.addHandler("/follow/", new FollowHandler());

        //艺术品
        server.addHandler("/art/", new ArtHandler());

        //视频
        server.addHandler("/video/", new VideoHandler());

        //文章
        server.addHandler("/page/", new PageHandler());

        //收藏
        server.addHandler("/favor/", new FavorHandler());

        //评论
        server.addHandler("/comment/", new CommentHandler());

        //轮播图片
        server.addHandler("/banner/", new BannerHandler());

        //排行
        server.addHandler("/rank/", new RankHandler());

        //交易
        server.addHandler("/trade/", new TradeHandler());

        //其它
        server.addHandler("/other/", new OtherHandler());

        server.start();
    }
}
