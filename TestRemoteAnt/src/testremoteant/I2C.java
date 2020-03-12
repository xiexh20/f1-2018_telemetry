/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testremoteant;

/**
 *
 * @author 13014
 */
import java.io.IOException;
import java.util.Arrays;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;
import com.pi4j.util.Console;

/**
 * This example code demonstrates how to perform simple I2C
 * communication on the BananaPro.  For this example we will
 * connect to a 'TSL2561' LUX sensor.
 *
 * Data Sheet:
 * https://www.adafruit.com/datasheets/TSL256x.pdf
 *
 * You should get something similar printed in the console
 * when executing this program:
 *
 * > <--Pi4J--> I2C Example ... started.
 * > ... reading ID register from TSL2561
 * > TSL2561 ID = 0x50 (should be 0x50)
 * > ... powering up TSL2561
 * > ... reading DATA registers from TSL2561
 * > TSL2561 DATA 0 = 0x1e
 * > TSL2561 DATA 1 = 0x04
 * > ... powering down TSL2561
 * > Exiting I2CExample
 *
 *
 * @author Robert Savage
 */
public class I2C {

    // TSL2561 I2C address
    public static final int PIC_ADDR = 0x20; // address pin not connected (FLOATING)


    /**
     * Program Main Entry Point
     *
     * @param args
     * @throws InterruptedException
     * @throws PlatformAlreadyAssignedException
     * @throws IOException
     * @throws UnsupportedBusNumberException
     */
    public static void main(String[] args) throws InterruptedException, PlatformAlreadyAssignedException, IOException, UnsupportedBusNumberException {

        // ####################################################################
        //
        // since we are not using the default Raspberry Pi platform, we should
        // explicitly assign the platform as the BananaPro platform.
        //
        // ####################################################################


        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate code)
        final Console console = new Console();

        // print program title/header
        console.title("<-- The Pi4J Project -->", "I2C Example");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

         // fetch all available busses
        try {
            int[] ids = I2CFactory.getBusIds();
            console.println("Found follow I2C busses: " + Arrays.toString(ids));
        } catch (IOException exception) {
            console.println("I/O error during fetch of I2C busses occurred");
        }

        // find available busses
        for (int number = I2CBus.BUS_0; number <= I2CBus.BUS_17; ++number) {
            try {
                @SuppressWarnings("unused")
                I2CBus bus = I2CFactory.getInstance(number);
                console.println("Supported I2C bus " + number + " found");

            } catch (IOException exception) {
                console.println("I/O error on I2C bus " + number + " occurred");
            } catch (I2CFactory.UnsupportedBusNumberException exception) {
                console.println("Unsupported I2C bus " + number + " required");
            }
        }
        // get the I2C bus to communicate on
        I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1);
       

        // create an I2C device for an individual device on the bus that you want to communicate with
        // in this example we will use the default address for the TSL2561 chip which is 0x39.
        I2CDevice device = i2c.getDevice(PIC_ADDR);

        // next, lets perform am I2C READ operation to the TSL2561 chip
        // we will read the 'ID' register from the chip to get its part number and silicon revision number
        console.println("... reading ID register from PIC");
        byte data1 = 1;

        
        while(true){
            //send a frame to device 1
            console.println("###########################################");
            for(int i=0;i<10;i++){
                console.println("Sending to device1: data[" +i +"]=" + String.format("0x%02x", data1));
                device.write(data1);
                
            }
            //read a frame from device 1
            console.println("-----------------------------------------");
            for(int i=0;i<10;i++){
                int recv = device.read();
                console.println("Receive from device1: data[" +i +"]=" + String.format("0x%02x", recv));
            }
            Thread.sleep(100);
        }
    }
}