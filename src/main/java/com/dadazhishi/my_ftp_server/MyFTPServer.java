package com.dadazhishi.my_ftp_server;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.UserFactory;

public class MyFTPServer {

  public static void main(String[] args) throws FtpException {
    if (args.length != 4) {
      System.err.println("usage: java -jar my-ftp-server-*.jar 21 username password dir");
      System.exit(-1);
      return;
    }
    int port = Integer.parseInt(args[0]);
    String username = args[1];
    String password = args[2];
    String dir = args[3];

    FtpServerFactory serverFactory = new FtpServerFactory();
    ListenerFactory factory = new ListenerFactory();
    factory.setIdleTimeout(60);
    factory.setPort(port);
    serverFactory.addListener("default", factory.createListener());

    ConnectionConfigFactory configFactory = new ConnectionConfigFactory();
    configFactory.setAnonymousLoginEnabled(false);
    configFactory.setMaxAnonymousLogins(0);

    configFactory.setMaxLoginFailures(5);
    configFactory.setLoginFailureDelay(30);

    configFactory.setMaxThreads(10);
    configFactory.setMaxLogins(10);
    serverFactory.setConnectionConfig(configFactory.createConnectionConfig());

    UserFactory userFact = new UserFactory();
    userFact.setName(username);
    userFact.setPassword(password);
    userFact.setHomeDirectory(dir);
    User user = userFact.createUser();
    InMemoryUserManager userManager = new InMemoryUserManager();
    userManager.save(user);
    serverFactory.setUserManager(userManager);

    FtpServer server = serverFactory.createServer();
    server.start();
  }
}
