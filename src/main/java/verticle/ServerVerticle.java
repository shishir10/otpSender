package verticle;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import route.EventCapture;

public class ServerVerticle extends AbstractVerticle {

	private final static Logger LOG = LogManager.getLogger(ServerVerticle.class);

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Vertx vertx = Vertx.vertx();
//		vertx.deployVerticle(new ServerVerticle());
		vertx.deployVerticle(new ServerVerticle(), new DeploymentOptions().setWorker(true), result ->
        {
            if (result.succeeded())
            {
                LOG.info("Server started.");
            }
            else
            {
                LOG.error("Server did not start. Message:" + result.result(), result.cause());
                System.exit(1);
            }
        });
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		// TODO Auto-generated method stub
		LOG.info("Server verticle is starting ...");
		Router router = Router.router(vertx);

		router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST)
				.allowedMethod(HttpMethod.OPTIONS).allowedHeader("Authorization").allowedHeader("Content-Type"));
		
//		Routing all the requests to this handler
		router.mountSubRouter("/event/", new EventCapture(vertx));

		vertx.createHttpServer().requestHandler(router::accept).listen(8000, result -> {
			if (result.succeeded()) {
				LOG.info("Server verticle has been successfully started !!");
			} else {
				LOG.error("There was some issue in starting the server !!" + result.cause());
			}
		});
		startFuture.complete();
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
	}

}
