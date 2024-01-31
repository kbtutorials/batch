package com.example.batch.csvtoxmlconversion;

import org.springframework.batch.item.ItemProcessor;

public class CsvToXmlConversionProcessor
        implements ItemProcessor<CsvToXmlConversion,CsvToXmlConversion> {
    @Override
    public CsvToXmlConversion process(CsvToXmlConversion item) throws Exception {
        return item;
    }
}
