package com.bmi.mobileart.service.DAO;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.entity.annotation.*;

/**
 * 
 * @author xiaoyu
 * Date 2015/4/14
 */

@Table("t_follow")
public class Follow {
	@Id
	private int id;
	
	@Column("userid")
	private int userid;
	
	@Column("targetid")
	private int targetid;
	
	@Column("createtime")
    private Date createTime;

	public int getId() {
		return id;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getTargetid() {
		return targetid;
	}

	public void setTargetid(int targetid) {
		this.targetid = targetid;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Map toKVPair() {
        Map map = new HashMap();

        map.put("id", id);
        map.put("userid", userid);
        map.put("targetid", targetid);
        map.put("createtime", createTime);

        return map;
    }
}
