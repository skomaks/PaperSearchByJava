import java.io.*;
import java.util.*;
import com.alibaba.fastjson.*;

class Doc{
    Doc(){

    }
    String name;
    class metadata {
        class section{
            String heading;
            String text;
        }
        class reference{
            String title;
            String [] author;
            String venue;
            String citeRegEx;
            String shortCiteRegEx;
            int year;
        }
        class referenceMention{
            int referenceID;
            String context;
            int startOffset;
            int endOffset;
        }
        String source;
        String title;
        String [] authors;
        String [] emails;
        section [] sections;
        reference [] references;
        referenceMention [] referenceMentions;
        int year;
        String abstractText;
        String creator;
    }


}

public class Hello {

    public static Map <String,Set> dic=new HashMap<>();
    public static Set allFiles=new HashSet();
    public static Set answerFiles=new HashSet();
    private final static File paperFolder=new File("Papers\\");
    private static File [] paperFiles=paperFolder.listFiles();
    public static void init(){
        System.out.println("Initiating...");
        System.out.println("paperFolder:"+paperFolder.getAbsolutePath());
        System.out.println("Getting files:");
        for(var file : paperFiles){
            //System.out.println(" "+file.getAbsolutePath());
        }
        System.out.println("Initiation Complete");
    }
    public static void printFile(File file) throws IOException {
        String s;
        try (InputStream input = new FileInputStream(file.getAbsolutePath())) {
            int n;
            StringBuilder sb = new StringBuilder();
            while ((n = input.read()) != -1) {
                sb.append((char) n);
            }
            s = sb.toString();
        } // 编译器在此自动为我们写入finally并调用close()
        System.out.println(s);
    }
    public static JSONObject parseFile(File file)throws IOException {
        String s;
        try (InputStream input = new FileInputStream(file.getAbsolutePath())) {
            int n;
            StringBuilder sb = new StringBuilder();
            while ((n = input.read()) != -1) {
                sb.append((char) n);
            }
            s = sb.toString();
        } // 编译器在此自动为我们写入finally并调用close()
        JSONObject obj = JSON.parseObject(s);
        //System.out.println(JSON.toJSONString(obj));
        return obj;
    }
    public static String stringifyFile(File file) throws IOException {
        JSONObject obj;
        obj=parseFile(file);
        var doc=JSON.parse(obj.toString());
        String s=doc.toString();
        return s;
    }
    public static int getFileWordCount(String str){
        StringTokenizer sc = new StringTokenizer(str," ,\\/.\"_=()[]{}1234567890:;\'<>?~`|+");
        int n=0;
        while(sc.hasMoreTokens())
        {
            String s = sc.nextToken();
            if(s.length()>2)n++;
            //System.out.println(s);
        }
        //System.out.println("String contains "+n+" words");
        return n;
    }
    public static void expandDictionary(String str,File file){
        StringTokenizer sc = new StringTokenizer(str," ,\\/.\"_=()[]{}1234567890:;\'<>?~`|+");
        int n=sc.countTokens();
        while(sc.hasMoreTokens())
        {
            String word = sc.nextToken();
            if(word.length()<=2)continue;
            word=word.toLowerCase();//to lower case;
            if(dic.containsKey(word) && !dic.get(word).contains(file)){
                dic.get(word).add(file);
            }
            else if(!dic.containsKey(word)){
                Set set=new HashSet();
                set.add(file);
                dic.put(word,set);
            }
        }
    }

    public static void main(String[] args){
        init();
        int i=1;
        for(var file : paperFiles){
            i++;

            if(i==100)break;
            file.setReadOnly();
            allFiles.add(file);

            System.out.println("Parsing file: "+file.getAbsolutePath());
            //try{printFile(file);}catch (IOException exp){ }
            try{
            String s=stringifyFile(file);
            //System.out.println("File contains "+getFileWordCount(s)+" words");
            System.out.println(i+"files parsed");
            expandDictionary(s,file);
            //System.out.println("paper dictionary: "+dic);
            }catch (IOException exp){ }
        }
        System.out.println("All files parsed.");

        Scanner scan = new Scanner(System.in);
        answerFiles=allFiles;
        while(true){
            System.out.println("Please enter command: (s search   a append  l link)");
            String command=scan.nextLine();
            if(command.equals("s")){
                answerFiles=allFiles;
                System.out.println("Search: Please type in keywords:");
            }
            else if(command.equals("a")) {
                System.out.println("Append: Please type in appending keywords:");
            }
            else if(command.equals("l")){
                Iterator <File> it=answerFiles.iterator();
                while(it.hasNext()) {
                    var ite = it.next();
                    System.out.println("  file location:" + ite.getAbsolutePath());
                }
                    continue;
            }
            else{
                //System.out.println(" Invalid command.");
                continue;
            }
            String word=scan.next();
            word=word.toLowerCase();
            //System.out.println(word);
            if(dic.containsKey(word)){
                answerFiles.retainAll(dic.get(word));
            }
            else {
                System.out.println(" No file contains all keywords. Please try again.");
            }
            System.out.println("Papers matching above are:");
            Iterator <File> it=answerFiles.iterator();
            while(it.hasNext()){
                var ite=it.next();
                System.out.println("  file location:"+ite);
            }
        }

    }


}
