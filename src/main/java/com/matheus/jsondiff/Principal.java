
package com.matheus.jsondiff;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import java.nio.file.Files;  
import java.nio.file.Paths;  
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;


public class Principal {

    public static void main(String[] args) throws Exception {
            
 
            String fileName1, file1, location1;  
            String fileName2, file2, location2;

            fileName1 = "Screen1.json";  
            location1 = "C:\\Users\\Matheus Lima\\Desktop\\tcc";   

            //Converte o arquivo json em String e salva na variável
            file1 = convertFileIntoString(location1+"\\"+fileName1);  

            
            fileName2 = "Screen1_2.json";  
            location2 = "C:\\Users\\Matheus Lima\\Desktop\\tcc";  
            
             //Converte o arquivo json em String e salva na variável
            file2 = convertFileIntoString(location2+"\\"+fileName2); 
            
            ObjectMapper mapper = new ObjectMapper();
            
            //Lê a string colocando ela no formato JsonNode
            JsonNode node1 = mapper.readTree(file1);
            JsonNode node2 = mapper.readTree(file2);
            
            //Comparação usando o import zjsonpatch.JsonDiff retornando TODAS as diferenças
            String diff = JsonDiff.asJson(node1, node2).toString();    
            
            System.out.println(diff);
              
            try {
                
            //List<String> pathsList = new ArrayList<>();
            JSONArray jsonArray2 = new JSONArray(diff);
            List<Diff> diferencaList = new ArrayList<>();       

                for (Object obj : jsonArray2) {
                    JSONObject jsonObject = (JSONObject) obj;
                    Diff diferenca = new Diff();
                    
                    diferenca.setOp(jsonObject.get("op").toString());  
                    diferenca.setPath(jsonObject.get("path").toString());   
                    if(!diferenca.getOp().equals("remove")){
                    diferenca.setValue(jsonObject.get("value").toString());  
                    }
                    //pathsList.add(path);
                    String[] pathDividido = diferenca.getPath().split("/");                      
                    
                    JSONObject jsonComparador = new JSONObject(file2);  
                    
                    Map<String, Integer> contagem = contarOcorrencias(pathDividido);

                    String alvo = "$Components";
                    int ocorrencias = contagem.getOrDefault(alvo, 0);                              
                    
                    for(int i=1; i < pathDividido.length ; i++){
                      
                    try {
                         
                        
                        if(pathDividido[i].equals("Properties") && pathDividido.length == 3){
                            JSONObject properties = jsonComparador.getJSONObject("Properties");
                            diferenca.setName(properties.getString("$Name"));
                            diferenca.setType(properties.getString("$Type"));
                        }
                        else if(pathDividido[i].equals("$Components") && ocorrencias == 1){
                                                    
                            JSONObject properties = jsonComparador.getJSONObject(pathDividido[i - 1]);
                            
                            
                            JSONArray componentsArray = properties.getJSONArray(pathDividido[i]);
                            JSONObject secondComponent = componentsArray.getJSONObject(Integer.parseInt(pathDividido[i + 1]));
                            diferenca.setName(secondComponent.getString("$Name"));
                            diferenca.setType(secondComponent.getString("$Type"));
                        }
                        else if (pathDividido[i].equals("$Components") && ocorrencias > 1 && i == 4) {
                            
                            JSONObject properties = jsonComparador.getJSONObject(pathDividido[1]);
                            
                            
                            JSONArray componentsArray = properties.getJSONArray(pathDividido[2]);
                            JSONObject secondComponent = componentsArray.getJSONObject(Integer.parseInt(pathDividido[3]));
                            JSONArray componentsArray2 = secondComponent.getJSONArray(pathDividido[4]);
                            JSONObject secondComponent2 = componentsArray2.getJSONObject(Integer.parseInt(pathDividido[5]));
                            diferenca.setName(secondComponent2.getString("$Name"));
                            diferenca.setType(secondComponent2.getString("$Type"));
                            
                        }
                        }
                        catch (ArrayIndexOutOfBoundsException e) {
                        // Tratamento da exceção ArrayIndexOutOfBoundsException
                            diferenca.setName("Components");
                            diferenca.setType("Components");
              
                        }
                    }                    
                  
                    diferencaList.add(diferenca);
                    
                } 
                
                Conexao conexao = new Conexao();
               conexao.criaNodeNeo4j(diferencaList);
                
                
                for(Diff diff2:diferencaList){
                    System.out.println("op: " + diff2.getOp());
                    System.out.println("path: " + diff2.getPath());
                    System.out.println("value: " + diff2.getValue());
                    System.out.println("name: " + diff2.getName());
                    System.out.println("type: " + diff2.getType());
                    System.out.println("----------");
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            
            }
      
        //Método para fazer a leitura do arquivo e transforma-lo em string
        public static String convertFileIntoString(String file)throws Exception  
        {  

            String result;  
            result = new String(Files.readAllBytes(Paths.get(file)));  

            return result;  
        } 
    
        private static void escreverJsonNoArquivo(String jsonString, String caminhoArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            // Escrever a string JSON no arquivo
            writer.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        
        public static boolean isNumeric(String str) {
        try {
            int d = Integer.parseInt(str);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
        }
        
        public static Map<String, Integer> contarOcorrencias(String[] lista) {
        Map<String, Integer> contagem = new HashMap<>();
        
        for (String str : lista) {
            contagem.put(str, contagem.getOrDefault(str, 0) + 1);
        }
        
        return contagem;
        }
       
        
} 