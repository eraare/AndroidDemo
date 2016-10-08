package com.guohua.mlight.upgrade;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Leo
 * @detail 解析xml文件
 * @time 2015-12-15
 */
public class ParseXmlService {
    /**
     * DOM解析XML
     *
     * @param inStream
     * @return
     */
    public HashMap<String, String> parseXml(InputStream inStream) {
        HashMap<String, String> hashMap = new HashMap<>();

        NodeList childNodes = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inStream);
            Element root = document.getDocumentElement();
            childNodes = root.getChildNodes();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null)
                    inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                hashMap.put(childElement.getNodeName(), childElement.getFirstChild().getNodeValue());
            }
        }

        return hashMap;
    }

    /**
     * 根据URL对XML进行DOM解析
     *
     * @param url
     * @return
     */
    public HashMap<String, String> parseXmlByUrl(String url) {
        InputStream is = getInputStreamFromUrl(url);
        if(is == null){
            System.out.println("ParseXmlService parseXmlByUrl InputStream is: null");
            return null;
        }
        return parseXml(is);
    }

    /**
     * 根据url得到InputStream
     *
     * @param url
     * @return
     */
    private InputStream getInputStreamFromUrl(String url) {
        InputStream is = null;
        try {
            URL mUrl = new URL(url);
            HttpURLConnection mConn = (HttpURLConnection) mUrl.openConnection();
            is = mConn.getInputStream();
        } catch (IOException e) {
            System.out.println("InputStream getInputStreamFromUrl " + url + " exception: "+ e.toString());
            e.printStackTrace();
        }
        return is;
    }
}