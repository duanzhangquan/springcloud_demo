/*

package com.common.util;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


*/
/**
 * http请求实现
 *
 * @author hooyang
 * @date 2019/7/16 10:23
 *//*


public class RestTemplateUtil {
    private static Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);

    public RestTemplateUtil() {
    }


*/
/**
     *
     *
     * @param url
     * @param securityValue
     * @return
     *//*


    public static String postForEntity(String url, String securityValue) {
        String reData = "";
        try {
            RestTemplate template = new RestTemplate(generateHttpRequestFactory());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8"));

            //参数放入一个map中，restTemplate不能用hashMap
            MultiValueMap<String, String> param = new LinkedMultiValueMap<String, String>();
            param.add("securityMsg", securityValue);

            //将参数和header组成一个请求
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(param, headers);
            ResponseEntity<String> response = template.postForEntity(url, request, String.class);
            logger.debug("postForEntity reStatusCode:=" + response.getStatusCode());
            logger.debug("postForEntity reHeaders:=" + response.getHeaders());
            logger.debug("postForEntity reBody:=" + response.getBody());
            reData = response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reData;
    }


*/
/**
     * RestTemplete 忽略安全证书
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws KeyStoreException
     *//*


    private static HttpComponentsClientHttpRequestFactory generateHttpRequestFactory()
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
//        TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        };

        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setSSLSocketFactory(connectionSocketFactory);
        CloseableHttpClient httpClient = httpClientBuilder.build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        return factory;
    }

}

*/
