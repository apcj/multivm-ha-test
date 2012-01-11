package org.neo4j.testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.kernel.HaConfig;
import org.neo4j.kernel.HighlyAvailableGraphDatabase;
import org.neo4j.tooling.GlobalGraphOperations;

public class UniqueFactoryPlay
{
    public static final int NUM_ATTEMPTS_PER_THREAD = 10000;
    public static final int NUM_UNIQUE_NODES = 1000;
    public static final int NUM_THREADS = 100;

    public static void main( String[] args ) throws IOException, InterruptedException
    {
        int machineId = Integer.parseInt( args[0] );

        String storeDir = "neo4j-enterprise-1.6.M03-" + machineId + "/data/graph.db";

        HashMap<String, String> config = new HashMap<String, String>();
        config.put( HaConfig.CONFIG_KEY_SERVER_ID, String.valueOf( machineId ) );
        config.put( HaConfig.CONFIG_KEY_SERVER, "localhost:6001" );
        config.put( HaConfig.CONFIG_KEY_COORDINATORS, "localhost:2181,localhost:2182,localhost:2183" );

        GraphDatabaseService database = new HighlyAvailableGraphDatabase( storeDir, config );
        System.out.println("Started Database");

        ArrayList<Thread> threads = new ArrayList<Thread>();

        for (int i = 0; i < NUM_THREADS; i++) {
            threads.add( new Thread( new CreateNodes( database ) ) );
        }

        for ( Thread thread : threads )
        {
            thread.start();
        }

        for ( Thread thread : threads )
        {
            thread.join();
        }

        for (int i = 1; i < NUM_UNIQUE_NODES; i++) {
            Node node = database.index().forNodes( "people" ).get( "key", "value" + i ).getSingle();
            System.out.printf( "%d:%d ", i, node.getId() );
        }

        int count = countAllNodesInDatabase( database );
        if (count != NUM_UNIQUE_NODES + 1) {
            throw new RuntimeException( String.format( "Found %d nodes", count ) );
        }

        database.shutdown();
    }

    private static int countAllNodesInDatabase( GraphDatabaseService database )
    {
        Iterable<Node> allNodes = GlobalGraphOperations.at( database ).getAllNodes();
        int foundNodeCount = 0;
        for ( Node node : allNodes )
        {
            foundNodeCount++;
        }
        return foundNodeCount;
    }

    static class CreateNodes implements Runnable {

        UniqueFactory.UniqueNodeFactory factory;

        CreateNodes( GraphDatabaseService database )
        {
            factory = new UniqueFactory.UniqueNodeFactory( database, "people" )
            {
                protected void initialize( Node node, Map<String, Object> stringObjectMap )
                {
                }
            };
        }

        public void run()
        {
            for(int i = 0; i < NUM_ATTEMPTS_PER_THREAD; i++)
            {
                int random = (int) (Math.random() * NUM_UNIQUE_NODES);
                String value = "value" + random;
                factory.getOrCreate( "key", value );
            }
        }
    }
}
