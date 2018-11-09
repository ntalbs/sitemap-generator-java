package ntalbs.sitemapgen;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PathsCollectorSync extends PathsCollector {

  PathsCollectorSync(String baseUrl) {
    super(baseUrl);
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
