package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.DAO.*;
import com.bmi.mobileart.service.Server.ApiMonitor;
import com.bmi.mobileart.service.Server.BaseHandler;
import com.bmi.mobileart.service.Config;
import com.bmi.mobileart.service.utils.TextUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.nutz.dao.Cnd;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Keith on 2015/4/10.
 */
public class IndexHandler extends BaseHandler {

    private Comparator<ApiStatus> comparatorStatus = new Comparator<ApiStatus>() {
        @Override
        public int compare(ApiStatus o1, ApiStatus o2) {
            return o1.getApiName().compareTo(o2.getApiName());
        }
    };

    public void api_(){
        writeError("非法请求");
    }

    public void api_status(){
        Map map = new HashMap();

        List statusList = ApiMonitor.get().getStatusList();
        Collections.sort(statusList, comparatorStatus);

        map.put("cnt", statusList.size());
        map.put("status",statusList);

        writeResult(map);
    }
}