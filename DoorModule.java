                               
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;



import java.util.*;

/**
 * 
 * 
 * Modified from ListenGpioExample.java by  Robert Savage
 */


public class DoorModule {

    static boolean accHasChanged;
	static boolean Door1HasChanged;
	static boolean Motion1HasChanged;

	static MMSS_ServerCommander sc;
static String server;
    public static void main(String args[]) throws InterruptedException {
for(int i =0; i<args.length; ++i)
{
	if ( args[i].equals("-d"))
{
server = args[i+1];	
}



}	

	accHasChanged = false;
	Door1HasChanged = false;
	Motion1HasChanged = false;        
	System.out.println("DoorModule service started.");
	
    GpioController gpio = GpioFactory.getInstance();
	try{
	sc = new MMSS_ServerCommander("http://" + server );  //"http://192.168.43.53:8081");
}
catch(Exception e)
{
System.out.println(e);
}


    GpioPinDigitalInput doorSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP);

	GpioPinDigitalInput motionSensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01);

	doorSwitch.setShutdownOptions(true);

    doorSwitch.addListener(new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println("Door has changed");
				accHasChanged = true;
				Door1HasChanged = true;
				
            }
    });

    motionSensor.addListener(new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
			System.out.println("Motion sensor has changed");
			accHasChanged = true;
			Motion1HasChanged = true;
        }
	});

	PassableModule myModule = new PassableModule();



	
	myModule.name = "FrontDoorandMotion";
	myModule.type = "SensorModule";
	myModule.id = "1599";
	myModule.mainServerID = server; // "192.168.43.53:8081";
		
	myModule.parameterData.add("FrontDoor");
	myModule.parameterData.add("FamilyRoomMotion");
	myModule.isBeingListened = true;
	try
	{
		System.out.println(myModule.toJSON());
		sc.addModule(myModule);
	} 
	catch ( Exception e) 
	{ 	
		System.out.println( e);
	}

	
	while(true) {

        Thread.sleep(500);

		if ( accHasChanged ) {
			PassableLog logEntry = new PassableLog();
						
			logEntry.time = new ServerDate();

			logEntry.authorInfo = new PassableShortInfo("1599","module");
			
		if( Door1HasChanged )
{

			logEntry.subjectType = "Door Switch";
			logEntry.data.add("Front Door");  
			if ( doorSwitch.isLow() ) 
			{
				logEntry.message = "Front door closed";
				logEntry.data.add("Closed");
			} else 
			{
				logEntry.message = "Front door opened";
				logEntry.data.add("Open");
			}
			Door1HasChanged = false;
}


	
	
		if( Motion1HasChanged)
{
			logEntry.subjectType = "Motion Sensor";
			logEntry.data.add("Motion Sensor");  
			if ( motionSensor.isHigh() ) 
			{
				logEntry.message = "Movement detected";
				logEntry.data.add("Movement detected");
			} else 
			{
				logEntry.message = "Movement ceased";
				logEntry.data.add("Movement ceased");
			}
Motion1HasChanged = false;
}	
			try
			{
				System.out.println(logEntry.toJSON());
				sc.addModuleLog(logEntry);
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
			accHasChanged = false;
			}
		} 
	}
}
