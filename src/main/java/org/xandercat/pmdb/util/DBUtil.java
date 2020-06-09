package org.xandercat.pmdb.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DBUtil {

	private static final Calendar GMT_CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
	
	public static void setGMTTimestamp(PreparedStatement ps, int parameterIndex, Date date) throws SQLException {
		setGMTTimestamp(ps, parameterIndex, (date == null)? null : new Timestamp(date.getTime()));
	}
	
	public static void setGMTTimestamp(PreparedStatement ps, int parameterIndex, Timestamp timestamp) throws SQLException {
		ps.setTimestamp(parameterIndex, timestamp, GMT_CALENDAR);
	}
	
	public static void setLocalDate(PreparedStatement ps, int parameterIndex, LocalDate date) throws SQLException {
		ps.setDate(parameterIndex, java.sql.Date.valueOf(date));
	}
	
	public static Date getDateFromGMTTimestamp(ResultSet rs, int columnIndex) throws SQLException {
		Timestamp ts = getTimestampFromGMTTimestamp(rs, columnIndex);
		return (ts == null)? null : new Date(ts.getTime());
	}
	
	public static Timestamp getTimestampFromGMTTimestamp(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getTimestamp(columnIndex, GMT_CALENDAR);
	}
}
