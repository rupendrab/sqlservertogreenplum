/*
 * Original Developer: Rupendra Bandyopadhyay
 * Company: Pivotal Inc.
 * Publish Date: 06/16/2013
 */
package sqlserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Hashtable;

public class GenExtractSQL
{

  Hashtable<String,Boolean> gpKeywords = new Hashtable<String,Boolean>(400); 
  Connection hCon = null;
  Statement stmt = null;
  ResultSet rs = null;
  String sql;
  BufferedWriter ddlWriter;
  String specificTableName;
  String specificSchemaName;
  
  public void connectToHSQL(String hDB) throws SQLException, ClassNotFoundException
  {
    Class.forName("org.hsqldb.jdbcDriver");
    hCon = DriverManager.getConnection("jdbc:hsqldb:file:" + hDB + ";shutdown=true", "sa", ""); // can through sql exception
    stmt = hCon.createStatement();
  }

  public void getKeywords() throws SQLException
  {
    String sql = "select keyword_id from gp_keywords order by 1";
    ResultSet rs = stmt.executeQuery(sql);
    while (rs.next())
    {
      gpKeywords.put(rs.getString(1), true);
    }
    rs.close();
  }
  
  public String addQuotes (String name)
  {
    if (gpKeywords.containsKey(name) || name.contains(" "))
    {
      return "\"" + name + "\"";
    }
    else
    {
      return name;
    }
  }
  
  public GenExtractSQL (String hDB, String schemaName, String tableName) throws ClassNotFoundException, SQLException, IOException
  {
    connectToHSQL(hDB);
    getKeywords();
    specificSchemaName = schemaName;
    specificTableName = tableName;    
    if (tableName != null)
    {
      String[] tableParts = tableName.split("\\.");
      if (tableParts.length > 1)
      {
        specificSchemaName = tableParts[0];
        specificTableName = tableParts[1];
      }
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
      if (hCon != null)
      {
        hCon.close();
      }
    }
    catch (Exception e)
    {
    }
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
  
  public String getPartitionClause (String schemaName, String tableName, PreparedStatement psPart) throws IOException, SQLException
  {
    StringBuilder sb = new StringBuilder(50);
    psPart.setString(1, schemaName);
    psPart.setString(2, tableName);
    ResultSet rs = psPart.executeQuery();
    int cnt = 0;
    int written = 0;
    while (rs.next())
    {
      cnt++;
      int colIndex = 0;
      String columnName = rs.getString(++colIndex);
      int boundaryId = rs.getInt(++colIndex);
      boolean boundaryIsRight = rs.getBoolean(++colIndex);
      String partType = rs.getString(++colIndex);
      String boundaryValue = rs.getString(++colIndex);
      int partNumber = rs.getInt(++colIndex);
      if (partType.equalsIgnoreCase("range")) // Implemented range partitions only
      {
        written++;
        if (cnt == 1)
        {
          sb.append("partition by range (" + addQuotes(columnName.toLowerCase()) + ")");
          sb.append("\n");
          sb.append("(");
          sb.append("\n");
        }
        else
        {
          sb.append(",\n");
        }
        if (boundaryValue != null)
        {
          sb.append ("  partition p_" + partNumber + " end ('" + boundaryValue + "')");
          sb.append (boundaryIsRight?" exclusive":" inclusive");
        }
        else
        {
          sb.append("  default partition p_default");
        }
      }
    }
    rs.close();
    if (written > 1)
    {
      sb.append("\n");
      sb.append(")");
      sb.append("\n");
    }
    return sb.toString();
  }
  
  public void tableSQL() throws SQLException, IOException
  {
    sql = "select schemaname, tablename, count(*) from table_cols @@myfilter group by schemaname, tablename order by 1, 2";
    String whereClause = "where 1=1";
    if (specificSchemaName != null)
    {
      whereClause = whereClause + " and lower(schemaname) = '" + specificSchemaName.toLowerCase() + "'";
    }
    if (specificTableName != null)
    {
      whereClause = whereClause + " and lower(tablename) = '" + specificTableName.toLowerCase() + "'";
    }
    sql = sql.replaceAll("@@myfilter", whereClause);
    rs = stmt.executeQuery(sql);
    String sqlCols = getSQL("translate.sql");
    PreparedStatement ps = hCon.prepareStatement(sqlCols);
    String sqldist = getSQL("dist.sql");
    PreparedStatement psDist = hCon.prepareStatement(sqldist);
    String sqlpart = getSQL("part.sql");
    PreparedStatement psPart = hCon.prepareStatement(sqlpart);
    while (rs.next())
    {
      String schemaName = rs.getString(1);
      String tableName = rs.getString(2);
      int cntCols = rs.getInt(3);
      String newSchemaName = new String(schemaName);
      if (schemaName.equalsIgnoreCase("dbo"))
      {
        newSchemaName = "public";
      }
      System.out.print("select");
      System.out.print("\n");
      ps.setString(1, schemaName);
      ps.setString(2, tableName);
      ResultSet rs2 = ps.executeQuery();
      int colCount = 0;
      while (rs2.next())
      {
        colCount++;
        int colIndex = 0;
        String columnName = rs2.getString(++colIndex);
        int colId = rs2.getInt(++colIndex);
        String dataType = rs2.getString(++colIndex);
        String actDataType = rs2.getString(++colIndex);
        String gpType = rs2.getString(++colIndex);
        String dataTypeTranslated = rs2.getString(++colIndex);
        String nullClause = rs2.getString(++colIndex);
        String defaultVal = rs2.getString(++colIndex);
        String computeDef = rs2.getString(++colIndex);
        String seedValue = rs2.getString(++colIndex);
        StringBuilder line = new StringBuilder(100);
        if (gpType == null || dataTypeTranslated == null)
        {
          line.append("  --Attn " + columnName + ", " + dataType + "(" + actDataType + ")" + ", " + nullClause + ", default=" + defaultVal + ", computed=" + computeDef);
        }
        else if (gpType.equals("bit"))
        {
          
          line.append(String.format("  cast(%s as varchar(100))", addQuotes(columnName.toLowerCase())));
          if (colCount < cntCols)
          {
            line.append(",");
          }
        }
        else
        {
          line.append(String.format("  %s", addQuotes(columnName.toLowerCase())));
          if (colCount < cntCols)
          {
            line.append(",");
          }
        }
        System.out.print(line.toString());
        System.out.print("\n");
      }
      rs2.close();
      System.out.print("from " + addQuotes(newSchemaName.toLowerCase()) + "." + addQuotes(tableName.toLowerCase()));
      System.out.print("\n");
      System.out.print("\n");
    }
    System.out.print("\n");
    rs.close();
    ps.close();
    psDist.close();
  }

  public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
  {
    String tableName = Utility.getArgument("-t", args);
    String schemaName = Utility.getArgument("-s", args);
    if ( tableName == null || tableName.trim().equals(""))
    {
      System.out.println("Usage: java GenExtractSQL -t <Table Name> -s <Schema Name>");
      System.exit(1);
    }
    /*
    if (tableName != null && ! tableName.trim().equals(""))
    {
      if (schemaName != null && ! schemaName.trim().equals(""))
      {
        System.out.println("Use either schema name or table name");
        System.exit(1);
      }
    }
    */
    GenExtractSQL ges = new GenExtractSQL("mydb/db", schemaName, tableName);
    ges.tableSQL();
    ges.closeAll();
  }
}
