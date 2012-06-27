/*
 * Copyright 2012 by Florian Bausch
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.fbausch.queens;

import java.util.Vector;

/**
 * A class used for parallelized computation of the n-queens-problem.
 * 
 * @author Florian Bausch
 * 
 */
public class Runner extends Thread {

	/**
	 * Number of cores of the CPU.
	 */
	public static final int CORES = Runtime.getRuntime().availableProcessors();
	/**
	 * A buffer containing working sets.
	 */
	private static Vector<Queen> buffer = new Vector<Queen>();
	/**
	 * The start time of computation.
	 */
	private static long starttime;
	/**
	 * The end time of the computation.
	 */
	private static long endtime;
	/**
	 * The size of the chess boards.
	 */
	private static int size;
	/**
	 * The number of runners actually used.
	 */
	private static int runners = CORES;

	/**
	 * Creates working sets for chess boards of size <code>size</code>.
	 * 
	 * @param size
	 *            The size.
	 */
	public static void createBuffer(int size) {
		if (size < 1) {
			throw new ArrayIndexOutOfBoundsException(size);
		}
		for (int i = 0; i < size; i++) {
			buffer.add(new Queen(size, i));
		}
		Runner.size = size;
	}

	/**
	 * Starts the {@link Runner}s for the working set created with
	 * {@link #createBuffer(int)}.
	 */
	public static void startRunners() {
		Runner[] ar = new Runner[runners];
		for (int i = 0; i < runners; i++) {
			ar[i] = new Runner();
		}
		Runner.starttime = System.nanoTime();
		for (int i = 0; i < runners; i++) {
			ar[i].start();
		}
		for (int i = 0; i < runners; i++) {
			try {
				ar[i].join();
			} catch (InterruptedException ignore) {
				ignore.printStackTrace();
			}
		}
		Runner.endtime = System.nanoTime();
	}

	/**
	 * Returns the next working set from the buffer.
	 * 
	 * @return The next working set.
	 */
	private static synchronized Queen get() {
		try {
			return buffer.remove(0);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public void run() {
		Queen q;
		while ((q = Runner.get()) != null) {
			q.compute();
		}
	}

	/**
	 * Sets the number of running threads.
	 * 
	 * @param runners
	 *            The number of threads.
	 */
	public static void setNumberOfRunners(int runners) {
		Runner.runners = Math.max(runners, 1);
	}

	/**
	 * Prints the time consumed for computing the number of patterns for a chess
	 * board.
	 */
	public static void printTime() {
		if (Runner.starttime != 0 && Runner.endtime != 0) {
			long time = (Runner.endtime - Runner.starttime) / 1000000;
			System.out.println("Board: "
					+ Runner.size
					+ "x"
					+ Runner.size
					+ "; patterns found: "
					+ Queen.getSum()
					+ "; Time: "
					+ ((time == 0) ? (Runner.endtime - Runner.starttime + "ns")
							: (time + "ms")) + " = "
					+ ((time / 1000) / (60 * 60)) + "h "
					+ (((time / 1000) / 60) % 60) + "m " + ((time / 1000) % 60)
					+ "s");
		}
	}

	/**
	 * The main method of this application.
	 * 
	 * @param args
	 *            The command line arguments.
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			Runner.setNumberOfRunners(Integer.parseInt(args[1]));
			Runner.createBuffer(Integer.parseInt(args[0]));
			Runner.startRunners();
			Runner.printTime();
		} else if (args.length == 3) {
			Runner.setNumberOfRunners(Integer.parseInt(args[2]));
			int x1 = Math.min(Integer.parseInt(args[0]),
					Integer.parseInt(args[1]));
			int x2 = Math.max(Integer.parseInt(args[0]),
					Integer.parseInt(args[1]));
			for (int i = x1; i < x2; i++) {
				Runner.createBuffer(i);
				Runner.startRunners();
				Runner.printTime();
			}
		} else {
			System.out.println("Usage:");
			System.out.println("<number_of_queens> <number_of_threads>");
			System.out
					.println("<number_of_queens_min> <number_of_queens_max> <number_of_threads>");
			System.out
					.println("\nSet <number_of_threads> to 1 for non-parallel computation.");
		}
	}

}
