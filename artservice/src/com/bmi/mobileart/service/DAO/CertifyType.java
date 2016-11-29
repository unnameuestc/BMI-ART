package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * Created by Keith on 2015/4/13.
 */
@Table("t_certifytype")
public class CertifyType {
    @Id
    private int id;

    @Column("name")
    private String name;

    @Column("title")
    private String title;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
