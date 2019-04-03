package fi.muikku.ui.base;

import org.joda.time.DateTime;
import org.junit.Test;

import fi.muikku.TestUtilities;
import fi.muikku.mock.model.MockCourseStudent;
import fi.muikku.mock.model.MockStaffMember;
import fi.muikku.mock.model.MockStudent;
import fi.muikku.ui.AbstractUITest;
import fi.muikku.atests.Workspace;
import fi.pyramus.rest.model.CourseStaffMember;
import fi.pyramus.rest.model.Sex;
import fi.pyramus.rest.model.UserRole;
import fi.muikku.mock.PyramusMock.Builder;
import static fi.muikku.mock.PyramusMock.mocker;

public class GuiderTestsBase extends AbstractUITest {

  @Test
  public void filterByNameTest() throws Exception {
    MockStaffMember admin = new MockStaffMember(1l, 1l, "Admin", "Person", UserRole.ADMINISTRATOR, "090978-1234", "testadmin@example.com", Sex.MALE);
    MockStudent student = new MockStudent(3l, 3l, "Second", "User", "teststudent@example.com", 1l, new DateTime(1990, 2, 2, 0, 0, 0, 0), "121212-1212", Sex.FEMALE, TestUtilities.toDate(2012, 1, 1), TestUtilities.getNextYear());
    Builder mockBuilder = mocker();
    mockBuilder.addStaffMember(admin).addStudent(student).mockLogin(admin).build();
    login();
    Workspace workspace = createWorkspace("testscourse", "test course for testing", "3", Boolean.TRUE);
    Workspace workspace2 = createWorkspace("diffentscourse", "Second test course", "4", Boolean.TRUE);
    MockCourseStudent mcs = new MockCourseStudent(3l, workspace.getId(), student.getId());
    mockBuilder.addCourseStudent(workspace.getId(), mcs).build();
    try {
      navigate("/guider", true);
      sendKeys(".gt-search .search", "Second User");
      waitForPresent(".gt-user .gt-user-meta-topic>span");
      assertText(".gt-user .gt-user-meta-topic>span", "Second User (Test Study Programme)");
    } finally {
      deleteWorkspace(workspace.getId());
      deleteWorkspace(workspace2.getId());
    }

  }
  
  @Test
  public void filterByWorkspaceTest() throws Exception {
    MockStaffMember admin = new MockStaffMember(1l, 1l, "Admin", "Person", UserRole.ADMINISTRATOR, "090978-1234", "testadmin@example.com", Sex.MALE);
    MockStudent student = new MockStudent(3l, 3l, "Second", "User", "teststudent@example.com", 1l, new DateTime(1990, 2, 2, 0, 0, 0, 0), "121212-1212", Sex.FEMALE, TestUtilities.toDate(2012, 1, 1), TestUtilities.getNextYear());
    Builder mockBuilder = mocker();
    mockBuilder.addStaffMember(admin).addStudent(student).mockLogin(admin).build();
    login();
    Workspace workspace = createWorkspace("testcourse", "test course for testing", "1", Boolean.TRUE);
    Workspace workspace2 = createWorkspace("diffentcourse", "Second test course", "2", Boolean.TRUE);
    MockCourseStudent mcs = new MockCourseStudent(1l, workspace.getId(), student.getId());
    mockBuilder.addCourseStudent(workspace.getId(), mcs).build();
    try {
      navigate("/guider", true);
      waitAndClick(String.format("#workspace-%d>a", workspace.getId()));
      waitForPresent(".gt-user .gt-user-meta-topic>span");
      assertText(".gt-user .gt-user-meta-topic>span", "Second User (Test Study Programme)");
    } finally {
      deleteWorkspace(workspace2.getId());
      deleteWorkspace(workspace.getId());
    }
  }
  
}