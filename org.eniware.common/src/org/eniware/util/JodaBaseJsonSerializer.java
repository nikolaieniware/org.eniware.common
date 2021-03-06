/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

/**
 * Abstract {@link JsonSerializer} class for converting Joda objects into simple
 * strings.
 *
 * @version 1.1
 */
public abstract class JodaBaseJsonSerializer<T> extends StdScalarSerializer<T> {

	private static final long serialVersionUID = -7898014390210021054L;

	private final DateTimeFormatter formatter;

	/**
	 * Construct from a String date pattern.
	 * 
	 * @param clazz
	 *        the class type
	 * @param pattern
	 *        the Joda date format pattern
	 */
	public JodaBaseJsonSerializer(Class<T> clazz, String pattern) {
		this(clazz, pattern, null);
	}

	/**
	 * Construct from a String date pattern.
	 * 
	 * @param clazz
	 *        the class type
	 * @param pattern
	 *        the Joda date format pattern
	 * @param timeZone
	 *        the time zone to format in
	 */
	public JodaBaseJsonSerializer(Class<T> clazz, String pattern, TimeZone timeZone) {
		super(clazz);
		if ( timeZone != null ) {
			formatter = DateTimeFormat.forPattern(pattern).withZone(DateTimeZone.forTimeZone(timeZone));
		} else {
			formatter = DateTimeFormat.forPattern(pattern);
		}
	}

	/**
	 * Serialize a JodaTime object into a string using the configured formatter.
	 * 
	 * @param propertyValue
	 *        the JodaTime object
	 * @return the String, or <em>null</em> if {@code propertyValue} is
	 *         <em>null</em>
	 * @throws IllegalArgumentException
	 *         if {@code propertyValue} is not a supported JodaTime object
	 */
	public final String serializeWithFormatter(Object propertyValue) {
		if ( propertyValue == null ) {
			return null;
		} else if ( propertyValue instanceof ReadableInstant ) {
			return formatter.print((ReadableInstant) propertyValue);
		} else if ( propertyValue instanceof ReadablePartial ) {
			return formatter.print((ReadablePartial) propertyValue);
		} else if ( propertyValue instanceof Date ) {
			return formatter.print(((Date) propertyValue).getTime());
		} else if ( propertyValue instanceof Calendar ) {
			return formatter.print(((Calendar) propertyValue).getTimeInMillis());
		}
		throw new IllegalArgumentException(
				"Unsupported date object [" + propertyValue.getClass() + "]: " + propertyValue);
	}

}
