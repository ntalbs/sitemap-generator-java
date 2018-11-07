package ntalbs.sitemapgen;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;

public class PathsCollector {

  private String baseUrl;

  public PathsCollector(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  private String loadPage(String url) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(url))
      .GET()
      .build();
    return HttpClient.newHttpClient()
      .send(request, BodyHandlers.ofString())
      .body();
  }

  private Predicate<String> isInternal = (link) -> link.startsWith("/") && !link.startsWith("//") || link.startsWith(baseUrl);
  private Function<String, String> toPath = (link) -> link.replace(baseUrl, "");

  private Set<String> getPathsIn(String path) throws IOException, InterruptedException {
    String html = loadPage(baseUrl + path);

    return Jsoup.parse(html).getElementsByTag("a").eachAttr("href").stream()
      .filter(isInternal)
      .map(toPath)
      .collect(Collectors.toSet());
  }

  private Set<String> collectAllPaths(String path, Set<String> visited) throws IOException, InterruptedException {
    if (visited.contains(path)) {
      return visited;
    }

    System.out.println("visiting " + path);
    Set<String> toVisit = getPathsIn(path);
    visited.add(path);
    toVisit.removeAll(visited);

    for (String p : toVisit) {
      collectAllPaths(p, visited);
    }
    visited.addAll(toVisit);
    return visited;
  }

  public Set<String> collectPaths() throws IOException, InterruptedException {
    return collectAllPaths("/", new HashSet<>());
  }

}
