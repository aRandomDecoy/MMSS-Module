                               
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

    static boolean hasChanged;


    public static void main(String args[]) throws InterruptedException {
	

	hasChanged = false;        
	System.out.println("DoorModule service started.");

        GpioController gpio = GpioFactory.getInstance();

        GpioPinDigitalInput doorSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP);

	GpioPinDigitalInput motionSensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01);//, PinPullResistance.PULL_UP);

	//foo();
	
        doorSwitch.setShutdownOptions(true);

        doorSwitch.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                
	//	ledpin.toggle();
		hasChanged = true;
            }

        });

        motionSensor.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                
	//	ledpin.toggle();
		System.out.println("Motion sensor has changed");
		hasChanged = true;
            }
});

		PassableModule myModule = new PassableModule();



	
		myModule.name = "Front Door Sensor";
		myModule.type = "SensorModule";
		myModule.id = "1";
		myModule.mainServerID = "127.0.0.1:9999";
		
		myModule.parameterData.add("DoorClosed");
		myModule.parameterData.add(doorSwitch.isLow());
		myModule.parameterData.add("MotionDetected");
		myModule.parameterData.add(motionSensor.isLow());
		myModule.isBeingListened = true;
try{
		System.out.println(myModule.toJSON());
		ConnectionExample.send("HTTP://localhost:9999/module/add", myModule.toJSON());
} catch ( Exception e) { 	System.out.println( e);}

	

        // keep program running until user aborts (CTRL-C)
        while(true) {

            Thread.sleep(500);

	    if ( hasChanged ) {

		//System.out.println("Motion sensor has changed");
		PassableLog logEntry = new PassableLog();
		//logEntry.author = new PassableShortInfo("Module1","FrontDoor");
		logEntry.subjectType = "Door Switch";
		logEntry.data.add("Front Door");  
		logEntry.time = new ServerDate();
		//logEntry.data.add(motionSensor.isLow());              
		if ( doorSwitch.isLow() ) 
		{
			logEntry.message = "The door has closed.";
			logEntry.data.add("Closed");
		} else 
		{
			logEntry.message = "The door has opened.";
			logEntry.data.add("Open");
		}
		if ( motionSensor.isHigh() ) 
		{
			System.out.println("Motion sensor is high");
		
		} else 
		{
			System.out.println("Motion sensor is low");
		}
		
		

		try{

			System.out.println(logEntry.toJSON());
			ConnectionExample.send("HTTP://localhost:9999/module/log", logEntry.toJSON());
		}catch(Exception e){
			System.out.println("Problem with log entry.");
		}




		hasChanged = false;
	    }
	} 
		

    }
/* 
public static void foo()
{
        GpioController gpio = GpioFactory.getInstance();
	GpioPinDigitalInput motionSensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.PULL_UP);
		motionSensor.setShutdownOptions(true);

        motionSensor.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                
	//	ledpin.toggle();
		hasChanged = true;
            }

        });

}
*/
}
