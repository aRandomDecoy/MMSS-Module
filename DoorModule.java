
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;



import java.util.*;

/**
 * 
 * 
 * Modified from ListenGpioExample.java by  Robert Savage
 */


public class DoorModule2 {

    static boolean hasChanged;

    public static void main(String args[]) throws InterruptedException {
	

	hasChanged = false;        
	System.out.println("DoorModule service started.");

        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalInput doorSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP);

	final GpioPinDigitalInput motionSensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.PULL_UP);

	final GpioPinDigitalOutput ledpin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);	

	ledpin.setShutdownOptions(true, PinState.LOW);
	ledpin.low();
	
        doorSwitch.setShutdownOptions(true);

        doorSwitch.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                
		ledpin.toggle();
		hasChanged = true;
            }

        });

	motionSensor.setShutdownOptions(true);

        motionSensor.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                
		ledpin.toggle();
		hasChanged = true;
            }

        });


	

        // keep program running until user aborts (CTRL-C)
        while(true) {

            Thread.sleep(500);
	    if ( hasChanged ) {

		PassableModule myModule = new PassableModule();
		myModule.name = "Front Door Sensor";
		myModule.type = "SensorModule";
		myModule.id = "1";
		myModule.mainServerID = "127.0.0.1:80";
		myModule.parameterData.add("DoorClosed");
		myModule.parameterData.add(doorSwitch.isLow());
		myModule.parameterData.add("MotionDetected");
		myModule.parameterData.add(doorSwitch.isLow());
try{
		System.out.println(myModule.toJSON());
} catch ( Exception e) { 	System.out.println("something goofed");}	
		//replace this statement with crateing a json string sensor values
		// 
		// then send that json string to server
                if ( doorSwitch.isLow() ) 
		{
			System.out.println("The door is closed"); 
		} else 
		{
			System.out.println("The door is open");
		}

                if ( motionSensor.isHigh() ) 
		{
			System.out.println("Motion was detected"); 
		} else 
		{
			System.out.println("No Motion was detected");
		}

		hasChanged = false;
	    }
	} 
		

    }
}