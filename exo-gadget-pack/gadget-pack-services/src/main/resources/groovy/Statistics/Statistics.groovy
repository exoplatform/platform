import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.CacheControl
import javax.ws.rs.Produces

import org.exoplatform.container.ExoContainerContext

import org.exoplatform.groovyscript.text.TemplateStatisticService
import org.exoplatform.groovyscript.text.TemplateStatistic
import org.exoplatform.portal.application.ApplicationStatisticService
import org.exoplatform.portal.application.ApplicationStatistic


import MessageBean

@Path("/statistics")
public class Statistics {

  @GET
  @Path("application/slowest")
  @Produces("application/json")
  public Response applicationSlowest() {
    ApplicationStatisticService applicationStatisticService = (ApplicationStatisticService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ApplicationStatisticService.class);
    String[] applications = applicationStatisticService.getSlowestApplications();

    ArrayList liste = makeDataApplications(applications, applicationStatisticService)
    return renderJSON(liste)
  }

  @GET
  @Path("template/slowest")
  @Produces("application/json")
  public Response templateSlowest() {
    TemplateStatisticService templateStatisticService = (TemplateStatisticService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(TemplateStatisticService.class);
    String[] templates = templateStatisticService.getSlowestTemplates();

    ArrayList liste = makeDataTemplates(templates, templateStatisticService)
    return renderJSON(liste)
  }

  @GET
  @Path("application/all")
  @Produces("application/json")
  public Response applicationAll() {
    ApplicationStatisticService applicationStatisticService = (ApplicationStatisticService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ApplicationStatisticService.class);
    String[] applications = applicationStatisticService.getApplicationList();

    ArrayList liste = makeDataApplications(applications, applicationStatisticService)
    return renderJSON(liste)
  }
  @GET
  @Path("template/all")
  @Produces("application/json")
  public Response templateAll() {
    TemplateStatisticService templateStatisticService = (TemplateStatisticService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(TemplateStatisticService.class);
    String[] templates = templateStatisticService.getTemplateList();
    ArrayList liste = makeDataTemplates(templates, templateStatisticService)
    return renderJSON(liste)
  }
  
  @GET
  @Path("application/fastest")
  @Produces("application/json")
  public Response applicationFastest() {
    ApplicationStatisticService applicationStatisticService = (ApplicationStatisticService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ApplicationStatisticService.class);
    String[] applications = applicationStatisticService.getFastestApplications();

    ArrayList liste = makeDataApplications(applications, applicationStatisticService)
    return renderJSON(liste)
  }

  @GET
  @Path("template/fastest")
  @Produces("application/json")
  public Response templateFastest() {
    TemplateStatisticService templateStatisticService = (TemplateStatisticService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(TemplateStatisticService.class);
    String[] templates = templateStatisticService.getFastestTemplates();
    ArrayList liste = makeDataTemplates(templates, templateStatisticService)
    return renderJSON(liste)
  }
  
  @GET
  @Path("application/most-executed")
  @Produces("application/json")
  public Response applicationMostExecuted() {
    ApplicationStatisticService applicationStatisticService = (ApplicationStatisticService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ApplicationStatisticService.class);
    String[] applications = applicationStatisticService.getMostExecutedApplications();

    ArrayList liste = makeDataApplications(applications, applicationStatisticService)
    return renderJSON(liste)
  }

  @GET
  @Path("template/most-executed")
  @Produces("application/json")
  public Response templateMostExecuted() {
    TemplateStatisticService templateStatisticService = (TemplateStatisticService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(TemplateStatisticService.class);
    String[] templates = templateStatisticService.getMostExecutedTemplates();
    ArrayList liste = makeDataTemplates(templates, templateStatisticService)
    return renderJSON(liste)
  }

  /**
   * TODO: To Describe
   */
  private ArrayList makeDataTemplates(String[] templates, TemplateStatisticService templateStatisticService) {
    ArrayList liste = new ArrayList()
    templates.each{
        def templateStatistics = templateStatisticService.getTemplateStatistic(it)
        def datasTemplate = new HashMap()
        datasTemplate.put 'name', it
        datasTemplate.put 'timeUnit', 'ms'
        datasTemplate.put 'minTime', templateStatistics.minTime
        datasTemplate.put 'maxTime', templateStatistics.maxTime
        datasTemplate.put 'averageTime', templateStatistics.averageTime
        datasTemplate.put 'execution', templateStatistics.executionCount()
        
        liste.add datasTemplate
    }
    return liste
  }
  /**
   * TODO: To Describe
   */
  private ArrayList makeDataApplications(String[] applications, ApplicationStatisticService applicationStatisticService) {
    ArrayList liste = new ArrayList()
    applications.each{
        def applicationStatistics = applicationStatisticService.getApplicationStatistic(it)
        def datas = new HashMap()
        datas.put 'name', it
        datas.put 'timeUnit', 'ms'
        datas.put 'minTime', applicationStatistics.minTime
        datas.put 'maxTime', applicationStatistics.maxTime
        datas.put 'averageTime', applicationStatistics.averageTime
        datas.put 'throughput', applicationStatistics.throughput
        datas.put 'execution', applicationStatistics.executionCount()
        
        liste.add datas
    }
    return liste
  }
  /**
   * Render the response with JSON format
   */
  private Response renderJSON(List<Object> liste) {
    CacheControl cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    MessageBean data = new MessageBean();
    data.setData(liste);
    return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
  }
  }
  
    
    public class MessageBean {
      private List<Object> data;
      
      public void setData(List<Object> list) {
        this.data = list;
      }
      public List<Object> getData() {
        return data;
      }
      
    }
    
    