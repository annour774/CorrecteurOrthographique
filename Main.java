import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    static List<String> trigramme(String mot){
        List<String> trigramme = new ArrayList<>();
        int indexDebut=0;
        int indexFin=3;
        mot = "<"+mot+">";
        while(indexFin <= mot.length()){
            trigramme.add(mot.substring(indexDebut,indexFin));
            indexDebut++;
            indexFin++;
        }
        return trigramme;
    }
     static int distanceLevenshtein(String mot1, String mot2) {
        int longueur1 = mot1.length();
        int longueur2 = mot2.length();

         int[][] M = new int[longueur1 + 1][longueur2 + 1];
         for (int i = 0; i <=longueur1; i++) {
             M[i][0] = i;
         }
         for (int j = 0; j <= longueur2; j++) {
             M[0][j] = j;
         }
         for (int i = 1; i <= longueur1; i++) {
             for (int j = 1; j <= longueur2; j++) {
                 if (mot1.charAt(i - 1) == mot2.charAt(j - 1)) {
                     M[i][j] = M[i - 1][j - 1];
                 } else {
                     M[i][j] = 1 + Math.min(M[i - 1][j - 1], Math.min(M[i][j - 1], M[i - 1][j]));
                 }
             }
         }
         return M[longueur1][longueur2];
     }
     static List<String> dictionnaire(){
         String cheminFichier = "dico.txt";
         List<String> dictionnaire = new ArrayList<>();
         String ligne;
         try (BufferedReader lecteur = new BufferedReader(new FileReader(cheminFichier))) {
             while((ligne = lecteur.readLine()) != null){
                 dictionnaire.add(ligne);
             }
         } catch(IOException e){
             e.printStackTrace();
         }
         return dictionnaire;
     }
     static HashMap<String,List<String>> triTrigramme(List<String> dictionnaire){
         HashMap<String,List<String>> selection = new HashMap<>();
         for ( String mot : dictionnaire) {
             List<String> trigrammes = trigramme(mot);
             for(String trigramme : trigrammes){
                 if(!selection.containsKey(trigramme)){
                     List<String> listesDesMots = new ArrayList<>();
                     listesDesMots.add(mot);
                     selection.put(trigramme,listesDesMots);
                 }
                 else {
                     List<String> listesDesMots = selection.get(trigramme);
                     listesDesMots.add(mot);
                     selection.put(trigramme, listesDesMots);
                 }
             }
         }
         return selection;
     }
     static HashMap<String, List<String>> selection(String mot, HashMap<String,List<String>> triTrigrammes ){
         List<String> trigrammes = trigramme(mot);
         HashMap<String, List<String>> selection = new HashMap<>();
         for(String trigramme : trigrammes){
             if(triTrigrammes.containsKey(trigramme))
                 selection.put(trigramme, triTrigrammes.get(trigramme));
         }
        return selection;
     }
     static HashMap<String, Integer> nbOccurrence(HashMap<String, List<String>> selection){
         HashMap<String, Integer> nbOccurrence = new HashMap<>();
         for(List<String> mots : selection.values()){
             for(String mot : mots){
                 if(!nbOccurrence.containsKey(mot)){
                     nbOccurrence.put(mot, 1);
                 } else {
                     nbOccurrence.put(mot, nbOccurrence.get(mot) + 1 );
                 }
             }
         }
        return nbOccurrence;
     }
     static int maxOccurence(HashMap<String,Integer> nbOccurence){
        int max = 0;
         for (int occurence : nbOccurence.values()) {
             if( occurence > max ) max = occurence;
         }
         return  max ;
     }
     static void parmiCentPremiers(List<String> mots, int max,HashMap<String, Integer> nbOccurence){
         for ( String mot : nbOccurence.keySet()) {
             if(nbOccurence.get(mot) == max && mots.size() < 100 ){
                 if(!mots.contains(mot))
                    mots.add(mot);
             }
         }
     }
     static List<String> centPremiers(HashMap<String, Integer> nbOccurence){
         List<String> centPremiers = new ArrayList<>();
         int max = maxOccurence(nbOccurence);
         parmiCentPremiers(centPremiers,max,nbOccurence);
        while(centPremiers.size() < 100 && 0 < max) {
            parmiCentPremiers(centPremiers, max--, nbOccurence);
        }
         return centPremiers;
     }
     static String motCorrige(String motACorriger,List<String> centPremiers){
        int max = distanceLevenshtein(motACorriger, centPremiers.get(0));
        String resultat = centPremiers.get(0);
        for(int i = 1; i < centPremiers.size(); i++ ){
            if(distanceLevenshtein(motACorriger, centPremiers.get(i)) < max ){
                max = distanceLevenshtein(motACorriger,centPremiers.get(i));
                resultat = centPremiers.get(i);
            }        }
         return resultat;
     }
    static List<String> faute(){
        String cheminFichier = "C:\\Users\\merum\\Desktop\\tp2algo\\src\\fautes.txt";
        List<String> fautes = new ArrayList<>();
        String ligne;
        try (BufferedReader lecteur = new BufferedReader(new FileReader(cheminFichier))) {
            while((ligne = lecteur.readLine()) != null){
                fautes.add(ligne);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return fautes;
    }


    public static void main(String[] args) {
        //System.out.println(dictionnaire());
        //System.out.println(distanceLevenshtein("logarytmique","algorithmique"));
        //System.out.println(triTrigramme(dictionnaire()));
        //Scanner scanner = new Scanner(System.in);
        //System.out.print("Veuillez saisir le mot a corriger : ");
        //String mot = scanner.nextLine();
        long startTime = System.currentTimeMillis();
        HashMap<String,List<String>> triTrigrammes = triTrigramme(dictionnaire());

        for(String mot : faute()){
            HashMap<String, List<String>> selectionne = selection(mot, triTrigrammes);
            HashMap<String, Integer> nbOccurrence = nbOccurrence(selectionne);
            System.out.println(motCorrige(mot,centPremiers(nbOccurrence)));
        }
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println ("temps d'exécution: " + executionTime + " Miliseconde");

    }
}