package fi.muikku.plugins.announcer.dao;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.muikku.plugins.CorePluginsDAO;
import fi.muikku.plugins.announcer.model.Announcement;
import fi.muikku.plugins.announcer.model.AnnouncementUserGroup;
import fi.muikku.plugins.announcer.model.AnnouncementUserGroup_;
import fi.muikku.plugins.announcer.model.Announcement_;

public class AnnouncementDAO extends CorePluginsDAO<Announcement> {
	
  private static final long serialVersionUID = -8721990589622544635L;
  
  public Announcement create(
      Long publisherUserEntityId,
      String caption,
      String content,
      Date created,
      Date startDate,
      Date endDate,
      boolean archived,
      boolean publiclyVisible
  ) {
    Announcement announcement = new Announcement();
    announcement.setPublisherUserEntityId(publisherUserEntityId);
    announcement.setCaption(caption);
    announcement.setContent(content);
    announcement.setCreated(created);
    announcement.setStartDate(startDate);
    announcement.setEndDate(endDate);
    announcement.setArchived(archived);
    announcement.setPubliclyVisible(publiclyVisible);
    
    return persist(announcement);
 }
  
  public void archive(Announcement announcement) {
    if(announcement != null){
      announcement.setArchived(true);
      getEntityManager().persist(announcement);
    }
  }
  
  public List<Announcement> listByArchived(boolean archived){
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Announcement> criteria = criteriaBuilder.createQuery(Announcement.class);
    Root<Announcement> root = criteria.from(Announcement.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Announcement_.archived), archived));
    criteria.orderBy(criteriaBuilder.desc(root.get(Announcement_.startDate)));
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<Announcement> listByArchivedAndDateAndUserGroupEntityIdsAndPubliclyVisible(
      boolean archived,
      Date currentDate,
      List<Long> userGroupEntityIds,
      boolean publiclyVisible
  ) {
    currentDate = onlyDateFields(currentDate);
    if (userGroupEntityIds.isEmpty()) {
      return Collections.emptyList();
    }
    
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Announcement> criteria = criteriaBuilder.createQuery(Announcement.class);
    Root<AnnouncementUserGroup> root = criteria.from(AnnouncementUserGroup.class);
    Join<AnnouncementUserGroup, Announcement> announcement = root.join(AnnouncementUserGroup_.announcement);
    criteria.select(announcement);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.lessThanOrEqualTo(announcement.get(Announcement_.startDate), currentDate),
          criteriaBuilder.greaterThanOrEqualTo(announcement.get(Announcement_.endDate), currentDate),
          criteriaBuilder.equal(announcement.get(Announcement_.archived), archived),
          criteriaBuilder.equal(root.get(AnnouncementUserGroup_.archived), false),
          root.get(AnnouncementUserGroup_.userGroupEntityId).in(userGroupEntityIds),
          criteriaBuilder.equal(announcement.get(Announcement_.publiclyVisible), publiclyVisible)));
    criteria.orderBy(criteriaBuilder.desc(announcement.get(Announcement_.startDate)));
    
    return entityManager.createQuery(criteria).getResultList();
  }

  private Date onlyDateFields(Date currentDate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(currentDate);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    currentDate = cal.getTime();
    return currentDate;
  }
  
  public List<Announcement> listActive() {
    EntityManager entityManager = getEntityManager(); 
    Date currentDate = new Date();
    currentDate = onlyDateFields(currentDate);
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Announcement> criteria = criteriaBuilder.createQuery(Announcement.class);
    Root<Announcement> root = criteria.from(Announcement.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.lessThanOrEqualTo(root.get(Announcement_.startDate), currentDate),
        criteriaBuilder.greaterThanOrEqualTo(root.get(Announcement_.endDate), currentDate)
      )
    );
    criteria.orderBy(criteriaBuilder.desc(root.get(Announcement_.startDate)));
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<Announcement> listByArchivedAndDateAndPubliclyVisible(
      boolean archived,
      Date currentDate,
      boolean publiclyVisible
  ) {
    EntityManager entityManager = getEntityManager();
    currentDate = onlyDateFields(currentDate);
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Announcement> criteria = criteriaBuilder.createQuery(Announcement.class);
    Root<Announcement> root = criteria.from(Announcement.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.lessThanOrEqualTo(root.get(Announcement_.startDate), currentDate),
          criteriaBuilder.greaterThanOrEqualTo(root.get(Announcement_.endDate), currentDate),
          criteriaBuilder.equal(root.get(Announcement_.archived), archived),
          criteriaBuilder.equal(root.get(Announcement_.publiclyVisible), publiclyVisible)));
    criteria.orderBy(criteriaBuilder.desc(root.get(Announcement_.startDate)));
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<Announcement> listByArchivedAndDate(boolean archived, Date currentDate) {
    EntityManager entityManager = getEntityManager(); 
    currentDate = onlyDateFields(currentDate);
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Announcement> criteria = criteriaBuilder.createQuery(Announcement.class);
    Root<Announcement> root = criteria.from(Announcement.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Announcement_.archived), false),
        criteriaBuilder.lessThanOrEqualTo(root.get(Announcement_.startDate), currentDate),
        criteriaBuilder.greaterThanOrEqualTo(root.get(Announcement_.endDate), currentDate)));
    criteria.orderBy(criteriaBuilder.desc(root.get(Announcement_.startDate)));
    return entityManager.createQuery(criteria).getResultList();
  }

  public Announcement updateCaption(
      Announcement announcement,
      String caption
  ) {
    announcement.setCaption(caption);

    return persist(announcement);
  }

  public Announcement updateContent(
      Announcement announcement,
      String content
  ) {
    announcement.setContent(content);

    return persist(announcement);
  }

  public Announcement updateStartDate(
      Announcement announcement,
      Date startDate
  ) {
    announcement.setStartDate(startDate);

    return persist(announcement);
  }

  public Announcement updateEndDate(
      Announcement announcement,
      Date endDate
  ) {
    announcement.setEndDate(endDate);

    return persist(announcement);
  }

  public Announcement updatePubliclyVisible(
      Announcement announcement,
      boolean publiclyVisible
  ) {
    announcement.setPubliclyVisible(publiclyVisible);

    return persist(announcement);
  }
  
  public void delete(Announcement announcement) {
    super.delete(announcement);
  }
  
}
