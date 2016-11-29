package com.bmi.mobileart.service.Handler;

import java.util.*;

import org.nutz.dao.Cnd;

import com.bmi.mobileart.service.DAO.Follow;
import com.bmi.mobileart.service.DAO.User;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.utils.TextUtils;
/**
 * Date:2015/04/15
 * @author xiaoyu
 *
 */
public class FollowHandler extends BaseHandler{
	/**
	 * add follow
	 */
	public void api_addFollow(){
        User user = UserHandler.checkUserAuth(this);
        if (user == null) {
            return;
        }

		String targetId = (String) this.getArgument("targetid");
		if (TextUtils.isEmpty(targetId)) {
            this.writeError("参数不足");
            return;
        }

		Follow f = dao.fetch(Follow.class,
                Cnd.where("userid", "=", user.getId()).and("targetid", "=", targetId));
		if (f != null) {
            this.writeError("已关注");
            return;
        }

        User targetUser = dao.fetch(User.class, Cnd.where("id", "=", targetId));
        if(targetUser == null){
            this.writeError("目标关注用户不存在");
            return;
        }
		
		Follow follow = new Follow();
		follow.setUserid(user.getId());
		follow.setTargetid(Integer.parseInt(targetId));
		follow.setCreateTime(new Date());
		
		if(dao.insert(follow) == null){
			this.writeError("数据库操作错误");
			return;
		}

        this.writeResult(null);
	}
	
	/**
	 * delete follow
	 */
	public void api_delFollow(){
        User user = UserHandler.checkUserAuth(this);
        if (user == null) {
            return;
        }

		String targetId = (String) this.getArgument("targetid");
		if (TextUtils.isEmpty(targetId)) {
            this.writeError("参数不足");
            return;
        }
		
		Follow f = dao.fetch(Follow.class,
				Cnd.where("userid", "=", user.getId()).and("targetid", "=", targetId));
		if (f == null) {
            this.writeError("尚未关注");
            return;
        }
		
		if(dao.delete(f) <= 0){
			this.writeError("数据库操作错误");
			return;
		}

        this.writeResult(null);
	}
	
	/**
	 * isFollow
	 */
	public void api_isFollow(){
        String userId = (String) this.getArgument("userid");
		String targetId = (String) this.getArgument("targetid");
		if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(targetId)) {
            this.writeError("参数不足");
            return;
        }

        String[] userIdList = userId.split(",");
        String[] targetIdList = targetId.split(",");

        List resList = new ArrayList();
        int minLen = Math.min(userIdList.length, targetIdList.length);
        for(int i = 0;i < minLen; i++){
            Follow f = dao.fetch(Follow.class,
                    Cnd.where("userid", "=", userIdList[i]).and("targetid", "=", targetIdList[i]));

            Map resPart = new HashMap();
            resPart.put("userid", userIdList[i]);
            resPart.put("targetid", targetIdList[i]);
            resPart.put("follow", f != null);

            resList.add(resPart);
        }

        Map res = new HashMap();
        res.put("follows", resList);
        this.writeResult(res);
	}
	
	/**
	 * 查找当前登录用户所关注的所有艺术家
	 */
	public void api_getFollowList(){
        User user = UserHandler.checkUserAuth(this);
        if (user == null) {
            return;
        }

        String page = (String) this.getArgument("page");
        if(TextUtils.isEmpty(page)){
            page = "1";
        }

        //分页，每页10个
		List<Follow> followList = dao.query(Follow.class,
                Cnd.where("userid", "=", user.getId()).desc("createtime"), dao.createPager(Integer.parseInt(page), 10));

        Map res = new HashMap();
        List<Map> userList = new ArrayList<Map>(followList.size());
        for (Follow f : followList) {
            User u = dao.fetch(User.class, Cnd.where("id", "=", f.getTargetid()));
            if (u != null) {
                userList.add(u.toKVPair());
            }
        }

        res.put("page", page);
        res.put("cnt", userList.size());
        res.put("users", userList);

        this.writeResult(res);
	}

    /**
     * 查找艺术家的粉丝列表
     */
    public void api_getFansList(){
        String userId = (String) this.getArgument("userid");
        if(TextUtils.isEmpty(userId)){
            this.writeError("参数不足");
            return;
        }

        String page = (String) this.getArgument("page");
        if(TextUtils.isEmpty(page)){
            page = "1";
        }

        String pageSize = (String) this.getArgument("pagesize");
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }

        //分页
        List<Follow> followList = dao.query(Follow.class,
                Cnd.where("targetid", "=", userId).desc("createtime"), dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));

        Map res = new HashMap();
        List<Map> userList = new ArrayList<Map>(followList.size());
        for (Follow f : followList) {
            User u = dao.fetch(User.class, Cnd.where("id", "=", f.getUserid()));
            if (u != null) {
                userList.add(u.toKVPair());
            }
        }

        res.put("page", page);
        res.put("cnt", userList.size());
        res.put("users", userList);

        this.writeResult(res);
    }
}
