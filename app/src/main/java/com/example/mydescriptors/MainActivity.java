package com.example.mydescriptors;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /*usb var*/
    UsbDevice myUsbdevice=null;
    UsbManager myUsbmanager=null;
    UsbDeviceConnection myUsbConnection=null;
    UsbInterface myUsbInterface=null;
     byte[] buffer =new byte[512];
    int bufferLength=0;
    byte[] rawDescriptors;
    int indexStrVendor,indexStrProduckt,indexStrSerial;

    TextView tvConnection, tvVisibl;
    Button clearBut, deviceBut, serialBut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Mycode
        //запуск службы инициализация все верно~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                                   myUsbmanager=(UsbManager)getSystemService(Context.USB_SERVICE);

                                  // myUsbdevice=(UsbDevice)getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE) ;
         // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        tvConnection=(TextView)findViewById(R.id.visConnectView);
        tvVisibl=(TextView)findViewById(R.id.visView);

        clearBut=(Button)findViewById(R.id.clrButton);
        deviceBut=(Button)findViewById(R.id.deviceButton);
        serialBut=(Button)findViewById(R.id.serialButton);
        tvConnection.setText("onCreat");

        View.OnClickListener pressButton =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.deviceButton:
                       /* if(myUsbdevice==null) {
                            tvConnection.setText("myUsbdevice==null");
                        }
                        else {
                            myGetRaw();
                        }
                        // tvConnection.setText("myUsbdevice==null");
                                                // myUsbConnection.close();*/
                        break;
                    case R.id.serialButton:
                        myUsbdevice=(UsbDevice)getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE) ;
                        myUsbConnection = myUsbmanager.openDevice(myUsbdevice);
                        myUsbInterface = myUsbdevice.getInterface(0);
                        myUsbConnection.claimInterface(myUsbInterface, true);
                        if(myUsbmanager.hasPermission(myUsbdevice)) {

                    serialBut.setEnabled(false);
                                   myGetRaw();
                        tvConnection.setText("Vendor: "+ myGetStrDescriptors(indexStrVendor)+"\n");
                      tvConnection.setText(tvConnection.getText()+"Product: "+ myGetStrDescriptors(indexStrProduckt)+"\n");
                        tvConnection.setText(tvConnection.getText()+"s/n: "+ myGetStrDescriptors(indexStrSerial)+"\n");
                            tvConnection.setText(tvConnection.getText()+"language: "+ myGetStrDescriptors(0)+"\n");
                        }
                        else {
                            tvConnection.setText("NO Answer");
                        }

                       // myGetStrDescriptors(1);
                      //   myUsbConnection.releaseInterface(myUsbInterface);
                        serialBut.setEnabled(true);
                        break;
                }

            }
        }; //end listiner
        deviceBut.setOnClickListener(pressButton);
        serialBut.setOnClickListener(pressButton);
    }//end onCreat

//GETRAW~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~m

 void myGetRaw() {

   // myUsbConnection = myUsbmanager.openDevice(myUsbdevice);// возможно дублирует
   // myUsbdevice=(UsbDevice)getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE) ;
     rawDescriptors = myUsbConnection.getRawDescriptors();
     bufferLength = rawDescriptors.length;
     tvConnection.setText(Integer.toString(bufferLength));
     tvVisibl.setText("");
     for (int i = 0; i < rawDescriptors.length; i++) {
         String tempHex = Integer.toHexString(rawDescriptors[i] & 0xff);
         if (tempHex.length() == 1) {
             tempHex = "0" + tempHex;
         }
         tvVisibl.setText(tvVisibl.getText() + tempHex + " ");
     }
     indexStrVendor=rawDescriptors[14];
     indexStrProduckt=rawDescriptors[15];
     indexStrSerial=rawDescriptors[16];

 }//end GetRaw~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~<
   String myGetStrDescriptors(int indexStr){
       String snName="";

     //  boolean forceClaim = true;
        //myUsbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);

        // myUsbConnection = myUsbmanager.openDevice(myUsbdevice);
      // myUsbConnection.getRawDescriptors();
        if(myUsbConnection==null) {
            tvConnection.setText("myUsbConnection FALSE");
        }
        else {
          //  myUsbInterface = myUsbdevice.getInterface(0);
           // myUsbConnection.claimInterface(myUsbInterface, true);

            bufferLength = myUsbConnection.controlTransfer(128, 6, (3 * 256) + indexStr, 1033, buffer, 512, 1000);
            if(bufferLength>0){
            snName =myByteToCharStr(buffer);
            }
            else {
               snName="No StringDescriptor of "+"index: "+indexStr;
            }
           // tvConnection.setText(tvConnection.getText()+"Vendor: "+ snName);
        //  myUsbConnection.releaseInterface(myUsbInterface);
          // myUsbConnection.close();
        }
        return snName;

    }

    String myByteToCharStr(byte[] buffer){
       String   strOut="";
       for(int i = 1; i < ((bufferLength )>>1); i++) {
            int bpos = i << 1;
            strOut=strOut+(char)(((buffer[bpos]&0x00FF)) + (buffer[bpos+1]&0x00FF)*256);
        }
        return strOut;

    }

    public void clrView(View view) {
        tvVisibl.setText("");
        tvConnection.setText("");
    }
}//endActiv

