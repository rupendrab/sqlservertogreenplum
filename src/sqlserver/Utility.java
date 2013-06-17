/*
 * Original Developer: Rupendra Bandyopadhyay
 * Company: Pivotal Inc.
 * Publish Date: 06/16/2013
 */
package sqlserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility
{
  static Pattern timePattern;
  
  public static String getArgument(String key, String[] arguments)
  {
    String value = null;
    for (int i = 0; i < arguments.length; i++)
    {
      if (arguments[i].equals(key))
      {
        try
        {
          value = arguments[i + 1];
          break;
        } catch (ArrayIndexOutOfBoundsException e)
        {
          break;
        }
      }
    }
    return value;
  }

  public static boolean existsArgument(String key, String[] arguments)
  {
    for (int i = 0; i < arguments.length; i++)
    {
      if (arguments[i].equals(key))
      {
        return true;
      }
    }
    return false;
  }
  
  public static String nvl(String in, String alternate)
  {
    return in==null?alternate:in;
  }
  
  public static String parseTime(String str)
  {
    Matcher m = timePattern.matcher(str);
    String newDateStr = null;
    if (m.find())
    {
      String month = m.group(1);
      String day = m.group(2);
      String year = m.group(3);
      String time = m.group(4);
      year = year==null?null:year.trim();
      newDateStr = month + " " + day + " " + year + " " + time;
      if (year == null)
      {
        java.util.Calendar currentTime = java.util.Calendar.getInstance();
        int yr = currentTime.get(java.util.Calendar.YEAR);
        year = yr + "";
        SimpleDateFormat fmt = new java.text.SimpleDateFormat ("MMM d yyyy hh:mm:ss");
        newDateStr = month + " " + day + " " + year + " " + time;
        java.util.Date dt = null;
        try
        {
          dt = fmt.parse(newDateStr);
        } 
        catch (ParseException e)
        {
          return str;
        }
        if (currentTime.getTime().before(dt))
        {
          year = (yr-1) + "";
          newDateStr = month + " " + day + " " + year + " " + time;
        }
      }
    }
    return newDateStr;
  }

  public static String arrayToDelimitedString(String[] input, String delim)
  {
    if (input == null)
    {
      return "";
    }
    StringBuilder sb = new StringBuilder(300);
    for (int i = 0; i < input.length; i++)
    {
      if (i > 0)
      {
        sb.append(delim);
      }
      sb.append(input[i]);
    }
    return sb.toString();
  }
  
  public static String[] readCSVLine(BufferedReader reader, int columns) throws Exception
  {
    int fields = columns==-1?10:columns;
    ArrayList<String> fieldList = new ArrayList<String>((fields));
    int colsRead = 0;
    boolean inField = false;
    String line = null;
    char startChar;
    boolean quotedField = false;
    StringBuilder sb = new StringBuilder(50);
    while ((line = reader.readLine()) != null)
    {
      // System.out.println("Original:" + line);
      char[] lineChars = line.toCharArray();
      for (int i=0; i<lineChars.length; i++)
      {
        char c = lineChars[i];
        if (! inField)
        {
          sb = new StringBuilder(50);
          startChar = c;
          if (startChar == '"')
          {
            quotedField = true;
          }
          else if (startChar == ',')
          {
            inField = false;
            fieldList.add(null);
            colsRead++;
            continue;
          }
          else
          {
            quotedField = false;
            sb.append(c);
          }
          inField = true;
        }
        else
        {
          if ( c == ',')
          {
            if (! quotedField)
            {
              inField = false;
              fieldList.add(sb.toString());
              colsRead++;
            }
            else
            {
              if (lineChars[i-1] == '"')
              {
                inField = false;
                fieldList.add(sb.toString());
                colsRead++;
              }
              else
              {
                sb.append(c);
              }
            }
          }
          else if (c == '"')
          {
            if (! quotedField)
            {
              sb.append(c);
            }
            else
            {
              if (i==(lineChars.length-1))
              {
                inField = false;
                fieldList.add(sb.toString());
                colsRead++;
                break;
              }
              else if (lineChars[i+1] == ',')
              {
              }
              else if (lineChars[i+1] == '"')
              {
                sb.append(c);
                i++;
                if (i == (lineChars.length-1))
                {
                  break;
                }
              }
              else
              {
                throw new Exception("Unterminated Quote at line : " + line);
              }
            }
          }
          else
          {
            sb.append(c);
          }
        }
        if (i == (lineChars.length-1))
        {
          if (! quotedField)
          {
            inField = false;
            fieldList.add(sb.toString());
            colsRead++;
          }
          else
          {
            sb.append("\n");
          }
        }
      }
      if (! inField)
      {
        break;
      }
    }
    if (fieldList.size() == 0)
    {
      return null;
    }
    String[] ret = new String[fieldList.size()];
    fieldList.toArray(ret);
    return ret;
  }

  public static String readEnv(String var)
  {
    return System.getenv(var);
  }

  static
  {
    String patternStr = "^([a-zA-Z]{3}) +([0-9]{1,2})( +[0-9]{4})? +([0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2})";
    timePattern = java.util.regex.Pattern.compile(patternStr);
  }

}
