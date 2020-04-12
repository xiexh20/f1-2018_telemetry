/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor
 * test the maximum speed of I2C, with two devices
 */
package remotepii2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.util.Console;

import java.io.IOException;
import java.util.Arrays;

import java.io.IOException; 
import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.net.InetAddress; 
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 *
 * @author 25691
 */
public class RemotePiI2C {

    /**
     * @param args the command line arguments
     */
    
    private static final byte PIC1_ADDR = 0x18;       // address of the PIC
    private static final byte PIC2_ADDR = 0x20;      // address of the second PIC

    private static final int FRAME_LEN = 10;      // the length of a frame
    private final static int MAXADCRESULT = 956;
    private final static int MINADCRESULT = 40;
    
    static final String SERVER_IP = "192.168.1.10";
    static final int SERVER_PORT = 5200;

    /**
     * Program Main Entry Point
     *
     * @param args
     * @throws InterruptedException
     * @throws PlatformAlreadyAssignedException
     * @throws IOException
     * @throws I2CFactory.UnsupportedBusNumberException
     */
    public static void main(String[] args) throws InterruptedException, PlatformAlreadyAssignedException, IOException, I2CFactory.UnsupportedBusNumberException {

        
        // UDP client setup
        DatagramSocket ds = new DatagramSocket(); 
        InetSocketAddress address = new InetSocketAddress(SERVER_IP, SERVER_PORT);
        InetAddress ip = address.getAddress();
        System.out.println("Server socket: "+ip.toString()+":"+address.getPort());
        byte buf[] = null; 
        
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
//        I2CDevice device = i2c.getDevice(TSL2561_ADDR);
        I2CDevice device = i2c.getDevice(PIC1_ADDR);
        I2CDevice device2 = i2c.getDevice(PIC2_ADDR);
        //TODO: get address automatically


        
        byte data1 = 0;
        byte data2 = 0b1111111;
        int [] recv_last = new int[FRAME_LEN];
        
        while(true){
//            //send a frame to device 1
//            console.println("###########################################");
//            for(int i=0;i<FRAME_LEN;i++){
//                console.println("Sending to device1: data[" +i +"]=" + String.format("0x%02x", data1));
//                device.write(data1);
//                data1++;
//            }
            //read a frame from device 1
//            console.println("-----------------------------------------");
            int [] recv_buf = new int[FRAME_LEN];
            for(int i=0;i<FRAME_LEN;i++){
                recv_buf[i] = device.read();
//                console.println("Receive from device1: data[" +i +"]=" + String.format("0x%02x", recv_buf[i]));
            }
            console.println("ADC result=" + String.format("0x%02x", recv_buf[0]));
            String command = null;
//            if(recv_buf[0]-recv_last[0]>0){
//                command  = "LEFT";
//            }
//            else if(recv_buf[0]-recv_last[0]<0){
//                command  = "RIGHT";
//            }
//            else{
//                command = "NULL";
//            }
            if(recv_buf[0]>150){
                
                command = "PRESS_LEFT";
            }
            else if(recv_buf[0]<100){
                command = "PRESS_RIGHT";
            }
            else{
                command = "RELEASE";
            }
            
            
            // send UDP packet
            buf = command.getBytes();
            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, SERVER_PORT); 
            ds.send(DpSend); 
//            System.out.println("UDP Data sent: "+command);
            
            
            recv_last = recv_buf;
            Thread.sleep(1);
            
            
//            console.println("*************************************************");
//            for(int i=0;i<FRAME_LEN;i++){
//                console.println("Sending to device2: data[" +i +"]=" + String.format("0x%02x", data2));
//                device2.write(data2);
//                data2--;
//            }
//            //read a frame from device 1
//            console.println("-----------------------------------------");
//            for(int i=0;i<FRAME_LEN;i++){
//                int recv = device2.read();
//                console.println("Receive from device2: data[" +i +"]=" + String.format("0x%02x", recv));
//            }
//            Thread.sleep(100);
        }
        
        
        
    }
    
}
