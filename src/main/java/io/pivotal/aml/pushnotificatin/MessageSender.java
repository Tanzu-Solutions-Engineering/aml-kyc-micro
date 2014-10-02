package io.pivotal.aml.pushnotificatin;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.google.gson.Gson;


public class MessageSender {

	static Logger logger = Logger.getLogger(MessageSender.class);
	private String  vendor, amt, pushurl;
	private Set<String> TAGS;

	public MessageSender(String vendor, String amt, String url) {
		this.vendor = vendor;
		this.amt = amt;
		this.pushurl = url;
	}

	public void sendMessage() {
		final String data = getBackEndMessageRequestString();
		if (data == null) {
			logger.error("Can not send message. Please register first.");
			//return;
		}
		logger.warn("Sending message via Pivotal Push Notifications Service");
		logger.warn("Message body data: \"" + data + "\"");


		OutputStream outputStream = null;
		BufferedReader error = null;

		try {
			final URL url = new URL( pushurl+ "v1/push");
			final HttpURLConnection urlConnection = getUrlConnection(url);
			urlConnection.setDoOutput(true);
			urlConnection.addRequestProperty("Authorization", getBasicAuthorizationValue());
			urlConnection.connect();
			//error = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
			outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
			writeConnectionOutput(data, outputStream);

			final int statusCode = urlConnection.getResponseCode();
			if (statusCode >= 200 && statusCode < 300) {
				logger.warn("Back-end server accepted network request to send message. HTTP response status code is " + statusCode + ".");
			} else {
				logger.error("Back-end server rejected network request to send message. HTTP response status code is " + statusCode + ".");
/*				String line;
				while ((line = error.readLine()) != null) 
					logger.error(line);
				error.close();*/
			}


			urlConnection.disconnect();

		} catch (IOException e) {
			logger.error("ERROR: got exception parsing network response from Back-end server: " + e.getLocalizedMessage(),e);

		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {}
			}
		}
		return;

	}

	private String getBackEndMessageRequestString() {
		/* final String device_uuid = readIdFromFile("device_uuid");
         if (device_uuid == null) {
             return null;
         }*/
		final String device_uuid = "be7436ea-496e-4942-ad6e-ac931635ae77";
		//final String[] devices = new String[]{device_uuid};
        //TAGS = new HashSet<String>();
        //TAGS.add("test");
		final String[] devices = new String[]{"test"};
		final String platforms = "android";
		final String messageBody = "Your request received on " + getLogTimestamp() + " to send " + amt+ " to "+vendor + " is under AML/KYC review" ;
		final BackEndMessageRequest messageRequest = new BackEndMessageRequest(messageBody, platforms, devices);
		final Gson gson = new Gson();
		return gson.toJson(messageRequest);
	}

	private String getBasicAuthorizationValue() throws UnsupportedEncodingException {
		final String environmentUuid = "19cb4f65-9e31-4b11-9748-17ae5f55b489";
		final String environmentKey = "586d0ecf-d011-4a29-92b5-9d8ab4ccf941";
		final String stringToEncode = environmentUuid + ":" + environmentKey;
		return "Basic  " + DatatypeConverter.printBase64Binary(stringToEncode.getBytes("UTF-8"));
	}

	private void writeConnectionOutput(String requestBodyData, OutputStream outputStream) throws IOException {
		final byte[] bytes = requestBodyData.getBytes();
		for (byte b : bytes) {
			outputStream.write(b);
		}
		outputStream.close();
	}
	private HttpURLConnection getUrlConnection(URL url) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setDoInput(true);
		urlConnection.setRequestMethod("POST");
		urlConnection.setConnectTimeout(60000);
		urlConnection.setReadTimeout(60000);
		urlConnection.addRequestProperty("Content-Type", "application/json");
		return urlConnection;
	}
	private String getLogTimestamp() {
		Date date= new java.util.Date();
		return new Timestamp(date.getTime()).toString();
	}
}

