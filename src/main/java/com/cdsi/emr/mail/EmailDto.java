package com.cdsi.emr.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {
    private String sender;
    private String[] recipients;
    private String subject;
    private String body;
}
