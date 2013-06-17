/*
 * Original Developer: Rupendra Bandyopadhyay
 * Company: Pivotal Inc.
 * Publish Date: 06/16/2013
 */
package sqlserver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.sql.*;

import com.microsoft.sqlserver.jdbc.SQLServerStatement;

public class GetDDLInfoOld
{
  Connection con = null;
  Statement stmt = null;
  ResultSet rs = null;
  BufferedWriter outWriter;
  
  public void connectToDatabase (String host, String port, String dbName, String userID, String password) throws ClassNotFoundException, SQLException
  {
    String connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";" + "databaseName=" + dbName + ";" + "responseBuffering=adaptive";
    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    con = DriverManager.getConnection(connectionUrl, userID, password);
  }
  
  public GetDDLInfoOld (String host, String port, String dbName, String userID, String password, String fileName) throws ClassNotFoundException, SQLException, IOException
  {
    connectToDatabase(host, port, dbName, userID, password);
    if (fileName != null && ! fileName.trim().equals(""))
    {
      outWriter = new BufferedWriter(new FileWriter( new File(fileName)));
    }
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
  
  public static String getDDLSQL()
  {
    return
    "select sch.name schemaname,                                                          \n" +
    "       tab.name tablename,                                                           \n" +
    "       col.name columnname,                                                          \n" +
    "       col.colid,                                                                    \n" +
    "       typ.name datatype,                                                            \n" +
    "       typ2.name actdatatype,                                                        \n" +
    "       col.length,                                                                   \n" +
    "       col.prec,                                                                     \n" +
    "       col.scale,                                                                    \n" +
    "       col.iscomputed,                                                               \n" +
    "       cc.definition,                                                                \n" +
    "       col.isnullable,                                                               \n" +
    "       sind.rowcnt                                                                   \n" +
    "from   sys.tables tab                                                                \n" +
    "left outer join sys.sysindexes sind on sind.id = tab.object_id and                   \n" +
    "sind.indid in (0,1)                                                                  \n" +
    "inner join sys.schemas sch on sch.schema_id = tab.schema_id                          \n" +
    "left outer join sys.syscolumns col on col.id = tab.object_id                         \n" +
    "left outer join sys.computed_columns cc on cc.object_id = col.id                     \n" +
    "                                       and cc.column_id = col.colid                  \n" +
    "inner join sys.types typ on typ.system_type_id = col.xtype                           \n" +
    "                        and typ.user_type_id = col.xusertype                         \n" +
    "left outer join sys.types typ2 on typ2.system_type_id = col.xtype                    \n" +
    "                              and typ2.user_type_id = col.xtype                      \n" +
    "order by 1,2,4                                                                       \n";
  }
  
  public void runDDLExtractOne() throws SQLException, IOException
  {
    SQLServerStatement stmtOne = createStmt();
    rs = stmt.executeQuery(getDDLSQL());
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
    String outFile = Utility.getArgument("-f", args);;
    if ( host == null || host.trim().equals("") || port == null || port.trim().equals("") || dbName == null || dbName.trim().equals("") || userID == null || userID.trim().equals(""))
    {
      System.out.println("Usage: java GetDDLInfo -h <Host> -p <Port> -d <Database> -u <User> -f <Output File>");
      System.exit(1);
    }
    ReadPassword rp = new ReadPassword();
    String password = rp.getPassword();
    if (password == null || password.trim().equals(""))
    {
      System.out.println("Password must be entered.");
      System.exit(1);
    }
    
    // GetDDLInfo gdi = new GetDDLInfo("10.0.0.19", "1433", "AdventureWorks2008", "sa", "n3wf0x");
    GetDDLInfoOld gdi = new GetDDLInfoOld(host, port, dbName, userID, password, outFile);
    gdi.runDDLExtractOne();
    gdi.closeAll();
  }
}
