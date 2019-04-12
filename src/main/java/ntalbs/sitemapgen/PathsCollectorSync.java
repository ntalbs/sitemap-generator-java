package ntalbs.sitemapgen;

import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PathsCollectorSync extends PathsCollector {

  private static final Logger logger = LogManager.getLogger();

  PathsCollectorSync(String baseUrl) {
    super(baseUrl);
  }

  private Set<String> collectAllPaths(Set<String> pathsToVisit, Set<String> pathsVisited) {
    pathsToVisit.removeAll(pathsVisited);
    if (pathsToVisit.isEmpty()) {
      return pathsVisited;
    }

    Set<String> pathsToVisitNext = pathsToVisit.stream()
      .peek(p -> logger.info("visiting " + p))
      .map(this::getPathsIn)
      .collect(HashSet::new, Set::addAll, Set::addAll);

    pathsVisited.addAll(pathsToVisit);
    return collectAllPaths(pathsToVisitNext, pathsVisited);
  }

  @Override
  public Set<String> collectPaths() {
    Set<String> root = new HashSet<>();
    root.add("/");
    return collectAllPaths(root, new HashSet<>());
  }

}
