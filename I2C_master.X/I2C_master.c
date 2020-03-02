/*
 * File:   I2C_master.c
 * Author: 25691
 *
 * Created on February 28, 2020, 4:04 PM
 */


#include <xc.h>

#define SLAVE_ADDR 0x30
#define _XTAL_FREQ 48000000   // define Fosc frequency in order to use delay function

#define BIT0 0x01
#define BIT1 0x02
#define BIT2 0x04
#define BIT3 0x08
#define BIT4 0x10
#define BIT5 0x20
#define BIT6 0x40
#define BIT7 0x80

void init_Chip();
void init_I2C();
void I2C_master_Tx(unsigned char data);     // send a byte data
unsigned char I2C_master_Rx();
void I2C_master_start();
void I2C_master_stop();

void main(void) 
{
    init_Chip();
    init_I2C();
    
    unsigned char data = 0x77;
    
    
    INTCONbits.GIE = 1;	// Turn on global interrupt
    
    LATB = 0;
    LATC = 0;
    while(1){
        LATCbits.LATC2 ^= 1;
//        while((SSPSTATbits.R_W));    // wait for writing ready
//        while ((SSPSTAT & 0x04) || (SSPCON2 & 0x1F)); //Transmit is in progress
//        SSPCON2bits.SEN = 1;        // send start bit
        
        // master send
        I2C_master_start();
        I2C_master_Tx(SLAVE_ADDR);
        I2C_master_Tx(data);
        I2C_master_Tx(data);
        I2C_master_Tx(data);
//        LATA = data;
//        data++;
        I2C_master_stop();
        
//        __delay_ms(30);  
        
        I2C_master_start();
        I2C_master_Tx(SLAVE_ADDR|BIT0); // send 7bit address and R
        data = I2C_master_Rx();
        LATA = data;
        I2C_master_stop();

        
        __delay_ms(30);      
    }
    
    
    
}


void init_Chip()
{
    LATA = 0x00; //Initial PORTA
    TRISA = 0x00; //
    ADCON1 = 0x00; //AD voltage reference
    ANSELA = 0x00; // define analog or digital
    CM1CON0 = 0x00; //Turn off Comparator
    LATB = 0x00; //Initial PORTB
    TRISB = 0x00; //Define PORTB as output
    LATC = 0x00; //Initial PORTC
    TRISC = 0x00; //Define PORTC as output
	INTCONbits.GIE = 0;	// Turn Off global interrupt
    INTCONbits.TMR0IE = 1;
}

void init_I2C()
{
    // configure SCL and SDA as input
    TRISBbits.TRISB0 = 1;   // SDA as input
    TRISBbits.TRISB1 = 1;   // SCL as input
    
    SSPCON1 = 0x28;
    SSPCON2 = 0x00;
    SSPADD = (_XTAL_FREQ/(4*100000))-1;;     // set baud rate
    SSPSTAT = 0x80;     // slew rate control disable
    
    
    
    
    PIE1bits.SSPIE = 1;         // enable SPI interrupt
    INTCONbits.PEIE = 1;    //enable peripheral interrupt
    
}

void I2C_master_Tx(unsigned char data)
{
//    while((SSPSTATbits.R_W));    // wait for writing ready
    while ((SSPSTAT & 0x04) || (SSPCON2 & 0x1F)); //Transmit is in progress
    SSPBUF = data;  // star transmitting
    
}
void I2C_master_start()
{
    while ((SSPSTAT & 0x04) || (SSPCON2 & 0x1F)); //Transmit is in progress
    SSPCON2bits.SEN = 1;        // send start bit
}

void I2C_master_stop()
{
    while ((SSPSTAT & 0x04) || (SSPCON2 & 0x1F)); //Transmit is in progress
    SSPCON2bits.PEN = 1;        // send stop bit
}

unsigned char I2C_master_Rx()
{
    unsigned char temp = 0;
  
    while ((SSPSTAT & 0x04) || (SSPCON2 & 0x1F)); //Transmit is in progress
    SSPCON2bits.RCEN = 1;
    while ((SSPSTAT & 0x04) || (SSPCON2 & 0x1F)); //Transmit is in progress
    temp = SSPBUF;
    while ((SSPSTAT & 0x04) || (SSPCON2 & 0x1F)); //Transmit is in progress
    SSPCON2bits.ACKDT = 1;
    SSPCON2bits.ACKEN = 1;
    return temp;
}


/********************************************************* 
	Interrupt Handler
**********************************************************/
void __interrupt (high_priority) high_ISR(void)
{       
    LATBbits.LATB4 ^= 1;
    if(PIR1bits.SSPIF==1){
        // serial transmission interrupt
        
        PIR1bits.SSPIF = 0;     // don't forget to clear flag
        LATCbits.LATC1 ^= 1;
        
        
    }
    
   
}