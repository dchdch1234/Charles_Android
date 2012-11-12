package com.jerry.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;


import com.jerry.model.Article;
import com.jerry.model.LoginInfo;
import com.jerry.model.Mail;
import com.jerry.model.SingleArticle;

public class DocParser {
	
	public static boolean sendMail(String post2, String content, Context context, int tryTime) {
		if(tryTime <= 0) {
			return false;
		}
		try {
			LoginInfo loginInfo;
			HttpResponse httpResponse;
			if(tryTime == 1) {
				loginInfo = LoginHelper.resetLoginInfo(context);
			} else {
				loginInfo = LoginHelper.getInstance(context);
			}
			String newUrl = "http://bbs.nju.edu.cn/" + loginInfo.getLoginCode() + "/bbssndmail?pid=0&userid=" + post2;
			HttpPost httpRequest = new HttpPost(newUrl);
			ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
			postData.add(new BasicNameValuePair("action", "bbssndmail?pid=0&userid=" + post2));
			postData.add(new BasicNameValuePair("signature", "1"));
			postData.add(new BasicNameValuePair("text", content));
			postData.add(new BasicNameValuePair("title", "Õ¾  " +
					"" +
					"ÄÚÐÅ"));
			postData.add(new BasicNameValuePair("userid", post2));
			httpRequest.addHeader("Cookie", loginInfo.getLoginCookie());
			httpRequest.setEntity(new UrlEncodedFormEntity(postData, "GB2312"));
			httpResponse = new DefaultHttpClient().execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
				return sendMail(post2, content, context, tryTime - 1);
			}
		}catch (UnsupportedEncodingException e) {
			return sendMail(post2, content, context, tryTime - 1);
		} catch (IOException e) {
			return sendMail(post2, content, context, tryTime - 1);
		}
	}
	
	public static boolean isNewVersionAvailable(Context context) {
		try {
			String doc = Jsoup.connect("http://bbs.nju.edu.cn/blogcon?userid=WStaotao&file=1346326084").get().toString();
			doc = doc.substring(doc.indexOf("version:["));
			doc = doc.substring(0,doc.indexOf("]"));
			doc = doc.substring(doc.indexOf("[") + 1);
			 PackageManager packageManager = context.getPackageManager();
	           // getPackageName()ÊÇÄãµ±Ç°ÀàµÄ°üÃû£¬0´ú±íÊÇ»ñÈ¡°æ±¾ÐÅÏ¢
	           PackageInfo packInfo;
			try {
				packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
				String version = packInfo.versionName;
				double d1 = Double.valueOf(doc);
				double d2 = Double.valueOf(version);
				if( d1 > d2 ) {
					return true;
				} else {
					return false;
				}
			} catch (NameNotFoundException e) {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean replyMail(String url, String content, Context context, int tryTime) {
		if(tryTime <= 0) {
			return false;
		}
		LoginInfo loginInfo;
		if(tryTime == 1) {
			loginInfo = LoginHelper.resetLoginInfo(context);
		} else {
			loginInfo = LoginHelper.getInstance(context);
		}
		String mailto = url.substring(url.indexOf("userid=")+7);
		mailto = mailto.substring(0, mailto.indexOf("&"));
		String pidString = url.substring(url.indexOf("pid=")+4);
		pidString = pidString.substring(0, pidString.indexOf("&"));
		String mailId = url.substring(url.indexOf("file=") + 5);
		mailId = mailId.substring(0, mailId.indexOf("&"));
		String titleString = url.substring(url.indexOf("title=")+6);
		String newUrl = "http://bbs.nju.edu.cn/" + loginInfo.getLoginCode() + "/bbssndmail?pid=" + pidString + "&userid=" + mailto;
		try {
			HttpResponse httpResponse;
			HttpPost httpRequest = new HttpPost(newUrl);
			ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
			postData.add(new BasicNameValuePair("action", mailId));
			postData.add(new BasicNameValuePair("signature", "1"));
			postData.add(new BasicNameValuePair("text", content));
			postData.add(new BasicNameValuePair("title", titleString));
			postData.add(new BasicNameValuePair("userid", mailto));
			httpRequest.addHeader("Cookie", loginInfo.getLoginCookie());
			httpRequest.setEntity(new UrlEncodedFormEntity(postData, "GB2312"));
			httpResponse = new DefaultHttpClient().execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
				return replyMail(newUrl, content, context, tryTime - 1);
			}
		}catch (UnsupportedEncodingException e) {
			return replyMail(newUrl, content, context, tryTime - 1);
		} catch (IOException e) {
			return replyMail(newUrl, content, context, tryTime - 1);
		}
	}

	public static Bundle getMailContent(String url, Context context, int tryTime) {
		if(tryTime <= 0) {
			return null;
		}
		LoginInfo loginInfo;
		if(tryTime == 1) {
			loginInfo = LoginHelper.resetLoginInfo(context);
		} else {
			loginInfo = LoginHelper.getInstance(context);
		}
		String contentUrl = "http://bbs.nju.edu.cn/" + loginInfo.getLoginCode() + "/" + url;
		try {
			HttpPost httpRequest = new HttpPost(contentUrl);
			httpRequest.addHeader("Cookie", loginInfo.getLoginCookie());
			HttpResponse httpResponse;
			httpResponse = new DefaultHttpClient().execute(httpRequest);
			String result = "";
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity());
			}
			Document doc = Jsoup.parse(result);
			String content = doc.select("textarea").get(0).text();
			content = formatContent(content);
			Elements links = doc.select("a");
			String replyUrl = links.get(links.size() - 3).attr("href");
			Bundle bundle = new Bundle();
			content = content.replaceAll("À´ Ô´: \\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", "");
			bundle.putString("content", content);
			bundle.putString("postUrl", replyUrl);
			return bundle;
		} catch (IOException e) {
			return getMailContent(contentUrl, context, tryTime - 1);
		}
	}
	public static final List<Mail> getMailList(int tryTimes, List<String> blockList, Context context) {
		if(tryTimes <= 0) {
			return null;
		}
		List<Mail> list = new ArrayList<Mail>();
		try {
			LoginInfo loginInfo;
			if(tryTimes == 1) {
				loginInfo = LoginHelper.resetLoginInfo(context);
			} else {
				loginInfo = LoginHelper.getInstance(context);
			}
			String url = "http://bbs.nju.edu.cn/" + loginInfo.getLoginCode() + "/bbsmail";
			HttpPost httpRequest = new HttpPost(url);
			httpRequest.addHeader("Cookie", loginInfo.getLoginCookie());
			HttpResponse httpResponse;
			httpResponse = new DefaultHttpClient().execute(httpRequest);
			String result = "";
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity());
			}
			Elements rows = Jsoup.parse(result).select("tr");
			for(Element line : rows) {
				Elements links = line.select("a");
				if (links.size() == 0) {
					continue;
				}
				if(links.size() == 2) {
					String authorName = links.get(0).select("a").text();
					if(blockList.contains(authorName)) {
						continue;
					}
					Mail mail = new Mail();
					mail.setPoster(authorName);
					mail.setPosterUrl(links.get(0).select("a").attr("href"));
					mail.setContentUrl(links.get(1).select("a").attr("href"));
					mail.setTitle(links.get(1).select("a").text());
					mail.setTime(line.select("td").get(4).text());
					mail.setRead(line.select("img").size() != 0);
					list.add(mail);
				}

			}
		} catch (ClientProtocolException e) {
			return getMailList(tryTimes - 1, blockList, context);
		} catch (IOException e) {
			return getMailList(tryTimes - 1, blockList, context);
		}
		return list;
	}
	/*
	 * ·µ»ØÒ»¸öÒ³ÃæËùÓÐÌû×ÓµÄ×ÜÌåList£¬±ÈÈçÊ®´óÌû×ÓµÄList
	 */
	public static final List<Article> getArticleTitleList(String url, int tryTimes,List<String> blockList) {
		if(tryTimes <= 0) {
			return null;
		}
		try {
			List<Article> list = new ArrayList<Article>(); 
			Document doc = Jsoup.connect(url).timeout(5000).get();
			Elements blocks = doc.select("tr");
			for (Element block : blocks) {
				Elements links = block.select("a[href]");
				if (links.size()==0) {
					continue;
				}
				links = block.select("td");
				String authorName = links.get(3).select("a").text();
				if(blockList.contains(authorName)) {
					continue;
				}
				Article article = new Article();
				article.setAuthorName(authorName);
				article.setAuthorUrl(links.get(3).select("a").attr("abs:href"));
				article.setBoard(links.get(1).select("a").text());
				article.setBoardUrl(links.get(1).select("a").attr("abs:href"));
				article.setContentUrl(links.get(2).select("a").attr("abs:href"));
				article.setView(Integer.valueOf(links.get(4).text()));
				article.setTitle(links.get(2).select("a").text());
				list.add(article);
			}
			return list;
		} catch (IOException e) {
			return getArticleTitleList(url, tryTimes - 1, blockList);
		}
	}

	public static final List<Article> getBoardArticleTitleList(String url, String boardName, int tryTimes, List<String>blockList) {
		if(tryTimes <= 0) {
			return null;
		}
		try {
			List<Article> list = new ArrayList<Article>(); 
			Document doc = Jsoup.connect(url).get();
			Elements blocks = doc.select("tr");
			for (int i=blocks.size()-1; i>=0; i--) {
				Element block = blocks.get(i);
				
				//Ìø¹ýÖÃ¶¥Ìû
				if ( (block.select("nobr")).size() != 0 )
					continue;
							
				Elements links = block.getElementsByTag("a");
				if (links.size() == 0) {
					continue;
				}
				String authorName = links.get(0).select("a").text();
				if(blockList.contains(authorName)) {
					continue;
				}
				Article article = new Article();
				article.setAuthorName(authorName);
				article.setAuthorUrl(links.get(0).select("a").attr("abs:href"));
				article.setTitle(links.get(1).select("a").text().substring(2));
				article.setContentUrl(links.get(1).select("a").attr("abs:href"));
				Elements fonts = block.getElementsByTag("font");
				article.setView(Integer.valueOf(fonts.get(fonts.size() - 1).select("font").text()));
				if (fonts.get(0).select("nobr").size() == 0) {
					article.setReply(Integer.valueOf(fonts.get(fonts.size() - 2).select("font").text()));
				}
				list.add(article);

			}
			String allString = doc.toString();
			int i = allString.indexOf("<a href=\"bbstdoc?board=" + boardName + "&amp;start=");
			String s = allString.substring(i);
			Document subDoc = Jsoup.parse(allString.substring(i));
			Elements links = subDoc.getElementsByTag("a");
			Article article = new Article();
			article.setBoard(links.get(0).select("a").attr("href"));
			if (s.indexOf("ÏÂÒ»Ò³") > 0) {
				article.setBoardUrl(links.get(1).select("a").attr("href"));
			}
			list.add(article);
			return list;
		} catch (IOException e) {
			return getBoardArticleTitleList(url, boardName, tryTimes - 1, blockList);
		}
	}

	public static boolean sendReply(String boardName,String title, String pidString,String reIdString,String replyContent, String authorName,String picPath, Context context, int tryTimes) {
		if(tryTimes <= 0) {
			return false;
		}
		LoginInfo loginInfo;
		if(tryTimes == 1) {
			loginInfo = LoginHelper.resetLoginInfo(context);
		} else {
			loginInfo = LoginHelper.getInstance(context);
		}
		try {
			String newurlString = "http://bbs.nju.edu.cn/" + loginInfo.getLoginCode() + "/bbssnd?board=" + boardName;
			HttpPost httpRequest = new HttpPost(newurlString);
			ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
			postData.add(new BasicNameValuePair("title", title.replace("¡ð", "Re:")));
			postData.add(new BasicNameValuePair("pid", pidString));
			postData.add(new BasicNameValuePair("reid", reIdString));
			postData.add(new BasicNameValuePair("signature", "1"));
			postData.add(new BasicNameValuePair("autocr", "on"));
			postData.add(new BasicNameValuePair("text", DocParser.formatString(replyContent, authorName, picPath, context)));
			httpRequest.addHeader("Cookie", loginInfo.getLoginCookie());
			httpRequest.setEntity(new UrlEncodedFormEntity(postData, "GB2312"));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
				return sendReply(boardName, title, pidString, reIdString, replyContent, authorName, picPath, context, tryTimes - 1);
			}
		} catch (UnsupportedEncodingException e) {
			return sendReply(boardName, title, pidString, reIdString, replyContent, authorName, picPath, context, tryTimes - 1);
		} catch (IOException e) {
			return sendReply(boardName, title, pidString, reIdString, replyContent, authorName, picPath, context, tryTimes - 1);
		}
	}

	public static final String upLoadPic2Server(List<String> picPath, String board, int tryTimes, Context context) {
		if (picPath == null || picPath.size() == 0) {
			return "";
		}
		if(tryTimes <= 0) {
			return null;
		}
		LoginInfo loginInfo;
		if(tryTimes == 1) {
			loginInfo = LoginHelper.resetLoginInfo(context);
		} else {
			loginInfo = LoginHelper.getInstance(context);
		}
		File file = new File(picPath.get(0));
		HttpClient client;
		BasicHttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		client = new DefaultHttpClient(httpParameters);
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		String url = "http://bbs.nju.edu.cn/" + loginInfo.getLoginCode() + "/bbsdoupload?ptext=text&board=" + board;
		HttpPost imgPost = new HttpPost(url);
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			reqEntity.addPart("up", new FileBody(file));
			reqEntity.addPart("exp", new StringBody("", Charset.forName("GB2312")));
			reqEntity.addPart("ptext", new StringBody("text", Charset.forName("GB2312")));
			reqEntity.addPart("board", new StringBody(board, Charset.forName("GB2312")));
		} catch (Exception e){
			e.printStackTrace();
		}
		imgPost.setEntity(reqEntity);
		imgPost.addHeader("Cookie", loginInfo.getLoginCookie());
		imgPost.addHeader(reqEntity.getContentType());

		String result;
		HttpResponse httpResponse;
		try {
			httpResponse = client.execute(imgPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity());
				result = result.substring(result.indexOf("url=")+2, result.indexOf(">")-1);
				String fileNum = result.substring(result.indexOf("file=")+5, result.indexOf("&name"));
				String fileName = file.getName();
				if (fileName.length() > 10) {
					fileName = fileName.substring(fileName.length() - 10);
				}
				url = "http://bbs.nju.edu.cn/" + loginInfo.getLoginCode() + "/bbsupload2?board=" + board + "&file=" + fileNum + "&name=" + fileName + "&exp=&ptext=text";
				HttpGet uploadGet = new HttpGet(url);
				uploadGet.addHeader("Cookie", loginInfo.getLoginCookie());
				httpResponse = client.execute(uploadGet);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					result = EntityUtils.toString(httpResponse.getEntity());
					String urlString = result.substring(result.indexOf("'\\n") + 3, result.indexOf("\\n'"));
					System.gc();
					return '\n' + urlString + '\n';
				}
			}
		} catch (ClientProtocolException e) {
			return upLoadPic2Server(picPath, board, tryTimes - 1, context);
		} catch (ConnectTimeoutException e) {
			return upLoadPic2Server(picPath, board, tryTimes - 1, context);
		} catch (IOException e) {
			return upLoadPic2Server(picPath, board, tryTimes - 1, context);
		}
		return upLoadPic2Server(picPath, board, tryTimes - 1,context);
	}

	public static final String getPid(String replyUrl,int tryTimes, Context context) {
		if(tryTimes <= 0) {
			return null;
		}
		LoginInfo loginInfo;
		if(tryTimes == 1) {
			loginInfo = LoginHelper.resetLoginInfo(context);
		} else {
			loginInfo = LoginHelper.getInstance(context);
		}
		String tempString = "http://bbs.nju.edu.cn/" + loginInfo.getLoginCode() + replyUrl.substring(replyUrl.indexOf("/bbspst"));
		URL mUrl;
		try {
			mUrl = new URL(tempString);
			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) mUrl.openConnection();
				conn.setRequestProperty("Cookie", loginInfo.getLoginCookie());
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				conn.connect();
				InputStream in = conn.getInputStream(); 
				BufferedReader reader1 = new BufferedReader(new InputStreamReader(in,"gb2312")); 
				String inputLine = null;
				while ((inputLine = reader1.readLine()) != null) {  
					if ( inputLine.contains("name=pid") ) {
						String temp = inputLine.substring(inputLine.indexOf("name=pid"));
						if (temp.indexOf("value='")!=-1 && temp.indexOf("'>")!=-1) {
							reader1.close();
							in.close();
							return temp.substring(temp.indexOf("value='") + 7,temp.indexOf("'>"));
						}
					}
				}
			} catch (IOException e) {
				return getPid(replyUrl,tryTimes -1, context);
			}

		} catch (MalformedURLException e) {
			return getPid(replyUrl, tryTimes -1, context);
		}
		return getPid(replyUrl,tryTimes -1, context);
	}

	public static final List<Article> getHotArticleTitleList(String url, int tryTimes) {
		if(tryTimes <= 0) {
			return null;
		}
		try {
			List<Article> list = new ArrayList<Article>(); 
			Document doc = Jsoup.connect(url).get();
			Elements blocks = doc.select("tr");
			for (Element block : blocks) {
				Elements links = block.getElementsByTag("a");
				if (links.size() == 0) {
					continue;
				}
				Article article = new Article();
				article.setTitle(links.get(0).select("a").text());
				article.setContentUrl(links.get(0).select("a").attr("abs:href"));
				article.setBoard(links.get(1).select("a").text());
				article.setBoardUrl(links.get(1).select("a").attr("abs:href"));
				list.add(article);
			}
			return list;
		} catch (IOException e) {
			return getHotArticleTitleList(url, tryTimes - 1);
		}
	}

	public static final List<SingleArticle> getSingleArticleList(String url, int tryTimes, List<String> blockList) {
		if(tryTimes <= 0) {
			return null;
		}
		try {
			List<SingleArticle> list = new ArrayList<SingleArticle>(); 
			Document doc = Jsoup.connect(url).get();
			Elements blocks = doc.select("tr");
			for (int i = 0; i < blocks.size() - 1; i++) {
				Elements links = blocks.get(i).select("a[href]");
				if (links.size()==0) {
					continue;
				}
				Elements content =  blocks.get(i + 1).select("textarea");
				String authorName = links.get(2).select("a").text();
				if(blockList.contains(authorName)) {
					continue;
				}
				SingleArticle article = new SingleArticle();
				article.setAuthorName(authorName);
				article.setAuthorUrl(links.get(2).select("a").attr("abs:href"));
				article.setReplyUrl(links.get(1).select("a").attr("abs:href"));
				article.setContent(formatContent(content.get(0).text()));
				list.add(article);
			}
			String allString = doc.toString();
			String s = allString.substring(allString.indexOf("</script>±¾Ö÷Ìâ¹²ÓÐ ") + 15);
			SingleArticle article = new SingleArticle();
			article.setAuthorName(s.substring(0, s.indexOf(" ")));
			list.add(article);

			return list;
		} catch (IOException e) {
			return getSingleArticleList(url, tryTimes - 1, blockList);
		}
	}

	public static final Bundle getAuthorInfo(String authorUrl,String authorName, int tryTimes) {
		if(tryTimes <= 0) {
			return null;
		}
		Bundle bundle = new Bundle();
		try {
			String doc = Jsoup.connect(authorUrl).get().toString();
			if(doc.indexOf("[[1;36m") > 0) {
				bundle.putBoolean("isMale", true);
			} else if(doc.indexOf("[[1;35m") > 0) {
				bundle.putBoolean("isMale", false);
			}
			if(doc.indexOf("Ä¿Ç°ÔÚÕ¾ÉÏ") > 0) {
				bundle.putBoolean("isOnline", true);
			} else {
				bundle.putBoolean("isOnline", false);
			}
			String name = doc.substring(doc.indexOf("([33m") + 6, doc.indexOf("[37m)"));
			bundle.putString("name", name);
		} catch (IOException e) {
			return getAuthorInfo(authorUrl, authorName, tryTimes - 1);
		}
		return bundle;
	}

	public static final LoginInfo login(Bundle userInfo) {
		return login(userInfo.getString("username"),userInfo.getString("password"), 3);
	}

	public static final LoginInfo login(String username, String password, int tryTimes) {
		if(tryTimes <= 0) {
			return null;
		}
		int s = new Random().nextInt(99999)%(90000) + 10000;
		String urlString = "http://bbs.nju.edu.cn/vd" + String.valueOf(s) + "/bbslogin?type=2&id=" + username + "&pw=" + password;
		try {
			String doc = Jsoup.connect(urlString).get().toString();
			if (doc.indexOf("setCookie") < 0) {
				return login(username, password, tryTimes - 1);
			} else {
				LoginInfo info = new LoginInfo();
				String loginString = doc.substring(doc.indexOf("setCookie"));
				loginString =  loginString.substring(11, loginString.indexOf(")") - 1) + "+vd" + String.valueOf(s);
				String[] tmpString =  loginString.split("\\+");
				String _U_KEY = String.valueOf(Integer.parseInt(tmpString[1])-2);
				String[] loginTmp = tmpString[0].split("N");
				String _U_UID = loginTmp[1];
				String _U_NUM = "" + String.valueOf(Integer.parseInt(loginTmp[0]) + 2);
				info.setLoginCookie("_U_KEY=" + _U_KEY + "; " + "_U_UID=" + _U_UID + "; " + "_U_NUM=" + _U_NUM + ";");
				info.setLoginCode(tmpString[2]);
				info.setUsername(username);
				info.setPassword(password);
				return info;
			}
		} catch (IOException e) {
			return login(username, password, tryTimes - 1);
		}
	}

	public static final String formatString(String replyContent,String authorName,String picPath, Context context) {
		if (authorName != null) {
			replyContent += "¡¾ÔÚ  " +  authorName + "  µÄ´ó×÷ÖÐÌáµ½¡¿";
		}
		String finalString = replyContent;
		if(replyContent.length() > 40) {
			StringBuffer buffer = new StringBuffer();
			for(int i = 0; i <replyContent.length(); i++) {
				buffer.append(replyContent.charAt(i));
				if (i > 0 && i % 40 == 0) {
					buffer.append('\n');
				}
			}
			finalString = buffer.toString();
		}
		finalString += picPath;
		String sign = DatabaseDealer.getSettings(context).getSign();
		if (sign != null && sign.length() > 0) {
			finalString += ('\n' + "--" + sign);
		}
		return finalString;
	}

	public static final List <ContentValues>  synchronousFav(LoginInfo loginInfo, int tryTimes) {
		if(tryTimes <= 0) {
			return null;
		}
		String cookie = loginInfo.getLoginCookie();
		String loginCode = loginInfo.getLoginCode();
		String urlString = "http://bbs.nju.edu.cn/" + loginCode + "/bbsmybrd";
		String result = "";
		try {
			HttpClient client;
			BasicHttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000 );
			HttpConnectionParams.setSoTimeout(httpParameters, 10000 );
			client = new DefaultHttpClient(httpParameters);
			client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpGet uploadGet = new HttpGet(urlString);
			uploadGet.addHeader("Cookie", cookie);
			HttpResponse httpResponse = client.execute(uploadGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ClientProtocolException e) {
			return synchronousFav(loginInfo, tryTimes - 1);
		} catch (ConnectTimeoutException e) {
			return synchronousFav(loginInfo, tryTimes - 1);
		} catch (IOException e) {
			return synchronousFav(loginInfo, tryTimes - 1);
		}
		Document doc = Jsoup.parse(result);
		Elements boards = doc.select("input[checked]");
		if (boards.size() == 0) {
			return new ArrayList<ContentValues>();
		}
		List <ContentValues> favList = new ArrayList<ContentValues>();
		for (Element board : boards) {
			ContentValues values = new ContentValues();
			String boardName = board.nextSibling().toString();
			boardName = boardName.substring(boardName.indexOf(">") + 1);
			boardName = boardName.substring(0, boardName.indexOf("<"));
			values.put("english", boardName.substring(0, boardName.indexOf("(")));
			values.put("chinese", boardName.substring(boardName.indexOf("(") + 1, boardName.length() - 1));
			values.put("islocal", 0);
			favList.add(values);
		}
		return favList;
	}

	private static final String formatContent(String content) {
		String result = content.substring(content.indexOf("·¢ÐÅÕ¾: ÄÏ¾©´óÑ§Ð¡°ÙºÏÕ¾ (") + 41);
		result = result.indexOf("- ") > 0 ? result.substring(0,result.indexOf("- ")-1) : result;
		result = result.replaceAll("¡¾ ÔÚ .*µÄ´ó×÷ÖÐÌáµ½: ¡¿", "");
		
		result = result.replace("http://bbs.nju.edu.cn/file", "<br/><img src='http://bbs.nju.edu.cn/file");
		if(result.indexOf("[") >= 0) {
			result = result.replace("[:s]", "<img src='emotion_s'/>");
			result = result.replace("[:O]", "<img src='emotion_o'/>");
			result = result.replace("[:|]", "<img src='emotion_v'/>");
			result = result.replace("[:$]", "<img src='emotion_d'/>");
			result = result.replace("[:X]", "<img src='emotion_x'/>");
			result = result.replace("[:'(]", "<img src='emotion_q'/>");
			result = result.replace("[:@]", "<img src='emotion_a'/>");
			result = result.replace("[:-|]", "<img src='emotion_h'/>");
			result = result.replace("[:P]", "<img src='emotion_p'/>");
			result = result.replace("[:D]", "<img src='emotion_e'/>");
			result = result.replace("[:)]", "<img src='emotion_b'/>");
			result = result.replace("[:(]", "<img src='emotion_c'/>");
			result = result.replace("[:Q]", "<img src='emotion_f'/>");
			result = result.replace("[:T]", "<img src='emotion_g'/>");
			result = result.replace("[;P]", "<img src='emotion_i'/>");
			result = result.replace("[;-D]", "<img src='emotion_j'/>");
			result = result.replace("[:!]", "<img src='emotion_k'/>");
			result = result.replace("[:L]", "<img src='emotion_l'/>");
			result = result.replace("[:?]", "<img src='emotion_m'/>");
			result = result.replace("[:U]", "<img src='emotion_n'/>");
			result = result.replace("[:K]", "<img src='emotion_r'/>");
			result = result.replace("[:C-]", "<img src='emotion_t'/>");
			result = result.replace("[;X]", "<img src='emotion_u'/>");
			result = result.replace("[:H]", "<img src='emotion_w'/>");
			result = result.replace("[;bye]", "<img src='emotion_y'/>");
			result = result.replace("[;cool]", "<img src='emotion_z'/>");
			//[:-b][:-8][;PT][:hx][;K][:E][:-(][;hx][:-v][;xx]
			result = result.replace("[:-b]", "<img src='emotion_0'/>");
			result = result.replace("[:-8]", "<img src='emotion_1'/>");
			result = result.replace("[;PT]", "<img src='emotion_2'/>");
			result = result.replace("[:hx]", "<img src='emotion_3'/>");
			result = result.replace("[;K]", "<img src='emotion_4'/>");
			result = result.replace("[:E]", "<img src='emotion_5'/>");
			result = result.replace("[:-(]", "<img src='emotion_6'/>");
			result = result.replace("[;hx]", "<img src='emotion_7'/>");
			result = result.replace("[:-v]", "<img src='emotion_8'/>");
			result = result.replace("[;xx]", "<img src='emotion_9'/>");
		}
		result = result.replace("[uid]", "<uid>");
		result = result.replace("[/uid]", "</uid>");
		result = result.replace("jpg", "jpg'/><br/>");
		result = result.replace("JPG", "JPG'/><br/>");
		result = result.replace("gif", "gif'/><br/>");
		result = result.replace("GIF", "GIF'/><br/>");
		result = result.replace("png", "png'/><br/>");
		result = result.replace("PNG", "PNG'/><br/>");
		result = result.replace("jpeg", "jpeg'/><br/>");
		result = result.replace("JPEG", "JPEG'/><br/>");
		result = result.replaceAll("\\[(1;.*?|37;1|32|33)m", "");
		return result;
	}
}
