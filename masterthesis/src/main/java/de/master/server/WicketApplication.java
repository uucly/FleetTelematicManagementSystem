package de.master.server;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 * 
 * @see de.master.masterthesis.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return Index.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
		
		mountResource("/vehicleDevice", new ResourceReference("restReference"){
			VehicleDeviceResource vehicelResource = new VehicleDeviceResource();
			@Override
			public IResource getResource() {
				return vehicelResource;
			}
		});
		
		mountResource("/vehicleOrder", new ResourceReference("restReference"){
			VehicleOrderResource vehicelOrderResource = new VehicleOrderResource();
			@Override
			public IResource getResource() {
				return vehicelOrderResource;
			}
		});
	}
}
