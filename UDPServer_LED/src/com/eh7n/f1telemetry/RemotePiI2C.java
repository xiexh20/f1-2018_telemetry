/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package remotepii2c;
package com.eh7n.f1telemetry;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.util.Console;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 25691
 */
public class RemotePiI2C {

    /**
     * @param args the command line arguments
     */
    private static final byte PIC1_ADDR = 0x20;       // address of the PIC
    private static final byte PIC2_ADDR = 0x20;      // address of the second PIC

    private static final int FRAME_LEN = 10;      // the length of a frame
    private final static int MAXADCRESULT = 956;
    private final static int MINADCRESULT = 40;

    private I2CDevice device;

    /**
     * construction
     * @throws com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException 
     */
    public RemotePiI2C() throws I2CFactory.UnsupportedBusNumberException {
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
//                console.println("Supported I2C bus " + number + " found");

            } catch (IOException exception) {
                console.println("I/O error on I2C bus " + number + " occurred");
            } catch (I2CFactory.UnsupportedBusNumberException exception) {
                console.println("Unsupported I2C bus " + number + " required");
            }
        }

        // get the I2C bus to communicate on
        I2CBus i2c;
        try {
            i2c = I2CFactory.getInstance(I2CBus.BUS_1);
            this.device = i2c.getDevice(PIC1_ADDR);

        } catch (IOException ex) {
            Logger.getLogger(RemotePiI2C.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void sendByte(byte data)
    {
        try {
            this.device.write(data);
        } catch (IOException ex) {
            Logger.getLogger(RemotePiI2C.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int readByte()
    {
        try {
            return this.device.read();
        } catch (IOException ex) {
            Logger.getLogger(RemotePiI2C.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

//    public static RemotePiI2C getInstance() throws I2CFactory.UnsupportedBusNumberException{
//        
//    }
    /**
     * Program Main Entry Point
     *
     * @param args
     * @throws InterruptedException
     * @throws PlatformAlreadyAssignedException
     * @throws IOException
     * @throws I2CFactory.UnsupportedBusNumberException
     */
    public static void RunI2C() throws InterruptedException, PlatformAlreadyAssignedException, IOException, I2CFactory.UnsupportedBusNumberException {

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
//                console.println("Supported I2C bus " + number + " found");

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
//        I2CDevice device = i2c.getDevice(TSL2561_ADDR);
        I2CDevice device = i2c.getDevice(PIC1_ADDR);
        I2CDevice device2 = i2c.getDevice(PIC2_ADDR);
        //TODO: get address automatically

        // next, lets perform am I2C READ operation to the TSL2561 chip
        // we will read the 'ID' register from the chip to get its part number and silicon revision number
//        console.println("... reading ID register from TSL2561");
//        int response = device.read(TSL2561_REG_ID);
//        console.println("TSL2561 ID = " + String.format("0x%02x", response) + " (should be 0x50)");
        // next we want to start taking light measurements, so we need to power up the sensor
//        console.println("... powering up TSL2561");
        byte data1 = 0;
        byte data2 = 0b1111111;

        int[] recv_buf = new int[FRAME_LEN];

//        byte recv = 0;      // received data
//        while(true){
        // send 2 frames to PIC
//            for(int k= 0; k<3; k++) {
//                for (int i = 0; i < FRAME_LEN; i++) {
//                    console.println("########################################");
//                    console.println("Sending data[" + i + "] to PIC1: " + String.format("0x%02x", data1));
//                    device.write(data1);
//                    data1++;
//                    Thread.sleep(300);
//                }
//            }
        // read one frame from PIC
        for (int i = 0; i < FRAME_LEN; i++) {
            recv_buf[i] = device.read();
//                int recv1 = 
//                if(i==0){
//                    console.println("ADCRESH = "+ String.format("0x%02x", recv1));
//                }
//                else if(i==1){
//                    console.println("ADCRESL = "+ String.format("0x%02x", recv1));
//                    console.println("######################################");
//                }
            Thread.sleep(1);
        }

        int ADCresult = 4 * recv_buf[0] + recv_buf[1] / 64;
        double degree = 135 - 270.0 * (ADCresult - MINADCRESULT) / (MAXADCRESULT - MINADCRESULT);
        console.println("ADC result = " + ADCresult + ", Degree = " + degree);
    }
//    }

}
