package ntalbs.sitemapgen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

public class SiteMapXml {

  private static String front = "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
  private static String urlFmt = "  <url>\n"
    + "    <loc>%s%s</loc>\n"
    + "    <lastmod>%s</lastmod>\n"
    + "    <changefreq>monthly</changefreq>\n"
    + "    <priority>0.5</priority>\n"
    + "  </url>\n";
  private static String back  = "</urlset>";

  private String baseUrl;

  public SiteMapXml(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  private String dateString() {
    return Instant.now().toString();
  }

  public void generate(Iterable<String> paths) {
    String date = dateString();
    try (PrintWriter w = new PrintWriter(new FileWriter("./sitemap.xml"))) {
      w.println(front);
      for (String path : paths) {
        w.printf(urlFmt, baseUrl, path, date);
      }
      w.println(back);
    } catch (IOException e) {
      // do nothing
    }
  }

}
