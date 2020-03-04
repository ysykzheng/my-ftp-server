package com.dadazhishi.my_ftp_server;

import java.util.ArrayList;
import java.util.List;
import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

public class InMemoryUserManager implements UserManager {

  private BaseUser user;

  public User getUserByName(String s) throws FtpException {
    if (user.getName().equals(s)) {
      return user;
    }
    return null;
  }

  public String[] getAllUserNames() throws FtpException {
    return new String[]{user.getName()};
  }

  public void delete(String s) throws FtpException {

  }

  public void save(User user) throws FtpException {
    this.user = (BaseUser) user;
    if(user.getAuthorities() == null || user.getAuthorities().isEmpty()){
      List<Authority> authorities = new ArrayList<Authority>();
      authorities.add(new WritePermission());
      authorities.add(new ConcurrentLoginPermission(10, 10));
      this.user.setAuthorities(authorities);
    }
  }

  public boolean doesExist(String s) throws FtpException {
    return user.getName().equals(s);
  }

  public User authenticate(Authentication auth) throws AuthenticationFailedException {
    if(auth!=null && auth instanceof UsernamePasswordAuthentication){
      UsernamePasswordAuthentication userAuth = (UsernamePasswordAuthentication) auth;
      if(user.getName().equals(userAuth.getUsername()) && user.getPassword().equals(userAuth.getPassword())){
        return user;
      }
    }
    return null;
  }

  public String getAdminName() throws FtpException {
    return user.getName();
  }

  public boolean isAdmin(String s) throws FtpException {
    return user.getName().equals(s);
  }
}
