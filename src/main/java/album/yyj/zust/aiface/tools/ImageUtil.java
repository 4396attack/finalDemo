package album.yyj.zust.aiface.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

/**
 * 图片处理工具类
 */
public class ImageUtil {
    private Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    private static String DEFAULT_TEMP = "/temp_";
    private static String DEFAULT_CUT = "cut_";
    public final static String TEMP_PATH = System.getProperty("user.dir") + "\\facetest\\temp\\";

    public final static String CUT_PATH = System.getProperty("user.dir") + "\\facetest\\cut\\";

    public static boolean cutImg(File srcImg, OutputStream output,java.awt.Rectangle rect){
        if(srcImg.exists()){
            FileInputStream fis = null;
            ImageInputStream iis = null;
            try{
                fis = new FileInputStream(srcImg);
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]",",");
                String suffix = null;
                //获取图片后缀
                if(srcImg.getName().indexOf(".")>-1){
                    suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
                }//类型和图片后缀全部小写，然后判断后缀是否合法
                if(suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()+",")<0){
                    return false;
                }
                //将FileInputSteam转换为ImageInputStream
                iis = ImageIO.createImageInputStream(fis);
                //根据图片类型获取该种类型的ImageReader
                ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
                reader.setInput(iis,true);
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(rect);
                BufferedImage bi = reader.read(0,param);
                ImageIO.write(bi,suffix,output);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                try{
                    if(fis != null){
                        fis.close();
                    }
                    if(iis != null){
                        iis.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
            return true;
        }else {
            System.out.println(1);
            return false;
        }
    }

    /**
     *
     * @param srcImg
     * @param output
     * @param x 起点横坐标
     * @param y 起点纵坐标
     * @param width 宽
     * @param height 高
     * @return 是否裁剪成功
     */
    public static boolean cutImage(File srcImg,OutputStream output,int x ,int y,int width,int height){
        return cutImg(srcImg,output, new Rectangle(x,y,width,height));
    }

    public static boolean cutImage(File srcImg,String destImgPath,Rectangle rect){
        File destImg = new File(destImgPath);
        if(destImg.exists()){
            String p =destImg.getPath();
            try{
                if(!destImg.isDirectory()){
                    p = destImg.getParent();
                }
                if (!p.endsWith(File.separator)) {
                    p = p + File.separator;
                }
                return cutImg(srcImg,new FileOutputStream(p + DEFAULT_CUT + srcImg.getName()),rect);
            }catch (FileNotFoundException e){
                System.out.println(3);
                return false;
            }
        }else {
            System.out.println(2);
            return false;
        }
    }

    public static boolean cutImage(File srcImg,String destImg,int x,int y,int width,int height){
        return cutImage(srcImg,destImg,new Rectangle(x,y,width,height));
    }

    public static boolean cutImage(String srcImg,String destImg,int x,int y,int width,int height){
        return cutImage(new File(srcImg),destImg,new Rectangle(x,y,width,height));
    }

    public static void main(String[] args){
//        System.out.println(cutImage("E:\\finalDemo\\facetest\\photo6.jpg","E:\\finalDemo\\facetest\\cut",123,95,253,343));
        System.out.println(TEMP_PATH + "1.jpg");
    }

}
