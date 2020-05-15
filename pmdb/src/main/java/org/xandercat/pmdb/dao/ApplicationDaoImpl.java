package org.xandercat.pmdb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.ApplicationAttribute;
import org.xandercat.pmdb.util.DBUtil;

@Component
public class ApplicationDaoImpl implements ApplicationDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void addApplicationAttribute(ApplicationAttribute applicationAttribute) {
		final String sql = "INSERT INTO application_attributes(attribute_name, attribute_value, attribute_date) VALUES (?, ?, ?)";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, applicationAttribute.getName());
				ps.setString(2, applicationAttribute.getValue());
				DBUtil.setLocalDate(ps, 3, applicationAttribute.getDate());
			}
		});		
	}

	@Override
	public void updateApplicationAttribute(ApplicationAttribute applicationAttribute) {
		final String sql = "UPDATE application_attributes SET attribute_value = ? WHERE attribute_name = ? AND attribute_date = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, applicationAttribute.getValue());
				ps.setString(2, applicationAttribute.getName());
				DBUtil.setLocalDate(ps, 3, applicationAttribute.getDate());
			}
		});
	}

	@Override
	public ApplicationAttribute getApplicationAttribute(String name, LocalDate date) {
		final String sql = "SELECT attribute_value FROM application_attributes WHERE attribute_name = ? AND attribute_date = ?";
		final ApplicationAttribute applicationAttribute = new ApplicationAttribute();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, name);
				DBUtil.setLocalDate(ps, 2, date);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				applicationAttribute.setName(name);
				applicationAttribute.setDate(date);
				applicationAttribute.setValue(rs.getString(1));
			}
		});
		return StringUtils.isEmptyOrWhitespace(applicationAttribute.getName())? null : applicationAttribute;
	}

}
