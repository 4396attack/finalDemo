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
import java.util.UUID;

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

    public boolean upload(String bucket, String objName, String fileName){
        boolean flag = false;
        File file = new File(fileName);
        if(file.exists()){
            ossClient.putObject(bucket,objName,file);
            flag = true;
        }
        return flag;
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
        String uuid = UUID.randomUUID().toString() + ".jpg";
        String path = System.getProperty("user.dir") + "\\facetest\\" + uuid;
        ossClient.getObject(new GetObjectRequest(bucket,objName),new File(path));
        return new File(path);
    }
    public void downloadToPath(String bucket,String objName,String dest){
        ossClient.getObject(new GetObjectRequest(bucket,objName),new File(dest));
    }
    public void close(){
        ossClient.shutdown();
    }
}
