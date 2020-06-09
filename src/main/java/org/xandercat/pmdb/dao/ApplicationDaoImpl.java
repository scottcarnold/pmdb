package org.xandercat.pmdb.dao;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
		jdbcTemplate.update(sql, ps -> {
			ps.setString(1, applicationAttribute.getName());
			ps.setString(2, applicationAttribute.getValue());
			DBUtil.setLocalDate(ps, 3, applicationAttribute.getDate());			
		});	
	}

	@Override
	public void updateApplicationAttribute(ApplicationAttribute applicationAttribute) {
		final String sql = "UPDATE application_attributes SET attribute_value = ? WHERE attribute_name = ? AND attribute_date = ?";
		jdbcTemplate.update(sql, ps -> {
			ps.setString(1, applicationAttribute.getValue());
			ps.setString(2, applicationAttribute.getName());
			DBUtil.setLocalDate(ps, 3, applicationAttribute.getDate());
		});
	}

	@Override
	public Optional<ApplicationAttribute> getApplicationAttribute(String name, LocalDate date) {
		final String sql = "SELECT attribute_value FROM application_attributes WHERE attribute_name = ? AND attribute_date = ?";
		final ApplicationAttribute applicationAttribute = new ApplicationAttribute();
		jdbcTemplate.query(sql, ps -> {
			ps.setString(1, name);
			DBUtil.setLocalDate(ps, 2, date);
		}, rs -> {
			applicationAttribute.setName(name);
			applicationAttribute.setDate(date);
			applicationAttribute.setValue(rs.getString(1));
		});
		return StringUtils.isEmptyOrWhitespace(applicationAttribute.getName())? Optional.empty() : Optional.of(applicationAttribute);
	}

}
