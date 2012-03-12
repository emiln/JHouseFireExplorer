import java.io.*;
import java.util.*;
import java.awt.*;
import dk.brics.automaton.*;

public class Util {
    public static boolean isPossible4chanAutomaton(Automaton aut) {
        try {
            RegExp reg = new RegExp("(.|\".\"|\"/\"){8}");
            Automaton legal = reg.toAutomaton();
            return !aut.intersection(legal).isEmpty();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    public static boolean isValidRegExp(String regExp) {
        try {
            RegExp re = new RegExp(regExp);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
    public static void writeTextToFile(String text, String path) {
        try {
            FileWriter fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(text);
            bw.close();
        } catch (IOException e) {
        }
    }

    public static Iterator<String> readLinesFromString(String str) {
        try {
            StringReader sr = new StringReader(str);
            BufferedReader reader = new BufferedReader(sr);
            final LinkedList<String> list = new LinkedList<String>();
            String temp;
            while ((temp = reader.readLine()) != null) {
                if (temp.length() > 0) {
                    list.add(temp);
                }
            }
            return new Iterator<String>() {
                public String next() {
                    return list.remove();
                }
                public boolean hasNext() {
                    return list.size() > 0;
                }
                public void remove() {
                    list.remove();
                }
                private LinkedList<String> lines = list;
            };
        } catch (IOException e) {
            return null;
        }
    }

    public static Iterator<String> readLinesFromFile(String path) {
        final FileInputStream fis;
        final BufferedReader buf;
        final String first;
        try {
            fis = new FileInputStream(path);
            buf = new BufferedReader(new InputStreamReader(fis));
            first = buf.readLine();
        } catch (IOException e) {
            return null;
        }
        return new Iterator<String>() {
            {
                if (first != null) {
                    str = first;
                    hasNext = true;
                } else {
                    str = null;
                    hasNext = false;
                }
            }
            public boolean hasNext() {
                return hasNext;
            }
            public String next() {
                String last = str;
                try {
                    str = br.readLine();
                } catch (IOException e) {
                    str = null;
                }
                hasNext = (str != null);
                return last;
            }
            public void remove() {
                try {
                    br.readLine();
                } catch (IOException e) {
                }
            }
            private BufferedReader br = buf;
            private String str;
            private boolean hasNext;
        };
    }
    public static Color BG_COLOR = Color.white;
}
