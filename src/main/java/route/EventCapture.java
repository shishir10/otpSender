package route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.session.DatabaseAdaptor;

import eclair.database;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import twilio.SmsSender;

public class EventCapture extends AbstractRouteHandler {

	private final static Logger LOG = LogManager.getLogger(EventCapture.class);

	public EventCapture(Vertx vertx) {
		// TODO Auto-generated constructor stub
		super(vertx);
		this.route().handler(BodyHandler.create());
		this.post("/sendMessage").handler(this::sendMessage);
		this.post("/saveContact").handler(this::saveContacts);
		this.post("/getDetails").handler(this::getDetails);
		this.post("/getAllDetails").handler(this::getAllDetails);
	}
	
	private void getAllDetails(RoutingContext context) {
		Connection conn = database.getConnection();
		String sql = "select full_name, phone_number, otp_sent from contacts";
		JsonObject response = new JsonObject();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int i = 0;
			while(rs.next()) {
				JsonObject row = new JsonObject();
				row.put("name", rs.getString(1));
				row.put("number", rs.getString(2));
				row.put("otpSent", rs.getBoolean(3));
				response.put(String.valueOf(++i), row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error(e.getMessage());
			response.put("status", "error");
			response.put("message", e.getMessage());
		}
		database.close(conn);
		sendJsonResponse(context, response.toString());
	}
	
	private void getDetails(RoutingContext context) {
		Connection conn = database.getConnection();
		String sql = "select * from contacts where otp_sent=true";
		JsonObject response = new JsonObject();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int i = 0;
			while(rs.next()) {
				JsonObject row = new JsonObject();
				row.put("name", rs.getString(1));
				row.put("number", rs.getString(2));
				row.put("otp", rs.getString(5));
				JsonObject data = SmsSender.getDetail(rs.getString(4));
				row.put("messageStatus", data.getString("messageStatus"));
				row.put("date", data.getString("date"));
				response.put(String.valueOf(++i), row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error(e.getMessage());
			response.put("status", "error");
			sendJsonResponse(context, response.toString());
		}
		database.close(conn);
		sendJsonResponse(context, response.toString());
	}

	private void sendMessage(RoutingContext routingContext) {
		JsonObject json = new JsonObject();
		JsonObject status = new JsonObject();
		String sql = "UPDATE public.contacts SET message_sid=?,otp=?,otp_sent=true WHERE phone_number=?";
		json = routingContext.getBodyAsJson();
		status = SmsSender.sendMessage(json.getString("number"), json.getInteger("OTP").toString());
		if(status.getString("status").equals("Success")) {
			Connection conn = database.getConnection();
			PreparedStatement ps;
			try {
				ps = conn.prepareStatement(sql);
				ps.setString(1, status.getString("message"));
				ps.setInt(2, json.getInteger("OTP"));
				ps.setString(3, json.getString("number"));
				ps.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error(e.getMessage());
				status.put("status", "error");
				status.put("message", e.getMessage());
			} finally {
				database.close(conn);
			}
		}
		sendJsonResponse(routingContext, status.toString());
//		LOG.info(json);
	}

	private void saveContacts(RoutingContext routingContext) {
		JsonObject json = new JsonObject();
		json = routingContext.getBodyAsJson().getJsonObject("contacts");
		JsonObject response = new JsonObject();
		String sql = "INSERT INTO public.contacts (full_name,phone_number) VALUES (?,?)";
		String sql2 = "select count(*) from contacts where phone_number = ?";
		try {
			Class.forName("org.postgresql.Driver");
			Connection con = database.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			PreparedStatement ps2 = con.prepareStatement(sql2);
			if (!json.isEmpty()) {
				JsonObject jo = new JsonObject();
				Set<String> StrKey = json.fieldNames();
				Iterator<String> it = StrKey.iterator();
				while (it.hasNext()) {
					jo = json.getJsonObject(it.next().toString());
					LOG.info(jo.getString("name"));
					LOG.info(jo.getString("number"));
					ps2.setString(1, jo.getString("number"));
					ResultSet rs = ps2.executeQuery();
					rs.next();
					int i = rs.getInt(1);
					if (i == 0) {
						ps.setString(1, jo.getString("name"));
						ps.setString(2, jo.getString("number"));
						ps.addBatch();
					}
					ps.executeBatch();
				}
				ps.close();
				database.close(con);
				response.put("message", "Contacts Saved Successfully");
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error(e.getMessage());
			response.put("message", "Error in connecting to DB");
		} finally {
			sendJsonResponse(routingContext, response.toString());
		}
	}

}
