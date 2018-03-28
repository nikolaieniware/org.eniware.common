/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Utilities for parsing values from {@link JsonNode} objects.
 *
 * @version 1.1
 * @since 1.35
 * @deprecated use {@link JsonUtils} instead
 */
@Deprecated
public final class JsonNodeUtils {

	private static final Logger LOG = LoggerFactory.getLogger(JsonNodeUtils.class);

	/**
	 * Parse a BigDecimal from a JSON object attribute value.
	 * 
	 * @param node
	 *        the JSON node (e.g. object)
	 * @param key
	 *        the attribute key to obtain from {@code node}
	 * @return the parsed {@link BigDecimal}, or <em>null</em> if an error
	 *         occurs or the specified attribute {@code key} is not available
	 */
	public static BigDecimal parseBigDecimalAttribute(JsonNode node, String key) {
		BigDecimal num = null;
		if ( node != null ) {
			JsonNode attrNode = node.get(key);
			if ( attrNode != null && !attrNode.isNull() ) {
				String txt = attrNode.asText();
				if ( txt.indexOf('.') < 0 ) {
					txt += ".0"; // force to decimal notation, so round-trip into samples doesn't result in int
				}
				try {
					num = new BigDecimal(txt);
				} catch ( NumberFormatException e ) {
					LOG.debug("Error parsing decimal attribute [{}] value [{}]: {}",
							new Object[] { key, attrNode, e.getMessage() });
				}
			}
		}
		return num;
	}

	/**
	 * Parse a Date from a JSON object attribute value.
	 * 
	 * If the date cannot be parsed, <em>null</em> will be returned.
	 * 
	 * @param node
	 *        the JSON node (e.g. object)
	 * @param key
	 *        the attribute key to obtain from {@code node}
	 * @param dateFormat
	 *        the date format to use to parse the date string
	 * @return the parsed {@link Date} instance, or <em>null</em> if an error
	 *         occurs or the specified attribute {@code key} is not available
	 */
	public static Date parseDateAttribute(JsonNode node, String key, DateFormat dateFormat) {
		Date result = null;
		if ( node != null ) {
			JsonNode attrNode = node.get(key);
			if ( attrNode != null && !attrNode.isNull() ) {
				try {
					String dateString = attrNode.asText();

					// replace "midnight" with 12:00am
					dateString = dateString.replaceAll("(?i)midnight", "12:00am");

					// replace "noon" with 12:00pm
					dateString = dateString.replaceAll("(?i)noon", "12:00pm");

					result = dateFormat.parse(dateString);
				} catch ( ParseException e ) {
					LOG.debug("Error parsing date attribute [{}] value [{}] using pattern {}: {}",
							new Object[] { key, attrNode,
									(dateFormat instanceof SimpleDateFormat
											? ((SimpleDateFormat) dateFormat).toPattern()
											: dateFormat.toString()),
									e.getMessage() });
				}
			}
		}
		return result;
	}

	/**
	 * Parse a Integer from a JSON object attribute value.
	 * 
	 * If the Integer cannot be parsed, <em>null</em> will be returned.
	 * 
	 * @param node
	 *        the JSON node (e.g. object)
	 * @param key
	 *        the attribute key to obtain from {@code node} node
	 * @return the parsed {@link Integer}, or <em>null</em> if an error occurs
	 *         or the specified attribute {@code key} is not available
	 */
	public static Integer parseIntegerAttribute(JsonNode node, String key) {
		Integer num = null;
		if ( node != null ) {
			JsonNode attrNode = node.get(key);
			if ( attrNode != null && !attrNode.isNull() ) {
				if ( attrNode.isIntegralNumber() ) {
					num = attrNode.asInt();
				} else {
					String s = attrNode.asText();
					if ( s != null ) {
						s = s.trim();
					}
					try {
						num = Integer.valueOf(s);
					} catch ( NumberFormatException e ) {
						LOG.debug("Error parsing integer attribute [{}] value [{}]: {}",
								new Object[] { key, attrNode, e.getMessage() });
					}
				}
			}
		}
		return num;
	}

	/**
	 * Parse a Long from a JSON object attribute value.
	 * 
	 * If the Long cannot be parsed, <em>null</em> will be returned.
	 * 
	 * @param node
	 *        the JSON node (e.g. object)
	 * @param key
	 *        the attribute key to obtain from {@code node} node
	 * @return the parsed {@link Long}, or <em>null</em> if an error occurs or
	 *         the specified attribute {@code key} is not available
	 */
	public static Long parseLongAttribute(JsonNode node, String key) {
		Long num = null;
		if ( node != null ) {
			JsonNode attrNode = node.get(key);
			if ( attrNode != null && !attrNode.isNull() ) {
				if ( attrNode.isIntegralNumber() ) {
					num = attrNode.asLong();
				} else {
					try {
						num = Long.valueOf(attrNode.asText());
					} catch ( NumberFormatException e ) {
						LOG.debug("Error parsing long attribute [{}] value [{}]: {}",
								new Object[] { key, attrNode, e.getMessage() });
					}
				}
			}
		}
		return num;
	}

	/**
	 * Parse a String from a JSON object attribute value.
	 * 
	 * If the String cannot be parsed, <em>null</em> will be returned.
	 * 
	 * @param node
	 *        the JSON node (e.g. object)
	 * @param key
	 *        the attribute key to obtain from {@code node} node
	 * @return the parsed {@link String}, or <em>null</em> if an error occurs or
	 *         the specified attribute {@code key} is not available
	 */
	public static String parseStringAttribute(JsonNode node, String key) {
		String s = null;
		if ( node != null ) {
			JsonNode attrNode = node.get(key);
			if ( attrNode != null && !attrNode.isNull() ) {
				s = attrNode.asText();
			}
		}
		return s;
	}

}
