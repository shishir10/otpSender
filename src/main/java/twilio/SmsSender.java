package twilio;

import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.rest.api.v2010.account.MessageFetcher;
import com.twilio.rest.api.v2010.account.MessageReader;
import com.twilio.type.PhoneNumber;

import io.vertx.core.json.JsonObject;

/* This class initializes the twilio API to send messages */

public class SmsSender {

	private final static Logger LOG = LogManager.getLogger(SmsSender.class);

	public static final String ACCOUNT_SID = "ACa133e50ad4ad14bee41992f13d4b294a";
	public static final String AUTH_TOKEN = "7f0be04308cbe2b7dd0f5bf885d86830";

	public static JsonObject sendMessage(String ToPhoneNumber, String OTP) {

		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		JsonObject status = new JsonObject();

		MessageCreator messageCreator = Message.creator(ACCOUNT_SID, new PhoneNumber(ToPhoneNumber),
				new PhoneNumber("+14155239121"), "Hi. Your OTP is : " + OTP);

		try {
			Message message = messageCreator.create();
			LOG.info(message.getSid());
			LOG.info(message.getStatus());
			status.put("status", "Success");
			status.put("message", message.getSid());
			return status;
		} catch (Exception e) {
			// TODO: handle exception
			LOG.error(e.getMessage());
			status.put("status", "Error");
			status.put("message", e.getMessage());
			return status;
		}
	}

	public static JsonObject getDetail(String SID) {

		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		JsonObject results = new JsonObject();

		MessageFetcher messageFetcher = Message.fetcher(ACCOUNT_SID, SID);

		Message message = messageFetcher.fetch();
		results.put("date", message.getDateCreated().toString());
		results.put("messageStatus", message.getStatus());
		return results;
	}

}
