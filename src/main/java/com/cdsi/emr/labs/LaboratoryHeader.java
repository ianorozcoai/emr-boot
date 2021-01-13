package com.cdsi.emr.labs;

import java.util.List;
import lombok.Data;

@Data
public class LaboratoryHeader {
	
    private String dateStr;
    private int colSpan;
    private List<String> timeStr;
    
}
