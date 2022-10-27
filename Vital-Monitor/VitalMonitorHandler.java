//E/17/327
//A.M.F.Shalha
//CO327-Mini Project Code

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
//Thread class to handle vital monitors inorder to initiate tcp connections
public class VitalMonitorHandler implements Runnable{
    //variables to hold Ip address,Monitor port and monitorID
    InetAddress sourceAddress;
    int port;
    String monitorID;
    
    //class constructor
    public VitalMonitorHandler(InetAddress sourceAddress,int port, String monitorID) {
        this.sourceAddress = sourceAddress;
        this.port=port;
        this.monitorID=monitorID;
        
    }
    //run method
    @Override
    public void run(){
        
        try {
            //initiate a tcp connection using source and port addresses of vital monitor
            try (Socket socket = new Socket(sourceAddress,port)) {
                System.out.println("------------------------------------------------------------------------");
                System.out.println("Vital Monitor:"+monitorID+" Connected");
                System.out.println("------------------------------------------------------------------------");
                //read send data from vital monitors after initiating tcp connection with gateway server
                BufferedReader inputData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                while (true) {
                    try {
                        
                        //read line by line 
                        String message = inputData.readLine();
                        //print the message
                        System.out.println("Message from vital monitor=" + message);
                    } catch (IOException e) {//error handling
                        e.printStackTrace();
                    }
                }
            }
            
        } catch (SocketException e) {//error handling
            e.printStackTrace();
        } catch (IOException e1) {//error handling
            e1.printStackTrace();
        }
        
    }
    
}