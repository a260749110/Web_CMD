package com.CMD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;

public class CMDManager {
	private Map<String, SSHWindowns> shellMap;
	private Random random = new Random();

	public static final CMDManager instance = new CMDManager();

	private CMDManager() {
		shellMap = new HashMap<>();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				clean();
			}
		}, 60l * 1000l, 60l * 1000l);
	}

	private void clean() {
		synchronized (shellMap) {
			System.err.println("clean");
			List<String> removes = new ArrayList<>();
			for (String key : shellMap.keySet()) {
				SSHWindowns shellWindows = shellMap.get(key);
				if (shellWindows.testTimeOut()) {
					removes.add(key);
				}
			}
			for (int i = 0; i < removes.size(); i++) {
				System.err.println("clean  " + removes.get(i));
				SSHWindowns shellWindows = shellMap.get(removes.get(i));
				shellWindows.stop();
				shellMap.remove(removes.get(i));
			}
			System.err.println("clean  end");
		}
	}

	public ReadObj read(HttpServletRequest request) {
		String key = (String) request.getParameter("key");
		ReadObj read = new ReadObj();
		if (key == null)

		{

			read.key = "";
			return read;
		}
		synchronized (shellMap) {
			SSHWindowns shellWindows = shellMap.get(key);
			if (shellWindows == null) {
				read.key = "";
				return read;
			}
			read.key = key;
			read.msg = shellWindows.read();
			read.title= shellWindows.title;
			return read;
		}
	}

	public String land(HttpServletRequest request) {
		return create(request);
	}

	public boolean cmd(HttpServletRequest request) {
		String key = (String) request.getParameter("key");
		String cmd = (String) request.getParameter("cmd");
		if (key == null || cmd == null) {
			// create(request);
			return false;
		}
		synchronized (shellMap) {
			SSHWindowns shellWindows = shellMap.get(key);
			if (shellWindows == null) {
				// create(request);
				return false;
			}
			shellWindows.cmd(cmd);
			return true;
		}
	}
	public boolean exit(HttpServletRequest request) {
		String key = (String) request.getParameter("key");

		if (key == null ) {
			// create(request);
			return false;
		}
		synchronized (shellMap) {
			SSHWindowns shellWindows = shellMap.get(key);
			if (shellWindows == null) {
				// create(request);
				return false;
			}
			shellWindows.exit();
			return true;
		}
	}
	private String create(HttpServletRequest request) {
		synchronized (shellMap) {
			try {
				String host = request.getParameter("host");
				int port = Integer.valueOf(request.getParameter("port"));
				String user = request.getParameter("user");
				String passw = request.getParameter("passw");
				
				request.getSession().setAttribute("host",host );;
				request.getSession().setAttribute("port",port );;
				request.getSession().setAttribute("user",user );;
				request.getSession().setAttribute("passw",passw );;
				
				String key = getRandomKey();
				System.err.println("create!!!!!!!!!!!!!");
				SSHWindowns shellWindows = new SSHWindowns(host, port, user,passw);
				shellMap.put(key, shellWindows);
				shellWindows.start();
				return key;
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}

		}
	}

	private String getRandomKey() {
		String key = random.nextLong() + "";
		if (shellMap.containsKey(key)) {
			return getRandomKey();
		}
		return key;
	}

	public static class ReadObj {
		public String key;
		public String msg;
		public String title;
	}
}
