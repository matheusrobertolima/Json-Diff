
package com.matheus.jsondiff;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import java.nio.file.Files;  
import java.nio.file.Paths;  
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Principal {

    public static void main(String[] args) throws Exception {
            
 
            String fileName1, file1, location1;  
            String fileName2, file2, location2;

            fileName1 = "calculadora1.json";  
            location1 = "E:\\Biblioteca\\Área de Trabalho\\tcc\\json file";   

            //Converte o arquivo json em String e salva na variável
            file1 = convertFileIntoString(location1+"\\"+fileName1);  

            
            fileName2 = "calculadora2.json";  
            location2 = "E:\\Biblioteca\\Área de Trabalho\\tcc\\json file";  
            
             //Converte o arquivo json em String e salva na variável
            file2 = convertFileIntoString(location2+"\\"+fileName2); 
            
            ObjectMapper mapper = new ObjectMapper();
            
            //Lê a string colocando ela no formato JsonNode
            JsonNode node1 = mapper.readTree(file1);
            JsonNode node2 = mapper.readTree(file2);
            
            //Comparação usando o import zjsonpatch.JsonDiff retornando TODAS as diferenças
            String diff = JsonDiff.asJson(node1, node2).toString();
            
            //Formatação para melhor visualização das diferenças
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Object[] jsonArray = gson.fromJson(diff, Object[].class);
            String formattedJson = gson.toJson(jsonArray);
            System.out.println(formattedJson);
            }
      
        //Método para fazer a leitura do arquivo e transforma-lo em string
        public static String convertFileIntoString(String file)throws Exception  
            {  

            String result;  
            result = new String(Files.readAllBytes(Paths.get(file)));  

            return result;  
            } 
    

       
            
         
        
         

}
