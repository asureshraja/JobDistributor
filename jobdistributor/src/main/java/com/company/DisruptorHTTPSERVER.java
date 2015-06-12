package com.company;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Created by suresh on 12/6/15.
 */

public class DisruptorHTTPSERVER {

        public static void main(String[] args) throws Exception {


            int port = 8080;
            final ServerSocket serverSocket = new ServerSocket(port);

            //////////////////////////////////////////////////////////////////////////////

            // Executor that will be used to construct new threads for consumers
            Executor executor = Executors.newCachedThreadPool();

            // The factory for the event
            RequestReceivedEventFactory factory = new RequestReceivedEventFactory();

            // Specify the size of the ring buffer, must be power of 2.
            int bufferSize = 1024;

            // Construct the Disruptor
            Disruptor<RequestReceivedEvent> disruptor = new Disruptor(factory, bufferSize, executor);

            // Connect the handler
            disruptor.handleEventsWith(new RequestProcessing());

            // Start the Disruptor, starts all threads running
            disruptor.start();

            // Get the ring buffer from the Disruptor to be used for publishing.
            RingBuffer<RequestReceivedEvent> ringBuffer = disruptor.getRingBuffer();

            final RequestReceivedEventProducer producer = new RequestReceivedEventProducer(ringBuffer);
            //////////////////////////////////////////////////////////////////////////////



                    while (true) {
                        Socket clientSocket = null;
                        try {
                            clientSocket = serverSocket.accept();
                            SocketAndStreams ss= new SocketAndStreams();
                            ss.setSocket(clientSocket);
                            //connectionQueue.put(ss);
                            producer.onData(ss);
                            //System.out.println("1");
//                            executorPool.execute(new Runnable() {
//                                public void run() {
                                    //System.out.println("2");
//                                    String s;
//                                    SocketAndStreams reqToRead=null;
//                                    BufferedReader in=null;
//                                    BufferedWriter out=null;
//                                        //System.out.println("3");
//                                        //System.out.println("size of connection queue"+connectionQueue.size());
//                                        if(connectionQueue.size()>0) {
//                                            try {
//                                                reqToRead = (SocketAndStreams) connectionQueue.take();
//                                            } catch (InterruptedException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                   // System.out.println(reqToRead.getSocket().getInetAddress());
//                                    if(reqToRead!=null) {
//                                 //       System.out.println("4");
//                                        try {
//                                            in = new BufferedReader(new InputStreamReader(reqToRead.getSocket().getInputStream()));
//                                            out = new BufferedWriter(new OutputStreamWriter(reqToRead.getSocket().getOutputStream()));
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//
//
//                                        reqToRead.setIn(in);
//                                        reqToRead.setOut(out);
//
//                                        try {
//                                            while ((s = in.readLine()) != null) {
//                                                //System.out.println(s);
//                                                if (s.isEmpty()) {
//                                                    break;
//                                                }
//                                            }
//
//                                            reqToRead.getOut().write("HTTP/1.0 200 OK\r\nDate: Fri, 31 Dec 1999 23:59:59 GMT\r\nServer: Apache/0.8.4\r\nContent-Type: text/html\r\nContent-Length: 40\r\nExpires: Sat, 01 Jan 2000 00:59:59 GMT\r\nLast-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n\r\n<TITLE>Example</TITLE><P>hello world</P>");
////
////                                            reqToRead.getOut().write("HTTP/1.0 200 OK\r\n");
////                                            reqToRead.getOut().write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
////                                            reqToRead.getOut().write("Server: Apache/0.8.4\r\n");
////                                            reqToRead.getOut().write("Content-Type: text/html\r\n");
////                                            reqToRead.getOut().write("Content-Length: 40\r\n");
////                                            reqToRead.getOut().write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
////                                            reqToRead.getOut().write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
////                                            reqToRead.getOut().write("\r\n");
////                                            reqToRead.getOut().write("<TITLE>Example</TITLE>");
////                                            reqToRead.getOut().write("<P>hello world</P>");
//
//                                            reqToRead.getOut().close();
//                                            reqToRead.getSocket().close();
//                                            in.close();
//
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                }
//                            });


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
//            });


//        }
}
