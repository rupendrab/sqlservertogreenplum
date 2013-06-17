/*
 * Original Developer: Rupendra Bandyopadhyay
 * Company: Pivotal Inc.
 * Publish Date: 06/16/2013
 */
package sqlserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.sql.*;

import com.microsoft.sqlserver.jdbc.SQLServerStatement;

public class RunSQL
{
  Connection con = null;
  Statement stmt = null;
  ResultSet rs = null;
  BufferedWriter outWriter;
  String sql;
  
  public void connectToDatabase (String host, String port, String dbName, String userID, String password) throws ClassNotFoundException, SQLException
  {
    String connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";" + "databaseName=" + dbName + ";" + "responseBuffering=adaptive";
    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    con = DriverManager.getConnection(connectionUrl, userID, password);
  }
  
  public RunSQL (String host, String port, String dbName, String userID, String password, String sql, String fileName) throws ClassNotFoundException, SQLException, IOException
  {
    connectToDatabase(host, port, dbName, userID, password);
    if (fileName != null && ! fileName.trim().equals(""))
    {
      outWriter = new BufferedWriter(new FileWriter( new File(fileName)));
    }
    this.sql = sql;
  }
  
  public SQLServerStatement createStmt () throws SQLException
  {
    try
    {
      if (stmt != null)
      {
        stmt.close();
      }
    }
    catch (Exception e)
    {
    }

    stmt = con.createStatement();

    SQLServerStatement SQLstmt = (SQLServerStatement) stmt;
    SQLstmt.setResponseBuffering("adaptive");

    // Display the response buffering mode.
    System.err.println("Response buffering mode has been set to " + SQLstmt.getResponseBuffering());
    return SQLstmt;
  }
  
  public static String getSQL(String fileName) throws IOException
  {
    StringBuilder sb = new StringBuilder(1000);
    BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
    String line;
    while ((line = reader.readLine()) != null)
    {
      sb.append(line);
      sb.append("\n");
    }
    reader.close();
    return sb.toString();
  }
  
  public void runExtractOne() throws SQLException, IOException
  {
    SQLServerStatement stmtOne = createStmt();
    rs = stmt.executeQuery(sql);
    ResultSetMetaData rsm = rs.getMetaData();
    int cntCols = rsm.getColumnCount();
    /*
    for (int i=1; i<=cntCols; i++)
    {
      String colName = rsm.getColumnName(i);
      String colClassName = rsm.getColumnClassName(i);
      System.out.println(colName + "\t" + colClassName);
    }
    */
    int lines = 0;
    while (rs.next())
    {
      StringBuilder sb = new StringBuilder(200);
      for (int i=1; i<=cntCols; i++)
      {
        Object o = rs.getObject(i);
        if (i > 1)
        {
          sb.append(",");
        }
        if (rsm.getColumnClassName(i).equals("java.lang.String"))
        {
          String str = (String) o;
          if (str == null)
          {
            sb.append("");
          }
          else
          {
            str = str.replaceAll("\"", "\"\"");
            sb.append("\"");
            sb.append(str);
            sb.append("\"");
          }
        }
        else
        {
          if (o != null)
          {
            sb.append(o);
          }
          else
          {
            sb.append("");
          }
        }
      }
      if (outWriter == null)
      {
        System.out.println(sb.toString());
        lines++;
      }
      else
      {
        outWriter.write(sb.toString() + "\n");
        lines++;
      }
    }
    
    System.err.println("Number of records written: " + lines);
    if (rs != null)
    {
      rs.close();
    }
    if (stmtOne != null)
    {
      stmtOne.close();
    }
    if (outWriter != null)
    {
      outWriter.close();
    }
  }
  
  public void closeAll()
  {
    try
    {
      if (rs != null)
      {
        rs.close();
      }
    }
    catch (Exception e)
    {
    }
    try
    {
      if (stmt != null)
      {
        stmt.close();
      }
    }
    catch (Exception e)
    {
    }
    try
    {
      if (con != null)
      {
        con.close();
      }
    }
    catch (Exception e)
    {
    }
  }
  public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
  {
    String host = Utility.getArgument("-h", args);
    String port = Utility.getArgument("-p", args);
    String dbName = Utility.getArgument("-d", args);
    String userID = Utility.getArgument("-u", args);
    String outFile = Utility.getArgument("-f", args);
    String sqlFile = Utility.getArgument("-sqlfile", args);;
    if ( host == null || host.trim().equals("") || port == null || port.trim().equals("") || dbName == null || dbName.trim().equals("") || userID == null || userID.trim().equals("") || sqlFile == null || sqlFile.trim().equals(""))
    {
      System.out.println("Usage: java RunSQL -h <Host> -p <Port> -d <Database> -u <User> -sqlfile <SQL File> -f <Output File>");
      System.exit(1);
    }
    String sql = getSQL(sqlFile);

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
    
    // GetDDLInfo gdi = new GetDDLInfo("10.0.0.19", "1433", "AdventureWorks2008", "sa", "n3wf0x");
    RunSQL rSQL = new RunSQL(host, port, dbName, userID, password, sql, outFile);
    rSQL.runExtractOne();
    rSQL.closeAll();
  }
}
