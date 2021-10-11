package ca.tokenizer.learn.learnhbase.token;

import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenRepositoryImplTest {
    @Autowired
    private TokenRepository tokenRepository;
    private static HBaseTestingUtility utility;
    private static final String CONTAINER = "container";
    private static final String NAMESPACE = "namespace";

    @BeforeClass
    public static void setup() throws Exception {
        utility = new HBaseTestingUtility();
        utility.cleanupTestDir();
        utility.getConfiguration().set("test.hbase.zookeeper.property.clientPort", "2181");
        utility.startMiniCluster();
        utility.getAdmin().createNamespace(NamespaceDescriptor.create(NAMESPACE).build());
        TableName t = TableName.valueOf(NAMESPACE, "qualifier");
        Table table = utility.createTable(t, "tokens");
        utility.checksumRows(table);
    }

    @Test
    public void testSave() throws Exception {
        setup();
        Token entity = new Token("", "", "", null, null);
        tokenRepository.save(entity);
        assertEquals(entity.getError(), "not configured");
        entity = new Token(CONTAINER, "VISA", "1234 1234 1234 1234", "xyz-token", null);
        tokenRepository.save(entity);
        assertNotNull(entity.getToken());
        assertEquals("xyz-token", entity.getToken());
        assertNull(entity.getError());
        entity = new Token(CONTAINER, "VISA", "1234 1234 1234 1234", "xyz-token-MODIFIED", null);
        tokenRepository.save(entity);
        assertNotNull(entity.getToken());
        assertEquals("xyz-token", entity.getToken());
        assertNull(entity.getError());
    }
}
