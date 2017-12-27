package org.mipyykko.roboexplorer.util;

import java.util.Stack;

public class Text {

	private String data;
	
	public Text(String data) {
		this.data = data;
	}
	
	public String[] flowTextLines(int length) {
		length = length > 17 ? 17 : length;
		
		int lines = 1;
		int cursor = 0;
		int lineCursor = 0;
		
		StringBuilder sb = new StringBuilder();
		Stack<String> stack = new Stack<String>();
		
		while (cursor + lineCursor < data.length()) {
			if (lineCursor > length) {
				while (data.charAt(cursor + lineCursor) != ' ' 
					&& data.charAt(cursor + lineCursor) != '-' 
					&& lineCursor > 0) {
					lineCursor--;
				}
				if (lineCursor == 0) {
					stack.push(data.substring(cursor, cursor + length));
					cursor += length + 1;
					lines++;
				} else {
					stack.push(data.substring(cursor, cursor + lineCursor));
					cursor += lineCursor;
					lineCursor = 0;
					lines++;
				}
			} else {
				lineCursor++;
			}
		}
		String[] array = new String[lines];
		for (int i = lines; i >= 0; i--) {
			array[i] = stack.pop();
		}
		return array;
	}
}
