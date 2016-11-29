package com.bmi.mobileart.service.DAO;

import org.nutz.dao.entity.annotation.*;

/**
 * Created by Keith on 2015/4/25.
 */

@Table("t_arttype")
@View("v_arttype")
public class ArtType {
    @Id
    private int id;

    @Column("name")
    private String name;

    @Column("description_cn")
    private String description_cn;

    @Column("description_en")
    private String description_en;

    @Column("picurl")
    private String picUrl;

    @Column("videotitle")
    private String videoTitle;

    @Column("artCnt")
    @Readonly
    private int artCnt;

    @Column("videoCnt")
    @Readonly
    private int videoCnt;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription_cn() {
        return description_cn;
    }

    public void setDescription_cn(String description_cn) {
        this.description_cn = description_cn;
    }

    public String getDescription_en() {
        return description_en;
    }

    public void setDescription_en(String description_en) {
        this.description_en = description_en;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }
}
