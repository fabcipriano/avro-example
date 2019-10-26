package br.com.facio.labs.avro.utils;

import java.io.IOException;
import java.util.Date;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

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
            out.writeIndex(1);
            out.writeLong(0);
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
            long index = in.readIndex();
            long time = in.readLong();
            if ( time == 0 ) {
                return null;
            }
            return new Date( time );
        }
    }

}
