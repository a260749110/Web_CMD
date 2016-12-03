package com.CMD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ShellWindows {
  
	public long refreshTime;
	public long maxTimeOut = 60l * 1000l * 2l;
	public boolean cmdFlag=false;
	public boolean titleFlag=false;
	public String title="";
	public void refresh() {
		refreshTime = System.currentTimeMillis();
System.err.println("ss");
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

	public ShellWindows() {

	}

	 BufferedReader reader;
	 BufferedReader erroReader;
	BufferedWriter out;
	private StringBuffer readBuff = new StringBuffer();

	public void cmd(String str) {
		if (reader == null) {
			append("NULL Reader");
			return;
		}
		try {
			if (runState != RunState.RUN) {
				append(" state is " + runState);
			} else {
				synchronized (out) {
					cmdFlag=true;
					out.write(str+"\r\n");
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
			readBuff.append(str).append("\r\n");
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
	private Thread erroThread;
	private Runtime runtime;
	private Process process;

	public void stop() {
		try {
			process.destroy();
			runState = RunState.END;
		} catch (Exception e) {
			// TODO: handle exception
			append(e.getMessage());
		}
	}

	public void start() {
		runState = RunState.WAITE;
		mainThread = new Thread(new Runnable() {
			public void run() {
				runtime = Runtime.getRuntime();

				try {

					process = runtime.exec("cmd.exe");
					reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					erroReader= new BufferedReader(new InputStreamReader(process.getErrorStream()));
					out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
					runState = RunState.RUN;
					append("start");
					process.waitFor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		erroThread=new Thread(new  Runnable() {
			public void run() {
				while (runState != RunState.END) {
					if (runState == RunState.RUN) {
						try {
							String str = erroReader.readLine();
							for (; str != null; str = erroReader.readLine()) {
								
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
