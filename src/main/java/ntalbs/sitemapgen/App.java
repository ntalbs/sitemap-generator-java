package ntalbs.sitemapgen;

import java.io.IOException;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class App {

  public static void main(String[] args) throws IOException, InterruptedException {
    Options options = new Options()
      .addOption(Option.builder("s")
        .longOpt("site")
        .hasArg()
        .required()
        .desc("site Url to create sitemap.xml.")
        .build())
      .addOption(Option.builder("x")
        .longOpt("exclude")
        .hasArg()
        .required(false)
        .desc("paths to ignore.")
        .build()
      );

    try {
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);

      String site = cmd.getOptionValue("s");
      String[] excludePaths = cmd.getOptionValues("x");

      PathsCollectorSync pathsCollector = new PathsCollectorSync(site);
      Set<String> paths = pathsCollector.collectPaths();

      SiteMapXml siteMapXml = new SiteMapXml(site, excludePaths);
      siteMapXml.generate(paths);
    } catch (ParseException e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("sitemap-gen", options);
      System.exit(0);
    }
  }

}
