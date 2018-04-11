import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * Created by zhangchen on 2015/10/05.
 */
public class JsonToObject {

	public static Object jsonToModel(String jsonStr, Class type) {
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonStr);
		  return JSONObject.toBean(jsonObject, type);
	}

	public static String modelToJson(Object obj) {

		// ���ؽY��
		String jsonStr = null;
		// �п�
		if (obj == null) {
			return "{}";
		}

		JsonConfig config = new JsonConfig();
		// �����������
		config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		config.setExcludes(new String[] { "handler", "hibernateLazyInitializer" });
		// ����ʱ������
		config.registerJsonBeanProcessor(java.sql.Date.class,
				new DateJsonValueProcessor());
//���л�����
		jsonStr = JSONObject.fromObject(obj).toString();
		return jsonStr;
	}

}
