package com.dadazhishi.my_ftp_server;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.UserFactory;

public class MyFTPServer {

  public static void main(String[] args) throws FtpException {
    ArgumentParser parser = ArgumentParsers.newFor("java -jar my-ftp-server-*-jar-with-dependencies.jar").build()
        .defaultHelp(true)
        .description("simple ftp server");
    parser.addArgument("-P", "--port").setDefault(21)
        .required(false)
        .help("ftp port");
    parser.addArgument("-u", "--user").setDefault("admin")
        .required(false)
        .help("username");
    parser.addArgument("-p", "--password").setDefault("admin")
        .required(false)
        .help("password");
    parser.addArgument("-d", "--dir")
        .required(true)
        .help("static file dir");
    Namespace ns;
    try {
      ns = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.exit(1);
      return;
    }
    Integer port = ns.getInt("port");
    String username = ns.getString("username");
    String password = ns.getString("password");
    String dir = ns.getString("dir");

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
