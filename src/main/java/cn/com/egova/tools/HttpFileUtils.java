package cn.com.egova.tools;


import cn.com.egova.bean.ShareFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * @author huangchen
 *         2017-12-26 20:42
 */
public class HttpFileUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpFileUtils.class);

    private static final int TYPE_UP = 1;
    private static final int TYPE_DEL = 2;
    private static final int TYPE_DL = 3;

    private static final String ADD_URL = "/home/httpfile/writefile.htm";
    private static final String DEL_URL = "/home/httpfile/deletefile.htm";
    private static final String DL_URL = "/home/httpfile/readfile.htm";


    public static boolean uploadFile(String filePath, ShareFileInfo info, InputStream fis){
        HttpURLConnection con = getConnection(filePath, info, TYPE_UP);
        String url = "";
        if(con == null){
        	return false;
    	}else{
            url=" [URL]:"+con.getURL()+" ";//新增打印URL路径
    	}
        OutputStream out = null;
        DataInputStream in = null;
        try {
            out = new DataOutputStream(con.getOutputStream());
            in = new DataInputStream(fis);
            int bytes = 0;
            byte[] bufferOut = new byte[2048];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            out.flush();
            logger.info("upload stream=finish");
        } catch (IOException e) {
            logger.error("输出数据失败！"+url, e);
        } finally {
            try {
                if(in != null)
                    in.close();
                if(out != null)
                    out.close();
            } catch (IOException e) {
                logger.error("关闭输入输出流异常！"+url, e);
            }
        }
        // 读取服务器响应，必须读取,否则提交不成功
        try {
            int resCode = con.getResponseCode();
            if(resCode != HttpURLConnection.HTTP_OK){
                logger.error("文件上传失败，服务器返回 " + resCode + " " + con.getResponseMessage()+url);
                return false;
            }
            return fetchResponse(con.getInputStream());
        } catch (IOException e) {
            logger.error("获取返回数据出错！"+url, e);
        } finally{
            con.disconnect();
        }
        return false;
    }

    /**
     * 获取文件输入流
     * filePath(media_path+media_uploaded_name|media_name) : rec/20201214/3995/3272ef10-a822-4b8c-b922-800b5748a6ed/d9f7b563-9bbf-4fae-b64b-c42258d4cdd1.jpg
     * @param filePath
     * @param info
     * @return
     */
    public static InputStream getFileInputStream(String filePath, ShareFileInfo info){
        HttpURLConnection con = getConnection(filePath, info, TYPE_DL);
        if(con == null) return null;
        try {
            int resCode = con.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                return con.getInputStream();
            }
            logger.error("文件下载失败，服务返回 " + resCode + " " + con.getResponseMessage());
        } catch (IOException e){
            logger.error("获取输入流失败！,服务器信息:{}",info.toString(), e);
        }
        return null;
    }


    private static HttpURLConnection getConnection(String filePath, ShareFileInfo info, int type){
        String url = genRequestUrl(filePath, info, type);
        URL urlObj = null;
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            logger.error("Http地址错误！", e);
            return null;
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) urlObj.openConnection();
        } catch (IOException e) {
            logger.error("Http连接打开失败！", e);
            return null;
        }
        con.setRequestProperty("Charset", "UTF-8");
        try {
            con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
        } catch (ProtocolException e) {
        }
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false); // post方式不能使用缓存
        con.setRequestProperty("Content-Type", "application/octet-stream");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        // 身份验证
        con.setRequestProperty("Authorization", "Basic " + info.getEncodeAuthString());
        return con;
    }

    private static String genRequestUrl(String path, ShareFileInfo info, int type){
        if(!path.startsWith("/"))
            path = "/" + path;
        path = path.replace("\\", "/");
        String url = null;
        switch (type){
            case TYPE_UP:
                url = "http://" + info.getAddr() + ADD_URL + "?path=" + path;
                break;
            case TYPE_DEL:
                url = "http://" + info.getAddr() + DEL_URL + "?path=" + path;
                break;
            case TYPE_DL:
                url = "http://" + info.getAddr() + DL_URL + "?path=" + path;
                break;
        }
        return url;
    }

    private static boolean fetchResponse(InputStream inputStream){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = br.readLine()) != null){
                if("0".equals(line))
                    return true;
            }
        } catch (IOException e) {
            logger.error("返回结果解析失败！", e);
        } finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }
}
