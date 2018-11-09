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

  private String loadPage(String url) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
      .GET()
      .build();
    return HttpClient.newHttpClient()
      .send(request, BodyHandlers.ofString())
      .body();
  }

  Set<String> getPathsIn(String path) throws IOException, InterruptedException {
    String html = loadPage(baseUrl + path);

    return Jsoup.parse(html).getElementsByTag("a").eachAttr("href").stream()
      .filter(isInternal)
      .map(toPath)
      .collect(Collectors.toSet());
  }

  CompletableFuture<Set<String>> getPathsInAsync(String path) {
    HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
      .GET()
      .build();
    return HttpClient.newHttpClient()
      .sendAsync(request, BodyHandlers.ofString())
      .thenApply(response -> Jsoup.parse(response.body())
        .getElementsByTag("a").eachAttr("href").stream()
        .filter(isInternal)
        .map(toPath)
        .collect(Collectors.toSet())
      );
  }

  public abstract Set<String> collectPaths() throws IOException, InterruptedException;

}
