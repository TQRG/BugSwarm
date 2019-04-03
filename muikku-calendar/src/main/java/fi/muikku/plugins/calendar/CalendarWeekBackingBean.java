package fi.muikku.plugins.calendar;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.muikku.session.SessionController;
import fi.otavanopisto.security.LoggedIn;

@Named
@Stateful
@RequestScoped
@Join(path = "/calendar/week", to = "/jsf/calendar/view_week.jsf")
@LoggedIn
public class CalendarWeekBackingBean {
	
  @Inject
  private CalendarController calendarController;

  @Inject
  private SessionController sessionController;

  @RequestAction
  public String init() {
    userCalendarsIds = StringUtils.join(calendarController.listUserCalendarIds(sessionController.getLoggedUserEntity()).toArray(new Long[0]), ",");
    return null;
  }
  
  public String getUserCalendarsIds() {
    return userCalendarsIds;
  }
  
  private String userCalendarsIds;
}

