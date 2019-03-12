package album.yyj.zust.aiface.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FaceTools {


    /**
     * 计算MD5+BASE64
     */
    public final static String URL_POS = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/detect";//人脸定位API

    public final static String URL_ATTR = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/attribute";//属性识别

    public final static String URL_VERI = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/verify";//人脸对比

    public static String MD5Base64(String s){
        if(s == null){
            return null;
        }
        String encodeStr = "";
        byte[] utfBytes = s.getBytes();
        MessageDigest mdTemp;
        try{
            mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(utfBytes);
            byte[] md5Bytes = mdTemp.digest();
            BASE64Encoder b64Encoder = new BASE64Encoder();
            encodeStr = b64Encoder.encode(md5Bytes);
        }catch (Exception e){
            return null;
        }
        return encodeStr;
    }
    /**
     * 计算HMAC-SHA1
     */
    public static String HMACSha1(String data,String key){
        String result;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = (new BASE64Encoder()).encode(rawHmac);
        } catch (Exception e) {
            return null;
        }
        return result;
    }
    /*
     * 等同于javaScript中的 new Date().toUTCString();
     */
    public static String toGMTString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.UK);
        df.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }
    /*
     * 发送POST请求
     */
    public static String sendPost(String url, String body, String ak_id, String ak_secret) throws Exception {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        int statusCode = 200;
        try {
            URL realUrl = new URL(url);
            /*
             * http header 参数
             */
            String method = "POST";
            String accept = "application/json";
            String content_type = "application/json";
            String path = realUrl.getFile();
            String date = toGMTString(new Date());
            // 1.对body做MD5+BASE64加密
            String bodyMd5 = MD5Base64(body);
            String stringToSign = method + "\n" + accept + "\n" + bodyMd5 + "\n" + content_type + "\n" + date + "\n"
                    + path;
            // 2.计算 HMAC-SHA1
            String signature = HMACSha1(stringToSign, ak_secret);
            // 3.得到 authorization header
            String authHeader = "Dataplus " + ak_id + ":" + signature;
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", accept);
            conn.setRequestProperty("content-type", content_type);
            conn.setRequestProperty("date", date);
            conn.setRequestProperty("Authorization", authHeader);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(body);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            statusCode = ((HttpURLConnection)conn).getResponseCode();
            if(statusCode != 200) {
                in = new BufferedReader(new InputStreamReader(((HttpURLConnection)conn).getErrorStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (statusCode != 200) {
            throw new IOException("\nHttp StatusCode: "+ statusCode + "\nErrorMessage: " + result);
        }
        return result;
    }
    /*
     * GET请求
     */
    public static String sendGet(String url, String ak_id, String ak_secret) throws Exception {
        String result = "";
        BufferedReader in = null;
        int statusCode = 200;
        try {
            URL realUrl = new URL(url);
            /*
             * http header 参数
             */
            String method = "GET";
            String accept = "application/json";
            String content_type = "application/json";
            String path = realUrl.getFile();
            String date = toGMTString(new Date());
            // 1.对body做MD5+BASE64加密
            // String bodyMd5 = MD5Base64(body);
            String stringToSign = method + "\n" + accept + "\n" + "" + "\n" + content_type + "\n" + date + "\n" + path;
            // 2.计算 HMAC-SHA1
            String signature = HMACSha1(stringToSign, ak_secret);
            // 3.得到 authorization header
            String authHeader = "Dataplus " + ak_id + ":" + signature;
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", accept);
            connection.setRequestProperty("content-type", content_type);
            connection.setRequestProperty("date", date);
            connection.setRequestProperty("Authorization", authHeader);
            connection.setRequestProperty("Connection", "keep-alive");
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            statusCode = ((HttpURLConnection)connection).getResponseCode();
            if(statusCode != 200) {
                in = new BufferedReader(new InputStreamReader(((HttpURLConnection)connection).getErrorStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (statusCode != 200) {
            throw new IOException("\nHttp StatusCode: "+ statusCode + "\nErrorMessage: " + result);
        }
        return result;
    }

    /**
     * 将图片转为base64编码
     * @param imgFile
     * @return
     */
    public static String getImgStr(String imgFile){
        InputStream in =null;
        byte[] data = null;
        //读取图片字节数组
        try{
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }catch (IOException e){
            return null;
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    public static String encodeImageToBase64(File file){
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
//        loggerger.info("图片的路径为:" + file.getAbsolutePath());
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            return null;
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String base64 = encoder.encode(data);
        return base64;//返回Base64编码过的字节数组字符串
    }

    /**
     * 发生人脸定位请求 ,content形式
     * @param accessId
     * @param accessSecret
     * @return
     */
    public static String getFacePos(String accessId,String accessSecret,File file){
        String res = "";
        String encodeStr = encodeImageToBase64(file);
        if(encodeStr != null){
            String data = encodeStr.replaceAll("[\\s*\t\n\r]", "");
            data = "'" + data + "'";
            String body = "{\"type\": "+1+", \"content\": "+data+"}";
            try {
                res = sendPost(URL_POS, body, accessId, accessSecret);
            } catch (Exception e) {

            }
        }
        return res;
    }
    public static String getFacePosByURL(String accessId,String accessSecret,String url){
        String res = "";
        String body = "{\"type\": \"0\", \"image_url\":"+ url+"}";
        try {
            res = sendPost(URL_POS, body, accessId, accessSecret);
        } catch (Exception e) {
        }
        return res;
    }
    public static void main(String[] args) throws Exception {
        // 发送POST请求示例
        System.out.println("*********************");
        String ak_id = "LTAIqnevufzufutK"; //用户ak
        String ak_secret = "6xMPcRlAArDdYQdB3TbCzr0rpVGABR"; // 用户ak_secret
        String url = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/detect";
        String fileName = "E:\\finalDemo\\facetest\\photo6.jpg";
        String encodeStr = getImgStr(fileName);
        if(encodeStr == null){
            System.out.println("over");
        }
        System.out.println(encodeStr);
        String body = "{\"type\": " + 0 + " , \"image_url\": \"http://ai-face-yyj.oss-cn-hangzhou.aliyuncs.com/user1/photo6.jpg?Expires=1552117813&OSSAccessKeyId=LTAIqnevufzufutK&Signature=uoc3%2Bryjvh6rsgWhFJ11dz2xmL0%3D \"}";
        System.out.println("response body:" + sendPost(url, body, ak_id, ak_secret));
//        String data = encodeStr.replaceAll("[\\s*\t\n\r]", "");
//        data = "'" + data + "'";
//        String body = "{\"type\": "+1+", \"content\": "+data+"}";
//        System.out.println("response body:" + sendPost(url, body, ak_id, ak_secret));
    }
}
