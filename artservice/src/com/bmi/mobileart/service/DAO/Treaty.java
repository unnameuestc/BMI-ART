package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.*;

/**
 * 
 * @author xiaoyu
 *
 */
@Table("t_treaty")
public class Treaty {
	@Id
	private int id;
	
	@Column("content")
	private String content;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
