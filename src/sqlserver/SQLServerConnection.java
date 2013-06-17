/*
 * Original Developer: Rupendra Bandyopadhyay
 * Company: Pivotal Inc.
 * Publish Date: 06/16/2013
 */
package sqlserver;

import java.sql.*;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;

public class SQLServerConnection
{
  public static void main(String[] args)
  {
    String host = Utility.getArgument("-h", args);
    String port = Utility.getArgument("-p", args);
    String dbName = Utility.getArgument("-d", args);
    String userID = Utility.getArgument("-u", args);
    if ( host == null || host.trim().equals("") || port == null || port.trim().equals("") || dbName == null || dbName.trim().equals("") || userID == null || userID.trim().equals(""))
    {
      System.out.println("Usage: java GetDDLInfo -h <Host> -p <Port> -d <Database> -u <User>");
      System.exit(1);
    }

    String password = Utility.readEnv("SSPASSWORD");
    if (password == null)
    {
      ReadPassword rp = new ReadPassword();
      password = rp.getPassword();
      if (password == null || password.trim().equals(""))
      {
        System.out.println("Password must be entered.");
        System.exit(1);
      }
    }

    String connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";" + "databaseName=" + dbName + ";" + "responseBuffering=adaptive";

    // Declare the JDBC objects.
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    try
    {
      // Establish the connection.
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      con = DriverManager.getConnection(connectionUrl, userID, password);

      // In adaptive mode, the application does not have to use a server cursor
      // to avoid OutOfMemoryError when the SELECT statement produces very large
      // results.

      // Create and execute an SQL statement that returns some data.
      String SQL = "SELECT getdate()";
      stmt = con.createStatement();

      // If you have not already set the responseBuffering=adaptive in the
      // connection properties, you can set the response buffering to adaptive
      // on the statement level before executing the query, such as:
      SQLServerStatement SQLstmt = (SQLServerStatement) stmt;
      SQLstmt.setResponseBuffering("adaptive");

      // Display the response buffering mode.
      System.out.println("Response buffering mode has been set to " + SQLstmt.getResponseBuffering());

      // Get the updated data from the database and display it.
      rs = stmt.executeQuery(SQL);

      while (rs.next())
      {
        String tstmp = rs.getString(1);
        System.out.println("OK: " + tstmp);
      }
    }
    // Handle any errors that may have occurred.
    catch (Exception e)
    {
      e.printStackTrace();
    } finally
    {
      if (rs != null)
        try
        {
          rs.close();
        } catch (Exception e)
        {
        }
      if (stmt != null)
        try
        {
          stmt.close();
        } catch (Exception e)
        {
        }
      if (con != null)
        try
        {
          con.close();
        } catch (Exception e)
        {
        }
    }
  }
}
