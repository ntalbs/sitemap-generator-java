package ntalbs.sitemapgen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PathsCollectorAsync extends PathsCollector {

  private static Logger logger = LogManager.getLogger();

  PathsCollectorAsync(String baseUrl) {
    super(baseUrl);
  }

  private Set<String> collectAllPaths(Set<String> pathsToVisit, Set<String> pathsVisited) {
    pathsToVisit.removeAll(pathsVisited);
    if (pathsToVisit.isEmpty()) {
      return pathsVisited;
    }

    List<CompletableFuture<Set<String>>> pathSetFuture = pathsToVisit.stream()
      .peek(p -> logger.info("visiting " + p))
      .map(this::getPathsInAsync)
      .collect(Collectors.toList());

    Set<String> pathsToVisitInNextRound = pathSetFuture.stream()
      .map(CompletableFuture::join)
      .collect(HashSet::new, Set::addAll, Set::addAll);

    pathsVisited.addAll(pathsToVisit);
    return collectAllPaths(pathsToVisitInNextRound, pathsVisited);
  }

  public Set<String> collectPaths() {
    Set<String> root = new HashSet<>();
    root.add("/");
    return collectAllPaths(root, new HashSet<>());
  }
}
