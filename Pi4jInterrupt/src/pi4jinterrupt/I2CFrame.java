/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pi4jinterrupt;

/**
 * a data structure for I2C data frame
 * @author 25691
 */
public class I2CFrame 
{
    private int idx;        // index of the current sending byte
    private byte data[];        // data buffer
    
    public I2CFrame(int frameLength)
    {
        data = new byte[frameLength];
        idx = 0;
    }
    
    
}
