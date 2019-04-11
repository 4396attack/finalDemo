package album.yyj.zust.aiface.serviceimpl;

import album.yyj.zust.aiface.info.ErrorCodes;
import com.aliyun.oss.ClientException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.swing.text.html.FormSubmitEvent;

import java.io.*;
import java.util.Map;

import static javax.swing.text.html.FormSubmitEvent.*;

/**
 * @Auther: 杨玉杰
 * @Date: 2019/4/10 10:23
 * @Description:
 * 获取OSS服务
 */
@Service
public class AliOSSService {
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Value("${oss.STS_ACCESS_ID}")
    private String aliOSSid;

    @Value("${oss.STS_ENDPOINT}")
    private String aliOSSendPoint;

    @Value("${oss.STS_ACCESS_SECRET}")
    private String aliOSSsecret;

    @Value("${oss.STS_API_VERSION}")
    private String aliOSSSTS_API_VERSION;

    @Value("${oss.roleArn}")
    private String aliOSSRoleArn;

    @Value("${oss.policyFile}")
    private String aliOSSPolicyFile;

    @Value("${oss.tokenExpireTime}")
    private int aliOSSTokenExpireTime;

    /**
     * 获取阿里云oss sts临时权限
     * @param roleSessionName
     * 			临时Token的会话名称
     * @param data
     * @return	令牌
     * @throws ClientException
     */
    public Integer assumeRole(String roleSessionName, Map<String, Object> data){
        Integer error = ErrorCodes.SUCCESS;
        logger.info(aliOSSTokenExpireTime + "");
        // 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
        // 只有 RAM用户（子账号）才能调用 AssumeRole 接口
        // 阿里云主账号的AccessKeys不能用于发起AssumeRole请求
        // 请首先在RAM控制台创建一个RAM用户，并为这个用户创建AccessKeys
        IClientProfile profile = DefaultProfile.getProfile(aliOSSendPoint, aliOSSid,
                aliOSSsecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        // 创建一个 AssumeRoleRequest 并设置请求参数
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setVersion(aliOSSSTS_API_VERSION);
        request.setMethod(MethodType.POST);
        // 此处必须为 HTTPS
        request.setProtocol(ProtocolType.HTTPS);
        // RoleArn 需要在 RAM 控制台上获取
        request.setRoleArn(aliOSSRoleArn);
        // RoleSessionName 是临时Token的会话名称，自己指定用于标识你的用户，主要用于审计，或者用于区分Token颁发给谁
        // 但是注意RoleSessionName的长度和规则，不要有空格，只能有'-' '_' 字母和数字等字符
        // 具体规则请参考API文档中的格式要求
        request.setRoleSessionName(roleSessionName);
        // 授权策略
        request.setPolicy(readJson(aliOSSPolicyFile));
        // 设置token时间
        request.setDurationSeconds(aliOSSTokenExpireTime * 60L);
        // 发起请求，并得到response
        try {
            AssumeRoleResponse assumeRoleResponse = client.getAcsResponse(request);
            data.put("endPoint", aliOSSendPoint);// 空间名称
//            data.put("bucketName", aliyunOssBucketName);
//
            // 账号ID
            data.put("accessKeyId", assumeRoleResponse.getCredentials().getAccessKeyId());
            // 密码
            data.put("accessKeySecret", assumeRoleResponse.getCredentials().getAccessKeySecret());
            // token
            data.put("securityToken", assumeRoleResponse.getCredentials().getSecurityToken());
            // 有效时间
            data.put("expiration", assumeRoleResponse.getCredentials().getExpiration());
        } catch (com.aliyuncs.exceptions.ClientException e) {
            logger.info(e.getErrCode() + e.getErrMsg());
            error = ErrorCodes.GET_OSS_TOKEN_FAIL;
        }
        return error;
    }

    public String readJson(String path) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        // 返回值,使用StringBuffer
        StringBuffer data = new StringBuffer();
        //
        try {
            inputStream = new ClassPathResource(path).getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            // 每次读取文件的缓存
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                data.append(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭文件流
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }
}
