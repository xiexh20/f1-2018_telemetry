/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pi4jmultithread;
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
 * A project to test the thread-safety of pi4j I2C read & write
 * @author 25691
 */
public class Pi4jMultiThread {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        final byte PIC_ADDR = 0x18; // address of the pic
        final int FRAME_LEN = 10;   // length of a data frame
        final Console console = new Console();

        // print program title/header
        console.title("<-- Pi4j multithread test -->");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // fetch all available busses
        try {
            int[] ids = I2CFactory.getBusIds();
            console.println("Found follow I2C busses: " + Arrays.toString(ids));
        } catch (IOException exception) {
            console.println("I/O error during fetch of I2C busses occurred: "+exception.getMessage());
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
        I2CBus i2c;
        try {
            i2c = I2CFactory.getInstance(I2CBus.BUS_1);
            I2CDevice device = i2c.getDevice(PIC_ADDR);
        } catch (I2CFactory.UnsupportedBusNumberException ex) {
            Logger.getLogger(Pi4jMultiThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
