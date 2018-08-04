import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static String path = "";
    private static String keyWord = "";
    private static int maxNumber = 250;

    public static void main(String[] args) throws IOException {
        boolean quit = false;
        System.out.println(" *1* Search & Download.\n"
                + " *2* List of Options.\n"
                + " *3* Exit.");
        while (!quit) {
            System.out.println("Choice : ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    toolInterface();
                    break;
                case 2:
                    System.out.println(" *1* Search & Download.\n"
                            + " *2* List of Options.\n"
                            + " *3* Exit.");
                    break;
                case 3:
                    quit = true;
                    break;
            }
        }
    }

    private static boolean getAndDownload(String keyWord, int maxNumber) throws IOException {
        ArrayList<String> imgUrls = new ArrayList<>();
        for (int i = 0; i <= 250; i += 50) {
            Document doc = Jsoup.connect("https://www.bing.com/images/async?q=" + keyWord.replace(", ", "%2C+").replace(" ", "+") + "&first=" + i + "&count=50&relp=35&lostate=r&mmasync=1&dgState=x*0_y*0_h*0_c*6_i*176_r*29").timeout(60000).userAgent("firefox/61.0.1").get();
            Elements urls = doc.select("div.img_cont.hoff img.mimg");
            for (Element ele : urls) {
                String dirty = ele.toString();
                String clean = cleanUrl(dirty);
                if (!clean.isEmpty()) {
                    if (!imgUrls.contains(clean)) {
                        imgUrls.add(clean);
                    }
                }
            }
            Elements urls2 = doc.select("div.img_cont.hoff img.mimg.vimgld");
            for (Element ele : urls2) {
                String dirty = ele.toString();
                String clean = cleanUrl(dirty);
                if (!clean.isEmpty()) {
                    if (!imgUrls.contains(clean)) {
                        imgUrls.add(clean);
                    }
                }
            }
        }
        for (String url : imgUrls) {
            downloadImg(url);
        }
        if (new File(path).listFiles().length > 1) {
            return true;
        }
        return false;
    }

    private static String cleanUrl(String dirty) {
        String clean = "";
        Pattern p = null;
        if (!dirty.contains("https://tse1.mm.bing.net")) {
            dirty = "https://tse1.mm.bing.net" + dirty;
        }
        if (dirty.contains("data-src")) {
            p = compile("(?<=data-src=\")(.*?)(?=\" alt=)");
        } else {
            p = compile("(?<=src=\")(.*?)(?=\" alt=)");
        }
        Matcher m = p.matcher(dirty);
        while (m.find()) {
            clean = m.group();
        }
        return clean;
    }

    private static void downloadImg(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buf))) {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        Random random = new Random();
        int randName = random.nextInt(20000);
        FileOutputStream fos = new FileOutputStream(path+"/" + keyWord + "_" + randName + ".jpg");
        fos.write(response);
        fos.close();
    }

    private static void toolInterface() throws IOException {
        System.out.println("Enter The KeyWord : ");
        keyWord = scanner.nextLine();
        System.out.println("Enter Max number of results desired ( 0 for default : 250 ): ");
        int overRiddenMaxNum = scanner.nextInt();
        System.out.println("<<a path to an empty directory is advisable :)>>");
        System.out.println("Enter the Directory Path : ");
        path = scanner.next();
        if (new File(path).exists() && new File(path).isDirectory()) {
            if (overRiddenMaxNum == 0) {
                if (getAndDownload(keyWord, maxNumber)) {
                    System.out.println("Downloading finished Check ur Directory");
                    System.out.println("cd " + path);
                }
            } else {
                if (getAndDownload(keyWord, overRiddenMaxNum)) {
                    System.out.println("Downloading finished Check ur Directory");
                    System.out.println("cd " + path);
                }
            }
        } else {
            System.out.println("Directory doesn't exist or u entered the path to a file.");
        }
    }
}
