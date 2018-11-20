package ntalbs.sitemapgen;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;

public abstract class PathsCollector {

  private String baseUrl;

  protected final Predicate<String> isInternal = (link) -> link.startsWith("/")
    && !link.startsWith("//")
    || link.startsWith(baseUrl);
  protected final Function<String, String> toPath = (link) -> link.replace(baseUrl, "");

  PathsCollector(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  private HttpRequest newHttpRequest(String path) {
    return HttpRequest.newBuilder(URI.create(baseUrl + path))
      .GET()
      .build();
  }

  Set<String> getPathsIn(String path) {
    var request = newHttpRequest(path);

    try {
      var html = HttpClient.newHttpClient()
        .send(request, BodyHandlers.ofString())
        .body();
      return Jsoup.parse(html).getElementsByTag("a").eachAttr("href").stream()
        .filter(isInternal)
        .map(toPath)
        .collect(Collectors.toSet());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  CompletableFuture<Set<String>> getPathsInAsync(String path) {
    var request = newHttpRequest(path);

    return HttpClient.newHttpClient()
      .sendAsync(request, BodyHandlers.ofString())
      .thenApply(response -> Jsoup.parse(response.body())
        .getElementsByTag("a").eachAttr("href").stream()
        .filter(isInternal)
        .map(toPath)
        .collect(Collectors.toSet())
      );
  }

  public abstract Set<String> collectPaths();

}
