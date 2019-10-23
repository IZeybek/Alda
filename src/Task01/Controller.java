package Task01;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.InputMismatchException;


public class Controller<K, V> implements ControllerInterface<K, V> {

    Dictionary<String, String> dict;

    @Override
    public void read(int n, String path) throws IOException {

        LineNumberReader in;
        in = new LineNumberReader(new FileReader(path));
        String line;
        int index = 0;
        // Text einlesen und HÃ¤figkeiten aller WÃ¶rter bestimmen:
        while ((line = in.readLine()) != null && (index < n || n == 0)) {
            String[] wf = line.split(" ");
            if (wf[0].length() == 0 || wf[1].length() == 0 || wf.length != 2) continue;
            if (n != 0) ++index;
            dict.insert(wf[0], wf[1]);
            System.out.printf("inserted -> key: %s | value: %s\n", wf[0], wf[1]);
        }
        System.out.printf("read %d words!\n", dict.size() * 2);
    }


    @Override
    public void create(String dict) {
        if (dict == null) {
            this.dict = new SortedArrayDictionary<>();
            System.out.println("SortedArrayDictionary has been created!");
        } else if (dict.equals("HashDictionary")) {
            this.dict = new HashDictionary<>(7);
            System.out.println("HashDictionary has been created!");
        } else System.out.println("Wrong implementation! try again!");


    }


    @Override
    public void print() {
        if (dict != null) {
            for (Dictionary.Entry<String, String> d : dict) {
                System.out.println(d.getKey() + ": " + d.getValue());
            }
            System.out.println();
        } else {
            System.out.println("no Dictionary has been created! create first and try again!");
        }
    }

    @Override
    public V search(K key) {
        if (key instanceof String) {
            String value = dict.search(String.valueOf(key));
            if (value != null) System.out.println("my Value of Key is: " + value);
            else System.out.println("Key not found! please insert it firs!");
            return (V) value;
        } else {
            throw new InputMismatchException("need Strings as parameters");
        }
    }

    @Override
    public void insert(K deutsch, V englisch) {
        if (deutsch instanceof String && englisch instanceof String) {
            System.out.println("i am : " + String.valueOf(deutsch));
            dict.insert(String.valueOf(deutsch), String.valueOf(englisch));
        }
    }

    @Override
    public void remove(K key) {
        if (key instanceof String) {
            String value = dict.remove(String.valueOf(key));
            if (value != null) System.out.println("my removed keys value is: " + value);
            else System.out.println("nothing to be removed! please try again!");
        } else {
            throw new InputMismatchException("need to have String as parameter");
        }
    }
}