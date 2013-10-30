package cg3204l.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateImageDB {

	public static void main(String[] args) throws ClassNotFoundException {
		// load the sqlite-JDBC driver using the current class loader
	    Class.forName("org.sqlite.JDBC");

	    Connection connection = null;
	    try
	    {
	      // create a database connection
	      connection = DriverManager.getConnection("jdbc:sqlite:image.db");
	      Statement statement = connection.createStatement();
	      statement.setQueryTimeout(30);  // set timeout to 30 sec.

	      statement.executeUpdate("drop table if exists image");
	      statement.executeUpdate("create table image (id integer PRIMARY KEY, url string, keyword string)");

	      ResultSet rs = statement.executeQuery("select * from image where keyword like '%abc%'");
	      
	      while(rs.next())
	      {
	        // read the result set
	        System.out.println("url = " + rs.getString("url"));
	        System.out.println("keyowrd = " + rs.getString("keyword"));
	      }
	    }
	    catch(SQLException e)
	    {
	      // if the error message is "out of memory", 
	      // it probably means no database file is found
	      System.err.println(e.getMessage());
	    }
	    finally
	    {
	      try
	      {
	        if(connection != null)
	          connection.close();
	      }
	      catch(SQLException e)
	      {
	        // connection close failed.
	        System.err.println(e);
	      }
	    }
	}
}
