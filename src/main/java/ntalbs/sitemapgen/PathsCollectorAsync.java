package ntalbs.sitemapgen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PathsCollectorAsync extends PathsCollector {

  PathsCollectorAsync(String baseUrl) {
    super(baseUrl);
  }

  private Set<String> collectAllPaths(Set<String> pathsToVisit, Set<String> visited) {
    pathsToVisit.removeAll(visited);
    if (pathsToVisit.isEmpty()) {
      return visited;
    }

    List<CompletableFuture<Set<String>>> pathSetFuture = pathsToVisit.stream()
      .peek(p -> System.out.println("visiting " + p))
      .map(this::getPathsInAsync)
      .collect(Collectors.toList());

    Set<String> pathsToVisitInNextRound = pathSetFuture.stream()
      .map(CompletableFuture::join)
      .collect(HashSet::new, Set::addAll, Set::addAll);

    visited.addAll(pathsToVisit);
    return collectAllPaths(pathsToVisitInNextRound, visited);
  }

  public Set<String> collectPaths() {
    Set<String> root = new HashSet<>();
    root.add("/");
    return collectAllPaths(root, ConcurrentHashMap.newKeySet());
  }
}
