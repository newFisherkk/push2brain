package cn.com.egova.tools;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpClientPoolUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientPoolUtils.class);
    private static final String CHARSET = "UTF-8";
    
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_CON_TIMEOUT = 5000;
    private static final int MAX_READ_TIMEOUT = 60000;

    static {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", createSSLConnSocketFactory())
                .build();
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 设置连接池大小
        connMgr.setMaxTotal(10);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_CON_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_READ_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_CON_TIMEOUT);
        requestConfig = configBuilder.build();
    }

    private static CloseableHttpClient httpClient;
    /**
     * 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext ctx = SSLContext.getInstance("SSL");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            sslsf = new SSLConnectionSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (GeneralSecurityException e) {
            logger.error("创建SSL连接工厂错误：{}",e);
        }
        return sslsf;
    }


    private static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (HttpClientPoolUtils.class){
                if (httpClient == null) {
                    httpClient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
                }
            }
        }
        return httpClient;
    }

    /**
     * 执行get方法
     *
     * @param url+param
     */
    public static String sendGet(String url) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        String body = "";
        try {
            HttpGet get = new HttpGet(url);
            response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                body = EntityUtils.toString(response.getEntity(), Charset.forName(CHARSET));
            } else {
                logger.info("statusCode：{}", statusCode);
            }
        } catch (Exception e) {
            logger.error("HttpClient.GET exception ", e);
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("CloseableHttpResponse exception ", e);
            }
        }
        return body;
    }

    /**
     * 使用httpclint 发送文件，如果不传输文件，直接设置fileParams=null，
     * 如果不设置请求头参数，直接设置headerParams=null，就可以进行普通参数的POST请求了
     *
     * @param url          请求路径
     * @param fileParams   文件参数
     * @param otherParams  其他字符串参数
     * @param headerParams 请求头参数
     * @return
     */
    public static String sendPostWithFile(String url, Map<String, MultipartFile> fileParams, Map<String, String> otherParams, Map<String, String> headerParams) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        String body = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            //设置请求头
            if (headerParams != null && headerParams.size() > 0) {
                for (Map.Entry<String, String> e : headerParams.entrySet()) {
                    String value = e.getValue();
                    String key = e.getKey();
                    if (null!=value || value.length()!=0) {
                        httpPost.setHeader(key, value);
                    }
                }
            }
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName(CHARSET));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//加上此行代码解决返回中文乱码问题
            //    文件传输http请求头(multipart/form-data)
            if (fileParams != null && fileParams.size() > 0) {
                for (Map.Entry<String, MultipartFile> e : fileParams.entrySet()) {
                    String fileParamName = e.getKey();
                    MultipartFile file = e.getValue();
                    if (file != null) {
                        builder.addBinaryBody(fileParamName, file.getInputStream(), ContentType.MULTIPART_FORM_DATA, file.getOriginalFilename());// 文件流
                    }
                }
            }
            //    字节传输http请求头(application/json)
            ContentType contentType = ContentType.create("application/json", Charset.forName(CHARSET));
            if (otherParams != null && otherParams.size() > 0) {
                for (Map.Entry<String, String> e : otherParams.entrySet()) {
                    String value = e.getValue();
                    if (null!=value || value.length()!=0) {
                        builder.addTextBody(e.getKey(), value, contentType);// 类似浏览器表单提交，对应input的name和value
                    }
                }
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                body = EntityUtils.toString(responseEntity, Charset.forName(CHARSET));
            } else {
                logger.info("statusCode：{}", statusCode);
            }
        } catch (IOException e) {
            logger.error("执行post请求出错：sendPostWithFile");
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("关闭CloseableHttpResponse出错", e);
            }
        }
        return body;
    }

    /**
     * 处理json格式的body post请求
     */
    public static String sendPostJson(String postUrl, String jsonStr,String token) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        String body = "";
        try {
            HttpPost post = new HttpPost(postUrl);
            post.setHeader("Content-Type", "application/json;charset=utf-8");
            post.setHeader("Accept-Charset", CHARSET);
            if(token!=null){
                post.setHeader("token",token);
            }
            StringEntity s = new StringEntity(jsonStr, CHARSET);
            post.setEntity(s);

            response = httpClient.execute(post);
            //获取结果实体
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                body = EntityUtils.toString(entity, Charset.forName(CHARSET));
            } else {
                logger.info("statusCode：{}", statusCode);
            }
        } catch (Exception e) {
            logger.error("请求HttpClient.POSTJSON出错", e);
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("关闭CloseableHttpResponse出错", e);
            }
        }
        return body;
    }

    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }


    public static CloseableHttpClient getHttpClientCore() {
        try {
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(), NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(createIgnoreVerifySSL(), NoopHostnameVerifier.INSTANCE))
                    .build();

            HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
            httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(200)
                            .setSocketTimeout(1000)
                            .setConnectionRequestTimeout(2000)
                            .setStaleConnectionCheckEnabled(true)
                            .build())
                    .build();
        } catch (Exception e) {
            logger.error("获取httpclient失败，错误堆栈原因；" + e.getMessage(), e);
        }
        return httpClient;
    }

//    public static CloseableHttpClient getHttpClient() {
//        if (httpClient == null) {
//            return getHttpClientCore();
//        }
//        return httpClient;
//    }
}