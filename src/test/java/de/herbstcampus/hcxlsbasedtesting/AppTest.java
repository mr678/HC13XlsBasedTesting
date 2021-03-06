package de.herbstcampus.hcxlsbasedtesting;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Unit test for simple App.
 */
@RunWith(Parameterized.class)
public class AppTest {

    private List<Object> testRow;

    public AppTest(Object in1, Object in2, Object result) {
        testRow = Arrays.asList(new Object[]{in1, in2, result});
    }

    @Test
    public void testMultiply() throws IOException {
        Double in1 = (Double) testRow.get(0);
        Double in2 = (Double) testRow.get(1);
        Double expected = (Double) testRow.get(2);

        Double actual = new App().multiply(in1, in2);

        Assert.assertEquals(expected, actual);
    }

    @Parameterized.Parameters
    public static Collection herbstcampusXlsProvider() throws IOException {
        List<Object[]> result = new ArrayList<>();

        InputStream xlsFile = AppTest.class.getResourceAsStream("/test-cases.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(xlsFile);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            List<Object> resultRow = new ArrayList<>();

            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                Double value = new Double(cell.getNumericCellValue());
                resultRow.add(value);
            }
            result.add(resultRow.toArray());
        }
        return result;
    }

    @Test
    public void seleniumTest() throws InterruptedException {

        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://localhost:8080/HCWebApp/faces/index.xhtml");

       
        driver.findElement(By.id("in1")).sendKeys(String.valueOf(testRow.get(0)));
        driver.findElement(By.id("in2")).sendKeys(String.valueOf(testRow.get(1)));
        driver.findElement(By.id("buttonCalc")).click();


        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                return (d.findElement(By.id("resultGroup")).getText().trim().length() > 5);
            }
        });

        Double actual = Double.valueOf(driver.findElement(By.id("result")).getText());
        org.junit.Assert.assertEquals((Double)testRow.get(2), actual);
    }
}
