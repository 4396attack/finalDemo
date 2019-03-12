package album.yyj.zust.aiface.tools;

import album.yyj.zust.aiface.properties.Ossproperties;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Date;

public class OSSImageClient {
    private OSSClient ossClient;
    public OSSImageClient() {

    }
    public OSSImageClient(String endPoint,String accessKeyId,String accessKeySecret) {
//        this.ossproperties = new Ossproperties();
        System.out.println(endPoint + accessKeyId + accessKeySecret);
//        this.ossClient =  new OSSClient(ossproperties.getEndPoint(),ossproperties.getAccessKeyId(),ossproperties.getAccessKeySecret());
//        this.ossClient =  new OSSClient("http://oss-cn-hangzhou.aliyuncs.com","LTAIqnevufzufutK","6xMPcRlAArDdYQdB3TbCzr0rpVGABR");
        this.ossClient = new OSSClient(endPoint,accessKeyId,accessKeySecret);
    }

    public void upload(String bucket, String objName, String fileName){
        File file = new File(fileName);
        if(file != null){
            ossClient.putObject(bucket,objName,file);
        }
    }
    public boolean exitObj(String bucket, String objName){
        return ossClient.doesObjectExist(bucket,objName);
    }

    public String getPhotoURL(String bucket,String objName){
        Date nowTime = new Date();
        Date expire = new Date(nowTime.getTime() + 60*60);
        URL url = ossClient.generatePresignedUrl(bucket,objName,expire);
        return url.toString();
    }
    public File downloadFile(String bucket,String objName){
        File file = new File("temp");
        ossClient.getObject(new GetObjectRequest(bucket,objName),file);
        return file;
    }
    public void close(){
        ossClient.shutdown();
    }
}
