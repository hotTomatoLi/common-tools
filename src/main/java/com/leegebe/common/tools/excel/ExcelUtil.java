package com.leegebe.common.tools.excel;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * EXCEL工具类
 */
public class ExcelUtil implements Serializable {

    private static final long serialVersionUID = -8592636390237723340L;

    private static final Log log = LogFactory.getLog(ExcelUtil.class);

    private static final int EXCEL_MAX_PER_PAGE = 60000;

    private static final int MAX_COLUMN_WIDTH = 30;

    /**
     * 生成excel对象
     *
     * @param excelName
     * @param titles
     * @param data
     * @return
     */
    public static HSSFWorkbook generateWorkbook(String excelName,
                                                String[] titles, List<Object[]> data, String firstLine) {
        HSSFWorkbook workBook = new HSSFWorkbook();

        if (!CollectionUtils.isEmpty(data)) {//有数据
            int size = data.size();
            int page = (size % EXCEL_MAX_PER_PAGE == 0) ? (size / EXCEL_MAX_PER_PAGE) : (size / EXCEL_MAX_PER_PAGE + 1);
            for (int i = 0; i < page; i++) {
                HSSFSheet sheet = workBook.createSheet();
                if (i != page - 1) {
                    List<Object[]> dataPerPage = new ArrayList<Object[]>(EXCEL_MAX_PER_PAGE);
                    dataPerPage = data.subList(i * EXCEL_MAX_PER_PAGE, (i + 1) * EXCEL_MAX_PER_PAGE);
                    writeDataToSheet(titles, dataPerPage, firstLine, workBook, sheet);
                } else {
                    writeDataToSheet(titles, data.subList(i * EXCEL_MAX_PER_PAGE, data.size()), firstLine, workBook, sheet);
                }
            }
        } else {//没数据
            HSSFSheet sheet = workBook.createSheet();
            writeDataToSheet(titles, null, firstLine, workBook, sheet);
        }

        return workBook;
    }

    private static void writeDataToSheet(String[] titles, List<Object[]> data, String firstLine, HSSFWorkbook workBook, HSSFSheet sheet) {
        if (titles != null) {
            int contentStartIndex = 0;
            if (!StringUtils.isEmpty(firstLine)) {//第一行有提示语句
                contentStartIndex = 1;
                HSSFRow firstRow = sheet.createRow(0);//第0行
                HSSFCell cell = firstRow.createCell(0);//单元格
                cell.setCellValue(firstLine);//设置第一行的值
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titles.length - 1));//合并单元格
                HSSFCellStyle cellStyle = workBook.createCellStyle();
                cellStyle.setWrapText(true);
                cell.setCellStyle(cellStyle);
            }

            // 初始化列宽
            int[] columWidth = new int[titles.length];
            for (int i = 0; i < titles.length; i++) {
                columWidth[i] = 0;
            }

            HSSFRow titleRow = sheet.createRow(contentStartIndex);
            for (int i = 0, size = titles.length; i < size; i++) {
                HSSFCell cell = titleRow.createCell(i);
                cell.setCellValue(titles[i]);
                // 计算列宽
                int width = getStringDisplayLength(titles[i]);
                if (width > columWidth[i]) {
                    columWidth[i] = width;
                }
            }
            contentStartIndex++;
            if (data != null && data.size() != 0) {//写入Excel数据
                for (int i = 0, size = data.size(); i < size; i++) {
                    HSSFRow row = sheet.createRow(contentStartIndex + i);
                    for (int j = 0; j < data.get(i).length; j++) {
                        HSSFCell cell = row.createCell(j);
                        Object object = data.get(i)[j];
                        if (object == null) {
                            continue;
                        }
                        // 计算列宽
                        int width = getStringDisplayLength(object.toString());
                        if (width > columWidth[j]) {
                            columWidth[j] = width;
                        }
                        if (object instanceof Integer) {
                            cell.setCellValue((Integer) object);
                        } else if (object instanceof Double) {
                            cell.setCellValue((Double) object);
                        } else {
                            cell.setCellValue(object.toString());
                        }
                    }
                }
            }
            for (int i = 0; i < titles.length; i++) {
                int width = columWidth[i] + 2;
                width = width > MAX_COLUMN_WIDTH ? MAX_COLUMN_WIDTH : width;
                sheet.setColumnWidth(i, width * 256);
            }
        }
    }

    /**
     * 设置背景颜色
     *
     * @param style
     * @param color
     */
    public static void setBackGroundColor(HSSFCellStyle style, Short color) {
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(color);
    }

    /**
     * 从excel中读取String类型格式
     *
     * @param cell
     * @return
     */
    public static String getStringDataFromCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:// 数字类型
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    //用于转化为日期格式
                    Date d = cell.getDateCellValue();
                    DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                    return formater.format(d);
                } else {
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);//转成字符串格式
                    return cell.getStringCellValue();
                }
            case HSSFCell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            default:
                return cell.getStringCellValue();
        }
    }

    /**
     * 解析excel的数据格式
     *
     * @param cell
     * @return
     */
    public static String parseExcelData(Cell cell) {
        String result = new String();
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:// 数字类型
                if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                    SimpleDateFormat sdf = null;
                    if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
                            .getBuiltinFormat("h:mm")) {
                        sdf = new SimpleDateFormat("HH:mm");
                    } else {// 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                    }
                    Date date = cell.getDateCellValue();
                    result = sdf.format(date);
                } else if (cell.getCellStyle().getDataFormat() == 58) {
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    double value = cell.getNumericCellValue();
                    Date date = org.apache.poi.ss.usermodel.DateUtil
                            .getJavaDate(value);
                    result = sdf.format(date);
                } else {
                    double value = cell.getNumericCellValue();
                    CellStyle style = cell.getCellStyle();
                    DecimalFormat format = new DecimalFormat();
                    String temp = style.getDataFormatString();
                    // 单元格设置成常规
                    if (temp.equals("General")) {
                        format.applyPattern("#");
                    }
                    result = format.format(value);
                }
                break;
            case HSSFCell.CELL_TYPE_STRING:// String类型
                result = cell.getRichStringCellValue().toString();
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                result = "";
            default:
                result = "";
                break;
        }
        return result;
    }


    /**
     * 从
     *
     * @param inputStream excel类型的输入流
     * @param startRowId  数据开始的行
     * @param sheetIndex  excel的索引，默认值为0
     * @return List<List<Object>>
     * 如果无数据，返回null
     */
    public static List<List<String>> readDataFromInputStream(InputStream inputStream, int startRowId, int sheetIndex) {
        HSSFWorkbook wb = null;
        List<List<String>> data = null;
        if (inputStream == null) {
            return data;
        }
        try {
            wb = new HSSFWorkbook(inputStream);
            Sheet sheet = wb.getSheetAt(sheetIndex);
            int length = sheet.getLastRowNum();
            data = new ArrayList<List<String>>();
            for (int i = startRowId; i < length; i++) {
                HSSFRow row = (HSSFRow) sheet.getRow(i);
                int cellLength = row.getLastCellNum();
                List<String> stringList = new ArrayList<String>();
                for (int j = 0; j < cellLength; j++) {
                    HSSFCell cell = row.getCell(j);
                    String cellValue = cell.getStringCellValue();
                    stringList.add(cellValue);
                }
                data.add(stringList);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            return data;
        }
    }

    /**
     * 判断是否为空行
     *
     * @return
     */
    public static boolean isBlankRow(Row row) {
        if (row == null) {
            return true;
        }
        boolean result = true;
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i, HSSFRow.RETURN_BLANK_AS_NULL);
            String value = "";
            if (cell != null) {
                value = getCellValue(cell);
                if (!value.trim().equals("")) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 获得Excel一行的列数
     *
     * @param row
     * @return -1如果为空行
     */
    public static int getColumnIndex(Row row) {
        int index = -1;
        if (row == null) {
            return index;
        }
        for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i, HSSFRow.RETURN_BLANK_AS_NULL);
            String value = "";
            if (cell != null) {
                value = getCellValue(cell);
                if (StringUtils.isEmpty(value)) {
                    continue;
                } else {
                    index = i;
                }
            }
        }
        return index;
    }

    /**
     * 获取一个Cell的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        String value = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf((int) cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = String.valueOf(cell.getCellFormula());
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * 校验一个Excel的导入格式
     * startRowIndex的数据列是否正确(数据列长度是否大于给定的数据长度，如果大于，表明数据内容错误)
     *
     * @param hssfWorkbook
     * @param startRowIndex 起始位置
     * @param columnSize    应该有的列数
     * @return
     */
    public static boolean checkExcelFormat(HSSFWorkbook hssfWorkbook, int startRowIndex, int columnSize) {
        if (hssfWorkbook == null) {
            return false;
        }
        Sheet sheet = hssfWorkbook.getSheetAt(0);
        if (sheet.getLastRowNum() < startRowIndex) {
            return false;
        }
        for (int i = startRowIndex; i <= startRowIndex; i++) {
            int columnSizePerRow = getColumnIndex(sheet.getRow(i));
            if (columnSize != columnSizePerRow) {
                return false;
            }
        }
        return true;
    }


    /**
     * 计算字符串宽度，统计每个字符：ASCII码127以下记作1，以上记作2
     */
    private static int getStringDisplayLength(String string) {
        int length = 0;
        if (string == null || string.length() == 0) {
            return length;
        }
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c > 127) {
                length += 2;
            } else {
                length++;
            }
        }
        return length;
    }

    /**
     * byte数组转换成16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 检测输入的文件是否是Excel xls类别
     *
     * @param is
     * @return
     */
    public static boolean checkExcelType(InputStream is) throws IOException {
        boolean result = false;
        if (is != null) {
            byte[] bytes = new byte[4];
            is.read(bytes, 0, bytes.length);
            String type = bytesToHexString(bytes).toUpperCase();
            result = type.contains("D0CF11E0");
        }
        return result;
    }
}
