package org.exoplatform.bonitasoft.services.rest.publication;

import java.util.ArrayList;
import java.util.List;

/**
 * this bean is used by the ChangePublication Service and he is initialized
 * by processing the configuration.xml file.
 */
public class PublicationConfig {

  private List<Path> paths;

  public PublicationConfig() {
    paths = new ArrayList<Path>();
  }

  public List<Path> getPaths() {
    return paths;
  }

  public void setPaths(List<Path> paths) {
    this.paths = paths;
  }

  static public class Path {
    private String path;
    private String repository;
    private String workspace;

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public String getRepository() {
      return repository;
    }

    public void setRepository(String repository) {
      this.repository = repository;
    }

    public String getWorkspace() {
      return workspace;
    }

    public void setWorkspace(String workspace) {
      this.workspace = workspace;
    }

  }

}
