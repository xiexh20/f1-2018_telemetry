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
#define RxADDR 1 // receiving address byte
#define RxDATA 2    // receiving data byte

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
void writePortB(unsigned char data);

unsigned char data = 0;
unsigned char addr = 0;
unsigned char sent = 0x11;
unsigned char data_past = 0;
unsigned char RxStatus = IDLE; // receiving status

void main(void) 
{
    init_Chip();
    init_I2C();
    
    INTCONbits.GIE = 1;	// Turn on global interrupt
    LATB = 0;
    LATA = 0;
    while(1)
    {
//        LATA = data;
        __delay_ms(1); 
        
//        LATBbits.LATB6 ^= 1;
//        if(data_past != data){
//            LATBbits.LATB3 ^= 1;
//        }
        
        data_past = data;
//        LATA = data;
        
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
//    INTCONbits.TMR0IE = 1;
}

void init_I2C()
{
    // configure SCL and SDA as input
    TRISBbits.TRISB0 = 1;   // SDA as input
    TRISBbits.TRISB1 = 1;   // SCL as input
    
    SSPADD = SLAVE_ADDR;
    SSPCON1 = 0x36; // slave mode
    SSPSTAT = 0x80;     // 
//    SSPCON2 = 0x00;     // clock stretching disabled
    SSPCON2 = 0x01;     // clock stretching enabled
//    SSPCON3 = 0x00;     //enable start and stop bit interrupt or not
    
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
//    LATA = 0xFF;
    if(PIR1bits.SSPIF==1){
        // serial transmission interrupt
//        if(RxStatus==IDLE){
//            // start receive address byte
//            RxStatus = RxADDR;
//            SSPSTATbits.S = 0;
//        }
//        else if(RxStatus==RxADDR){
//            // address byte is received
//            
//            RxStatus = RxDATA;
//            
//            SSPCON1bits.CKP = 1;        // set CKP bit manually
//        }
//        else if(RxStatus==RxDATA){
//            
//            data = SSPBUF;
//            LATA = SSPBUF;
//            SSPCON1bits.CKP = 1;        // set CKP bit manually
//        }
        
//        if ((SSPCONbits.SSPOV) || (SSPCONbits.WCOL))
//        {
//          addr = SSPBUF; // Read the previous value to clear the buffer
//          SSPCONbits.SSPOV = 0; // Clear the overflow flag
//          SSPCONbits.WCOL = 0;  // Clear the collision bit
//          
//        }
//        
//        if(!SSPSTATbits.D_nA && !SSPSTATbits.R_nW)
//        {
//          data = SSPBUF;
//          while(!SSPSTATbits.BF);
//          
//          
//        }
//        else if(!SSPSTATbits.D_nA && SSPSTATbits.R_nW)
//        {
////          z = SSPBUF;
//          BF = 0;
//          SSPBUF = sent ;
//          
//          while(SSPSTATbits.BF);
//        }
        
        if(SSPSTATbits.R_nW){
            LATCbits.LATC7 = 1;
            
            data--;
            SSPBUF = data;
            LATA = data;
//            sent++;
        }
        else{
            LATCbits.LATC7 = 0;
        }


        if((SSPSTAT&BIT5)==0){
//            LATBbits.LATB7 = 1;
            addr = SSPBUF;
//            LATA = SSPSTAT;
//            writePortB(SSPBUF);
        }
        else{
//            LATBbits.LATB6 = 1;
            
            data = SSPBUF;
//            LATA = data;
//            LATA = SSPSTAT;
//            writePortB(SSPBUF);
        }
        
//        writePortB(addr);
//        writePortB(SSPSTAT);
        
//        LATA = SSPBUF;
        SSPCON1bits.SSPOV = 0; // Clear the overflow flag
        SSPCON1bits.WCOL = 0;  // Clear the collision bit
//        LATBbits.LATB4 ^= 1;
        PIR1bits.SSPIF = 0;     // don't forget to clear flag
        SSPCON1bits.CKP = 1;        // set CKP bit manually(ACK to master)
        
    }
}