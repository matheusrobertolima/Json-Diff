
package com.matheus.jsondiff;

import java.util.List;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import static org.neo4j.driver.Values.parameters;



public class Conexao implements AutoCloseable {
    
    Driver driver = GraphDatabase.driver("bolt://localhost:7687/", AuthTokens.basic("neo4j", "jsondiff"));
    
    Pessoa pessoa = new Pessoa("AA");
    
     @Override
    public void close() throws RuntimeException {
        driver.close();
    }

  public void criaNodeNeo4j(List<Diff> lista){
      
      
  
    
    try(Session session = driver.session()){
            
                //adicionando nó pessoa
                session.run("CREATE (AA:Agente{name:$name, id: $id})",
                        parameters("name", pessoa.getName(), "id", pessoa.getId()));
                //criando os nós 
                for (Diff diff : lista) {
                //criar um if que bloqueie a entrada nesse comando quando o nó já existir. 
                //usei o MERGE, pois cria apenas um nó. sem duplicados. 
                session.run("MERGE (e:Entidade {path: $path, op: $op, name: $name, type: $type})",
                          parameters("path", diff.getPath(), "op", diff.getOp(), "name", diff.getName(), "type", diff.getType()));
                session.run("MERGE (a:Atividade {id: $id, op: $op})",
                        parameters("id", diff.getId(), "op", diff.getOp()+diff.getName()));
                session.run("CREATE(p:Propriedade {op: $op, path: $path, name: $name, type: $type})",
                          parameters("op", diff.getOp()+diff.getPath(), "path", diff.getPath(), "name", diff.getName(), "type", diff.getType()));
             
                
//                session.run ("MATCH (AA:Agente),(a:Atividade)" + 
//                "WHERE AA.name = 'AA' AND a.op = '"+diff.getOp()+diff.getName()+"'" +
//                "OPTIONAL MATCH (AA)-[:wasAssociatedWith]->(a) " +
//                "WHERE a IS NULL "+
//                "CREATE (AA)-[:wasAssociatedWith]->(a)"); 
                 
                
                   
                
//                session.run ("MATCH (AA:Agente),(a:Atividade),(e:Entidade) " + 
//                "WHERE AA.name = 'AA' AND a.op = '"+diff.getOp()+diff.getName()+"' AND e.name =  '"+diff.getName()+"' " +
//                "OPTIONAL MATCH (AA)-[:wasAssociatedWith]->(a) " +
//                "WHERE a IS NULL " +
//                "CREATE (AA)-[:wasAssociatedWith]->(a)" +
//                "WITH AA, a, e " +
//                "OPTIONAL MATCH (a)-[:wasGenerateBy]->(e)" +
//                "WHERE e IS NULL " +
//                "CREATE (a)<-[:wasGenerateBy]-(e)");
             }     
//                
//                
               
                
                    
             
    
    }finally 
        
            {
            driver.close();
            }
        // prov_entity : 

  }   
  
}
