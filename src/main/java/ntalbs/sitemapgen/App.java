package ntalbs.sitemapgen;

import java.io.IOException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class App {

  public static void main(String[] args) throws IOException {
    var options = buildOptions();

    try {
      var t0 = System.currentTimeMillis();

      var parser = new DefaultParser();
      var cmd = parser.parse(options, args);

      var site = cmd.getOptionValue("s");
      var excludePaths = cmd.getOptionValues("x");

      PathsCollector pathsCollector;
      if (cmd.hasOption("a")) {
        pathsCollector = new PathsCollectorAsync(site);
      } else {
        pathsCollector = new PathsCollectorSync(site);
      }

      var paths = pathsCollector.collectPaths();
      var siteMapXml = new SiteMapXml(site, excludePaths);
      siteMapXml.generate(paths);

      System.out.printf(
        "\nSitemap.xml has created. Took %d ms\n",
        System.currentTimeMillis() - t0
      );
    } catch (ParseException e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("sitemap-gen", options);
      System.exit(0);
    }
  }

  private static Options buildOptions() {
    return new Options()
      .addOption(Option.builder("a")
        .longOpt("async")
        .hasArg(false)
        .required(false)
        .desc("execute asynchronously")
        .build())
      .addOption(Option.builder("s")
        .longOpt("site")
        .hasArg()
        .required()
        .desc("site Url to create sitemap.xml")
        .build())
      .addOption(Option.builder("x")
        .longOpt("exclude")
        .hasArg()
        .required(false)
        .desc("paths to ignore")
        .build());
  }

}
