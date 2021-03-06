/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Common string helper utilities.
 *
 * @version 1.5
 */
public final class StringUtils {

	private StringUtils() {
		// don't construct me
	}

	/**
	 * Pattern to capture template variable names of the form
	 * <code>{name}</code>.
	 */
	public static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

	/**
	 * Replace variables in a string template with corresponding values.
	 * 
	 * <p>
	 * Template variables are encoded like <code>{name:default}</code> where the
	 * {@code :default} part is optional. The {@code name} value is treated as a
	 * key in the provided {@code variables} map, and any corresponding value
	 * found is turned into a string and replaces the template variable in the
	 * resulting string. The optional {@code default} value, if provided, will
	 * be used as the variable value if {@code name} is not found in
	 * {@code variables}.
	 * </p>
	 * 
	 * <p>
	 * Adapted from the {@code org.springframework.web.util.UriComponents}
	 * class, mimicking URI path variable substitutions.
	 * </p>
	 * 
	 * @param source
	 *        the template string to replace variables in
	 * @param variables
	 *        the variables
	 * @return the string with variables replaced, or {@literal null} if
	 *         {@code source} is {@literal null}
	 * @since 1.4
	 */
	public static String expandTemplateString(String source, Map<String, ?> variables) {
		if ( source == null ) {
			return null;
		}
		if ( source.indexOf('{') == -1 ) {
			return source;
		}
		if ( source.indexOf(':') != -1 ) {
			source = sanitizeVariableTemplate(source);
		}
		if ( variables == null ) {
			variables = Collections.emptyMap();
		}
		Matcher matcher = NAMES_PATTERN.matcher(source);
		StringBuffer sb = new StringBuffer();
		while ( matcher.find() ) {
			String match = matcher.group(1);
			Object variableValue = getVariableValue(match, variables);
			String variableValueString = getVariableValueAsString(variableValue);
			String replacement = Matcher.quoteReplacement(variableValueString);
			matcher.appendReplacement(sb, replacement);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Remove nested "{}" such as in template variables with regular
	 * expressions.
	 */
	private static String sanitizeVariableTemplate(String source) {
		int level = 0;
		StringBuilder sb = new StringBuilder();
		for ( char c : source.toCharArray() ) {
			if ( c == '{' ) {
				level++;
			}
			if ( c == '}' ) {
				level--;
			}
			if ( level > 1 || (level == 1 && c == '}') ) {
				continue;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	private static Object getVariableValue(String match, Map<String, ?> variables) {
		int colonIdx = match.indexOf(':');
		String name = (colonIdx != -1 ? match.substring(0, colonIdx) : match);
		String fallback = (colonIdx != -1 ? match.substring(colonIdx + 1) : null);
		Object val = variables.get(name);
		if ( val == null ) {
			val = fallback;
		}
		return val;
	}

	private static String getVariableValueAsString(Object variableValue) {
		return (variableValue != null ? variableValue.toString() : "");
	}

	/**
	 * Get a comma-delimited string from a collection of objects.
	 * 
	 * @param set
	 *        the set
	 * @return the comma-delimited string
	 * @see #delimitedStringFromCollection(Set, String)
	 */
	public static String commaDelimitedStringFromCollection(final Collection<?> set) {
		return delimitedStringFromCollection(set, ",");
	}

	/**
	 * Get a delimited string from a collection of objects.
	 * 
	 * <p>
	 * This will call the {@link Object#toString()} method on each object in the
	 * set, using the natural iteration ordering of the set.No attempt to escape
	 * delimiters within the set's values is done.
	 * </p>
	 * 
	 * @param set
	 *        the set
	 * @param delim
	 *        the delimiter
	 * @return the delimited string
	 */
	public static String delimitedStringFromCollection(final Collection<?> set, String delim) {
		if ( set == null ) {
			return null;
		}
		if ( delim == null ) {
			delim = "";
		}
		StringBuilder buf = new StringBuilder();
		for ( Object o : set ) {
			if ( buf.length() > 0 ) {
				buf.append(delim);
			}
			if ( o != null ) {
				buf.append(o.toString());
			}
		}
		return buf.toString();
	}

	/**
	 * Get a delimited string from a map of objects.
	 * 
	 * <p>
	 * This will call {@link #delimitedStringFromMap(Map, String, String)} using
	 * a {@code =} key value delimiter and a {@code ,} pair delimiter.
	 * </p>
	 * 
	 * @param map
	 *        the map
	 * @return the string
	 */
	public static String delimitedStringFromMap(final Map<?, ?> map) {
		return delimitedStringFromMap(map, "=", ",");
	}

	/**
	 * Get a delimited string from a map of objects.
	 * 
	 * <p>
	 * This will call the {@link Object#toString()} method on each key and value
	 * in the map, using the natural iteration ordering of the map. No attempt
	 * to escape delimiters within the map's values is done.
	 * </p>
	 * 
	 * @param map
	 *        the map
	 * @param keyValueDelim
	 *        the delimited to use between keys and values
	 * @param pairDelim
	 *        the delimiter to use betwen key/value pairs
	 * @return the string
	 */
	public static String delimitedStringFromMap(final Map<?, ?> map, String keyValueDelim,
			String pairDelim) {
		if ( map == null ) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		for ( Map.Entry<?, ?> me : map.entrySet() ) {
			if ( buf.length() > 0 ) {
				buf.append(pairDelim);
			}
			if ( me.getKey() != null ) {
				buf.append(me.getKey().toString());
			}
			buf.append(keyValueDelim);
			if ( me.getValue() != null ) {
				buf.append(me.getValue().toString());
			}
		}
		return buf.toString();
	}

	/**
	 * Get a Set via a comma-delimited string value.
	 * 
	 * @param list
	 *        the comma-delimited string
	 * @return the Set, or <em>null</em> if {@code list} is <em>null</em> or an
	 *         empty string
	 * @see #delimitedStringToSet(String, String)
	 */
	public static Set<String> commaDelimitedStringToSet(final String list) {
		return delimitedStringToSet(list, ",");
	}

	/**
	 * Get a string Set via a delimited String value.
	 * 
	 * <p>
	 * The format of the {@code list} String should be a delimited list of
	 * values. Whitespace is permitted around the delimiter, and will be
	 * stripped from the values. Whitespace is also trimmed from the start and
	 * end of the input string. The list order is preserved in the iteration
	 * order of the returned Set.
	 * </p>
	 * 
	 * @param list
	 *        the delimited text
	 * @param delim
	 *        the delimiter to split the list with
	 * @return the Set, or <em>null</em> if {@code list} is <em>null</em> or an
	 *         empty string
	 */
	public static Set<String> delimitedStringToSet(final String list, final String delim) {
		if ( list == null || list.length() < 1 ) {
			return null;
		}
		String[] data = list.trim().split("\\s*" + Pattern.quote(delim) + "\\s*");
		Set<String> s = new LinkedHashSet<String>(data.length);
		for ( String d : data ) {
			s.add(d);
		}
		return s;
	}

	/**
	 * Get string Map via a comma-delimited String value.
	 * 
	 * <p>
	 * The format of the {@code mapping} String should be:
	 * </p>
	 * 
	 * <pre>
	 * key=val[,key=val,...]
	 * </pre>
	 * 
	 * @param mapping
	 *        the delimited text
	 * @see #delimitedStringToMap(String, String, String)
	 */
	public static Map<String, String> commaDelimitedStringToMap(final String mapping) {
		return delimitedStringToMap(mapping, ",", "=");
	}

	/**
	 * Get a string Map via a delimited String value.
	 * 
	 * <p>
	 * The format of the {@code mapping} String should be:
	 * </p>
	 * 
	 * <pre>
	 * key=val[,key=val,...]
	 * </pre>
	 * 
	 * <p>
	 * The record and field delimiters are passed as parameters to this method.
	 * Whitespace is permitted around all delimiters, and will be stripped from
	 * the keys and values. Whitespace is also trimmed from the start and end of
	 * the input string.
	 * </p>
	 * 
	 * @param mapping
	 *        the delimited text
	 * @param recordDelim
	 *        the key+value record delimiter
	 * @param fieldDelim
	 *        the key+value delimiter
	 */
	public static Map<String, String> delimitedStringToMap(final String mapping,
			final String recordDelim, final String fieldDelim) {
		if ( mapping == null || mapping.length() < 1 ) {
			return null;
		}
		final String[] pairs = mapping.trim().split("\\s*" + Pattern.quote(recordDelim) + "\\s*");
		final Map<String, String> map = new LinkedHashMap<String, String>();
		final Pattern fieldSplit = Pattern.compile("\\s*" + Pattern.quote(fieldDelim) + "\\s*");
		for ( String pair : pairs ) {
			String[] kv = fieldSplit.split(pair, 2);
			if ( kv == null || kv.length != 2 ) {
				continue;
			}
			map.put(kv[0], kv[1]);
		}
		return map;
	}

	/**
	 * Create an array of regular expressions from strings. If
	 * {@code expressions} is <em>null</em> or empty, the result will be
	 * <em>null</em>. Pass {@bold 0} for {@code flags} if no special flags are
	 * desired.
	 * 
	 * @param expressions
	 *        the array of expressions to compile into {@link Pattern} objects
	 * @param flags
	 *        the Pattern flags to use, or {@bold 0} for no flags
	 * @return the compiled regular expressions, in the same order as
	 *         {@code expressions}, or <em>null</em> if no expressions supplied
	 * @throws PatternSyntaxException
	 *         If an expression's syntax is invalid
	 */
	public static Pattern[] patterns(final String[] expressions, int flags) {
		Pattern[] result = null;
		if ( expressions != null && expressions.length > 0 ) {
			result = new Pattern[expressions.length];
			for ( int i = 0, len = expressions.length; i < len; i++ ) {
				result[i] = (flags == 0 ? Pattern.compile(expressions[i])
						: Pattern.compile(expressions[i], flags));
			}
		}
		return result;
	}

	/**
	 * Create an array of expression strings from Pattern objects. If
	 * {@code patterns} is <em>null</em> or empty, the result will be
	 * <em>null</em>.
	 * 
	 * @param patterns
	 *        the array of Pattern objects to convert to strings (may be
	 *        <em>null</em>)
	 * @return the string expressions, in the same order as {@code patterns}, or
	 *         <em>null</em> if no patterns supplied
	 */
	public static String[] expressions(final Pattern[] patterns) {
		String[] results = null;
		if ( patterns != null && patterns.length > 0 ) {
			results = new String[patterns.length];
			for ( int i = 0, len = patterns.length; i < len; i++ ) {
				results[i] = patterns[i].pattern();
			}
		}
		return results;
	}

	/**
	 * Test if a string matches any one of a list of patterns. The
	 * {@code patterns} list will be tested one at a time, in array order. The
	 * first result that matches will be returned. If no match is found,
	 * <em>null</em> is returned.
	 * 
	 * @param patterns
	 *        the patterns to test (may be <em>null</em>)
	 * @param text
	 *        the string to test (may be <em>null</em>)
	 * @return a {@link Matcher} that matches {@code text} or <em>null</em> if
	 *         no match was found
	 */
	public static Matcher matches(final Pattern[] patterns, String text) {
		if ( patterns == null || patterns.length < 0 || text == null ) {
			return null;
		}
		for ( Pattern pattern : patterns ) {
			Matcher m = pattern.matcher(text);
			if ( m.matches() ) {
				return m;
			}
		}
		return null;
	}

}
