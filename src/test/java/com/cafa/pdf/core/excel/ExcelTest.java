package com.cafa.pdf.core.excel;

import com.cafa.pdf.core.dal.dao.TreatiseCategoryDAO;
import com.cafa.pdf.core.dal.entity.TreatiseCategory;
import com.cafa.pdf.core.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/4/28 19:56
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ExcelTest {

    @Autowired
    private TreatiseCategoryDAO treatiseCategoryDAO;

    @Test
    public void readExcel(){
        String filePath = "F:/t2.xls";
        List<Map<String, Object>> list = null;
        List<TreatiseCategory> treatiseCategories = new ArrayList<>(1111);
        try {
            list = ExcelUtil.readExcel2003(filePath);
            list.forEach(map -> {
                Object num = map.get("分类号");
                String numStr = num.toString();

                Object name = map.get("类名");
                String nameStr = name.toString();

//                System.out.print("分类号 = " + numStr);
//                System.out.println("  类名 = " + nameStr);

                treatiseCategories.add(new TreatiseCategory(0L, null, numStr, nameStr));
            });
            treatiseCategoryDAO.save(treatiseCategories);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
