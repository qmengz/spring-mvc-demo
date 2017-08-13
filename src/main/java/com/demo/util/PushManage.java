package com.demo.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.demo.mobel.*;
import net.sf.json.JSONObject;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

public class PushManage {

    GetJokeUtil gju = new GetJokeUtil();

    public String PushManageXml(InputStream is) throws JDOMException {
        String returnStr = ""; // 反回Servlet字符串
        String toName = ""; // 开发者微信号
        String fromName = ""; // 发送方帐号（一个OpenID）
        String type = ""; // 请求类型
        String con = ""; // 消息内容(接收)
        String event = ""; // 自定义按钮事件请求
        String eKey = ""; // 事件请求key值
        String recognition = ""; //语音识别结果

        try {
            SAXBuilder sax = new SAXBuilder();
            Document doc = sax.build(is);
            // 获得文件的根元素
            Element root = doc.getRootElement();
            // 获得根元素的第一级子节点
            List list = root.getChildren();
            StringBuffer firstMsg = new StringBuffer();
            for (int j = 0; j < list.size(); j++) {
                Element first = (Element) list.get(j);
                if (first.getName().equals("ToUserName")) {
                    toName = first.getValue().trim();
                    firstMsg.append(first.getName()+" : "+first.getValue().trim());
                } else if (first.getName().equals("FromUserName")) {
                    fromName = first.getValue().trim();
                    firstMsg.append(", "+first.getName()+" : "+first.getValue().trim());
                } else if (first.getName().equals("MsgType")) {
                    type = first.getValue().trim();
                    firstMsg.append(", "+first.getName()+" : "+first.getValue().trim());
                } else if (first.getName().equals("Content")) {
                    con = first.getValue().trim();
                    firstMsg.append(", "+first.getName()+" : "+first.getValue().trim());
                } else if (first.getName().equals("Event")) {
                    event = first.getValue().trim();
                    firstMsg.append(", "+first.getName()+" : "+first.getValue().trim());
                } else if (first.getName().equals("EventKey")) {
                    eKey = first.getValue().trim();
                    firstMsg.append(", "+first.getName()+" : "+first.getValue().trim());
                } else if (first.getName().equals("Recognition")) {
                    recognition = first.getValue().trim();
                    firstMsg.append(", "+first.getName()+" : "+first.getValue().trim());
                } else {
                    firstMsg.append(", "+first.getName()+" : "+first.getValue().trim());
                }
            }
            System.out.println("消息信息:");
            System.out.println(firstMsg.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (type.equals("event")) {     //此为事件
            System.out.println(event);
            if (event.equals("subscribe")) {// 此为 关注事件
                StringBuffer replyMsg = new StringBuffer();
                replyMsg.append("欢迎使用微信平台！");
                replyMsg.append("\r\n1、语音点歌");
                replyMsg.append("\r\n2、歌名点歌");
                replyMsg.append("\r\n3、看图文");
                replyMsg.append("\r\n4、微信精选");
                replyMsg.append("\r\n输入其他文字信息可以与我聊天哦...");

                returnStr = getBackXMLTypeText(toName, fromName, replyMsg.toString());

            } else if (event.equals("unsubscribe")) { //此为取消关注事件

            } else if (event.equals("CLICK")) { //此为 自定义菜单按钮点击事件
                // 以下为自定义按钮事件
                if (eKey.equals("V1")) { //菜单1
                    returnStr = getBackXMLTypeText(toName, fromName, "点击了菜单1");
                } else if (eKey.equals("V2")) {    //菜单2
                    returnStr = getBackXMLTypeText(toName, fromName, "点击了菜单2");
                }else {
                    System.out.println("点击了菜单");
                }
            }
        } else if (type.equals("voice")) {//voice

            if (" ".equals(recognition)||recognition.length()<=2){
                StringBuffer replyMsg = new StringBuffer();
                replyMsg.append("没有识别到语音消息请重新输入！");
                returnStr = getBackXMLTypeText(toName, fromName, replyMsg.toString());
            }else {

                recognition = recognition.substring(0,recognition.length()-1);

                if("天气".equals(recognition.substring(recognition.length()-2,recognition.length()))){
                    recognition = recognition.substring(0,recognition.length()-2);
                    String weather = gju.getRequest6(recognition);
                    returnStr = getBackXMLTypeText(toName, fromName, weather);
                }else {

                    JSONObject object = gju.getRequest5(recognition);

                    String songName = object.getString("songName");
                    String artistName = object.getString("artistName");
                    String albumName = object.getString("albumName");
                    String songLink = object.getString("songLink");
                    String showLink = object.getString("showLink");

                    ReplyMusicMessage mm = new ReplyMusicMessage();
                    Music m = new Music();

                    mm.setMessageType("music");
                    mm.setCreateTime(new Long(new Date().getTime()).toString());
                    mm.setToUserName(fromName);
                    mm.setFromUserName(toName);
                    mm.setFuncFlag("0");

                    m.setTitle(songName + "|" + artistName);
                    if (albumName != null) {
                        m.setDescription("专辑 | " + albumName);
                    } else {
                        m.setDescription("...");
                    }

                    String url = songLink;
                    String url2 = showLink;
                    m.setMusicUrl(url);
                    m.setHqMusicUrl(url2);

                    mm.setMusic(m);

                    returnStr = getBackXMLTypeMusic(mm);
                }
            }

        } else if (type.equals("text")) { // 此为 文本信息
            if (con.equals("1")) {

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                returnStr = getBackXMLTypeText(toName, fromName, "语音输入歌名点歌，快来听你喜欢的歌吧 [Hey] \r");

            } else if (con.equals("2")) {

                StringBuffer replyMsg = new StringBuffer();
                replyMsg.append("输入歌名加歌曲名点歌！");
                replyMsg.append("\r\n如:歌名演员");
                returnStr = getBackXMLTypeText(toName, fromName, replyMsg.toString());

            } else if (con.equals("3")) {

                ReplyTuwenMessage tw = new ReplyTuwenMessage();
                Articles articles = new Articles();
                Item item = new Item();
                List<Item> items = new ArrayList<Item>();

                tw.setMessageType("news");
                tw.setCreateTime(new Long(new Date().getTime()).toString());
                tw.setToUserName(fromName);
                tw.setFromUserName(toName);
                tw.setFuncFlag("0");
                tw.setArticleCount(1);

                item.setTitle("俊介");
                item.setDescription("俊介（SHUNSUKE）是Twitter上现在最流行的偶像犬，是哈多利系博美犬（即俗称英系博美），因为在网上卖萌而走红网络。");
                item.setPicUrl("http://img3.imgtn.bdimg.com/it/u=4214170739,449851863&fm=214&gp=0.jpg");
                item.setUrl("http://baike.baidu.com/view/6300265.htm");

                items.add(item);

                articles.setItems(items);
                tw.setArticles(articles);

                returnStr = getBackXMLTypeImg(tw);

            } else if (con.equals("4")) {

                ReplyTuwenMessage tw = gju.wxjx(fromName, toName);

                returnStr = getBackXMLTypeImgs(tw);

            } else if (con.substring(0,2).equals("歌名")) {

                JSONObject object = gju.getRequest5(con.substring(2));

                String songName = object.getString("songName");
                String artistName = object.getString("artistName");
                String albumName = object.getString("albumName");
                String songLink = object.getString("songLink");
                String showLink = object.getString("showLink");

                ReplyMusicMessage mm = new ReplyMusicMessage();
                Music m = new Music();

                mm.setMessageType("music");
                mm.setCreateTime(new Long(new Date().getTime()).toString());
                mm.setToUserName(fromName);
                mm.setFromUserName(toName);
                mm.setFuncFlag("0");

                m.setTitle(songName + "|" + artistName);
                if (albumName!=null){
                    m.setDescription("专辑 | " + albumName);
                }else {
                    m.setDescription("...");
                }

                String url = songLink;
                String url2 = showLink;
                m.setMusicUrl(url);
                m.setHqMusicUrl(url2);

                mm.setMusic(m);

                returnStr = getBackXMLTypeMusic(mm);


            } else {

                JSONObject object = gju.getRequest3(toName,con);
                String msg = object.getString("text");
                returnStr = getBackXMLTypeText(toName, fromName, msg);

            }
        }
        return returnStr;
    }


    /**
     * 编译文本信息
     *
     * @param tm
     * @return
     */
    private String getBackXMLTypeText(ReplyTextMessage tm) {

        String returnStr = "";

        Element rootXML = new Element("xml");

        rootXML.addContent(new Element("ToUserName").setText(tm.getToUserName()));
        rootXML.addContent(new Element("FromUserName").setText(tm.getFromUserName()));
        rootXML.addContent(new Element("CreateTime").setText(tm.getCreateTime()));
        rootXML.addContent(new Element("MsgType").setText(tm.getMessageType()));
        rootXML.addContent(new Element("Content").setText(tm.getContent()));

        Document doc = new Document(rootXML);
        XMLOutputter XMLOut = new XMLOutputter();
        returnStr = XMLOut.outputString(doc);

        return returnStr;
    }

    /**
     * 编译图片信息
     *
     * @param tw
     * @return
     */

    /**
     * 编译文本信息
     *
     * @param toName
     * @param fromName
     * @param content
     * @return
     */
    private String getBackXMLTypeText(String toName, String fromName,
                                      String content) {

        String returnStr = "";

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String times = format.format(new Date());

        Element rootXML = new Element("xml");

        rootXML.addContent(new Element("ToUserName").setText(fromName));
        rootXML.addContent(new Element("FromUserName").setText(toName));
        rootXML.addContent(new Element("CreateTime").setText(times));
        rootXML.addContent(new Element("MsgType").setText("text"));
        rootXML.addContent(new Element("Content").setText(content));

        Document doc = new Document(rootXML);

        XMLOutputter XMLOut = new XMLOutputter();
        returnStr = XMLOut.outputString(doc);

        return returnStr;
    }


    /**
     * 单条图文消息
     *
     * @param tw
     * @return
     */

    private String getBackXMLTypeImg(ReplyTuwenMessage tw) {

        String returnStr = "";

        Articles articles = tw.getArticles();
        Item item = articles.getItems().get(0);

        Element rootXML = new Element("xml");
        Element articlesXML = new Element("Articles");
        Element itemXML = new Element("item");

        rootXML.addContent(new Element("ToUserName").setText(tw.getToUserName()));
        rootXML.addContent(new Element("FromUserName").setText(tw.getFromUserName()));
        rootXML.addContent(new Element("CreateTime").setText(tw.getCreateTime()));
        rootXML.addContent(new Element("MsgType").setText(tw.getMessageType()));
        rootXML.addContent(new Element("ArticleCount").setText(tw.getArticleCount()+""));

        itemXML.addContent(new Element("Title").setText(item.getTitle()));
        itemXML.addContent(new Element("Description").setText(item.getDescription()));
        itemXML.addContent(new Element("PicUrl").setText(item.getPicUrl()));
        itemXML.addContent(new Element("Url").setText(item.getUrl()));

        articlesXML.addContent(itemXML);
        rootXML.addContent(articlesXML);

        Document doc = new Document(rootXML);
        XMLOutputter XMLOut = new XMLOutputter();
        returnStr = XMLOut.outputString(doc);

        return returnStr;
    }

    private String getBackXMLTypeImgs(ReplyTuwenMessage tw) {

        String returnStr = "";

        Articles articles = tw.getArticles();
        List<Item> items = articles.getItems();

        Element rootXML = new Element("xml");
        Element articlesXML = new Element("Articles");

        rootXML.addContent(new Element("ToUserName").setText(tw.getToUserName()));
        rootXML.addContent(new Element("FromUserName").setText(tw.getFromUserName()));
        rootXML.addContent(new Element("CreateTime").setText(tw.getCreateTime()));
        rootXML.addContent(new Element("MsgType").setText(tw.getMessageType()));
        rootXML.addContent(new Element("ArticleCount").setText(tw.getArticleCount()+""));

        for (int i=0;i<items.size();i++){
            Item item = items.get(i);
            System.out.println(item.toString());
            Element itemXML = new Element("item");
            itemXML.addContent(new Element("Title").setText(item.getTitle()));
            itemXML.addContent(new Element("Description").setText(item.getDescription()));
            itemXML.addContent(new Element("PicUrl").setText(item.getPicUrl()));
            itemXML.addContent(new Element("Url").setText(item.getUrl()));
            articlesXML.addContent(itemXML);
        }
        rootXML.addContent(articlesXML);

        Document doc = new Document(rootXML);
        XMLOutputter XMLOut = new XMLOutputter();
        returnStr = XMLOut.outputString(doc);

        return returnStr;
    }

    /**
     * 编译音乐信息
     *
     * @param mm
     * @return
     */
    private String getBackXMLTypeMusic(ReplyMusicMessage mm) {
        String returnStr = "";

        Music m = mm.getMusic();

        Element rootXML = new Element("xml");
        Element mXML = new Element("Music");

        rootXML.addContent(new Element("ToUserName").setText(mm.getToUserName()));
        rootXML.addContent(new Element("FromUserName").setText(mm.getFromUserName()));
        rootXML.addContent(new Element("CreateTime").setText(mm.getCreateTime()));
        rootXML.addContent(new Element("MsgType").setText(mm.getMessageType()));

        mXML.addContent(new Element("Title").setText(m.getTitle()));
        mXML.addContent(new Element("Description").setText(m.getDescription()));
        mXML.addContent(new Element("MusicUrl").setText(m.getMusicUrl()));
        mXML.addContent(new Element("HQMusicUrl").setText(m.getHqMusicUrl()));

        rootXML.addContent(mXML);
        Document doc = new Document(rootXML);
        XMLOutputter XMLOut = new XMLOutputter();
        returnStr = XMLOut.outputString(doc);

        return returnStr;
    }
}
