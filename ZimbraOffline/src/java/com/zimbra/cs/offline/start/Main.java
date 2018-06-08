/*
 * 
 */
package com.zimbra.cs.offline.start;

import java.lang.reflect.Method;

public class Main {
	public static void main(String[] args) {
		
		//The first argument must be the class name of the jetty starter
		if (args.length == 0) {
			System.err.println("Main launcher class name expected as first argument.");
			System.exit(1);
		}
		
		try {
			Class<?> starter = Class.forName(args[0]);
			Method main = starter.getMethod("main", new Class[] {String[].class});
			String[] newArgs;
			if (args.length > 1) {
				newArgs = new String[args.length - 1];
				System.arraycopy(args, 1, newArgs, 0, args.length - 1);
			} else {
				newArgs = new String[] {};
			}
			main.invoke(null, (Object)(newArgs));
		} catch (Exception x) {
			x.printStackTrace(System.err);
			System.exit(2);
		}
	}
}
