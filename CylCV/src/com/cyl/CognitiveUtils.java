package com.cyl;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 这个类主要是微软认知服务中的计算机视觉
 * 
 * @author liuyb
 *
 */
public class CognitiveUtils {
	public static final String KEY = "申请的微软的key";

	// 图像分析的链接
	public static final String ANALYZE_IMAGE = "https://api.projectoxford.ai/vision/v1.0/analyze";
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

	public static HttpClient httpclient = HttpClients.createDefault();

	// 调用微软认知服务获取数据

	/**
	 * 分析图片的接口
	 * 
	 * @param filePath
	 */
	public static JSONObject analyzeImage(String filePath) {
		try {
			URIBuilder builder = new URIBuilder(ANALYZE_IMAGE);

			// 为链接配置的一些参数
			/*
			 * 图像特征点： Categories:种类 ImageType:图片类型 Faces:人脸描述 Adult：监测成人内容 Color:颜色 Tags:标签
			 * Description:用一句完成的英文描述图片
			 */
			builder.setParameter("visualFeatures", "Description");
			/*
			 * 对一些特定领域的细节的返回
			 * 
			 * 目前只支持“名人”识别:Celebrities
			 */
			// builder.setParameter("details", "{string}");
			/*
			 * 以哪种语言返回，默认为英语 en 英文 zh 中文
			 */
			// builder.setParameter("language", "en");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			/**
			 * 请求类型 json application/json 图片以url方式发送 二进制流 application/octet-stream 图片以二进制流发送
			 * 多文件发送 multipart/form-data 图片以二进制流进行发送
			 */
			// request.setHeader("Content-Type", "application/json");
			// 以json方式访问，图片路径需要为一个能访问到的地址
			request.setHeader("Content-Type", "application/octet-stream"); // 以位进制文件流的方式访问，图片需要转化成一个二进制的流

			// 还有一种请求方式multipart/form-data

			/*
			 * 请求API的秘钥（需要去微软官方申请）
			 */
			request.setHeader("Ocp-Apim-Subscription-Key", KEY);

			File file = new File(filePath);

			if (!file.exists()) {
				System.out.println("图片不存在");
				return null;
			}

			// Request body
			// 以json方式访问时传递的是一个json字符串
			// StringEntity reqEntity = new StringEntity("{\"Url\":
			// \""+filePath+"\"}");

			// 以文件流方式访问时传递的是一个文件（最终会以位二进制进行请求）
			FileEntity reqEntity = new FileEntity(file);
			request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String result = EntityUtils.toString(entity);

				System.out.println();

				return (JSONObject) JSONObject.parse(result);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	/**
	 * 描述图片
	 * 
	 * @param filePath
	 */
	public static void describeImage(String filePath) {
		try {
			URIBuilder builder = new URIBuilder(DESCRIBE_IMAGE);

			// 最大数量的候选人描述返回。缺省值是1。如果是1，则返回可信度最高的结果
			builder.setParameter("maxCandidates", "1");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);

			// application/json application/octet-stream multipart/form-data
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", KEY);

			// Request body
			StringEntity reqEntity = new StringEntity("{\"Url\": \"" + filePath + "\"}");
			request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 获取缩略图
	 * 
	 * @param filePath
	 *            原始文件的url
	 * @param endPath
	 *            缩略图的url
	 */
	public static void getThumbnail(String filePath, String endPath) {
		try {
			URIBuilder builder = new URIBuilder(GET_THUMBNAIL);

			builder.setParameter("width", "200");
			builder.setParameter("height", "200");
			builder.setParameter("smartCropping", "true");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", KEY);

			// Request body
			StringEntity reqEntity = new StringEntity("{\"Url\": \"" + filePath + "\"}");
			request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request);

			HttpEntity entity = response.getEntity();

			InputStream inputStream = entity.getContent();

			// 将返回的图片输出
			IOUtils.outputImg(inputStream, endPath);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 获取特定领域模型列表（当前微软的API仅支持名人识别）
	 * 
	 * @param filePath
	 */
	public static JSONObject getListDomainSpecificModels() {
		try {
			URIBuilder builder = new URIBuilder(Domain_SPECIFIC);

			URI uri = builder.build();
			HttpGet request = new HttpGet(uri);
			request.setHeader("Ocp-Apim-Subscription-Key", KEY);

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			String modelJson = EntityUtils.toString(entity);

			if (entity != null) {
				// System.out.println(modelJson);

				return (JSONObject) JSONObject.parse(modelJson);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	/**
	 * 识别特定领域的内容(当前只支持名人识别),需要上一个接口getListDomainSpecificModels的支持
	 * 
	 * @param filePath
	 */
	public static void recognizeDomainSpecificContent(String filePath) {
		// 首先从特定领域模型列表中选取需要的领域模型
		JSONObject modelJson = getListDomainSpecificModels();

		// 获取领域模型数组
		JSONArray model = (JSONArray) modelJson.get("models");

		// 遍历领域模型并找到与图片的领域相关信息
		for (Object modelObj : model) {
			JSONObject json = (JSONObject) modelObj;

			String modelName = json.getString("name");

			recognizeDomainSpecificContent(modelName, filePath);
		}
	}

	/**
	 * 重载函数，用于识别给定领域的与图片相关的信息
	 * 
	 * @param modelName
	 * @param filePath
	 */
	public static void recognizeDomainSpecificContent(String modelName, String filePath) {
		String modelUrl = Domain_SPECIFIC + "/" + modelName + "/analyze";

		try {
			URIBuilder builder = new URIBuilder(modelUrl);

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", KEY);

			// Request body
			StringEntity reqEntity = new StringEntity("{\"Url\":\"" + filePath + "\"}");
			request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 获取图片中的文字信息
	 * 
	 * @param filePath
	 */
	public static JSONObject OCR(String filePath, String language) {
		try {
			URIBuilder builder = new URIBuilder(OCR);

			/**
			 * 语言支持： unk (AutoDetect) -- 默认 zh-Hans (ChineseSimplified) zh-Hant
			 * (ChineseTraditional) cs (Czech) da (Danish) nl (Dutch) en (English) fi
			 * (Finnish) fr (French) de (German) el (Greek) hu (Hungarian) it (Italian) Ja
			 * (Japanese) ko (Korean) nb (Norwegian) pl (Polish) pt (Portuguese, ru
			 * (Russian) es (Spanish) sv (Swedish) tr (Turkish)
			 */
			builder.setParameter("language", language);
			// 是否检测图像中的文本定位 true false
			builder.setParameter("detectOrientation ", "true");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			// request.setHeader("Content-Type", "application/json");
			request.setHeader("Content-Type", "application/octet-stream");
			request.setHeader("Ocp-Apim-Subscription-Key", KEY);

			// Request body
			// StringEntity reqEntity = new StringEntity("{\"Url\":\"" +
			// filePath + "\"}");
			// request.setEntity(reqEntity);

			File localFile = new File(filePath);

			if (!localFile.exists()) {
				System.out.println("图片不存在");

				return null;
			}

			// 发送本地图片
			// 以文件流方式访问时传递的是一个文件（最终会以位二进制进行请求）
			FileEntity reqEntity = new FileEntity(localFile);
			request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			// HttpResponse response = httpclient.execute(request);
			// HttpEntity entity = response.getEntity();

			if (entity != null) {
				String result = EntityUtils.toString(entity);

				System.out.println();

				return (JSONObject) JSONObject.parse(result);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	/**
	 * 获取图片的标签
	 * 
	 * @param filePath
	 */
	public static JSONObject getImgTag(String filePath) {
		try {
			URIBuilder builder = new URIBuilder(IMAGE_TAG);

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/octet-stream");
			request.setHeader("Ocp-Apim-Subscription-Key", KEY);

			File localFile = new File(filePath);

			if (!localFile.exists()) {
				System.out.println("图片不存在");

				return null;
			}

			// Request body
			// StringEntity reqEntity = new StringEntity("{\"Url\":\"" + filePath + "\"}");
			// request.setEntity(reqEntity);
			FileEntity reqEntity = new FileEntity(localFile);
			request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			String result = EntityUtils.toString(entity);

			return (JSONObject) JSONObject.parse(result);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

}
