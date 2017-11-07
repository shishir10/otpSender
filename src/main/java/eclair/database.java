package eclair;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class database {
	
	private final static Logger LOG = LogManager.getLogger(database.class);
	
//	Can use this to create a database pool also
	
	public static Connection getConnection() {
		try {
			URI dbUri = new URI("postgres://kmplfevqzvgizd:9954df5b2e9467330ec9ecfe69ce3f8259b8362468bd345c496cb45ff84b674f@ec2-184-72-249-88.compute-1.amazonaws.com:5432/d8i1g8seok1fl4");
			String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            return conn;
		} catch (URISyntaxException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	public static void close(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}

}
