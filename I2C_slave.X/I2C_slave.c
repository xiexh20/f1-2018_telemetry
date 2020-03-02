/*
 * File:   I2C_slave.c
 * Author: 25691
 *
 * Created on February 28, 2020, 4:31 PM
 */


#include <xc.h>


#define SLAVE_ADDR 0x40
#define _XTAL_FREQ 48000000   // define Fosc frequency in order to use delay function

#define IDLE 0
#define I2CTxing 1      // the I2C module is sending data out
#define I2CRxing 2      // the I2C module is receiving data in
//#define RxADDR 1 // receiving address byte
//#define RxDATA 2    // receiving data byte

#define BIT0 0x01
#define BIT1 0x02
#define BIT2 0x04
#define BIT3 0x08
#define BIT4 0x10
#define BIT5 0x20
#define BIT6 0x40
#define BIT7 0x80

#define FRAME_LEN 10    // the length of the data frame
typedef unsigned char uchar;

typedef struct buffer{
    uchar data[FRAME_LEN];       // data buffer
    uchar idx;      // index of the byte to be sent/just received
}buffer_t;

void init_Chip();
void init_I2C();
void writePortB(unsigned char data);

unsigned char data = 0;
unsigned char addr = 0;
unsigned char sent = 0x11;
unsigned char data_past = 0;
unsigned char RxStatus = IDLE; // receiving status

buffer_t Txbuf;      // store data to be sent out (fill ADC data in this variable)
buffer_t Rxbuf;     // store received data frame
uchar I2Cstatus = IDLE;     // status of the I2C module, either IDLE, sending data out or receiving data in

void main(void) 
{
    init_Chip();
    init_I2C();
    
    // init buffer
    Txbuf.idx = 0;
    Rxbuf.idx = 0;
    
    INTCONbits.GIE = 1;	// Turn on global interrupt
    LATB = 0;
    LATA = 0;
    while(1)
    {
        __delay_ms(1); 
        
//        LATBbits.LATB6 ^= 1;
//        if(data_past != data){
//            LATBbits.LATB3 ^= 1;
//        }
        
        data_past = data;
        
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
}

void init_I2C()
{
    // configure SCL and SDA as input
    TRISBbits.TRISB0 = 1;   // SDA as input
    TRISBbits.TRISB1 = 1;   // SCL as input
    
    SSPADD = SLAVE_ADDR;
    SSPCON1 = 0x36; // slave mode
    SSPSTAT = 0x80;     // 
    SSPCON2 = 0x01;     // clock stretching enabled
    
    PIE1bits.SSPIE = 1;         // enable SPI interrupt
    INTCONbits.PEIE = 1;    //enable peripheral interrupt
}

void writePortB(unsigned char data)
{
    LATB = data;
    LATCbits.LATC1 = (data>>2)&BIT0;
    LATCbits.LATC2 = (data>>1)&BIT0;
}


/********************************************************* 
	Interrupt Handler
**********************************************************/
void __interrupt (high_priority) high_ISR(void)
{       
//    LATBbits.LATB5 ^= 1;
    if(PIR1bits.SSPIF==1){
        // serial transmission interrupt

        if(SSPSTATbits.R_nW){
            // slave transmission mode: send data out
            LATCbits.LATC7 = 1;
            
            data--;
            SSPBUF = data;  // send data out
            LATA = data;
        }
        else{
            // slave reception mode: read data from the SSPBUF
            LATCbits.LATC7 = 0;
        }


        if((SSPSTAT&BIT5)==0){
            // an address byte is received
            addr = SSPBUF;
        }
        else{
            // a data byte is received
            data = SSPBUF;
        }
        
//        writePortB(addr);
//        writePortB(SSPSTAT);
        
        SSPCON1bits.SSPOV = 0; // Clear the overflow flag
        SSPCON1bits.WCOL = 0;  // Clear the collision bit
        PIR1bits.SSPIF = 0;     // don't forget to clear flag
        SSPCON1bits.CKP = 1;        // set CKP bit manually(ACK to master)
        
    }
}