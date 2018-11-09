package ntalbs.sitemapgen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class SiteMapXml {

  private static String front = "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
  private static String urlFmt = "  <url>\n"
    + "    <loc>%s%s</loc>\n"
    + "    <lastmod>%s</lastmod>\n"
    + "    <changefreq>monthly</changefreq>\n"
    + "    <priority>0.5</priority>\n"
    + "  </url>\n";
  private static String back  = "</urlset>";

  private String baseUrl;
  private String[] excludePaths;
  private Predicate<String> exclude;

  SiteMapXml(String baseUrl, String[] excludePaths) {
    this.baseUrl = baseUrl;
    this.excludePaths = excludePaths;
    this.exclude = createExcludePredicate(excludePaths);
  }

  private Predicate<String> createExcludePredicate(String[] excludePaths) {
    if (excludePaths == null) {
      return (path) -> false;
    } else {
      return (path) -> Stream.of(excludePaths).anyMatch(path::contains);
    }
  }

  private String dateString() {
    return Instant.now().toString();
  }

  void generate(Iterable<String> paths) throws IOException {
    String date = dateString();
    try (PrintWriter w = new PrintWriter(new FileWriter("./sitemap.xml"))) {
      w.println(front);
      StreamSupport.stream(paths.spliterator(), false)
        .filter(exclude)
        .forEach(path -> w.printf(urlFmt, baseUrl, path, date));
      w.println(back);
    }
  }

}
