package fi.muikku.plugins.material.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.coops.CoOpsApi;
import fi.foyt.coops.CoOpsForbiddenException;
import fi.foyt.coops.CoOpsInternalErrorException;
import fi.foyt.coops.CoOpsNotFoundException;
import fi.foyt.coops.CoOpsNotImplementedException;
import fi.foyt.coops.CoOpsUsageException;
import fi.foyt.coops.model.File;
import fi.muikku.model.users.EnvironmentRoleArchetype;
import fi.muikku.model.users.EnvironmentUser;
import fi.muikku.model.users.UserEntity;
import fi.muikku.plugin.PluginRESTService;
import fi.muikku.plugins.material.HtmlMaterialController;
import fi.muikku.plugins.material.model.HtmlMaterial;
import fi.muikku.plugins.workspace.WorkspaceMaterialContainsAnswersExeption;
import fi.muikku.rest.RESTPermitUnimplemented;
import fi.muikku.session.SessionController;
import fi.muikku.users.EnvironmentUserController;

@RequestScoped
@Path("/materials/html")
@Stateful
@Produces("application/json")
public class HtmlMaterialRESTService extends PluginRESTService {
  
  private static final long serialVersionUID = 5678403648328971273L;

  @Inject
  private HtmlMaterialController htmlMaterialController;

  @Inject
  private CoOpsApi coOpsApi;
  
  @Inject
  private SessionController muikkuSessionController;
  
  @Inject
  private EnvironmentUserController environmentUserController;
  
  private boolean isAuthorized() {
      if (!muikkuSessionController.isLoggedIn()) {
        return false;
      }
      
      UserEntity userEntity = muikkuSessionController.getLoggedUserEntity();
      
      EnvironmentUser environmentUser = environmentUserController.findEnvironmentUserByUserEntity(userEntity);
      
      if (environmentUser.getRole() == null || environmentUser.getRole().getArchetype() == EnvironmentRoleArchetype.STUDENT) {
        return false;
      }
      
      return true;
  }

  @POST
  @Path("/")
  @RESTPermitUnimplemented
  public Response createMaterial(HtmlRestMaterial entity) {
    
    if (!isAuthorized()) {
      return Response.status(Status.FORBIDDEN).entity("Permission denied").build();
    }

    if (StringUtils.isBlank(entity.getContentType())) {
      return Response.status(Status.BAD_REQUEST).entity("contentType is missing").build();
    }
    
    if (StringUtils.isBlank(entity.getTitle())) {
      return Response.status(Status.BAD_REQUEST).entity("title is missing").build();
    }
    
    HtmlMaterial htmlMaterial = htmlMaterialController.createHtmlMaterial(entity.getTitle(), entity.getHtml(), entity.getContentType(), 0l);
    return Response.ok(createRestModel(htmlMaterial)).build();
  }

  @GET
  @Path("/{id}")
  @RESTPermitUnimplemented
  public Response findMaterial(@PathParam("id") Long id, @QueryParam ("revision") Long revision, @Context Request request) {
    HtmlMaterial htmlMaterial = htmlMaterialController.findHtmlMaterialById(id);
    if (htmlMaterial == null) {
      return Response.status(Status.NOT_FOUND).build();
    } else {
      EntityTag tag = new EntityTag(DigestUtils.md5Hex(String.valueOf(revision == null ? htmlMaterial.getRevisionNumber() : revision)));
      ResponseBuilder builder = request.evaluatePreconditions(tag);
      if (builder != null) {
        return builder.build();
      }
      
      CacheControl cacheControl = new CacheControl();
      cacheControl.setMustRevalidate(true);
      
      if (revision == null) {
        return Response.ok(createRestModel(htmlMaterial)).build();
      } else {
        File fileRevision;
        try {
          fileRevision = coOpsApi.fileGet(id.toString(), revision);
        } catch (CoOpsNotImplementedException | CoOpsNotFoundException | CoOpsUsageException | CoOpsInternalErrorException | CoOpsForbiddenException e) {
          return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        if (fileRevision == null) {
          return Response.status(Status.NOT_FOUND).build();
        }
        
        return Response.ok(new HtmlRestMaterial(htmlMaterial.getId(), htmlMaterial.getTitle(), htmlMaterial.getContentType(), fileRevision.getContent(), fileRevision.getRevisionNumber(), htmlMaterial.getRevisionNumber())).build();
      }
    }
  }
  
  @POST
  @Path("/{id}/publish/")
  @RESTPermitUnimplemented
  public Response publishMaterial(@PathParam("id") Long id, HtmlRestMaterialPublish entity) {
    
    if (!isAuthorized()) {
      return Response.status(Status.FORBIDDEN).entity("Permission denied").build();
    }
    HtmlMaterial htmlMaterial = htmlMaterialController.findHtmlMaterialById(id);
    if (htmlMaterial == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    if (!htmlMaterial.getRevisionNumber().equals(entity.getFromRevision())) {
      return Response.status(Status.CONFLICT)
          .entity(new HtmlRestMaterialPublishError(HtmlRestMaterialPublishError.Reason.CONCURRENT_MODIFICATIONS)).build();
    }

    try {
      File fileRevision = coOpsApi.fileGet(id.toString(), entity.getToRevision());
      if (fileRevision == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      
      htmlMaterialController.updateHtmlMaterialToRevision(htmlMaterial, fileRevision.getContent(), entity.getToRevision(), false, entity.getRemoveAnswers() != null ? entity.getRemoveAnswers() : false);
    } catch (WorkspaceMaterialContainsAnswersExeption e) {
      return Response.status(Status.CONFLICT)
          .entity(new HtmlRestMaterialPublishError(HtmlRestMaterialPublishError.Reason.CONTAINS_ANSWERS)).build();
    } catch (CoOpsNotImplementedException | CoOpsNotFoundException | CoOpsUsageException | CoOpsInternalErrorException | CoOpsForbiddenException e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
    
    return Response.noContent().build();
  }
  
  @PUT
  @Path("/{id}/revert/")
  @RESTPermitUnimplemented
  public Response revertMaterial(@PathParam("id") Long id, HtmlRestMaterialRevert entity) {
    
    if (!isAuthorized()) {
      return Response.status(Status.FORBIDDEN).entity("Permission denied").build();
    }
    HtmlMaterial htmlMaterial = htmlMaterialController.findHtmlMaterialById(id);
    if (htmlMaterial == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    Long currentRevision = htmlMaterialController.lastHtmlMaterialRevision(htmlMaterial);
    if (!currentRevision.equals(entity.getFromRevision())) {
      return Response.status(Status.CONFLICT)
          .entity(new HtmlRestMaterialPublishError(HtmlRestMaterialPublishError.Reason.CONCURRENT_MODIFICATIONS)).build();
    }

    try {
      File fileRevision = coOpsApi.fileGet(id.toString(), entity.getToRevision());
      if (fileRevision == null) {
        return Response.status(Status.NOT_FOUND).entity("Specified revision could not be found").build(); 
      }
      
      htmlMaterialController.updateHtmlMaterialToRevision(htmlMaterial, fileRevision.getContent(), entity.getToRevision(), true, entity.getRemoveAnswers() != null ? entity.getRemoveAnswers() : false);
    } catch (WorkspaceMaterialContainsAnswersExeption e) {
      return Response.status(Status.CONFLICT)
          .entity(new HtmlRestMaterialPublishError(HtmlRestMaterialPublishError.Reason.CONTAINS_ANSWERS)).build();
    } catch (CoOpsNotImplementedException | CoOpsNotFoundException | CoOpsUsageException | CoOpsInternalErrorException | CoOpsForbiddenException e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
    
    return Response.noContent().build();
  }
  
  private HtmlRestMaterial createRestModel(HtmlMaterial htmlMaterial) {
    Long currentRevision = htmlMaterialController.lastHtmlMaterialRevision(htmlMaterial);
    if (currentRevision == null) {
      currentRevision = 0l;
    }
    
    return new HtmlRestMaterial(htmlMaterial.getId(),
                                htmlMaterial.getTitle(),
                                htmlMaterial.getContentType(),
                                htmlMaterial.getHtml(),
                                currentRevision,
                                htmlMaterial.getRevisionNumber());
  }
  
}
