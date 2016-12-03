package com.CMD;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str=" [0m;34mDownloads[0m ";
		System.err.println(str);
		System.err.println(str.replaceAll("\\[([0-9]).;([0-9]+)m", ""));
		System.err.println(str.replaceAll("\\[0m ", ""));
		                    
		                    
		                    
	}

}
