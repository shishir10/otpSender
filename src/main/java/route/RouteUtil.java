package route;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class RouteUtil {
	
	private static final RouteUtil instance = new RouteUtil();
	public static final String JSON_TYPE = "application/json";
    public static final String TEXT_HTML_TYPE = "text/html";
	
    public RouteUtil() {
		// TODO Auto-generated constructor stub
	}
    
    public static RouteUtil getInstance() {
        return instance;
    }
    
    public void sendJsonResponse(RoutingContext context, String json)
    {
        sendResponse(context, json, JSON_TYPE);
    }
    
    public void sendResponse(RoutingContext context, String text, String type)
    {
        HttpServerResponse response = context.response();
        response.putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.putHeader("content-type", type).end(text);
    }
    
    

}
