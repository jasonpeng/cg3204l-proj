package cg3204l;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cg3204l.model.Image;

public class LocalImageDB {
	
	protected Connection mConnection;

	public LocalImageDB() throws ClassNotFoundException {
		// load the sqlite-JDBC driver using the current class loader
	    Class.forName("org.sqlite.JDBC");

	    mConnection = null;
	    
	    try
	    {
	      // create a database connection
	      mConnection = DriverManager.getConnection("jdbc:sqlite:image.db");
	    }
	    catch(SQLException e)
	    {
	      // if the error message is "out of memory", 
	      // it probably means no database file is found
	      System.err.println(e.getMessage());
	    }
	}
	
	public List<Image> search(String keyword, Date date) {
		List<Image> imagelList = new ArrayList<Image>();
		
		try {
			Statement statement = mConnection.createStatement();
			ResultSet rs = statement
					.executeQuery("select * from image where keyword like '%" 
							+ keyword + "%'");
			
			while (rs.next()) {
				Image image = new Image(rs.getString("src"), null, null, rs.getString("url"),
						rs.getLong("updatedTime"));
				imagelList.add(image);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return imagelList;
	}

	public void insert(List<Image> imageList, String keyword) {
		
		try {
			Statement statement = mConnection.createStatement();
			
			for (Image image : imageList) {
				statement
				.executeUpdate("insert or replace into image (src, url, keyword)values ('" + image.getSrc() + "','" + image.getSiteUrl() + "','" + keyword + "')");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}