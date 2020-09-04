
import java.io.IOException;

import java.util.concurrent.*;

import java.util.regex.*;

import java.util.*;

import java.net.*;

 

class Worker implements Runnable {

 

    static int id = 0;

    final int iden = ++id;

    static volatile String ip = "192.168.43.138";
    static Pattern pattern = Pattern.compile(".\\d+$");

    static Matcher m = null;

    static volatile int i = 25;

    static volatile boolean cancel = false;

    static volatile List<String> reachables = new ArrayList<>();

    static volatile List<String> nonreachables = new ArrayList<>();

 

 

    public static synchronized void ping(int id, String ipLocal) throws UnknownHostException, IOException {

        InetAddress adr = null;

        if (i < 40) {

 

            m = pattern.matcher(ip);

            if (m.find()) {

                ip = m.replaceFirst("." + Integer.toString(++i));

                System.out.print("\nStore #" + id + " testing IP: " + ip);

 

                adr = InetAddress.getByName(ip);

                ipLocal = ip;

            }

        } else {

            cancel = true;

            return;

        }

 

        if (adr.isReachable(2000)) {

            System.out.print("\nAddress " + ipLocal + " is reachable!");

            reachables.add(ipLocal);

        } else {

            System.out.print("\nAddress " + ipLocal + " not reachable!");

            nonreachables.add(ipLocal);

        }

    }

 

    public void run() {

        String ipLocal = "";

        while (!cancel) {

            try {

                ping(iden, ipLocal);

                Thread.yield();

            } catch (IOException ex) {

                System.out.println("IOException caught!");

            }

 

        }

    }

}

 

public class Ping {

 

public static void main(String[] args) throws InterruptedException {

        ExecutorService exec = Executors.newCachedThreadPool();

 

     //string array

    String[] ipAddressArray = new String[] {"r6710","r6944","r6946","r6977","r6766","r6767","r5798","r5797","r5895",

    "r6238","r6234","r6178","r6115","r6981","t042"};

   

        System.out.println("The number of stores:" + ipAddressArray.length);

       

        for(int i = 0; i < ipAddressArray.length; i++ )

          {

          System.out.println("List of stores:" + ipAddressArray[i]);  

           }

               

        for (int j = 0; j < 15; ++j) {

            exec.execute(new Worker());

           

        }

 

        exec.shutdown();

        while (true) {

            if (exec.isTerminated()) {

                System.out.println("\n\nReachable IPs = ");

                for (String reach : Worker.reachables) {

                    System.out.println(reach);                        

                }

                System.out.println("The number of Reachables IPs:" +  Worker.reachables.size());

               

                System.out.println("\n\nnon Reachable IPs = ");

                for (String reach : Worker.nonreachables) {

                    System.out.println(reach);                  

                }

                System.out.println("The number of non reachables IPs:" +  Worker.nonreachables.size());

               

                break;            

            }

        }

    }

}