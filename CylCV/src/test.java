// This sample uses the following libraries:
//  - Apache HTTP client(org.apache.httpcomponents:httpclient:4.5.5)
//  - Apache HTTP core(org.apache.httpcomponents:httpccore:4.4.9)
//  - JSON library (org.json:json:20180130).

import java.io.File;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;

public class test{
    private static final String Key = "65daceefe43f421a98e9319a4fc55cac";
    // 图像分析的链接
    private static final String ANALYZE_IMAGE = "https://westcentralus.api.cognitive.microsoft.com/vision/v2.0/analyze";
	// 描述图像的链接
	public static final String DESCRIBE_IMAGE = "https://api.projectoxford.ai/vision/v1.0/describe";
	// 获取缩略图的链接
	public static final String GET_THUMBNAIL = "https://api.projectoxford.ai/vision/v1.0/generateThumbnail";
	// 特定领域模型识别(现在只支持名人识别，这个常量支持两个接口)
	public static final String Domain_SPECIFIC = "https://api.projectoxford.ai/vision/v1.0/models";
	// 识别图片中的文字
	public static final String OCR = "https://api.projectoxford.ai/vision/v1.0/ocr";
	// 获取图片的标签
	public static final String IMAGE_TAG = "https://api.projectoxford.ai/vision/v1.0/tag";

	public static HttpClient httpClient = HttpClients.createDefault();
	
    // 通过Url识别
    public static void ComputerVision_Url(String url, String language) {
    	//CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            URIBuilder builder = new URIBuilder(ANALYZE_IMAGE);

            // Request parameters. All of them are optional.
            builder.setParameter("visualFeatures", "Categories,Description,Color");
            builder.setParameter("language", language);

            // Prepare the URI for the REST API call.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", Key);
            
            // Request body.
            StringEntity requestEntity = new StringEntity("{\"url\":\"" + url + "\"}");
            request.setEntity(requestEntity);

            // Make the REST API call and get the response entity.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonString);
                System.out.println("REST Response:\n");
                System.out.println(json.toString(2));
            }
        } catch (Exception e) {
            // Display error message.
            System.out.println(e.getMessage());
        }
    }
    
    // 通过本地图片识别
	public static JSONObject ComputerVision_Local(String filePath, String language) {
		try {
			URIBuilder builder = new URIBuilder(ANALYZE_IMAGE);

			// 图像特征点： Categories:种类 ImageType:图片类型 Faces:人脸描述 Adult：监测成人内容 Color:颜色 Tags:标签
			// Description:用一句完成的英文描述图片
			builder.setParameter("visualFeatures", "Categories,Description,Color");
			// 对一些特定领域的细节的返回：目前只支持“名人”识别:Celebrities
			// builder.setParameter("details", "{string}");
			// 以哪种语言返回，默认为英语 en 英文 zh 中文
			builder.setParameter("language", language);

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			
			// 请求类型 json application/json 图片以url方式发送 二进制流 application/octet-stream 图片以二进制流发送
			// 多文件发送 multipart/form-data 图片以二进制流进行发送
			request.setHeader("Content-Type", "application/octet-stream"); 	// 以位进制文件流的方式访问，图片需要转化成一个二进制的流
			// request.setHeader("Content-Type", "application/json");		// 以json方式访问，图片路径需要为一个能访问到的地址
			request.setHeader("Ocp-Apim-Subscription-Key", Key);

			File file = new File(filePath);
			if (!file.exists()) {
				System.out.println("图片不存在");
				return null;
			}

			// 以文件流方式访问时传递的是一个文件（最终会以位二进制进行请求）
			FileEntity reqEntity = new FileEntity(file);
			request.setEntity(reqEntity);

            // Make the REST API call and get the response entity.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonString);
                System.out.println("REST Response:\n");
                System.out.println(json.toString(2));
                return json;
            }
            
            // Ali FasterJson
			//HttpResponse response = httpClient.execute(request);
			//HttpEntity entity = response.getEntity();
			//if (entity != null) {
			//	String result = EntityUtils.toString(entity);
			//	System.out.println(result);
			//	return (JSONObject) JSONObject.parse(result);
			//}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}
    
    public static void main(String[] args) {
    	// ComputerVision_Url("https://upload.wikimedia.org/wikipedia/commons/1/12/Broadway_and_Times_Square_by_night.jpg", "en");
    	// ComputerVision_Url("http://c.hiphotos.baidu.com/zhidao/pic/item/95eef01f3a292df552ee438eb7315c6034a87378.jpg", "zh");
    	
    	ComputerVision_Local("C:/Soft/PythonWorkspace/object_detection/timg.jpg", "zh");
    }
}