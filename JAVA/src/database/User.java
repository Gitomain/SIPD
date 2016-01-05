package database;

/**
 * TCellDB User records.
 * 
 * @author Athanasia Katsouraki
 */
public class User {
   public String userGID;
   public String TCellIP;
   public String PubKey;
  
  public User(String userGID,String TCellIP){
       this.userGID=userGID;
       this.TCellIP=TCellIP;   
  }
  public User(String userGID,String TCellIP, String PubKey){
      this.userGID=userGID;  
      this.TCellIP=TCellIP;
      this.PubKey = PubKey;
  }
  public User(String PubKey){
      this.PubKey = PubKey;
  }
  
}
