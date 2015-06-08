package com.marche.moonlightembeddedcontroller.SSH;

import android.app.Activity;
import android.content.Context;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.marche.moonlightembeddedcontroller.Events.GotGamesEvent;
import com.marche.moonlightembeddedcontroller.Events.LimelightDownloadedEvent;
import com.marche.moonlightembeddedcontroller.Events.LimelightExistsEvent;
import com.marche.moonlightembeddedcontroller.Events.MainThreadBus;
import com.marche.moonlightembeddedcontroller.Events.PairEvent;
import com.marche.moonlightembeddedcontroller.Events.SSHConnected;
import com.marche.moonlightembeddedcontroller.Events.SSHError;
import com.marche.moonlightembeddedcontroller.POJO.Device;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class SSHManager {

    private static SSHManager instance = null;

    public MainThreadBus SSHBus = new MainThreadBus();
    private JSch jsch;
    private Session session;

    public static SSHManager getInstance() {
        if(instance == null) {
            instance = new SSHManager();
        }
        return instance;
    }

    public void connectToSSH(final Context con, final Device device){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try{
                    jsch = new JSch();
                    session = jsch.getSession(device.login, device.ip, 22);
                    session.setPassword(device.password);

                    // Avoid asking for key confirmation
                    Properties prop = new Properties();
                    prop.put("StrictHostKeyChecking", "no");
                    session.setConfig(prop);

                    session.connect();

                    dispatchEventBus(con, new SSHConnected());

                } catch (JSchException e){
                    if(e.getMessage().contains("ECONNREFUSED")){
                        SSHBus.post(new SSHError());
                    } else if(e.getMessage().contains("Auth fail")){
                        SSHBus.post(new SSHError());
                    } else {
                        System.out.println(e.getMessage());
                    }
                }
            }
        };

        thread.start();
    }

    public void doesLimelightExist(final Context con, final String dir) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // SSH Channel
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    channel.setOutputStream(baos);

                    // Execute command
                    channel.setCommand("[ -f "+ dir +" ] && echo \"yes\" || echo \"no\"");
                    channel.connect();

                    InputStream in = channel.getInputStream();
                    byte[] tmp = new byte[1024];
                    String tmpString = "";
                    while (true) {
                        while (in.available() > 0) {
                            int i = in.read(tmp, 0, 1024);
                            if (i < 0) break;
                            tmpString = tmpString+new String(tmp, 0, i);
                            System.out.print(new String(tmp, 0, i));
                        }

                        if (channel.isClosed()) {
                            if (in.available() > 0) continue;
                            System.out.println("exit-status: " + channel.getExitStatus());

                            if(tmpString.contains("yes")){
                                dispatchEventBus(con,new LimelightExistsEvent(true));
                            } else {
                                dispatchEventBus(con,new LimelightExistsEvent(false));
                            }

                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (Exception ee) {
                        }
                    }
                    channel.disconnect();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        };

        thread.start();
    }

    public void doesLimelightExist(final Context con) {
        doesLimelightExist(con, "limelight/limelight.jar");
    }

    public void getGames(final Context con) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Channel channel=session.openChannel("shell");
                    OutputStream ops = channel.getOutputStream();
                    PrintStream ps = new PrintStream(ops, true);

                    channel.connect();
                    ps.println("cd limelight");
                    ps.println("java -jar limelight.jar list");
                    //give commands to be executed inside println.and can have any no of commands sent.
                    ps.close();

                    boolean gamesIncoming = false;

                    ArrayList<String> gameNames = new ArrayList<>();

                    InputStream in = channel.getInputStream();
                    byte[] tmp = new byte[1024];
                    while (true) {
                        while (in.available() > 0) {
                            int i = in.read(tmp, 0, 1024);
                            if (i < 0) break;

                            String tmpString = new String(tmp, 0, i);
                            System.out.println(tmpString);

                            if(gamesIncoming){
                                tmpString = tmpString.replace("(DX 11)", "");
                                tmpString = tmpString.replace("(DX 10)", "");
                                gameNames.add(tmpString.trim());
                            }

                            if(tmpString.contains("Search apps")){
                                gamesIncoming = true;
                            }
                        }

                        if (channel.isClosed()) {
                            if (in.available() > 0) continue;
                            System.out.println("exit-status: " + channel.getExitStatus());

                            dispatchEventBus(con, new GotGamesEvent(gameNames));
                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (Exception ee) {
                        }
                    }
                    channel.disconnect();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        };

        thread.start();
    }

    public void pairComputer(final Context con) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Channel channel=session.openChannel("shell");
                    OutputStream ops = channel.getOutputStream();
                    PrintStream ps = new PrintStream(ops, true);

                    channel.connect();
                    ps.println("cd limelight");
                    ps.println("java -jar limelight.jar pair");
                    //give commands to be executed inside println.and can have any no of commands sent.
                    ps.close();

                    InputStream in = channel.getInputStream();
                    byte[] tmp = new byte[1024];
                    while (true) {
                        while (in.available() > 0) {
                            int i = in.read(tmp, 0, 1024);
                            if (i < 0) break;

                            String tmpString = new String(tmp, 0, i);
                            System.out.println(tmpString);

                            if(tmpString.contains("Please enter the following")){
                                String pairCode = tmpString.replaceAll("\\D+","");
                                dispatchEventBus(con, new PairEvent(pairCode));
                            } else if(tmpString.contains("Paired successfully")){
                                dispatchEventBus(con, new PairEvent(true));
                                break;
                            } else if(tmpString.contains("Pairing failed")){
                                dispatchEventBus(con, new PairEvent(false));
                                break;
                            }
                        }

                        if (channel.isClosed()) {
                            if (in.available() > 0) continue;
                            System.out.println("exit-status: " + channel.getExitStatus());
                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (Exception ee) {
                        }
                    }
                    channel.disconnect();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        };

        thread.start();
    }

    public void createFolderAndDownloadFiles(final Context con) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Channel channel=session.openChannel("shell");
                    OutputStream ops = channel.getOutputStream();
                    PrintStream ps = new PrintStream(ops, true);

                    channel.connect();
                    ps.println("mkdir limelight");
                    ps.println("cd limelight");
                    ps.println("wget https://github.com/irtimmer/limelight-embedded/releases/download/v1.2.2/libopus.so");
                    ps.println("wget https://github.com/irtimmer/limelight-embedded/releases/download/v1.2.2/limelight.jar");
                    //give commands to be executed inside println.and can have any no of commands sent.
                    ps.close();


                    InputStream in = channel.getInputStream();
                    byte[] tmp = new byte[1024];
                    while (true) {
                        while (in.available() > 0) {
                            int i = in.read(tmp, 0, 1024);
                            if (i < 0) break;

                            String tmpString = new String(tmp, 0, i);
                            System.out.println(tmpString);

                            if(tmpString.contains("%")){
                                String perc = tmpString.substring(0, tmpString.indexOf("%")).trim();

                                if(tmpString.contains("limelight.jar' saved")){
                                    System.out.println("exit-status: " + channel.getExitStatus());
                                    dispatchEventBus(con, new LimelightDownloadedEvent(true));
                                    break;
                                }

                                if(isNumeric(perc)){
                                    dispatchEventBus(con, new LimelightDownloadedEvent(Integer.parseInt(perc)));
                                } else {
                                    dispatchEventBus(con, new LimelightDownloadedEvent(-1));
                                }
                            }
                        }

                        if (channel.isClosed()) {
                            if (in.available() > 0) continue;
                            System.out.println("exit-status: " + channel.getExitStatus());

                            dispatchEventBus(con, new LimelightDownloadedEvent(true));
                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (Exception ee) {
                        }
                    }
                    channel.disconnect();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        };

        thread.start();
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public void dispatchEventBus(Context con, final Object event){
        ((Activity)con).runOnUiThread(new Runnable(){
            public void run(){
                SSHBus.post(event);
            }
        });
    }
}
