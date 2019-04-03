package fi.muikku.ui.base.indexpage;

import static fi.muikku.mock.PyramusMock.mocker;

import java.io.IOException;

import org.junit.Test;

import fi.muikku.TestUtilities;
import fi.muikku.mock.PyramusMock.Builder;
import fi.muikku.mock.model.MockStaffMember;
import fi.muikku.mock.model.MockStudent;
import fi.muikku.ui.AbstractUITest;
import fi.pyramus.rest.model.Sex;
import fi.pyramus.rest.model.UserRole;

public class IndexPageTestsBase extends AbstractUITest {

  @Test
  public void indexPageTest() throws IOException {
    navigate("", true);
    assertVisible("main.content");
  }
  
  @Test
  public void studentLoginTest() throws Exception {
    MockStudent student = new MockStudent(2l, 2l, "Second", "User", "teststudent@example.com", 1l, TestUtilities.toDate(1990, 1, 1), "121212-1212", Sex.FEMALE, TestUtilities.toDate(2012, 1, 1), TestUtilities.getNextYear());
    Builder mockBuilder = mocker();
    mockBuilder.addStudent(student).mockLogin(student).build();
    login();
    assertVisible("#loggedUser");
  }
  
  @Test
  public void adminLoginTest() throws Exception {
    MockStaffMember admin = new MockStaffMember(1l, 1l, "Admin", "Person", UserRole.ADMINISTRATOR, "090978-1234", "testadmin@example.com", Sex.MALE);
    mocker().addStaffMember(admin).mockLogin(admin).build();
    login();
    assertVisible("#loggedUser");
  }
}
