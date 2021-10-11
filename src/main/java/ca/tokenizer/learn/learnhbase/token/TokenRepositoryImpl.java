package ca.tokenizer.learn.learnhbase.token;

import ca.tokenizer.learn.learnhbase.model.configuration.HBaseOptions;
import ca.tokenizer.learn.learnhbase.model.configuration.TokenProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class TokenRepositoryImpl implements TokenRepository {
    private static final byte[] FAMILY = Bytes.toBytes("tokens");
    private static final byte[] QUALIFIER = Bytes.toBytes("token");
    private final int threads;
    private final Configuration conf;
    private final HBaseOptions hbaseOptions;
    private final TokenProperties tokenProperties;
    Map<String, TableName> containerToTable = new HashMap<>();
    final Connection connection;

    @Autowired
    public TokenRepositoryImpl(HBaseOptions hbaseOptions, TokenProperties tokenProperties) throws IOException {
        this.hbaseOptions = hbaseOptions;
        this.tokenProperties = tokenProperties;
        tokenProperties.getMapping().stream().forEach((t) ->
                containerToTable.put(t.getContainer(), TableName.valueOf(hbaseOptions.getNamespace(), t.getHbasetable()))
        );
        this.threads = Runtime.getRuntime().availableProcessors() * 4;
        ExecutorService service = Executors.newFixedThreadPool(threads);
        conf = HBaseConfiguration.create();
        connection = ConnectionFactory.createConnection(conf, service);
        // Only do this if the number of regions in a table is easy to fit into memory.
        for (TableName t : new HashSet<TableName>(containerToTable.values())) {
            warmUpConnectionCache(connection, t);
        }
    }

    private void warmUpConnectionCache(Connection connection, TableName tn) throws IOException {
        try (RegionLocator locator = connection.getRegionLocator(tn)) {
            log.info("Warmed up region location cache for " + tn
                    + " got " + locator.getAllRegionLocations().size());
        }
    }

    @Override
    public Token save(Token entity) throws IOException {
        TableName tableName = containerToTable.get(entity.getContainer());
        if (tableName == null) {
            entity.setToken(null);
            entity.setError("not configured");
            return entity;
        }
        try (Table table = connection.getTable(tableName)) {
            byte[] row = Bytes.toBytes(entity.getField());
            Get get = new Get(row);
            Result result = table.get(get);
            byte[] value = result.getValue(FAMILY, QUALIFIER);
            if (value != null) {
                String persisted = Text.decode(value);
                entity.setToken(persisted);
                return entity;
            }
            byte[] token = entity.getToken() != null ? Bytes.toBytes(entity.getToken()) : generateToken();
            Put append = new Put(row);
            append.addColumn(FAMILY, QUALIFIER, token);
            CheckAndMutate checkAndMutate = CheckAndMutate.newBuilder(row).ifNotExists(FAMILY, QUALIFIER).build(append);
            CheckAndMutateResult r = table.checkAndMutate(checkAndMutate);
            if (!r.isSuccess()) {
                get = new Get(row);
                result = table.get(get);
                value = result.getValue(FAMILY, QUALIFIER);
                String persisted = Text.decode(value);
                entity.setToken(persisted);
            }
        }
        return entity;
    }

    private byte[] generateToken() {
        return Bytes.toBytes(StringUtils.remove(UUID.randomUUID().toString(), '-'));
    }
}
