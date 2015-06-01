package com.marche.moonlightembeddedcontroller.SSH;

import android.app.Activity;
import android.content.Context;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.marche.moonlightembeddedcontroller.Events.LimelightDownloadedEvent;
import com.marche.moonlightembeddedcontroller.Events.LimelightExistsEvent;
import com.marche.moonlightembeddedcontroller.Events.SSHConnected;
import com.squareup.otto.Bus;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class SSHManager {

    private static SSHManager instance = null;

    public Bus SSHBus = new Bus();
    private JSch jsch;
    private Session session;

    public static SSHManager getInstance() {
        if(instance == null) {
            instance = new SSHManager();
        }
        return instance;
    }

    public void connectToSSH(final Context con, final String ip, final String username,final String password){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try{
                    jsch = new JSch();
                    session = jsch.getSession(username,ip, 22);
                    session.setPassword(password);

                    // Avoid asking for key confirmation
                    Properties prop = new Properties();
                    prop.put("StrictHostKeyChecking", "no");
                    session.setConfig(prop);

                    session.connect();

                    dispatchEventBus(con, new SSHConnected());

                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        };

        thread.start();
    }

    public void doesLimelightExist(final Context con) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // SSH Channel
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    channel.setOutputStream(baos);

                    // Execute command
                    channel.setCommand("[ -d limelight ] && echo \"yes\" || echo \"no\"");
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
                            System.out.print(new String(tmp, 0, i));
                        }

                        if (channel.isClosed()) {
                            if (in.available() > 0) continue;
                            System.out.println("exit-status: " + channel.getExitStatus());

                            dispatchEventBus(con, new LimelightDownloadedEvent());
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

    public void dispatchEventBus(Context con, final Object event){
        ((Activity)con).runOnUiThread(new Runnable(){
            public void run(){
                SSHBus.post(event);
            }
        });
    }
}
