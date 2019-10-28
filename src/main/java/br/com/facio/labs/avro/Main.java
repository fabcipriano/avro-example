package br.com.facio.labs.avro;

import br.com.facio.labs.avro.model.User;
import br.com.facio.labs.avro.model.Address;
import br.com.facio.labs.avro.utils.CompressionUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author fabianocp
 */
public class Main {
    
    private static final Logger LOG = LogManager.getLogger(Main.class);
    private static final int MAX_USERS_NUMBERS = 2000;
    
    public static void main(String[] args) throws Exception {
        LOG.info("Hello World !!!");
        
        LOG.info("Creating objects ...");
        List<User> users = returnUser();
        LOG.info("Created.");

        serializeAndDeserializeUsingJava(users);
        
        serializeAndDeserializeUsingAvro(users);

        serializeDeserializeUsingAvroSchemeless(users);
    }

    private static void serializeAndDeserializeUsingAvro(List<User> users) throws IOException {
        Schema schema = loadSchema();
        
        File file = serializeObject(schema, users);        
        deserializeObject(schema, file);
    }

    private static void serializeDeserializeUsingAvroSchemeless(List<User> users) throws IOException, DataFormatException {
        Schema schema = loadSchema();
        serializeSchemelessObject(schema, users);
        deserializeSchemelessObject(schema);
    }

    private static void deserializeSchemelessObject(Schema schema) throws IOException, DataFormatException {
        LOG.info( "Schemeless deserialized begin..." );

        long start = System.currentTimeMillis();
        Path path = FileSystems.getDefault().getPath("./", "users.schemeless");
        byte[] allBytes = Files.readAllBytes(path);
        byte[] decompress = CompressionUtils.decompress(allBytes);
        BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(new ByteArrayInputStream(decompress), null);
        DatumReader<User> reader = new ReflectDatumReader<User>(schema);
        User u = null;
        try {
            do {
                u = reader.read(null, binaryDecoder);
            } while (u != null);
        } catch (EOFException eOFException) {
            LOG.warn("Finished to read avro file");
        }
        
        LOG.debug("Last user deserialized.: {} ", u);
        LOG.info( "Schemeless deserialized end time elapsed.: {} ms",(System.currentTimeMillis() - start));        
    }
    
    private static void serializeSchemelessObject(Schema schema, List<User> users) throws IOException, FileNotFoundException {
        LOG.info( "Schemeless serialized begin ..." );
        long start = System.currentTimeMillis();
        ByteArrayOutputStream avroSerialized = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(avroSerialized, null);
        DatumWriter<User> writer = new ReflectDatumWriter<User>(schema);
        for (User user : users) {
            writer.write(user, encoder);
        }
        encoder.flush();
        avroSerialized.close();
        
        File f = new File("./users.schemeless");
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(CompressionUtils.compress(avroSerialized.toByteArray()));
        fos.flush();
        fos.close();
        LOG.info( "Schemeless serialized end time elapsed.: {} ms",(System.currentTimeMillis() - start));
    }
    

    private static Schema loadSchema() throws IOException {
//        LOG.info("Generating AVRO Schema ...");
//        Schema schema = ReflectData.AllowNull.get().getSchema(User.class);
//        LOG.info("Generated AVRO Schema.");
//        LOG.debug("AVRO Schema.:\n {}", schema.toString(true));
//
//        saveSchemaToFile(schema);

        LOG.info("Loading AVRO Schema from file...");
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(new File("./users.avroschema.json"));
        return schema;
    }

    private static void saveSchemaToFile(Schema schema) throws IOException {
        File f = new File("./users.avroschema.json");
        try (FileOutputStream out = new FileOutputStream(f);
                OutputStreamWriter ow = new OutputStreamWriter(out);){
            ow.write(schema.toString(true));
        }
    }

    private static void deserializeObject(Schema schema, File file) throws IOException {
        long start = System.currentTimeMillis();
        LOG.info("Deserializing AVRO...");

        // open a file of packets
        DatumReader<User> reader = new ReflectDatumReader<User>(schema);
        DataFileReader<User> in = new DataFileReader<User>(file, reader);

        int count = 0;
        User last = null;
        for (User user : in) {
            last = user;
            count++;
        }

        // close the input file
        in.close();
        LOG.info("Deserealized AVRO. time.: {} ms", System.currentTimeMillis() - start);
        LOG.debug("User.: {}, count.: {}", last, count);
    }

    private static File serializeObject(Schema schema, List<User> users) throws IOException {
        long start = System.currentTimeMillis();
        LOG.info("Serializing AVRO...");
        File file = new File("./user.avroser");
        DatumWriter<User> writer = new ReflectDatumWriter<User>(User.class);
        DataFileWriter<User> out = new DataFileWriter<User>(writer)
                .setCodec(CodecFactory.deflateCodec(9))
                .create(schema, file);
        for (User user : users) {
            out.append(user);
        }
        out.close();
        LOG.info("Serealized AVRO. time.: {} ms", System.currentTimeMillis() - start);
        return file;
    }

    private static void serializeAndDeserializeUsingJava(List<User> users) {
        LOG.info("Serializing ...");
        long start = System.currentTimeMillis();
        try (FileOutputStream fileOut = new FileOutputStream("./user.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            
            
            for (User user: users) {
                out.writeObject(user);                
            }
        } catch (Exception e) {
            LOG.error("Failed to execute ", e);
        } finally {
            LOG.info("Serialized. time.: {} ms", System.currentTimeMillis() - start);
        }
        
        LOG.info("Deserializing ...");
        start = System.currentTimeMillis();
        try (FileInputStream fis = new FileInputStream("./user.ser");
                ObjectInputStream in = new ObjectInputStream(fis)) {
            int count = 0;
            Object last = null;
            for (int i = 0; i < MAX_USERS_NUMBERS; i++) {
                last = in.readObject();
                count++;
            }
            LOG.debug("Last User.: {}, count.: {}", last, count);
        } catch (Exception e) {
            LOG.error("Failed to execute ", e);
        } finally {
            LOG.info("Deserialized. time.: {} ms", System.currentTimeMillis() - start);
        }
    }

    private static List<User> returnUser() throws ParseException {
        List<User> users = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        User esposo = null;
        for (int i = 0; i < MAX_USERS_NUMBERS; i++) {
            Address addr1 = new Address("Rua Teste de Alcantara Luiz Fernandes Oliveira Ciceros",
                    666+i, "Bloco A apartamento 123", "Uberlândia", "Minas Gerais", "Brasil");
            Address addr2 = new Address("Rua Dom Pedro Segundo de Oliveira Fui",
                    999+i, "Bloco C apartamento 666", "São Paulo", "São Paulo", "Brasil");
            User filho = new User(i+100000, "Josefino", "de Olieira Alcantara", 2, null, addr1, sdf.parse("24/10/2019 10:33:50"));
            User filha = new User(i+200000, "Abigail", "de Olieira Alcantara", 22, null, addr2, sdf.parse("21/10/2019 22:12:21"));
            User esposa = new User(i+300000, "Cirofantia", "Estatuaria Silva de Olieira Alcantara", 32, null, addr1, sdf.parse("12/10/2019 08:50:34"));
            List<User> dependentes = new ArrayList<User>();
            dependentes.add(filho);
            dependentes.add(filha);
            dependentes.add(esposa);
            esposo = new User(i, "Josefino", "de Olieira Alcantara", 40, dependentes, addr1, null);
            users.add(esposo);
        }
        return users;
    }
}
