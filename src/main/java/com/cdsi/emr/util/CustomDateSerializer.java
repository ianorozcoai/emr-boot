package com.cdsi.emr.util;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CustomDateSerializer extends StdSerializer<Date> {
  
    private SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
 
    public CustomDateSerializer() {
        this(null);
    }
 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public CustomDateSerializer(Class t) {
        super(t);
    }
     
    @Override
    public void serialize (Date value, JsonGenerator gen, SerializerProvider arg2)
      throws IOException, JsonProcessingException {
        gen.writeString(formatter.format(value));
    }
}