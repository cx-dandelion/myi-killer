import java.util.Date; 
import java.text.SimpleDateFormat;

public class NowString { 
   public static void main(String[] args) {      
   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH");//设置日期格式
      System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
   } 
}
