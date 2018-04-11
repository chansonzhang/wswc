import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;

import java.sql.Date;
/**
 * Created by zhangchen on 2015/10/05.
 */
class DateJsonValueProcessor implements JsonBeanProcessor { 

public JSONObject processBean(Object bean, JsonConfig arg1) { 
JSONObject jsonObject = null; 
        if( bean instanceof Date ){
             bean = new Date( ((Date) bean).getTime());
        } 
        if( bean instanceof java.sql.Timestamp ){ 
            bean = new Date( ((java.sql.Timestamp) bean).getTime()); 
        } 
        if( bean instanceof Date ){ 

        	Date date=(Date) bean;
        	date.getYear();
        	System.out.print(date.getYear());
        //	bean = new Date((long)(java.sql.Date) bean); 
        
        }else{ 
             jsonObject = new JSONObject( true ); 
        } 
          return jsonObject; 
       }


}

