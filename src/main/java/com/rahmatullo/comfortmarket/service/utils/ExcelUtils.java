package com.rahmatullo.comfortmarket.service.utils;

import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.ExistsException;
import com.rahmatullo.comfortmarket.service.exception.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class ExcelUtils {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static  String SHEET = "Products";


    public static boolean hasExcelFormat(MultipartFile file) {
        return !Objects.equals(file.getContentType(), TYPE);
    }

    public static Map<Integer, List<ProductRequestDto>> excelToProducts(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet(SHEET);

            if(Objects.isNull(sheet)) {
                log.warn("Sheet cannot be null");
                throw new DoesNotMatchException("Sheet cannot be null");
            }

            Map<Integer, List<ProductRequestDto>> productsTable = new HashMap<>();

            int rowNumber= 0;
            for(Row row: sheet) {
                if(rowNumber==0) {
                    rowNumber++;
                    continue;
                }

                int cellIdx = 0;
                ProductRequestDto productRequestDto = new ProductRequestDto();
                Integer key = null;

                for(Cell cell :row) {
                    switch (cellIdx) {
                        case 0 -> productRequestDto.setName(cell.getStringCellValue());
                        case 1 -> productRequestDto.setBarcode(String.valueOf(Double.valueOf(cell.getNumericCellValue())));
                        case 2 -> productRequestDto.setCount((int) cell.getNumericCellValue());
                        case 3 -> productRequestDto.setPrice(cell.getNumericCellValue());
                        case 4 -> productRequestDto.setCategoryId((long) cell.getNumericCellValue());
                        case 5 -> key = Double.valueOf(cell.getNumericCellValue()).intValue();
                        default -> {
                        }
                    }

                    cellIdx++;
                }

                if(Objects.isNull(key)) {
                    log.warn("Premise id is not given");
                    throw new ExistsException("Premise id is not exists");
                }

                if(productsTable.containsKey(key)){
                    List<ProductRequestDto> productRequestDtos = productsTable.get(key);
                    if(Objects.isNull(productsTable.get(key))){
                        productRequestDtos = new ArrayList<>();
                    }
                    List<ProductRequestDto> temp = new ArrayList<>(productRequestDtos);
                    temp.add(productRequestDto);

                    productsTable.put(key, temp);
                } else {
                    productsTable.put(key, List.of(productRequestDto));
                }
            }

            return productsTable;
        } catch (IOException e) {
            throw new FileUploadException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
