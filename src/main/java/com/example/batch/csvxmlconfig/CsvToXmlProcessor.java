package com.example.batch.csvxmlconfig;

import com.example.batch.entity.CustomerModel;
import org.springframework.batch.item.ItemProcessor;


public class CsvToXmlProcessor
        implements ItemProcessor<CustomerModel,CustomerModel> {
    @Override
    public CustomerModel process(CustomerModel item) throws Exception {
        return item;
    }
}
