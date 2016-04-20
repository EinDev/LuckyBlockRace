package info.mymc.lbr.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLWrapper {
	
	private Connection conn;
	
	public SQLWrapper(String host, String db, String user, String pass) throws SQLException {
		conn = DriverManager.getConnection("jdbc:mysql://"+host+"/"+db+"?user="+user+"&password="+pass);
	}
	
	public void getUser(UUID uuid) {
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `user` WHERE `UUID` = ?");
			stmt.setString(1, uuid.toString());
			ResultSet r = stmt.executeQuery();
			while(r.next()) {
				
			}
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void close() throws SQLException {
		conn.close();
	}

}
