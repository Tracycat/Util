package com.yonyou.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.yonyou.gamc.vo.HeaderVO;


public class ExcelUtil {

	/**
	 * 简单表导出功能小方法
	 * 
	 * @param path
	 *            路径
	 * @param excelName
	 *            表名
	 * @param header
	 *            表头
	 * @param data
	 *            表内容
	 * @throws IOException
	 */
	public static void exportSimple(String path, String excelName, String[] header,
			List<List<String>> data) throws IOException {
		/*
		 * keep 100 rows in memory, exceeding rows will be flushed to disk
		 * 创建支持大数据导出的方法，默认以1000行为单位导出，此方法的原理是内存中达到1000行就写入硬盘。
		 */

		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Sheet sh = wb.createSheet();// 创建sheet
		// 样式 1 表头：粉色边线 文字居中
		CellStyle style1 = wb.createCellStyle();
		style1.setBorderBottom(CellStyle.BORDER_THIN);// 细边线
		style1.setBottomBorderColor((short) 45);// 粉色边线
		style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style1.setFillForegroundColor(IndexedColors.CORAL.index);
		style1.setBorderLeft(CellStyle.BORDER_THIN);
		style1.setLeftBorderColor((short) 45);
		style1.setBorderRight(CellStyle.BORDER_THIN);
		style1.setRightBorderColor((short) 45);
		style1.setBorderTop(CellStyle.BORDER_THIN);
		style1.setTopBorderColor((short) 45);
		style1.setAlignment(CellStyle.ALIGN_CENTER);// 左右居中
		style1.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
		// 样式 2 表内容：粉色边线 文字居左
		CellStyle style2 = wb.createCellStyle();
		style2.setBorderBottom(CellStyle.BORDER_THIN);// 细边线
		style2.setBottomBorderColor((short) 45);// 粉色边线
		style2.setBorderLeft(CellStyle.BORDER_THIN);
		style2.setLeftBorderColor((short) 45);
		style2.setBorderRight(CellStyle.BORDER_THIN);
		style2.setRightBorderColor((short) 45);
		style2.setBorderTop(CellStyle.BORDER_THIN);
		style2.setTopBorderColor((short) 45);
		style2.setAlignment(CellStyle.ALIGN_LEFT);// 左右居中
		style2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中

		// 单元格内的字符个数，计算列宽使用
		int index = 5;// 装入下面的map中，map只保存当前列最大的值
		Map<Integer, Integer> map2 = new HashMap<Integer, Integer>();
		Row row = sh.createRow(0);
		;
		for (int cellnum = 0; cellnum < header.length; cellnum++) {
			index = header[cellnum].length();
			map2.put(cellnum, index);
			Cell cell = row.createCell(cellnum);
			sh.setColumnWidth(cellnum, (index + 5) * 400);// 设置列宽
			cell.setCellValue(header[cellnum]);
			cell.setCellStyle(style1);
		}
		
		if(data != null && !data.isEmpty()){
			for (int rownum = 0; rownum < data.size(); rownum++) {
				Row row1 = sh.createRow(rownum + 1);
				for (int cellnum = 0; cellnum < data.get(rownum).size(); cellnum++) {
					Cell cell = row1.createCell(cellnum);
					cell.setCellValue(data.get(rownum).get(cellnum));
					cell.setCellStyle(style2);
					if (map2.get(cellnum) < data.get(rownum).get(cellnum).length()) {
						map2.put(cellnum, data.get(rownum).get(cellnum).length());
					}
					sh.setColumnWidth(cellnum, (map2.get(cellnum) + 1) * 512);// 设置列宽
				}
			}
		}
		FileOutputStream out = new FileOutputStream(path);
		wb.write(out);
		out.close();
		wb.dispose();// 用于清除此方法导出产生的临时文件。

	}

	
	public static void exportSimpleObject(String path, String excelName, String[] header,
			List<List<Object>> data) throws IOException {
		/*
		 * keep 100 rows in memory, exceeding rows will be flushed to disk
		 * 创建支持大数据导出的方法，默认以1000行为单位导出，此方法的原理是内存中达到1000行就写入硬盘。
		 */

		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Sheet sh = wb.createSheet();// 创建sheet
		// 样式 1 表头：粉色边线 文字居中
		CellStyle style1 = wb.createCellStyle();
		style1.setBorderBottom(CellStyle.BORDER_THIN);// 细边线
		style1.setBottomBorderColor((short) 45);// 粉色边线
		style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style1.setFillForegroundColor(IndexedColors.CORAL.index);
		style1.setBorderLeft(CellStyle.BORDER_THIN);
		style1.setLeftBorderColor((short) 45);
		style1.setBorderRight(CellStyle.BORDER_THIN);
		style1.setRightBorderColor((short) 45);
		style1.setBorderTop(CellStyle.BORDER_THIN);
		style1.setTopBorderColor((short) 45);
		style1.setAlignment(CellStyle.ALIGN_CENTER);// 左右居中
		style1.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
		// 样式 2 表内容：粉色边线 文字居左
		CellStyle style2 = wb.createCellStyle();
		style2.setBorderBottom(CellStyle.BORDER_THIN);// 细边线
		style2.setBottomBorderColor((short) 45);// 粉色边线
		style2.setBorderLeft(CellStyle.BORDER_THIN);
		style2.setLeftBorderColor((short) 45);
		style2.setBorderRight(CellStyle.BORDER_THIN);
		style2.setRightBorderColor((short) 45);
		style2.setBorderTop(CellStyle.BORDER_THIN);
		style2.setTopBorderColor((short) 45);
		style2.setAlignment(CellStyle.ALIGN_LEFT);// 左右居中
		style2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中

		// 单元格内的字符个数，计算列宽使用
		int index = 5;// 装入下面的map中，map只保存当前列最大的值
		Map<Integer, Integer> map2 = new HashMap<Integer, Integer>();
		Row row = sh.createRow(0);
		;
		for (int cellnum = 0; cellnum < header.length; cellnum++) {
			index = header[cellnum].length();
			map2.put(cellnum, index);
			Cell cell = row.createCell(cellnum);
			sh.setColumnWidth(cellnum, (index + 5) * 400);// 设置列宽
			cell.setCellValue(header[cellnum]);
			cell.setCellStyle(style1);
		}
		
		if(data != null && !data.isEmpty()){
			for (int rownum = 0; rownum < data.size(); rownum++) {
				Row row1 = sh.createRow(rownum + 1);
				for (int cellnum = 0; cellnum < data.get(rownum).size(); cellnum++) {
					Cell cell = row1.createCell(cellnum);
					
					if(data.get(rownum).get(cellnum) == null){
						cell.setCellValue("");
					}else if("java.lang.String".equals(data.get(rownum).get(cellnum).getClass().getName())){
						cell.setCellValue(data.get(rownum).get(cellnum).toString());
					}else if("java.math.BigDecimal".equals(data.get(rownum).get(cellnum).getClass().getName()) ||
							"java.lang.Integer".equals(data.get(rownum).get(cellnum).getClass().getName()) || 
							"java.lang.Double".equals(data.get(rownum).get(cellnum).getClass().getName()) || 
							"java.lang.Float".equals(data.get(rownum).get(cellnum).getClass().getName())){
						cell.setCellValue(Double.valueOf(data.get(rownum).get(cellnum).toString()));
					}else{
						cell.setCellValue(data.get(rownum).get(cellnum).toString());
					}
					
					cell.setCellStyle(style2);
					if (map2.get(cellnum) < data.get(rownum).get(cellnum).toString().length()) {
						map2.put(cellnum, data.get(rownum).get(cellnum).toString().length());
					}
					
					if(map2.get(cellnum) > 30){
						sh.setColumnWidth(cellnum, 30 * 512);// 设置列宽
					}else{
						sh.setColumnWidth(cellnum, (map2.get(cellnum) + 1) * 512);// 设置列宽
					}
				}
			}
		}
		FileOutputStream out = new FileOutputStream(path);
		wb.write(out);
		out.close();
		wb.dispose();// 用于清除此方法导出产生的临时文件。

	}
	
	/**
	 * 设动态表头
	 * 
	 * @param sh
	 * @param hcell
	 */
	public static void setCellL(Sheet sh, Map<Integer, Integer> map,
			List<List<HeaderVO>> hcell) {
		for (int i = 0; i < hcell.size(); i++) {
			Row row = sh.createRow(i);
			for (int j = 0; j < hcell.get(i).size(); j++) {
				Cell cell = row.createCell(hcell.get(i).get(j).getCol());
				cell.setCellValue(hcell.get(i).get(j).getValue());
				if (!map.containsKey(hcell.get(i).get(j).getCol())) {
					map.put(hcell.get(i).get(j).getCol(), hcell.get(i).get(j)
							.getValue().length());// 表格宽度
				} else if (map.containsKey(hcell.get(i).get(j).getCol())
						&& map.get(hcell.get(i).get(j).getCol()) < hcell.get(i)
								.get(j).getValue().length()) {
					map.put(hcell.get(i).get(j).getCol(), hcell.get(i).get(j)
							.getValue().length());// 表格宽度
				}
				cell.setCellStyle(hcell.get(i).get(j).getStyle());

			}
		}
	}

	/**
	 * 设固定表头
	 * 
	 * @param sh
	 * @param hcell
	 */
	public static void setCell(Sheet sh, Map<Integer, Integer> map, HeaderVO[][] hcell) {
		for (int i = 0; i < hcell.length; i++) {
			Row row = sh.createRow(i);
			for (int j = 0; j < hcell[i].length; j++) {
				Cell cell = row.createCell(hcell[i][j].getCol());
				cell.setCellValue(hcell[i][j].getValue());
				if (!map.containsKey(hcell[i][j].getCol())) {
					map.put(hcell[i][j].getCol(), hcell[i][j].getValue()
							.length());// 表格宽度
				} else if (map.containsKey(hcell[i][j].getCol())
						&& map.get(hcell[i][j].getCol()) < hcell[i][j]
								.getValue().length()) {
					map.put(hcell[i][j].getCol(), hcell[i][j].getValue()
							.length());// 表格宽度
				}
				cell.setCellStyle(hcell[i][j].getStyle());

			}
		}
	}

	/**
	 * 0003表 设置每一行表内容以及样式的通用方法
	 * 
	 * @param num
	 *            行数 从0开始计 方法中会自动将表头行加上。
	 * @param str0
	 *            表内容中第一列数据
	 * @param str1
	 *            表内容中第而二列数据
	 * @param str2
	 *            表内容中第三列数据
	 * @param str3
	 *            表内容中第四列数据
	 * @param str4
	 *            表内容中第五列数据
	 * @param flag
	 *            如果为false 则上月数据空着。若为true，则设置上月数据内容。
	 * @param sh
	 *            代表sheet
	 * @param style2
	 *            代表 样式
	 * @param map2
	 *            代表 控制列宽的数据
	 * @param data
	 *            本月数据 大小必须为40
	 * @param data1
	 *            上月数据 大小必须为40
	 */
	// public void setCell(int num, String str0, String str1, String str2,
	// String str3, String str4, Boolean flag, Sheet sh, CellStyle style2,
	// Map<Integer, Integer> map2, List<List<String>> data,
	// List<List<String>> data1) {
	// Row row2 = sh.createRow(num + 2);
	// Cell cell0 = row2.createCell(0);
	// cell0.setCellValue(str0);
	// cell0.setCellStyle(style2);
	// if (map2.get(0) < str0.length())
	// map2.put(0, str0.length());
	// Cell cell1 = row2.createCell(1);
	// cell1.setCellValue(str1);
	// cell1.setCellStyle(style2);
	// if (map2.get(1) < str1.length())
	// map2.put(1, str1.length());
	// Cell cell2 = row2.createCell(2);
	// cell2.setCellValue(str2);
	// cell2.setCellStyle(style2);
	// if (map2.get(2) < str2.length())
	// map2.put(2, str2.length());
	// Cell cell3 = row2.createCell(3);
	// cell3.setCellValue(str3);
	// cell3.setCellStyle(style2);
	// if (map2.get(3) < str3.length())
	// map2.put(3, str3.length());
	// Cell cell4 = row2.createCell(4);
	// cell4.setCellValue(str4);
	// cell4.setCellStyle(style2);
	// if (map2.get(4) < str4.length())
	// map2.put(4, str4.length());
	// if (flag == false) {
	// Cell cell5 = row2.createCell(5);
	// cell5.setCellValue("");
	// cell5.setCellStyle(style2);
	// } else {
	// Cell cell5 = row2.createCell(5);
	// cell5.setCellValue(data1.get(num).get(0));
	// cell5.setCellStyle(style2);
	// if (map2.get(5) < data1.get(num).get(0).length())
	// map2.put(5, data1.get(num).get(0).length());
	// }
	// for (int cellnum = 0; cellnum < data.get(num).size(); cellnum++) {
	// Cell cell = row2.createCell(cellnum + 6);
	// cell.setCellValue(data.get(num).get(cellnum));
	// cell.setCellStyle(style2);
	// if (map2.get(cellnum) < data.get(num).get(cellnum).length()) {
	// map2.put(cellnum, data.get(num).get(cellnum).length());
	// }
	// }
	// }

	// add by zhuoqingsen 2017-8-21 设置侧列内容 start
	/**
	 * 0003表 设置每一行表内容以及样式的通用方法
	 * 
	 * @param num
	 *            行数 从0开始计 方法中会自动将表头行加上。
	 * @param str0
	 *            表内容中第一列数据
	 * @param str1
	 *            表内容中第而二列数据
	 * @param str2
	 *            表内容中第三列数据
	 * @param str3
	 *            表内容中第四列数据
	 * @param sh
	 *            代表sheet
	 * @param style2
	 *            代表 样式
	 * @param map2
	 *            代表 控制列宽的数据
	 * @param data
	 *            本月数据
	 * 
	 */
	public static void setCell(int num, String str0, String str1, String str2,
			String str3, Sheet sh, CellStyle style2,
			Map<Integer, Integer> map2, List<List<String>> data) {
		Row row2 = sh.createRow(num + 2);

		Cell cell0 = row2.createCell(0);
		cell0.setCellValue(str0);
		cell0.setCellStyle(style2);
		if (map2.get(0) < str0.length())
			map2.put(0, str0.length());

		Cell cell1 = row2.createCell(1);
		cell1.setCellValue(str1);
		cell1.setCellStyle(style2);
		if (map2.get(1) < str1.length())
			map2.put(1, str1.length());

		Cell cell2 = row2.createCell(2);
		cell2.setCellValue(str2);
		cell2.setCellStyle(style2);
		if (map2.get(2) < str2.length())
			map2.put(2, str2.length());

		Cell cell3 = row2.createCell(3);
		cell3.setCellValue(str3);
		cell3.setCellStyle(style2);
		if (map2.get(3) < str3.length())
			map2.put(3, str3.length());

		for (int cellnum = 0; cellnum < data.get(num).size(); cellnum++) {
			Cell cell = row2.createCell(cellnum + 4);
			cell.setCellValue(data.get(num).get(cellnum));
			cell.setCellStyle(style2);
			if (map2.get(cellnum) < data.get(num).get(cellnum).length()) {
				map2.put(cellnum, data.get(num).get(cellnum).length());
			}
		}
	}
	
	// 给附件创建名称返回附件
			public static File getTempFile(String prefix, String suffix) {
				return getTempFile(prefix, suffix, null);
			}

			// 具体实现
			public static File getTempFile(String prefix, String suffix, File directory) {
				File file = null;
				try {
					if (directory == null || !directory.isDirectory()
							|| !directory.exists()) {
						// 在默认临时文件目录中创建一个空文件，使用给定前缀和后缀生成其名称。
						file = File.createTempFile(prefix, suffix);
					} else {
						// 在指定目录中创建一个新的空文件，使用给定的前缀和后缀字符串生成其名称。
						file = File.createTempFile(prefix, suffix, directory);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				File dest = new File(file.getParent(), prefix + suffix);
				file.renameTo(dest);
				return dest;
			}
}
