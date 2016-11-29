package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.DAO.Art;
import com.bmi.mobileart.service.DAO.DaoConst;
import com.bmi.mobileart.service.DAO.Trade;
import com.bmi.mobileart.service.DAO.Treaty;
import com.bmi.mobileart.service.DAO.User;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.utils.TextUtils;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Keith on 2015/6/16.
 * modified by xiaoyu on 2015/7/3.
 */
public class TradeHandler extends BaseHandler {

    public void api_getAllTrades() {
        String page = (String) this.getArgument("page");
        if (TextUtils.isEmpty(page)) {
            page = "1";
        }

        String pageSize = (String) this.getArgument("pagesize");
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }

        List<Trade> trades = dao.query(Trade.class, Cnd.NEW().desc("createtime"),
                dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));

        Map res = new HashMap();

        //订单总数
        int allCnt = dao.count(Trade.class, null);
        res.put("allCnt", allCnt);
        res.put("cnt", trades.size());
        res.put("trades", trades);
        res.put("page", page);

        this.writeResult(res);
    }

    public void api_getTradeInfo() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Trade trade = dao.fetch(Trade.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (trade == null) {
            this.writeError("订单不存在");
            return;
        }

        Map res = new HashMap();
        res.put("trade", trade);
        this.writeResult(res);
    }
    
    public void api_getMyTrades(){
    	User user = UserHandler.checkUserAuth(this);
    	if(user == null){
    		return;
    	}
    	
    	String page = (String) this.getArgument("page");
    	if(TextUtils.isEmpty(page)){
    		page = "1";
    	}
    	
    	String pageSize = (String) this.getArgument("pageSize");
    	if(TextUtils.isEmpty(pageSize)){
    		pageSize = "10";
    	}
    	
    	List<Trade> myTrades = dao.query(Trade.class, Cnd.where("userid", "=", user.getId()), 
    			dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));
    	
    	List<Trade> allMyTrades = dao.query(Trade.class, Cnd.where("userid", "=", user.getId()));    	
    	
    	Map res = new HashMap();
    	
    	res.put("allCnt", allMyTrades.size());
    	res.put("cnt", pageSize);
    	res.put("page", page);
    	res.put("myTrades", myTrades);
    	
    	this.writeResult(res);
    	
    }

    public void api_addTrade() {
        String artId = (String) this.getArgument("artid");
        String userId = (String) this.getArgument("userid");
        String recipient = (String) this.getArgument("recipient");
        String contact = (String) this.getArgument("contact");
        String addr = (String) this.getArgument("addr");

        if(TextUtils.isEmpty(artId) || TextUtils.isEmpty(userId)
                || TextUtils.isEmpty(recipient) || TextUtils.isEmpty(contact)
                || TextUtils.isEmpty(addr)){
            this.writeError("参数不足");
            return;
        }

        Art art = dao.fetch(Art.class, Cnd.where("id", "=", artId));
        if (art == null) {
            this.writeError("艺术品不存在");
            return;
        }

        User user = dao.fetch(User.class, Cnd.where("id", "=", userId));
        if (user == null) {
            this.writeError("用户不存在");
            return;
        }

        Trade trade = new Trade();
        trade.setArtId(art.getId());
        trade.setUserId(user.getId());
        trade.setRecipient(recipient);
        trade.setContact(contact);
        trade.setAddr(addr);
        trade.setCreateTime(new Date());
        trade.setState(DaoConst.TREAD_ING);

        if (dao.insert(trade) == null) {
            this.writeError("数据库操作错误");
            return;
        }

        //修改艺术品的订单状态
        art.setTradeState(DaoConst.TREAD_ING);
        if (dao.update(art) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("trade", trade);
        this.writeResult(res);
    }

    public void api_editTrade() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Trade trade = dao.fetch(Trade.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (trade == null) {
            this.writeError("订单不存在");
            return;
        }

        String recipient = (String) this.getArgument("recipient");
        if(!TextUtils.isEmpty(recipient)){
            trade.setRecipient(recipient);
        }

        String contact = (String) this.getArgument("contact");
        if(!TextUtils.isEmpty(contact)){
            trade.setContact(contact);
        }

        String addr = (String) this.getArgument("addr");
        if(!TextUtils.isEmpty(addr)){
            trade.setAddr(addr);
        }

        String stateStr = (String) this.getArgument("state");
        if(!TextUtils.isEmpty(stateStr)){
            int state = Integer.parseInt(stateStr);
            if(state != DaoConst.TREAD_ING && state != DaoConst.TREAD_FINISH && state != DaoConst.TREAD_ERROR){
                this.writeError("非法的订单状态");
                return;
            }
            trade.setState(state);
            if(state == DaoConst.TREAD_FINISH){
                trade.setFinishTime(new Date());
            }
        }

        if (dao.update(trade) <= 0) {
            this.writeError("数据库错误");
            return;
        }

        //修改艺术品的订单状态
        Art art = dao.fetch(Art.class, Cnd.where("id", "=", trade.getArtId()));
        if (art != null) {
            //交易失败则转为可交易
            art.setTradeState(trade.getState() == DaoConst.TREAD_ERROR ? DaoConst.TREAD_ENABLE : trade.getState());
            if (dao.update(art) <= 0) {
                this.writeError("数据库错误");
                return;
            }
        }

        Map res = new HashMap();
        res.put("trade", trade);
        this.writeResult(res);
    }

    public void api_delTrade() {
        String id = (String) this.getArgument("id");
        if (TextUtils.isEmpty(id)) {
            this.writeError("参数不足");
            return;
        }

        Trade trade = dao.fetch(Trade.class, Cnd.where(Cnd.exps("id", "=", id)));
        if (trade == null) {
            this.writeError("订单不存在");
            return;
        }

        if (dao.delete(trade) <= 0) {
            this.writeError("数据库操作错误");
            return;
        }

        this.writeResult(null);
    }

    /**
     * 订购协议相关操作
     */
    public void api_getTreaty() {
    	Treaty treaty = dao.fetch(Treaty.class);
    	if(treaty == null){
    		this.writeError("订购协议不存在");
    		return;
    	}
    	
    	Map res = new HashMap();
    	res.put("treaty", treaty);
    	this.writeResult(res);
    }
    
    /**
     * 如果表t_treaty为空，则添加订购协议；若不为空，则修改现有协议
     */
    public void api_editTreaty() {
    	Treaty treaty = null;
    	treaty = dao.fetch(Treaty.class);
    	
    	String content = (String) this.getArgument("content");    	    	
    	if(TextUtils.isEmpty(content)){
    		return;
    	}
    	
    	if(treaty == null) {
    		treaty = new Treaty();
    		treaty.setContent(content);
    		if (dao.insert(treaty) == null) {
                this.writeError("数据库错误");
                return;
            }
    	}else {
    		treaty.setContent(content);    		
    		if (dao.update(treaty) <= 0) {
                this.writeError("数据库错误");
                return;
            }
    	}
    	
    	Map res = new HashMap();
    	res.put("treaty", treaty);
    	this.writeResult(res);
    }
}
