/*
 * Original Developer: Rupendra Bandyopadhyay
 * Company: Pivotal Inc.
 * Publish Date: 06/16/2013
 */
package sqlserver;

import java.io.Console;

public class ReadPassword
{
  private String password;
  
  public ReadPassword()
  {
    char[] str;
    Console cons;
    if ((cons = System.console()) != null)
    {
      str = cons.readPassword("[%s]", "Password: ");
      if (str != null)
      {
        password = new String(str);
      }
      else
      {
        System.out.println("No Console Available...");
      }
    }
  }
  
  public void clearPassword()
  {
    password = null;
  }
  
  public String getPassword()
  {
    return password;
  }

  public static void main(String[] args)
  {
    ReadPassword rp = new ReadPassword();
    System.out.println(rp.getPassword());
    rp.clearPassword();
  }
}
