package fi.muikku.schooldata.events;

public class SchoolDataWorkspaceRoleRemovedEvent {

  public SchoolDataWorkspaceRoleRemovedEvent(String dataSource, String identifier) {
    super();
    this.dataSource = dataSource;
    this.identifier = identifier;
  }

  public String getDataSource() {
    return dataSource;
  }

  public String getIdentifier() {
    return identifier;
  }
  
  private String dataSource;
  private String identifier;
}
