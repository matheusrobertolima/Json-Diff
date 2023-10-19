
package com.matheus.jsondiff;

import java.util.List;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import static org.neo4j.driver.Values.parameters;



public class Conexao implements AutoCloseable {
    
    Driver driver = GraphDatabase.driver("bolt://localhost:7687/", AuthTokens.basic("neo4j", "jsondiff"));
    

    @Override
    public void close() throws RuntimeException {
        driver.close();
    }
    
    public void criaNodeNeo4j(List<Diff> lista){
    
        try (Session session = driver.session()) {
            for (Diff diff : lista) {
                session.run("CREATE (d:Diff {op: $op, path: $path, value: $value, name: $name, type: $type})",
                        parameters("op", diff.getOp(), "path", diff.getPath(), "value", diff.getValue(), "name", diff.getName(), "type", diff.getType()));
            }
        } finally {
            driver.close();
        }
        
    }
    
    public Conexao(){}

}
