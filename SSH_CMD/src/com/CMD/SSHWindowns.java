package com.CMD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SSHWindowns {
	private JSch jSch;
	private Session session;
	private Channel channel;
	private String host;
	private int port;
	private String user;
	private String passw;
     
	public SSHWindowns(String host, int port, String user, String passw) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.passw = passw;
	

	}

	public long refreshTime;
	public long maxTimeOut = 60l * 1000l * 2l;
	public boolean cmdFlag = false;
	public boolean titleFlag = false;
	public String title = "";

	public void refresh() {
		refreshTime = System.currentTimeMillis();

	}

	public boolean testTimeOut() {
		long now = System.currentTimeMillis();
		if ((now - refreshTime) >= maxTimeOut)
			return true;
		return false;
		
	}

	private RunState runState = RunState.WAITE;

	private enum RunState {
		WAITE, RUN, END
	}

	BufferedReader reader;

	BufferedWriter out;
	private StringBuffer readBuff = new StringBuffer();

	public void cmd(String str) {
		System.err.println(str);
		if (reader == null) {
			append("NULL Reader");
			return;
		}
		try {
			if (runState != RunState.RUN) {
				append(" state is " + runState);
			} else {
				synchronized (out) {
					cmdFlag = true;
					out.write(str + "\n");
					out.flush();

				}

			}
		} catch (Exception e) {
			// TODO: handle exception
			append(e.getMessage());
		}
	}
	public void exit() {
	
		if (reader == null) {
			append("NULL Reader");
			return;
		}
		try {
			if (runState != RunState.RUN) {
				append(" state is " + runState);
			} else {
				synchronized (out) {
				char[] c={17,67};
					out.write(c);
					out.flush();
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
			append(e.getMessage());
		}
	}
	private void append(String str) {
		synchronized (readBuff) {
		System.err.println(str);
			if(!str.startsWith("]0;"))
			{
				str=str.replaceAll("\\[0m", "");
				str=str.replaceAll("\\[([0-9]).;([0-9]+)m", "");
				
		
				readBuff.append(str).append("\n");
			}
			else
			{
				title=str.replace("]0;", "");
			}
			
		}
	}

	public String read() {
		synchronized (readBuff) {
			refresh();
			if (readBuff.length() == 0) {
				return null;
			}
			String str = readBuff.toString();
			readBuff.setLength(0);
			return str;
		}
	}

	private Thread mainThread;

	private Thread readThread;

	public void stop() {
		try {
			this.session.disconnect();
			this.channel.disconnect();
			out.flush();
			out.close();
			reader.close();
			
		
		} catch (Exception e) {
			// TODO: handle exception
			append(e.getMessage());
		} finally {
			runState = RunState.END;
		

		}
	}

	public void start() {
		runState = RunState.WAITE;
		mainThread = new Thread(new Runnable() {
			public void run() {

				try {

					jSch = new JSch();
					session = jSch.getSession(user, host, port);
					session.setPassword(passw);// …Ë÷√√‹¬Î
					// …Ë÷√µ⁄“ª¥Œµ«¬Ωµƒ ±∫ÚÃ· æ£¨ø…—°÷µ£∫(ask | yes | no)
					session.setConfig("StrictHostKeyChecking", "no");
					// …Ë÷√µ«¬Ω≥¨ ± ±º‰
					session.connect(30000);
					channel = (Channel) session.openChannel("shell");
					channel.connect(1000);
					reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
					out = new BufferedWriter(new OutputStreamWriter(channel.getOutputStream()));

					runState = RunState.RUN;
					append("start");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					append(e.getMessage());
				}

			}
		});

		readThread = new Thread(new Runnable() {
			public void run() {
				while (runState != RunState.END) {
					if (runState == RunState.RUN) {
						try {
							String str = reader.readLine();
							for (; str != null; str = reader.readLine()) {

								append(str);
							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});

		mainThread.start();
		readThread.start();

	}

}
