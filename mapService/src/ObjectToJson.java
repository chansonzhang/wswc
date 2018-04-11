import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangchen on 2015/10/05.
 */
public class ObjectToJson {

	public Object jsonToModel(String jsonStr,Class type){
		Gson gson=new Gson();
		Object object=gson.fromJson(jsonStr, type);
		return object;
	}
	public String objectToJson(Object obj){
		String jsonStr=null;
		Gson gson=new Gson();
		jsonStr=gson.toJson(obj);
		return jsonStr;
	}

	public <T> List<T> jsonToModelList1(String json){
		List<T> list=new ArrayList<T>();
		Gson gson=new Gson();
		Type listType=new TypeToken<ArrayList<T>>(){}.getType();
		list=gson.fromJson(json, listType);
		return list;	 
	}

}
