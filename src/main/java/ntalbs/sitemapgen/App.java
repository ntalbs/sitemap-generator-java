package ntalbs.sitemapgen;

import java.io.IOException;
import java.util.Set;

public class App {

  public static void main(String[] args) throws IOException, InterruptedException {
    if (args.length != 1) {
      System.out.println("Site Url should be provided.");
      System.exit(1);
    }

    String site = args[0];

    PathsCollector pathsCollector = new PathsCollector(site);
    Set<String> paths = pathsCollector.collectPaths();

    SiteMapXml siteMapXml = new SiteMapXml(site);
    siteMapXml.generate(paths);
  }

}
