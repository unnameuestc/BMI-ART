package com.bmi.mobileart.service.Server;

import com.bmi.mobileart.service.utils.TextUtils;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Created by Keith on 2015/4/12.
 */

//A filter used to pre- and post-process incoming requests.
public class ParameterFilter extends Filter {
    @Override
    public String description() {
        return "Parses the requested URI for parameters";	//parses 从语法上分析
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain)
            throws IOException {
        parseGetParameters(exchange);
        parsePostParameters(exchange);
        chain.doFilter(exchange);
    }

    private void parseGetParameters(HttpExchange exchange)
            throws UnsupportedEncodingException {

        Map parameters = new HashMap();
        URI requestedUri = exchange.getRequestURI();	//Get the request URI
        /**
         * Returns the raw query component of this URI.
         * 从URL请求中得到 “参数名=参数值”：apikey=A6F7F0D6CD13058D40C1110F007E7F13&name=xiaoyu
         */
        String query = requestedUri.getRawQuery();		
//        System.out.println("query的值：" + query);
        decodeParams(query, parameters);
        exchange.setAttribute("parameters", parameters);
    }

    private void parsePostParameters(HttpExchange exchange)
            throws IOException {

        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map parameters = (Map) exchange.getAttribute("parameters");

            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);

            //Post数据提交有四种方式
            if (contentType.startsWith("application/x-www-form-urlencoded")) {	//提交表单数据
                String line = br.readLine();
                while (line != null) {
                    if (line.length() > 0) {
                        decodeParams(line, parameters);
                        break;
                    }
                    line = br.readLine();
                }
            } else if (contentType.startsWith("multipart/form-data")) {		//使用表单上传文件
                String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);
                boundary = boundary.substring(0, boundary.indexOf(";"));

                String line = br.readLine();
                while (line != null && !line.equals("--" + boundary + "--")) {
                    if (line.startsWith("--" + boundary)) {
                        String nameStr = br.readLine();
                        if (!TextUtils.isEmpty(nameStr) && !nameStr.contains("filename=\"")) {    //不管文件
                            String key = nameStr.substring(nameStr.indexOf("name=\"") + 6, nameStr.length() - 1);
                            br.readLine();  //注意空行

                            StringBuilder value = new StringBuilder(br.readLine());

                            line = br.readLine();
                            while (!line.startsWith("--" + boundary)) {
                                value.append("\r\n" + line);
                                line = br.readLine();
                            }

                            parameters.put(URLDecoder.decode(key, "utf-8"), value.toString());
                        }
                    } else {
                        line = br.readLine();
                    }
                }
            }

            br.close();
            isr.close();
        }
    }

    private String decodePercent(String str) {
        String decoded = null;
        try {
        	//使用指定的编码机制对 application/x-www-form-urlencoded 字符串解码
            decoded = URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException ignored) {
        }
        return decoded;
    }

    private void decodeParams(String params, Map<String, String> p) {
        if (params == null) {
            return;
        }

        /**
         * StringTokenizer该类允许应用程序将字符串分解为标记，传入参数以&进行分割
         * 下面构造函数为指定字符串构造一个 stringtokenizer
         */
        StringTokenizer st = new StringTokenizer(params, "&");	
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
//            System.out.println("e的值：" + e);
            //Returns the index within this string of the first occurrence of the specified substring, starting at the specified index.
            int sep = e.indexOf('=');
            if (sep >= 0) {
                p.put(decodePercent(e.substring(0, sep)).trim(),
                        decodePercent(e.substring(sep + 1)));
            } else {
                p.put(decodePercent(e).trim(), "");
            }
        }
    }
}

