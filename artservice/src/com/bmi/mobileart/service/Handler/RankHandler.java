package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.DAO.*;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.utils.TextUtils;
import org.nutz.dao.Cnd;

import java.util.*;

/**
 * Created by Keith on 2015/6/16.
 */
public class RankHandler extends BaseHandler {

    public void api_topClick(){
        String sizeStr = (String) this.getArgument("size");
        if (TextUtils.isEmpty(sizeStr)) {
            sizeStr = "20";
        }

        int size = Integer.parseInt(sizeStr);

        //视频
        List<Video> videos = dao.query(Video.class, Cnd.NEW().desc("click"),
                dao.createPager(1, size));

        //艺术品
        List<Art> arts = dao.query(Art.class, Cnd.NEW().desc("click"),
                dao.createPager(1, size));

        //文章
        List<Page> pages = dao.query(Page.class, Cnd.NEW().desc("click"),
                dao.createPager(1, size));

        //合并排序
        List<CompContent> resList = new ArrayList<CompContent>(size * 3);

        for(Video v : videos){
            resList.add(new CompContent(v, v.getClick(), DaoConst.CONTENT_VIDEO));
        }

        for(Art a : arts){
            resList.add(new CompContent(a, a.getClick(), DaoConst.CONTENT_ART));
        }

        for(Page p : pages){
            resList.add(new CompContent(p, p.getClick(), DaoConst.CONTENT_PAGE));
        }

        Collections.sort(resList, new ComparatorTop());

        resList = resList.subList(0, size);
        int cnt = resList.size();

        Map map = new HashMap();
        map.put("cnt", cnt);
        map.put("list", resList);

        writeResult(map);
    }

    public void api_topComment(){
        String sizeStr = (String) this.getArgument("size");
        if (TextUtils.isEmpty(sizeStr)) {
            sizeStr = "20";
        }

        int size = Integer.parseInt(sizeStr);

        //视频
        List<Video> videos = dao.query(Video.class, Cnd.NEW().desc("commentcnt"),
                dao.createPager(1, size));

        //艺术品
        List<Art> arts = dao.query(Art.class, Cnd.NEW().desc("commentcnt"),
                dao.createPager(1, size));

        //文章
        List<Page> pages = dao.query(Page.class, Cnd.NEW().desc("commentcnt"),
                dao.createPager(1, size));

        //合并排序
        List<CompContent> resList = new ArrayList<CompContent>(size * 3);

        for(Video v : videos){
            resList.add(new CompContent(v, v.getCommentCnt(), DaoConst.CONTENT_VIDEO));
        }

        for(Art a : arts){
            resList.add(new CompContent(a, a.getCommentCnt(), DaoConst.CONTENT_ART));
        }

        for(Page p : pages){
            resList.add(new CompContent(p, p.getCommentCnt(), DaoConst.CONTENT_PAGE));
        }

        Collections.sort(resList, new ComparatorTop());

        resList = resList.subList(0, size);
        int cnt = resList.size();

        Map map = new HashMap();
        map.put("cnt", cnt);
        map.put("list", resList);

        writeResult(map);
    }

    private class CompContent {
        public Object obj;
        public int cmp;
        public int type;

        public CompContent(Object obj, int cmp, int type) {
            this.obj = obj;
            this.cmp = cmp;
            this.type = type;
        }
    }

    private class ComparatorTop implements Comparator<CompContent>{

        @Override
        public int compare(CompContent o1, CompContent o2) {
            return Integer.compare(o2.cmp, o1.cmp);
        }
    }

    public void api_topCertifyUser(){
        String sizeStr = (String) this.getArgument("size");
        if (TextUtils.isEmpty(sizeStr)) {
            sizeStr = "20";
        }

        int size = Integer.parseInt(sizeStr);

        //手工过滤0和4(管理员)
        List<User> users = dao.query(User.class, Cnd.NEW().andNot("certifytypes", "LIKE", "%0%")
                        .andNot("certifytypes", "LIKE", "%4%").andNot("certifytypes", "<>", "").desc("certifytime"),
                dao.createPager(1, size));

        int cnt = users.size();

        Map map = new HashMap();
        map.put("cnt", cnt);
        map.put("list", users);

        writeResult(map);
    }
}
