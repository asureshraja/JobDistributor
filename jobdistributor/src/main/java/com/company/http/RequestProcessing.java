package com.company.http;
import com.lmax.disruptor.EventHandler;

import java.io.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by suresh on 12/6/15.
 */

public class RequestProcessing implements EventHandler<RequestReceivedEvent>
{        static Executor executor = Executors.newFixedThreadPool(200);
    public void onEvent(RequestReceivedEvent event, long sequence, boolean endOfBatch)
    {


        String s;
        SocketAndStreams reqToRead=null;
        BufferedReader in=null;
        BufferedWriter out=null;
        //System.out.println("3");
        //System.out.println("size of connection queue"+connectionQueue.size());


                reqToRead = (SocketAndStreams) event.get();


        // System.out.println(reqToRead.getSocket().getInetAddress());
        if(reqToRead!=null) {
            //       System.out.println("4");
            try {
                in = new BufferedReader(new InputStreamReader(reqToRead.getSocket().getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(reqToRead.getSocket().getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }


            reqToRead.setIn(in);
            reqToRead.setOut(out);

            try {
                while ((s = in.readLine()) != null) {
                    //System.out.println(s);
                    if (s.isEmpty()) {
                        break;
                    }
                }

                reqToRead.getOut().write("HTTP/1.0 200 OK\r\nDate: Fri, 31 Dec 1999 23:59:59 GMT\r\nServer: Apache/0.8.4\r\nContent-Type: text/html\r\nContent-Length: 40\r\nExpires: Sat, 01 Jan 2000 00:59:59 GMT\r\nLast-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n\r\n<TITLE>Example</TITLE><P>hello world</P>");
//
//                                            reqToRead.getOut().write("HTTP/1.0 200 OK\r\n");
//                                            reqToRead.getOut().write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
//                                            reqToRead.getOut().write("Server: Apache/0.8.4\r\n");
//                                            reqToRead.getOut().write("Content-Type: text/html\r\n");
//                                            reqToRead.getOut().write("Content-Length: 40\r\n");
//                                            reqToRead.getOut().write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
//                                            reqToRead.getOut().write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
//                                            reqToRead.getOut().write("\r\n");
//                                            reqToRead.getOut().write("<TITLE>Example</TITLE>");
//                                            reqToRead.getOut().write("<P>hello world</P>");

                reqToRead.getOut().close();
                reqToRead.getSocket().close();
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
