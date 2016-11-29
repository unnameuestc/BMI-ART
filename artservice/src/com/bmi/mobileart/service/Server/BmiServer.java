package com.bmi.mobileart.service.Server;

import com.bmi.mobileart.service.Config;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * Created by Keith on 2015/4/10.
 */
public class BmiServer {
    private int port = 80;
    private int threadCnt = 10;

    private HttpServer httpServer = null;

    
    public BmiServer(int port, int threadCnt) { 
        this.port = port;
        //This is the maximum number of queued incoming connections to allow on the listening socket. 
        this.threadCnt = threadCnt;

        try {
        	//HttpServer:This class implements a simple HTTP server
            httpServer = HttpServer.create(new InetSocketAddress(port), threadCnt);
        } catch (IOException e) {
            if(Config.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public boolean addHandler(String path, BaseHandler handler){
        if(httpServer == null){
            return false;
        }

        ParameterFilter filter = new ParameterFilter();
        try {
        	//Returns the runtime class of this Object
            Class<?> c = handler.getClass();
            Method methods[] = c.getMethods();
            for(Method m : methods) {
                String name = m.getName();
                if(name.startsWith("api_")){
                	//HttpContext represents a mapping between the root URI path of an application to a HttpHandler
                    HttpContext context = httpServer.createContext(path + name.substring(4), handler);
                    //returns this context's list of Filters,then add filter
                    context.getFilters().add(filter);
                }
            }
        }catch (Exception e){
            if(Config.DEBUG){
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean start(){
        if(httpServer == null){
            return false;
        }

        System.out.println("Server Listen port " + port + " ...");
        httpServer.start();
        return true;
    }
}
