package ntalbs.sitemapgen;

import java.util.HashSet;
import java.util.Set;

public class PathsCollectorSync extends PathsCollector {

  PathsCollectorSync(String baseUrl) {
    super(baseUrl);
  }

  private Set<String> collectAllPaths(Set<String> pathsToVisit, Set<String> pathsVisited) {
    pathsToVisit.removeAll(pathsVisited);
    if (pathsToVisit.isEmpty()) {
      return pathsVisited;
    }

    Set<String> pathsToVisitNext = pathsToVisit.stream()
      .peek(p -> System.out.println("visiting " + p))
      .map(this::getPathsIn)
      .collect(HashSet::new, Set::addAll, Set::addAll);

    pathsVisited.addAll(pathsToVisit);
    return collectAllPaths(pathsToVisitNext, pathsVisited);
  }

  public Set<String> collectPaths() {
    Set<String> root = new HashSet<>();
    root.add("/");
    return collectAllPaths(root, new HashSet<>());
  }

}
