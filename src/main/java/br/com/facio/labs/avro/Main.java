package br.com.facio.labs.avro;

import br.com.facio.labs.avro.model.User;
import br.com.facio.labs.avro.model.Address;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
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
    private static final int MAX_USERS_NUMBERS = 1;
    
    public static void main(String[] args) throws Exception {
        LOG.info("Hello World !!!");
        
        LOG.info("Creating objects ...");
        List<User> users = returnUser();
        LOG.info("Created.");

        serializeUsingJava(users);
        
        serializeUsingAvro(users);
    }

    private static void serializeUsingAvro(List<User> users) throws IOException {
        LOG.info("Generating AVRO Schema ...");
        Schema schema = ReflectData.AllowNull.get().getSchema(User.class);
        LOG.info("Generated AVRO Schema.");
        LOG.debug("AVRO Schema.:\n {}", schema.toString(true));

        LOG.info("Serializing AVRO...");
        File file = new File("./user.avroser");
        long start = System.currentTimeMillis();
        DatumWriter<User> writer = new ReflectDatumWriter<User>(User.class);
        DataFileWriter<User> out = new DataFileWriter<User>(writer)
                .setCodec(CodecFactory.deflateCodec(9))
                .create(schema, file);
        
        for (User user : users) {
            out.append(user);
        }
        out.close();
        LOG.info("Serealized AVRO. time.: {} ms", System.currentTimeMillis() - start);
        
        LOG.info("Deserializing AVRO...");
        start = System.currentTimeMillis();

        // open a file of packets
        DatumReader<User> reader = new ReflectDatumReader<User>(schema);
        DataFileReader<User> in = new DataFileReader<User>(file, reader);

        int count = 0;
        for (User user : in) {
            LOG.debug("User.: {}, count.: {}", user, count);
            count++;
        }

        // close the input file
        in.close();
        LOG.info("Deserealized AVRO. time.: {} ms", System.currentTimeMillis() - start);
    }

    private static void serializeUsingJava(List<User> users) {
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
            User filho = new User("Josefino", "de Olieira Alcantara", 2, null, addr1, sdf.parse("24/10/2019 10:33:50"));
            User filha = new User("Abigail", "de Olieira Alcantara", 22, null, addr2, sdf.parse("21/10/2019 22:12:21"));
            User esposa = new User("Cirofantia", "Estatuaria Silva de Olieira Alcantara", 32, null, addr1, sdf.parse("12/10/2019 08:50:34"));
            List<User> dependentes = new ArrayList<User>();
            dependentes.add(filho);
            dependentes.add(filha);
            dependentes.add(esposa);
            esposo = new User("Josefino", "de Olieira Alcantara", 40, dependentes, addr1, null);
            users.add(esposo);
        }
        return users;
    }
    
}
