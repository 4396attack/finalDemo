package album.yyj.zust.aiface.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
//@PropertySource("application.yml")
@ConfigurationProperties(prefix = "oss")
public class Ossproperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketFace;
    private String bucketDetail;

    private String endPoint;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketFace() {
        return bucketFace;
    }

    public void setBucketFace(String bucketFace) {
        this.bucketFace = bucketFace;
    }

    public String getBucketDetail() {
        return bucketDetail;
    }

    public void setBucketDetail(String bucketDetail) {
        this.bucketDetail = bucketDetail;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public String toString() {
        return "Ossproperties{" +
                "accessKeyId='" + accessKeyId + '\'' +
                ", accessKeySecret='" + accessKeySecret + '\'' +
                ", bucketFace='" + bucketFace + '\'' +
                ", bucketDetail='" + bucketDetail + '\'' +
                ", endPoint='" + endPoint + '\'' +
                '}';
    }

}
