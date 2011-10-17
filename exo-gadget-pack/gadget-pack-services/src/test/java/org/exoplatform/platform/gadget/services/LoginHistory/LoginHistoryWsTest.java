package org.exoplatform.platform.gadget.services.LoginHistory;

import java.util.Date;
import java.util.Random;

import org.exoplatform.platform.gadget.services.LoginHistory.LoginHistoryService;
import org.exoplatform.platform.gadget.services.test.GadgetServiceTestcase;
import org.exoplatform.services.rest.impl.ContainerResponse;

public class LoginHistoryWsTest extends GadgetServiceTestcase{
	@SuppressWarnings("deprecation")
	public void setUp(){
		super.setUp();
		LoginHistoryService loginHistoryService = getService(LoginHistoryService.class);
		try {
			loginHistoryService.addLoginHistoryEntry("john", new Date("Jul 27 2011 13:52:57").getTime());
			loginHistoryService.addLoginHistoryEntry("john", new Date("Aug 10 2011 08:42:39").getTime());
			loginHistoryService.addLoginHistoryEntry("john", new Date("Aug 18 2011 11:23:45").getTime());
			loginHistoryService.addLoginHistoryEntry("john", new Date("Aug 19 2011 07:27:34").getTime());
			loginHistoryService.addLoginHistoryEntry("john", new Date("Aug 20 2011 09:56:12").getTime());
			loginHistoryService.addLoginHistoryEntry("mary", new Date("Jul 21 2011 14:07:25").getTime());
			loginHistoryService.addLoginHistoryEntry("mary", new Date("Aug 24 2011 17:45:15").getTime());
		} catch (Exception e) {
			fail("Failed to setup LoginHistoryWsTest: " + e.getMessage());
		}
	}
	
	public void testLastLogins(){
		ContainerResponse cres;
		try {
			cres = launcher.service("GET", "/loginhistory/lastlogins/5/%25", "", null, null, null);
			assertEquals(200, cres.getStatus());
			assertNotNull(cres.getEntity());

			/*Object messageBean = cres.getEntity();
			Object data = messageBean.getClass().getMethod("getData", null).invoke(messageBean, null);
			Object bean = data.getClass().getMethod("get", new Class[]{Integer.TYPE}).invoke(data, new Integer[]{0});
			
			System.out.println(data);
			System.out.println(bean);*/
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	

	public void testLoginHistory(){
		ContainerResponse cres;
		try {
			cres = launcher.service("GET", "/loginhistory/loginhistory/john/" + new Date("Aug 15 2011").getTime() + "/" + new Date("Aug 20 2011").getTime(), "", null, null, null);
			assertEquals(200, cres.getStatus());
			assertNotNull(cres.getEntity());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	
	
	public void testWeekStats(){
		ContainerResponse cres;
		try {
			cres = launcher.service("GET", "/loginhistory/weekstats/john/2011-08-15", "", null, null, null);
			assertEquals(200, cres.getStatus());
			assertNotNull(cres.getEntity());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	public void testMonthStats(){
		ContainerResponse cres;
		try {
			cres = launcher.service("GET", "/loginhistory/monthstats/john/2011-08-01/3", "", null, null, null);
			assertEquals(200, cres.getStatus());
			assertNotNull(cres.getEntity());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	public void testYearStats(){
		ContainerResponse cres;
		try {
			cres = launcher.service("GET", "/loginhistory/yearstats/john/2011-01-01", "", null, null, null);
			assertEquals(200, cres.getStatus());
			assertNotNull(cres.getEntity());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}		
	
	private long randDate(long fromDate, long toDate) {
		return fromDate + (long)((toDate - fromDate + 1) * new Random().nextDouble());
	}	
}
