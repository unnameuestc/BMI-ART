package com.bmi.mobileart.service.Server;

import com.bmi.mobileart.service.Config;
import com.bmi.mobileart.service.utils.TextUtils;
import com.sun.net.httpserver.*;
import org.nutz.dao.Dao;
import org.nutz.json.Json;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Keith on 2015/4/9.
 */
public abstract class BaseHandler implements HttpHandler {
    public Dao dao;

    private Headers requestHeaders;
    private Headers responseHeaders;
    private Map arguments;

    protected String response;

    public BaseHandler() {
        dao = Config.getDao();
    }
    
    /**
     * param: HttpExchange
     * function: Handle the given request and generate an appropriate response.
     */
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        requestHeaders = exchange.getRequestHeaders();
        arguments = (Map)exchange.getAttribute("parameters");

        responseHeaders = exchange.getResponseHeaders();

        //InetSocketAddress address = exchange.getRemoteAddress();

        if(checkApiKey()) {
            invokeMethod(exchange.getHttpContext().getPath());
        }

        responseHeaders.set("Content-Type", "application/json; charset=utf-8;");
        responseHeaders.set("Server", Config.ServerName);
        responseHeaders.set("Access-Control-Allow-Origin", "*");

        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(bytes);
        responseBody.close();

        exchange.close();
    }

    /**
     * 获取Query
     * @param key
     * @return
     */
    public Object getArgument(String key) {
        return this.arguments.get(key);
    }

    /**
     * 写Response(Json字符串)
     * @param isOk
     * @param errStr
     * @param data
     */
    private void writeResponse(boolean isOk, String errStr, Map data) {
        Map res = new HashMap();
        res.put("invoke", isOk);

        if (isOk) {
            res.put("result", data);
        } else {
            res.put("error", errStr);
        }

        response = Json.toJson(res);
    }

    /**
     * (调用成功)输出结果
     * @param data
     */
    public void writeResult(Map data) {
        writeResponse(true, "", data);
    }

    /**
     * (调用失败)输出出错信息
     * @param errStr
     */
    public void writeError(String errStr) {
        writeResponse(false, errStr, null);
    }

    /**
     * 通过反射调用对应api方法
     * @param urlPath
     */
    private void invokeMethod(String urlPath) {
        String method = urlPath.substring(urlPath.lastIndexOf('/') + 1);
        if (TextUtils.isEmpty(method)) {
            writeError("未指定api");
            return;
        }

        boolean isInvokeOk = true;
        long startTime = System.currentTimeMillis();

        Class me = this.getClass();
        try {
        	//通过反射调用其他类的方法
            Method queryMethod = me.getMethod("api_" + method);
            queryMethod.invoke(this);
        } catch (NoSuchMethodException e) {
            writeError("未找到api " + method);
            return;
        } catch (Exception e) {
            if (Config.DEBUG) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(response)) {
                writeError("服务器内部错误: " + e.getMessage());
            }

            isInvokeOk = false;
        }


        if (!method.equals("status")) {
            long costTime = System.currentTimeMillis() - startTime;
            ApiMonitor.get().addRecord(urlPath, !isInvokeOk, (int) costTime);//注意传进去的是urlPath
        }
    }


    /**
     * api授权检查
     * @return
     */
    public boolean checkApiKey() {
        String apiKey = (String) getArgument("apikey");
        if (TextUtils.isEmpty(apiKey) || !apiKey.equals(Config.ApiKey)) {
            writeError("未授权调用");
            return false;
        }

        return true;
    }
}
