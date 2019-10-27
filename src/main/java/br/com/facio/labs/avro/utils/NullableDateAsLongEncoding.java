package br.com.facio.labs.avro.utils;

import java.io.IOException;
import java.util.Date;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;
import org.apache.avro.AvroRuntimeException;
/**
 * this must be generated with ReflectData.AllowNull.get().getSchema(X)
 *
 * @author Fabiano
 */
public class NullableDateAsLongEncoding extends CustomEncoding<Date> {

    {
        schema = Schema.create(Schema.Type.LONG);
        schema.addProp("CustomEncoding", "NullableDateAsLongEncoding");        
    }
    
    @Override
    protected void write(Object datum, Encoder out) throws IOException {
        if ( datum == null ) {            
            out.writeIndex(0);
            out.writeNull();
        } else {            
            out.writeIndex(1);
            out.writeLong(((Date) datum).getTime());
        }        
    }

    @Override
    protected Date read(Object reuse, Decoder in) throws IOException {
        if (reuse instanceof Date) {
            ((Date) reuse).setTime(in.readLong());
            return (Date) reuse;
        } else {
            int index = in.readIndex();
            
            switch (index) {
                case 0:    
                    in.readNull();
                    return null;
                case 1:
                    return new Date( in.readLong() );
                default:
                    throw new AvroRuntimeException("Unexpected union branch");
            }
            
        }
    }

}
