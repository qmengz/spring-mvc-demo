package com.demo.controller;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.demo.mobel.Msg;
import com.demo.util.PushManage;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/wxcallback")
public class WXCallbackController {

    private final static String ENCODING = "utf-8";
    private static Unmarshaller UNMARSHALLER = null;
//	@Autowired
//	private JdbcTemplate jdbcTemplate;

    static {
        try {
            UNMARSHALLER = JAXBContext.newInstance(Msg.class).createUnmarshaller();
        } catch (JAXBException e) {
            System.out.println("init Unmarshaller JAXBException error:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/wx", method = RequestMethod.GET)
    public void get(HttpServletResponse response, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            request.setCharacterEncoding(ENCODING);
        } catch (UnsupportedEncodingException e1) {
            System.out.println("unsupported encoding:" + ENCODING);
        }
        response.setCharacterEncoding(ENCODING);
        String echostr = request.getParameter("echostr");

        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String signature = request.getParameter("signature");

        System.out.println(
                "echoStr=" + echostr + ";timestamp=" + timestamp + ";nonce=" + nonce + ";signature=" + signature);
        String responseText = "";
        try {

            if (validSign(timestamp, nonce, signature, true)) {
                responseText = echostr;
            } else {
                responseText = "F";
            }

            System.out.println("response:" + responseText);
            response.getWriter().print(responseText);
            response.getWriter().flush();
        } catch (Exception e) {
            System.out.println("weixin response error" + e);
        } finally {
            try {
                response.getWriter().close();
            } catch (IOException e) {
                System.out.println("close outstream error" + e);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("cost time:" + (endTime - startTime) + " ms");

    }

    /**
     * 微信公众平台 所有接口入口
     */

    @RequestMapping(value = "/wx", method = RequestMethod.POST)
    public void post(HttpServletResponse response, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String responseText;
        Msg msg ;
        try {
            request.setCharacterEncoding(ENCODING);
            response.setCharacterEncoding(ENCODING);
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String signature = request.getParameter("signature");
            System.out.println("返回成功 timestamp=" + timestamp + ";nonce=" + nonce + ";signature=" + signature);
            InputStream is = request.getInputStream();
            PushManage push = new PushManage();
            responseText = push.PushManageXml(is);
            System.out.println("XML信息: " + responseText);
            response.getWriter().print(responseText);
            response.getWriter().flush();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                response.getWriter().close();
            } catch (IOException e) {
                System.out.println("close outstream error" + e);
                e.printStackTrace();
            }
        }


//        try {
//            System.out.println("receive msg:" + msg);
//            responseText = msg.toString();
//            Msg xmlmsg = null;
//            try {
//                xmlmsg = XMLMsg(msg);
//            } catch (Exception e) {
//                System.out.println("XML转化失败！" + e.getMessage());
//            }
//            System.out.println("返回的XML信息: " + xmlmsg.toString());
//
//
//            String ticket = xmlmsg.getTicket();
//            String eventKey = xmlmsg.getEventKey();
//            String shopid = eventKey.replace("qrscene_", "");
//            System.out.println("ticket = " + ticket + ",eventKey = " + eventKey + ",shopid = " + shopid);
//            if (ticket != null) {
//                String msgsql = "replace into wx_fans_ticket(shopid,openid,ticket)"
//                        + " values('" + shopid + "','" + xmlmsg.getFromUserName() + "','" + xmlmsg.getTicket() + "')";
//				jdbcTemplate.update(msgsql);
//            }
//        } catch (Exception e) {
//            System.out.println("weixin response error" + e);
//            e.printStackTrace();
//        } finally {
//            try {
//                response.getWriter().close();
//            } catch (IOException e) {
//                System.out.println("close outstream error" + e);
//            }
//        }
        long endTime = System.currentTimeMillis();
        System.out.println("cost time:" + (endTime - startTime) + " ms");
    }

    private boolean validSign(String timestamp, String nonce, String sign, boolean in) {
        String token = "whouswx"; // weixin.getInToken();
        System.out.println("valid weixin sign token:" + token);
        List<String> paramList = new ArrayList<String>();
        paramList.add(token);
        paramList.add(timestamp);
        paramList.add(nonce);
        Collections.sort(paramList);
        StringBuilder paramSbBuilder = new StringBuilder();
        for (String p : paramList) {
            paramSbBuilder.append(p);
        }
        String mySign = DigestUtils.shaHex(paramSbBuilder.toString());
        boolean success = false;
        if (mySign != null && mySign.equalsIgnoreCase(sign)) {
            success = true;
        } else {
            success = false;
        }
        System.out.println("valid weixin sign:" + success);
        if (in && success) {
            // sellerWeixinService.updateBindInTime(weixin);// 验证成功更新接入时间
            System.out.println("update in time:" + success);
        }
        return success;
    }

    public static Msg XMLMsg(String str) throws DocumentException {
        Document doc = DocumentHelper.parseText(str);
        Element root = doc.getRootElement();
        Msg msg = new Msg();
        msg.setToUserName(root.elementText("ToUserName"));
        msg.setFromUserName(root.elementText("FromUserName"));
        msg.setCreateTime(root.elementText("CreateTime"));
        msg.setMsgType(root.elementText("MsgType"));
        msg.setEvent(root.elementText("Event"));
        msg.setEventKey(root.elementText("EventKey"));
        msg.setTicket(root.elementText("Ticket"));
        msg.setContent(root.elementText("Content"));
        msg.setMsgId(root.elementText("MsgId"));
        return msg;
    }

}
