package com.sample.system.reporting.service.batch;

import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RightToLeftSheetWriteHandlerTest {

    @Test
    void marksCreatedWorksheetAsRightToLeft() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Report");
            WriteSheetHolder sheetHolder = new WriteSheetHolder();
            sheetHolder.setSheet(sheet);

            new RightToLeftSheetWriteHandler().afterSheetCreate(null, sheetHolder);

            assertTrue(sheet.isRightToLeft());
        }
    }
}
