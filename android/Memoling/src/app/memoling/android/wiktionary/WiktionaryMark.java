package app.memoling.android.wiktionary;

import java.util.ArrayList;
import java.util.List;


public class WiktionaryMark {

	public static String toHtml(String str) {

		String html = str;
		html = html.replaceAll("''''([^']*)''''", "<i><b>$1</b></i>");
		html = html.replaceAll("'''([^']*)'''", "<b>$1</b>");
		html = html.replaceAll("''([^']*)''", "<i>$1</i>");

		html = html.replaceAll("======([^=]*)======", "<h6>$1</h6>");
		html = html.replaceAll("=====([^=]*)=====", "<h5>$1</h5>");
		html = html.replaceAll("====([^=]*)====", "<h4>$1</h4>");
		html = html.replaceAll("===([^=]*)===", "<h3>$1</h3>");
		html = html.replaceAll("==([^=]*)==", "<h2>$1</h2>");

		html = html.replaceAll("\\{context\\|([^\\}]*)\\}", "($1)");

		html = html.replaceAll("\\{en[-]*([^}]*\n*)\\}", "<i>$1</i>");
		html = html.replaceAll("\\{label\\|([^\\}]*)\\}", "<i>$1</i>");
		
		html = html.replaceAll("\\{defdate\\|([^\\}]*)\\}", "<small>&#91;$1&#93;</small>");

		html = html.replaceAll("\\{([^}]*)\\}", "(<i>$1</i>)");
		html = html.replaceAll("\\[([^\\] ]*)[ ]*([^\\]]*)\\]", "<a href=\"$1\">link ($2)</a>");
		
		html = processList(html);
		
		html = html.replaceAll("\\|_\\|", " ");
		html = html.replaceAll("([^\\|]*)\\|([^\\|]*)", "$1, $2");

		// html = html.replaceAll("\n", "<br/>");

		return html;
	}

	static class ListTag {
		public String Mark;
		public String Start;
		public String End;

		public ListTag(String mark, String start, String end) {
			Mark = mark;
			Start = start;
			End = end;
		}
	}

	private static String processList(String str) {

		StringBuilder sb = new StringBuilder();
		int level = 0;
		boolean inList = false;
		List<Integer> stack = new ArrayList<Integer>();
		
		String regex = "[#\\*]+[ ;:]*(.*)";
		String replace = "<li>$1</li>";
		
		for(String line : str.split("\n")) {
			String modified = line;
			
			if(line.length() < level+1) {
				continue;
			}
			
			char c = line.charAt(level);
			int type = charType(c);
			if(type > 0) {
				if(charType(line.charAt(level+1)) > 0) {
					// Start new sublist
					level++;
					type = charType(line.charAt(level));
					stack.add(type);
					modified = getStartForType(type) + modified.replaceAll(regex, replace);
					
				} else {
					if(inList == true) {
						// Same list
						modified = modified.replaceAll(regex, replace);
					} else {
						// Start new base list
						type = charType(line.charAt(level));
						stack.add(type);
						modified = getStartForType(type) + modified.replaceAll(regex, replace);
						inList = true;
					}
				}
			} else if(inList) {
				if(level == 0) {
					// End of base list
					type = stack.remove(stack.size()-1);
					modified = getEndForType(type) + modified;
					inList = false;
					
				} else if(charType(line.charAt(level-1)) > 0) {
					// End of sub list
					level--;
					type = stack.remove(stack.size()-1);
					modified = getEndForType(type) + modified.replaceAll(regex, replace);
				}
			}
			
			sb.append(modified);
		}

		return sb.toString();
	}
	
	private static int charType(char c) {
		if(c == '#') {
			return 1;
		} else if(c == '*') {
			return 2;
		}
		
		return 0;
	}
	
	private static String getStartForType(int type) {
		if(type == 1) return "<ol>";
		if(type == 2) return "<ul>";
		return "";
	}
	
	private static String getEndForType(int type) {
		if(type == 1) return "</ol>";
		if(type == 2) return "</ul>";
		return "";
	}
}
