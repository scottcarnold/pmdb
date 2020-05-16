package org.xandercat.pmdb.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;

public class ExcelPorter {

	public static enum Format {
		XLS("application/vnd.ms-excel", ".xls"), 
		XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");
		
		private String contentType;
		private String extension;
		
		private Format(String contentType, String extension) {
			this.contentType = contentType;
			this.extension = extension;
		}
		
		public String getContentType() {
			return contentType;
		}
		
		public String getExtension() {
			return extension;
		}
	}
	
	private Workbook workbook;
	private Format format;
	
	public ExcelPorter(Format format) {
		this.format = format;
		if (format == Format.XLS) {
			this.workbook = new HSSFWorkbook();
		} else {
			this.workbook = new XSSFWorkbook();
		}
	}
	
	public String getContentType() {
		return format.getContentType();
	}
	
	public String getFilename(String baseFilename) {
		return baseFilename + format.getExtension();
	}
	
	public void addSheet(MovieCollection movieCollection, Collection<Movie> movies, List<String> columns) {
		Sheet sheet = workbook.createSheet(movieCollection.getName());
		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue("Title");
		int colIdx = 1;
		for (String column : columns) {
			Cell cell = row.createCell(colIdx++);
			cell.setCellValue(column);
		}
		int rowIdx = 1;
		for (Movie movie : movies) {
			row = sheet.createRow(rowIdx++);
			row.createCell(0).setCellValue(movie.getTitle());
			colIdx = 1;
			for (String column : columns) {
				Cell cell = row.createCell(colIdx++);
				cell.setCellValue(movie.getAttributeValue(column));
			}
		}
	}

	public void closeWorkbook(OutputStream outputStream) throws IOException {
		workbook.write(outputStream);
	}
}
