package com.heiheipp.dataprepare;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

public class SQLExtractTest {

    @Test
    void extractSQL() throws Exception {
        Configuration configuration = new Configuration();
        String resource = "com/heiheipp/dataprepare/CdmTransDetailMapper.xml";
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            builder.parse();

            for (MappedStatement mappedStatement : builder.getConfiguration().getMappedStatements()) {
                try {
                    System.out.println(StringUtils.rightPad(mappedStatement.getId(), 120, " ") + "||"
                            + mappedStatement.getSqlCommandType().name() + "||"
                            + mappedStatement.getBoundSql(null).getSql().replaceAll("\\n", " "));
                } catch (Exception e) {
                    // ignore
                    System.out.println(StringUtils.rightPad(mappedStatement.getId(), 120, " ") + "||"
                            + mappedStatement.getSqlCommandType().name() + "||"
                            + "手动处理");
                }
            }
        }
    }
}
