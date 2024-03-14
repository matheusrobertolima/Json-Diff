
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

  public void criaNodeNeo4j(List<Diff> lista, Pessoa pessoa, String fileName1, String fileName2){
      
    try(Session session = driver.session()){
            
                //adicionando nÃ³ pessoa
                session.run("MERGE (AA:Agente{name:$name})",
                        parameters("name", pessoa.getName()));
                //criando os nÃ³s 
                for (Diff diff : lista) {            
                 
                session.run("MERGE (e:Entidade {name: $name, versao:$versao})",
                          parameters("name", diff.getName(), "versao", diff.getVersao()));
                session.run("CREATE (a:Atividade {op:$op, ultimoPath:$ultimoPath, name:$name})",
                        parameters("op", diff.getOp()+diff.getUltimoPath(), "ultimoPath", diff.getUltimoPath(), "name", diff.getName()));
                session.run("MERGE(p:Propriedade {op:$op, name:$name })",
                          parameters("op", diff.getOp(), "name", diff.getName()));

                    System.out.println("id: " + diff.getId());                   
                    System.out.println("op: " + diff.getOp());
                    System.out.println("path: " + diff.getPath());
                    System.out.println("value: " + diff.getValue());
                    System.out.println("name: " + diff.getName());
                    System.out.println("type: " + diff.getType());
                    System.out.println("ultimo path: " + diff.getUltimoPath());
                    System.out.println("versão: " + diff.getVersao());
                    System.out.println("----------");
                    //value, melhora do algoritmo
                    //dados do primeiro json = comparar com algum vazio
                 
                 //RELACIONANDO AGENTE E ATIVIDADE 
                    session.run("MATCH (AA:Agente), (a:Atividade)" +
                       "WHERE AA.name = '"+pessoa.getName()+"' AND a.op = '"+diff.getOp()+diff.getUltimoPath()+"'"+
                       "MERGE (AA)-[:wasAssociatedWith]->(a)");                              

                    //RELACIONANDO ATIVIDADE E PROPRIEDADE 
                    session.run("MATCH (a:Atividade), (p:Propriedade)" + 
                        "WHERE a.name = '"+diff.getName()+"' AND p.name = '"+diff.getName()+"' "+
                        "MERGE (a)-[r:wasAssociatedWith]->(p)");
                    
                 //RELACIONANDO PROPRIEDADE E ENTIDADE 
                    session.run("MATCH (p:Propriedade), (e:Entidade)" +
                        "WHERE p.name = '"+diff.getName()+"' AND e.name = '"+diff.getName()+"' "+
                        "MERGE (p)<-[:wasGeneratedBy]-(e)"); 
           }
    }finally 
        
            {
            driver.close();
            }
    
    System.out.print("Foram encontrados um total de "+lista.size()+" diferenças entre os arquivos "+fileName1+" e "+fileName2+".");

  }   
  
}