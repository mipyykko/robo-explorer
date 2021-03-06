package org.mipyykko.roboexplorer.util;

import java.util.Stack;

/**
 * Apuluokka tekstitoimintoja varten.
 * 
 * @author mipyykko
 *
 */
public class Text {

	private String data;
	
	public Text(String data) {
		this.data = data;
	}
	
	/**
	 * Rivittää annetun tekstin välilyöntien tai viivojen mukaan; jos sana on yli annetun pituuden,
	 * katkaistaan se keskeltä.
	 * 
	 * @param length
	 * @return Tekstirivit sisältävä String-taulukko 
	 */
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
					cursor += lineCursor + 1;
					lineCursor = 0;
					lines++;
				}
			} else {
				lineCursor++;
			}
		}
		if (lineCursor > 0) {
			stack.push(data.substring(cursor, cursor + lineCursor));
		}
		if (!stack.isEmpty()) {
			String[] array = new String[lines];
			for (int i = lines - 1; i >= 0; i--) {
				array[i] = stack.pop();
			}
			return array;
		}
		return new String[0];
	}
}
