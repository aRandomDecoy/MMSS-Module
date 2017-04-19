                               
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

    public static void main(String args[]) throws InterruptedException {
	

	accHasChanged = false;
	Door1HasChanged = false;
	Motion1HasChanged = false;        
	System.out.println("DoorModule service started.");
	
    GpioController gpio = GpioFactory.getInstance();
	try{
	sc = new MMSS_ServerCommander("http://127.0.0.1:9999");
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



	
	myModule.name = "Front Door Sensor";
	myModule.type = "SensorModule";
	myModule.id = "1";
	myModule.mainServerID = "127.0.0.1:9999";
		
	myModule.parameterData.add("FrontDoorClosed");
	myModule.parameterData.add(doorSwitch.isLow());
	myModule.parameterData.add("MainMotionDetected");
	myModule.parameterData.add(motionSensor.isHigh());
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
			
		if( Door1HasChanged )
{
			logEntry.subjectType = "Door Switch";
			logEntry.data.add("Front Door");  
			if ( doorSwitch.isLow() ) 
			{
				logEntry.message = "The door has closed.";
				logEntry.data.add("Closed");
			} else 
			{
				logEntry.message = "The door has opened.";
				logEntry.data.add("Open");
			}
			Door1HasChanged = false;
}

			logEntry.data.add("Motion Sensor");

	
	
		if( Motion1HasChanged)
{
			logEntry.subjectType = "Motion Sensor";
			logEntry.data.add("Motion Sensor");  
			if ( motionSensor.isHigh() ) 
			{
				logEntry.message = "Movement Detected";
				logEntry.data.add("Movement Detected");
			} else 
			{
				logEntry.message = "No Movement Detected";
				logEntry.data.add("No Movement Detected");
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
