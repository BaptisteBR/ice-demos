// **********************************************************************
//
// Copyright (c) 2003-2017 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

import Demo.*;

public class Client extends com.zeroc.Ice.Application
{
    class ShutdownHook extends Thread
    {
        @Override
        public void run()
        {
            communicator().destroy();
        }
    }

    private static void menu()
    {
        System.out.println(
            "usage:\n" +
            "g: send greeting\n" +
            "t: toggle null greeting\n" +
            "s: shutdown server\n" +
            "x: exit\n" +
            "?: help\n");
    }

    @Override
    public int run(String[] args)
    {
        if(args.length > 0)
        {
            System.err.println(appName() + ": too many arguments");
            return 1;
        }

        //
        // Since this is an interactive demo we want to clear the
        // Application installed interrupt callback and install our
        // own shutdown hook.
        //
        setInterruptHook(new ShutdownHook());

        GreetPrx greet = GreetPrx.checkedCast(communicator().propertyToProxy("Greet.Proxy"));
        if(greet == null) {

            System.err.println("invalid proxy");
            return 1;
        }

        MyGreeting greeting = new MyGreeting();
        greeting.text = "Hello there!";
        MyGreeting nullGreeting = null;

        boolean sendNull = false;

        menu();

        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

        String line = null;
        do
        {
            try
            {
                System.out.print("==> ");
                System.out.flush();
                line = in.readLine();
                if(line == null)
                {
                    break;
                }
                if(line.equals("g"))
                {
                    if(sendNull)
                    {
                        greet.sendGreeting(nullGreeting);
                    }
                    else
                    {
                        greet.sendGreeting(greeting);
                    }
                }
                else if(line.equals("t"))
                {
                    sendNull = !sendNull;
                }
                else if(line.equals("s"))
                {
                    greet.shutdown();
                }
                else if(line.equals("x"))
                {
                    // Nothing to do
                }
                else if(line.equals("?"))
                {
                    menu();
                }
                else
                {
                    System.out.println("unknown command `" + line + "'");
                    menu();
                }
            }
            catch(java.io.IOException ex)
            {
                ex.printStackTrace();
            }
            catch(com.zeroc.Ice.LocalException ex)
            {
                ex.printStackTrace();
            }
        }
        while(!line.equals("x"));

        return 0;
    }

    public static void main(String[] args)
    {
        Client app = new Client();
        int status = app.main("Client", args, "config.client");
        System.exit(status);
    }
}
