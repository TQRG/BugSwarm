package fi.muikku.schooldata.entity;

import org.joda.time.DateTime;

public abstract class AbstractUser implements User {
  
  public AbstractUser(String identifier, String firstName, String lastName, String displayName,
      String studyProgrammeName, String nationality, String language, String municipality, String school,
      DateTime studyStartDate, DateTime studyEndDate, DateTime studyTimeEnd, boolean hidden, 
      boolean startedStudies, boolean finishedStudies, boolean active, boolean evaluationFees) {
    super();
    this.identifier = identifier;
    this.firstName = firstName;
    this.lastName = lastName;
    this.displayName = displayName;
    this.studyProgrammeName = studyProgrammeName;
    this.nationality = nationality;
    this.language = language;
    this.municipality = municipality;
    this.school = school;
    this.studyStartDate = studyStartDate;
    this.studyEndDate = studyEndDate;
    this.studyTimeEnd = studyTimeEnd;
    this.hidden = hidden;
    this.active = active;
    this.startedStudies = startedStudies;
    this.finishedStudies = finishedStudies;
    this.evaluationFees = evaluationFees;
  }

  @Override
  public String getSearchId() {
    return getIdentifier() + "/" + getSchoolDataSource();
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  @Override
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @Override
  public String getFirstName() {
    return firstName;
  }

  @Override
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public String getLastName() {
    return lastName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String getStudyProgrammeName() {
    return studyProgrammeName;
  }
  
  @Override
  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  @Override
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  @Override
  public String getMunicipality() {
    return municipality;
  }

  public void setMunicipality(String municipality) {
    this.municipality = municipality;
  }

  @Override
  public String getSchool() {
    return school;
  }

  public void setSchool(String school) {
    this.school = school;
  }

  @Override
  public DateTime getStudyStartDate() {
    return studyStartDate;
  }

  public void setStudyStartDate(DateTime studyStartDate) {
    this.studyStartDate = studyStartDate;
  }
  
  @Override
  public DateTime getStudyEndDate() {
    return this.studyEndDate;
  }
  
  public void setStudyEndDate(DateTime studyEndDate) {
    this.studyEndDate = studyEndDate;
  }

  @Override
  public DateTime getStudyTimeEnd() {
    return studyTimeEnd;
  }

  public void setStudyTimeEnd(DateTime studyTimeEnd) {
    this.studyTimeEnd = studyTimeEnd;
  }

  @Override
  public boolean getHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }
  
  @Override
  public boolean getStartedStudies() {
    return startedStudies;
  }
  
  public void setStartedStudies(boolean startedStudies) {
    this.startedStudies = startedStudies;
  }
  
  @Override
  public boolean getFinishedStudies() {
    return finishedStudies;
  }
  
  public void setFinishedStudies(boolean finishedStudies) {
    this.finishedStudies = finishedStudies;
  }
  
  @Override
  public boolean getActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  @Override
  public boolean hasEvaluationFees() {
    return evaluationFees;
  }

  private String identifier;
  private String firstName;
  private String lastName;
  private String displayName;
  private String studyProgrammeName;
  private String nationality;
  private String language;
  private String municipality;
  private String school;
  private DateTime studyStartDate;
  private DateTime studyEndDate;
  private DateTime studyTimeEnd;
  private boolean hidden;
  private boolean startedStudies;
  private boolean finishedStudies;
  private boolean active;
  private boolean evaluationFees;
}
