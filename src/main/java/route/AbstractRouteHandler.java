package route;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RouterImpl;

public class AbstractRouteHandler extends RouterImpl {
	
	private final static Logger LOG = LogManager.getLogger(AbstractRouteHandler.class);
	private RouteUtil routeUtil = RouteUtil.getInstance();
	protected Vertx vertx;
	

	public AbstractRouteHandler(Vertx vertx) {
		// TODO Auto-generated constructor stub
		super(vertx);
        this.vertx = vertx;
	}
	
	protected void sendJsonResponse(RoutingContext context, String json)
    {
		LOG.info("Sending response");
        routeUtil.sendJsonResponse(context, json);
    }

}
