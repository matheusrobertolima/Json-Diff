
package com.matheus.jsondiff;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import java.nio.file.Files;  
import java.nio.file.Paths;  
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;


public class Principal {

    public static void main(String[] args) throws Exception {
            
 
            String fileName1, file1, location1;  
            String fileName2, file2, location2;
            String location3;

            fileName1 = "calculadora2.json";  
            location1 = "C:\\Users\\Matheus Lima\\Desktop\\tcc\\json file";   

            //Converte o arquivo json em String e salva na variável
            file1 = convertFileIntoString(location1+"\\"+fileName1);  

            
            fileName2 = "calculadora3.json";  
            location2 = "C:\\Users\\Matheus Lima\\Desktop\\tcc\\json file";  
            
             //Converte o arquivo json em String e salva na variável
            file2 = convertFileIntoString(location2+"\\"+fileName2); 
            
            ObjectMapper mapper = new ObjectMapper();
            
            //Lê a string colocando ela no formato JsonNode
            JsonNode node1 = mapper.readTree(file1);
            JsonNode node2 = mapper.readTree(file2);
            
            //Comparação usando o import zjsonpatch.JsonDiff retornando TODAS as diferenças
            String diff = JsonDiff.asJson(node1, node2).toString();        
              
            try {
                
            //List<String> pathsList = new ArrayList<>();
            JSONArray jsonArray2 = new JSONArray(diff);

                for (Object obj : jsonArray2) {
                    JSONObject jsonObject = (JSONObject) obj;
                    String op = jsonObject.get("op").toString();  
                    String path = jsonObject.get("path").toString();      
                    String value = jsonObject.get("value").toString();  
                    //pathsList.add(path);
                    System.out.println("op: " + op); 
                    System.out.println("path: " + path); 
                    System.out.println("value: " + value); 
                    String[] pathDividido = path.split("/");                      
                    
                    JSONObject jsonComparador = new JSONObject(file2);  
                    
                    Map<String, Integer> contagem = contarOcorrencias(pathDividido);

                    String alvo = "$Components";
                    int ocorrencias = contagem.getOrDefault(alvo, 0);                              
                    
                    for(int i=1; i < pathDividido.length ; i++){
                      
                    try {
                         
                        
                        if(pathDividido[i].equals("Properties") && pathDividido.length == 3){
                            JSONObject properties = jsonComparador.getJSONObject("Properties");
                            String nameValue = properties.getString("$Name");
                            String typeValue = properties.getString("$Type");
                            System.out.println("name: " + nameValue);   
                            System.out.println("type: " + typeValue);
                        }
                        else if(pathDividido[i].equals("$Components") && ocorrencias == 1){
                                                    
                            JSONObject properties = jsonComparador.getJSONObject(pathDividido[i - 1]);
                            
                            
                            JSONArray componentsArray = properties.getJSONArray(pathDividido[i]);
                            JSONObject secondComponent = componentsArray.getJSONObject(Integer.parseInt(pathDividido[i + 1]));
                            String nameValue = secondComponent.getString("$Name");
                            String typeValue = secondComponent.getString("$Type");
                            System.out.println("name: " + nameValue);  
                            System.out.println("type: " + typeValue);
                        }
                        else if (pathDividido[i].equals("$Components") && ocorrencias > 1 && i == 4) {
                            
                            JSONObject properties = jsonComparador.getJSONObject(pathDividido[1]);
                            
                            
                            JSONArray componentsArray = properties.getJSONArray(pathDividido[2]);
                            JSONObject secondComponent = componentsArray.getJSONObject(Integer.parseInt(pathDividido[3]));
                            JSONArray componentsArray2 = secondComponent.getJSONArray(pathDividido[4]);
                            JSONObject secondComponent2 = componentsArray2.getJSONObject(Integer.parseInt(pathDividido[5]));
                            String nameValue = secondComponent2.getString("$Name");
                            String typeValue = secondComponent2.getString("$Type");
                            System.out.println("name: " + nameValue);  
                            System.out.println("type: " + typeValue);
                            
                        }
                        }
                        catch (ArrayIndexOutOfBoundsException e) {
                        // Tratamento da exceção ArrayIndexOutOfBoundsException
                            String nameValue = "Components";
                            String typeValue = "Components";
                            System.out.println("name: " + nameValue);  
                            System.out.println("type: " + typeValue);
              
                        }
                    } 
                    
                   System.out.println("--------");
                    
                }  
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            //Localização de salvamento do diff em formato json
            location3 = "C:\\Users\\Matheus Lima\\Desktop\\tcc\\json file\\diff.json";
            
            //Método utilizando as bibliotecas BufferedWriter, FileWriter e IOException para criar o arquivo json no caminho acima
            //escreverJsonNoArquivo(formattedJson, location3);
            
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