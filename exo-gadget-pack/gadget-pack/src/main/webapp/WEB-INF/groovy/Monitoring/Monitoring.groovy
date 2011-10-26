import java.math.MathContext

import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.CacheControl
import javax.ws.rs.core.UriInfo
import javax.ws.rs.Produces

import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory as JmxFactory
import javax.management.remote.JMXServiceURL as JmxUrl

@Path("/monitoring")
@Produces("application/json")
public class Monitoring {
  @GET
  @Path("memory")
  public Response memory() {
    def memoryInfo = ManagementFactory.getMemoryMXBean()
    
    ArrayList liste = new ArrayList()

    def heapMemoryUsage = memoryInfo.getHeapMemoryUsage()
    def datasHeap = new HashMap()
    datasHeap.put 'type', "heap"
    datasHeap.put 'init', heapMemoryUsage.init
    datasHeap.put 'max', heapMemoryUsage.max
    datasHeap.put 'used', heapMemoryUsage.used
    datasHeap.put 'commited', heapMemoryUsage.committed
    liste.add datasHeap

    def nonHeapMemoryUsage = memoryInfo.getNonHeapMemoryUsage()
    def datasNonHeap = new HashMap()
    datasNonHeap.put 'type', "non-heap"
    datasNonHeap.put 'init', nonHeapMemoryUsage.init
    datasNonHeap.put 'max', nonHeapMemoryUsage.max
    datasNonHeap.put 'used', nonHeapMemoryUsage.used
    datasNonHeap.put 'commited', nonHeapMemoryUsage.committed
    liste.add datasNonHeap

    return renderJSON(liste)
  }

  @GET
  @Path("caches")
  public Response caches() {
    def server = ManagementFactory.getPlatformMBeanServer()
    def query = new ObjectName('exo:*')    

    String[] allNames = server.queryNames(query, null)
    def liste = new ArrayList()

    def mBeans = allNames.findAll{ name -> name.contains(',service=cache,') }.collect{ new GroovyMBean(server, it) }

    mBeans.each{
      try{  
        def datas = new HashMap()
        int hitCount = it.getCacheHit()
        int missCount = it.getCacheMiss()
        int totalCount = hitCount + missCount
        int capacity = it.getMaxSize()
        int used = it.getCacheSize()
        
        datas.put 'name', it.getName()
        datas.put 'capacity', capacity
        datas.put 'ttl', it.getLiveTime()
        datas.put 'callCount', hitCount + missCount
        datas.put 'hitCount', hitCount
        datas.put 'hitCountPercentage', totalCount == 0 ? 0 : hitCount * 100G / totalCount as float
        datas.put 'missCount', missCount
        datas.put 'missCountPercentage', totalCount == 0 ? 0 : missCount * 100G / totalCount as float
        datas.put 'capacityUsed', used
        datas.put 'capacityFree', capacity - used
        datas.put 'capacityUsedPercentage', used * 100G / capacity as float
        datas.put 'capacityFreePercentage', (capacity - used) * 100G / capacity as float
        liste.add datas
       } catch(Exception e) {
         //TODO: log
       }
    }
    
    return renderJSON(liste)
  }

  @GET
  @Path("jbcaches")
  public Response jbCaches() {
    def server = ManagementFactory.getPlatformMBeanServer()
    def query = new ObjectName('jboss.cache:*')    

    String[] allNames = server.queryNames(query, null)
    def liste = new ArrayList()

    def mBeans = allNames.findAll{ name -> name.contains('service=JBossCache,uniqueId=') }.collect{ new GroovyMBean(server, it) }

    def datas = new HashMap()
    mBeans.each{   
      /*
      def datas = new HashMap()
      int hitCount = it.getProperty('HitCount')
      int missCount = it.getProperty('MissCount')
      int totalCount = hitCount + missCount
      int capacity = it.getProperty('Capacity')
      int used = it.getProperty('Size')
      
      datas.put 'name', it.getProperty('Name')
      datas.put 'capacity', it.getProperty('Capacity')
      datas.put 'ttl', it.getProperty('TimeToLive')
      datas.put 'callCount', hitCount + missCount
      datas.put 'hitCount', hitCount
      datas.put 'hitCountPercentage', totalCount == 0 ? 0 : hitCount * 100G / totalCount as float
      datas.put 'missCount', missCount
      datas.put 'missCountPercentage', totalCount == 0 ? 0 : missCount * 100G / totalCount as float
      datas.put 'capacityUsed', it.getProperty('Size')
      datas.put 'capacityFree', capacity - it.getProperty('Size')
      datas.put 'capacityUsedPercentage', used * 100G / capacity as float
      datas.put 'capacityFreePercentage', (capacity - used) * 100G / capacity as float
      */
      //datas.put 'name', it.listAttributeNames() 
      //datas.put 'name', it.info().description
      //datas.put 'name', it.name()
      //datas.put 'MBeanName', it.name().canonicalName 
      def name = it.name().keyPropertyList['uniqueId']
      def datasCache
      if (datas.containsKey(name)) {
        datasCache = datas.get(name)
      } else {
        datasCache = new HashMap()
        datasCache.put 'name', name
        datasCache.put 'service' , it.name().keyPropertyList['service']
        
        datas.put name, datasCache 
      }

      def jmxResource = it.name().keyPropertyList['jmx-resource']
      if ('CacheMgmtInterceptor'.equals(jmxResource)) {
        datasCache.put 'hits', it.Hits
        datasCache.put 'misses', it.Misses
        datasCache.put 'hitMissRatio', it.HitMissRatio
        datasCache.put 'readWriteRatio', it.ReadWriteRatio
        datasCache.put 'stores', it.Stores
        datasCache.put 'attributes', it.NumberOfAttributes
        datasCache.put 'nodes', it.NumberOfNodes
      }
      
      //datas.put 'service', it.name().keyPropertyList['service']
      //datas.put 'type', it.name().keyPropertyList['jmx-resource']
      //liste.add datas
    }
    
    for (Map cache : datas.values()) {
        liste.add cache
    }
    
    return renderJSON(liste)
  }

  @GET
  @Path("jcr/session-registry")
  public Response jcrSessionRegistry() {
    def server = ManagementFactory.getPlatformMBeanServer()
    def query = new ObjectName('exo:*')    

    String[] allNames = server.queryNames(query, null)
    def liste = new ArrayList()

    def mBeans = allNames.findAll{ name -> name.contains(',service=SessionRegistry,') }.collect{ new GroovyMBean(server, it) }

    mBeans.each{  
      def datas = new HashMap()
      int registrySize = it.getProperty('Size')
      int timeout = it.getProperty('TimeOut')
      
      datas.put 'name', it.name().keyPropertyList['repository'].replaceAll('"', '')
      datas.put 'mbean', it.name().canonicalName
      datas.put 'size', registrySize
      datas.put 'timeout', timeout
      liste.add datas
    }
    
    return renderJSON(liste)
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

