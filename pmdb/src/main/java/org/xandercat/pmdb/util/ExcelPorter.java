package org.xandercat.pmdb.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;

public class ExcelPorter {

	private static final int MAX_SCAN_ROW_IDX = 10;
	private static final int MAX_SCAN_COL_IDX = 10;
	private static final DataFormatter DATA_FORMATTER = new DataFormatter();
	
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
	
	private class HeaderRow {
		private int rowIdx;
		private int colStartIdx;
		private int titleIdx;
		private List<CIString> headers; 
		public HeaderRow(int rowIdx, int colStartIdx, int titleIdx, List<CIString> headers) {
			this.rowIdx = rowIdx;
			this.colStartIdx = colStartIdx;
			this.titleIdx = titleIdx;
			this.headers = headers;
		}
		public int getIndex(CIString header) {
			int idx = headers.indexOf(header);
			return (idx < 0)? -1 : colStartIdx + idx;
		}
	}
	
	//TODO: Finish switching to using the DATA_FORMATTER to pull cell values
	
	private Workbook workbook;
	private Format format;
	private List<String> sheetNames = new ArrayList<String>();
	private List<String> allColumnNames = new ArrayList<String>();
	private Map<String, HeaderRow> headerRows;
	
	public ExcelPorter(Format format) {
		this.format = format;
		if (format == Format.XLS) {
			this.workbook = new HSSFWorkbook();
		} else {
			this.workbook = new XSSFWorkbook();
		}
	}
	
	public ExcelPorter(InputStream inputStream, String fileName) throws IOException {
		if (fileName.toLowerCase().endsWith(Format.XLSX.getExtension())) {
			this.format = Format.XLSX;
			this.workbook = new XSSFWorkbook(inputStream);
		} else if (fileName.toLowerCase().endsWith(Format.XLS.getExtension())) {
			this.format = Format.XLS;
			this.workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IOException("File name should end with " + Format.XLSX.getExtension() + " or " + Format.XLS.getExtension());
		}
		for (int i=0; i<workbook.getNumberOfSheets(); i++) {
			sheetNames.add(workbook.getSheetAt(i).getSheetName());
		}
		this.headerRows = scanForHeaderRows();
		
		// remove sheets we couldn't identify a header row for
		this.sheetNames.retainAll(headerRows.keySet());
		
		// combine headers for all sheets to create master list of column names
		Set<CIString> allColumnCINames = new HashSet<CIString>();
		for (HeaderRow headerRow : headerRows.values()) {
			allColumnCINames.addAll(headerRow.headers);
		}
		for (CIString columnName : allColumnCINames) {
			allColumnNames.add(columnName.toString());
		}
	}
	
	public String getContentType() {
		return format.getContentType();
	}
	
	public String getFilename(String baseFilename) {
		return baseFilename + format.getExtension();
	}
	
	public List<String> getSheetNames() {
		return sheetNames;
	}
	
	public List<String> getAllColumnNames() {
		return allColumnNames;
	}
	
	public void addSheet(MovieCollection movieCollection, Collection<Movie> movies, List<String> columns) {
		Sheet sheet = workbook.createSheet(movieCollection.getName());
		sheetNames.add(sheet.getSheetName());
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

	private Map<String, HeaderRow> scanForHeaderRows() {
		Map<String, HeaderRow> headerRows = new HashMap<String, HeaderRow>();
		for (String sheetName : sheetNames) {
			Sheet sheet = workbook.getSheet(sheetName);
			HeaderRow headerRow = scanSheetForHeaderRow(sheet);
			if (headerRow != null) {
				headerRows.put(sheetName, headerRow);
			}
		}
		return headerRows;
	}
	
	private HeaderRow scanSheetForHeaderRow(Sheet sheet) {
		for (int r=0; r<=MAX_SCAN_ROW_IDX; r++) {
			Row row = sheet.getRow(r);
			if (row != null) {
				for (int c=0; c<=MAX_SCAN_COL_IDX; c++) {
					Cell cell = row.getCell(c);
					boolean titleRowLikelyFound = false;
					int startIdx = c;
					int titleIdx = 0;
					List<CIString> headers = new ArrayList<CIString>();
					while (cell != null && !StringUtils.isEmptyOrWhitespace(cell.getStringCellValue())) {
						String heading = cell.getStringCellValue().trim();
						String origHeading = heading;
						CIString ciHeading = new CIString(heading);
						int dupIdx = 2;
						while (headers.contains(ciHeading)) {
							heading = origHeading + " (" + dupIdx + ")";
							ciHeading = new CIString(heading);
							dupIdx++;
						}
						headers.add(ciHeading);
						if (!titleRowLikelyFound && heading.toLowerCase().indexOf("title") >= 0) {
							titleRowLikelyFound = true;
							titleIdx = c;
						}
						cell = row.getCell(++c);
					}
					if (titleRowLikelyFound) {
						return new HeaderRow(r, startIdx, titleIdx, headers);
					}
				}
			}
		}
		return null;
	}
	
	public List<Movie> getMoviesForSheet(String sheetName, List<String> includedColumns) {
		Set<CIString> ciIncludedColumns = new HashSet<CIString>();
		for (String includedColumn : includedColumns) {
			ciIncludedColumns.add(new CIString(includedColumn));
		}
		List<Movie> movies = new ArrayList<Movie>();
		Sheet sheet = workbook.getSheet(sheetName);
		HeaderRow headerRow = headerRows.get(sheetName);
		int r = headerRow.rowIdx;
		while (true) {
			Row row = sheet.getRow(++r);
			Movie movie = new Movie();
			if (row != null) {
				for (CIString ciIncludedColumn : ciIncludedColumns) {
					int idx = headerRow.getIndex(ciIncludedColumn);
					Cell cell = row.getCell(idx);
					if (cell != null && !StringUtils.isEmptyOrWhitespace(DATA_FORMATTER.formatCellValue(cell))) {
						String value = DATA_FORMATTER.formatCellValue(cell).trim();
						if (idx == headerRow.titleIdx) {
							movie.setTitle(value);
						} else {
							movie.getAttributes().put(ciIncludedColumn, value);
						}
					}
				}
			}
			if (StringUtils.isEmptyOrWhitespace(movie.getTitle())) {
				return movies;
			} else {
				movies.add(movie);
			}
		}
	}
	
	public void writeWorkbook(OutputStream outputStream) throws IOException {
		workbook.write(outputStream);
		workbook.close();
	}
}
