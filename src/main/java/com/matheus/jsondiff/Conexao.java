
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
                 
            
                session.run("MERGE (p:Propriedade {op:$op, name: $name, versao:$versao})",
                          parameters("op", diff.getOp(),"name", diff.getName(), "versao", diff.getVersao()));
                session.run("MERGE (a:Atividade {op:$op, ultimoPath:$ultimoPath, name:$name, versao:$versao})",
                        parameters("op", diff.getOp()+diff.getUltimoPath(), "ultimoPath", diff.getUltimoPath(), "name", diff.getName(), "versao", diff.getVersao()));
                session.run("CREATE(e:Entidade {op:$op, name:$name, ultimoPath:$ultimoPath, versao:$versao, value:$value })",
                          parameters("op", diff.getOp()+diff.getUltimoPath(), "name", diff.getName(), "ultimoPath", diff.getUltimoPath(), "versao", diff.getVersao(), "value", diff.getValue()));

                    System.out.println("id: " + diff.getId());                   
                    System.out.println("op: " + diff.getOp());
                    System.out.println("path: " + diff.getPath());
                    System.out.println("value: " + diff.getValue());
                    System.out.println("name: " + diff.getName());
                    System.out.println("type: " + diff.getType());
                    System.out.println("ultimo path: " + diff.getUltimoPath());
                    System.out.println("versão: " + diff.getVersao());
                    System.out.println("----------");
                   
                 
                 //RELACIONANDO AGENTE E ATIVIDADE 
                    session.run("MATCH(AA:Agente), (a:Atividade)" +
                       "WHERE AA.name = '"+pessoa.getName()+"' AND a.op = '"+diff.getOp()+diff.getUltimoPath()+"'"+
                       "MERGE (AA)<-[:wasAssociatedWith]-(a)");     //verificar a direção dos relacionamentos no PROV                          

                    //RELACIONANDO ATIVIDADE E PROPRIEDADE 
                    session.run("MATCH(a:Atividade), (e:Entidade)" + 
                          "WHERE a.op = '"+diff.getOp()+diff.getUltimoPath()+"' AND e.op = '"+diff.getOp()+diff.getUltimoPath()+"' AND a.name = '"+diff.getName()+"' AND e.name = '"+diff.getName()+"' AND a.versao = "+diff.getVersao()+" AND e.versao = "+diff.getVersao()+" " +
                           "MERGE (a)<-[:WasGeneratedBy]-(e)"); //dúvida
                    //relacionamento que já existe, não faz nada. só cria quando qndo for diferente. 
                    
                 //RELACIONANDO PROPRIEDADE E ENTIDADE 
                    session.run("MATCH(e:Entidade), (p:Propriedade)" + //tirei o ultimo path, pois as entidades estavam duplicando
                        "WHERE e.name = '"+diff.getName()+"' AND p.name = '"+diff.getName()+"' AND e.op = '"+diff.getOp()+diff.getUltimoPath()+"' AND p.op = '"+diff.getOp()+"'AND e.versao = "+diff.getVersao()+" AND p.versao = "+diff.getVersao()+" "+ 
                        "MERGE (e)<-[:AlternateOf]-(p)"); 
            
                //RELACIONANDO ENTIDADES  
                    session.run("MATCH (p1:Propriedade), (p2:Propriedade)" +
                      "WHERE p1.name = p2.name AND p2.versao = p1.versao - 1 "+
                      "MERGE (p1)<-[:WasDerivedFrom]-(p2)"); 
                    

           }
    }finally 
        
            {
            driver.close();
            }
    
    System.out.print("Foram encontrados um total de "+lista.size()+" diferenças entre os arquivos "+fileName1+" e "+fileName2+".");

  }   
  
}