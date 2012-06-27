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

/**
 * A class representing a working set for {@link Runner}s. It is a sub-problem
 * of the n-queens-problem.
 * 
 * @author Florian Bausch
 * 
 */
public class Queen {
	/**
	 * The sum of patterns found.
	 */
	private static volatile long sum = 0L;

	/**
	 * An array representing the chess board.
	 */
	private boolean[][] board; // board[x-axis][y-axis]
	/**
	 * An array representing the diagonals of the chess board.
	 */
	private boolean[] diag1;
	/**
	 * An array representing the diagonals of the chess board.
	 */
	private boolean[] diag2;
	/**
	 * An array representing the rows of the chess board.
	 */
	private boolean[] row;
	/**
	 * The size of the chess board.
	 */
	private int size;
	/**
	 * Number of patterns found.
	 */
	private long count = 0L;
	/**
	 * Indicates whether the patterns should be printed to stdout.
	 */
	private boolean printPat = false;

	/**
	 * Initalizes a board with one preset queen.
	 * 
	 * @param size
	 *            The size of the board.
	 * @param q
	 *            The position of the queen on the board.
	 */
	public Queen(int size, int q) {
		Queen.sum = 0L;
		// Initialize the board and set the first queen.
		this.board = new boolean[size][size];
		this.size = size;
		this.diag1 = new boolean[2 * size - 1];
		this.diag2 = new boolean[2 * size - 1];
		this.row = new boolean[size];
		this.board[0][q] = true;
		this.row[q] = true;
		this.diag1[q] = true;
		this.diag2[-q + this.size - 1] = true;
	}

	/**
	 * Starts the computation.
	 * 
	 * @return The number of patterns found with this preset queen.
	 */
	public long compute() {
		this.setQueen(1);
		Queen.sum += count;
		return count;
	}

	/**
	 * Returns the number of total found patterns.
	 * 
	 * @return The sum.
	 */
	public static long getSum() {
		return Queen.sum;
	}

	/**
	 * Prints a pattern to stdout.
	 */
	private void printPattern() {
		int i, j;
		String row;
		for (j = this.size - 1; j >= 0; j--) {
			row = "";
			for (i = 0; i < this.size; i++) {
				row += (this.board[i][j] ? "Q" : "x") + " ";
			}
			System.out.println(row);
		}
		System.out.println("");
	}

	/**
	 * Sets recursively queens to the board.
	 * 
	 * @param k
	 */
	private void setQueen(int k) {
		int j;
		for (j = 0; j < this.size; j++) {
			if (this.row[j] || this.diag1[k + j]
					|| this.diag2[k - j + this.size - 1])
				continue;
			this.board[k][j] = true;
			this.row[j] = true;
			this.diag1[k + j] = true;
			this.diag2[k - j + this.size - 1] = true;
			if (k < this.size - 1) {
				this.setQueen(k + 1);
			} else {
				++this.count;
				if (this.printPat)
					this.printPattern();
			}
			this.board[k][j] = false;
			this.row[j] = false;
			this.diag1[k + j] = false;
			this.diag2[k - j + this.size - 1] = false;
		}
	}
}
