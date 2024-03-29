package Task04;

import java.awt.*;
import java.util.List;
import java.util.*;

//Klasse zur Verwaltung von Telefonknoten mit (x,y)-Koordinaten und zur Berechnung eines
// minimal aufspannenden Baums mit dem Algorithmus von Kruskal. Kantengewichte sind durch
// den Manhattan-Abstand definiert.
public class TelNet {
    int XMax = 0;
    int YMax = 0;
    Map<TelKnoten, Integer> telMap;
    int size = 0;
    List<TelVerbindung> minSpanTree = new LinkedList<>();
    List<TelVerbindung> edgelist = new LinkedList<>();
    int lbg;

    TelNet(int lbg) {
        telMap = new TreeMap<>();
        this.lbg = lbg;
    }

    //Fügt einen neuen Telefonknoten mit Koordinate (x,y) dazu.
    boolean addTelKnoten(int x, int y) {
        if (x > XMax)
            XMax = x;
        if (y > YMax)
            YMax = y;
        TelKnoten u = new TelKnoten(x, y);
        if (telMap.containsKey(u)) return false;
        else telMap.put(u, size++);

        int endCost = Integer.MAX_VALUE;

        TelKnoten endV = null;
        for (var map : telMap.entrySet()) {
            TelKnoten v = map.getKey();
            if (!u.equals(v)) {
                int secondCost = cost(u, v);

                if (secondCost <= lbg && secondCost < endCost) {
                    endCost = secondCost;
                    TelVerbindung c = new TelVerbindung(u, v, endCost);
                    TelVerbindung cr = new TelVerbindung(v, u, endCost);
                    if (!edgelist.contains(c)) {
                        edgelist.add(c);
                        edgelist.add(cr);

                    }
                }
            }
        }
        return true;
    }

    //    Berechnet ein optimales Telefonnetz als minimal aufspannenden Baum mit dem Algorithmus von Kruskal.
    boolean computeOptTelNet() {
        minSpanTree.addAll(minimumSpanningTree());
        return true;
    }


    List<TelVerbindung> minimumSpanningTree() {
        UnionFind forest = new UnionFind(size()); //{{v} / v ∊V};

        PriorityQueue<TelVerbindung> edges = new PriorityQueue<>(edgelist);

        System.out.printf("size edgelist = %d \n", edges.size());
        System.out.printf("forest size = %d \n", forest.size());

        List<TelVerbindung> minSpanTree2 = new LinkedList<>();
        while (forest.size() != 1 && !edges.isEmpty()) {
            TelVerbindung telVerb = edges.poll();

            int t1 = forest.find(telMap.get(telVerb.u)); //teilBaum
            int t2 = forest.find(telMap.get(telVerb.v)); // teilBaum
            System.out.printf("t1 = %d, t2 = %d \n", t1, t2);
            if (t1 != t2) {
                forest.union(t1, t2); //big TeilBaum
                minSpanTree2.add(telVerb);
//                for (int i = 0; i < forest.p.length; i++) System.out.printf("p[%d] = %d, \n", i, forest.p[i]);
                System.out.printf("removed value: %d forest size = %d \n", telVerb.c, forest.size());
            }
        }
//
//        if (edges.isEmpty() && forest.size() != 1)
//            return null; //“es existiert kein aufspannender Baum”;
//        else
        return minSpanTree2;
    }


    //    Zeichnet das gefundene optimale Telefonnetz mit der Größe xMax*yMax in ein Fenster.
    void drawOptTelNet(int xMax, int yMax) {
        StdDraw.clear();
        StdDraw.setCanvasSize(xMax, yMax);
        List<TelVerbindung> list = getOptTelNet();
        double pen = 1.0 / XMax;//300
        if (pen > 0.03)
            pen = 1.0 / 300;
        double factorX = 1.0 / XMax;//100
        double factorY = 1.0 / YMax;//100
        for (TelVerbindung v : list) {
            double x1 = (v.u.x) * factorX;
            double y1 = (v.u.y) * factorY;
            double x2 = (v.v.x) * factorX;
            double y2 = (v.v.y) * factorY;

            StdDraw.setPenColor(Color.BLUE);
            double penSquare = pen * 2;
            StdDraw.filledSquare(x1, y1, penSquare);
            StdDraw.filledSquare(x2, y2, penSquare);
            StdDraw.setPenColor(Color.RED);
            StdDraw.setPenRadius(pen);
            StdDraw.line(x1, y1, x2, y1);
            StdDraw.line(x2, y1, x2, y2);
        }
        StdDraw.show();
    }

    //    Fügt n zufällige Telefonknoten zum Netz dazu mit x-Koordinate aus [0,xMax] und y-Koordinate aus [0,yMax].
    void generateRandomTelNet(int n, int xMax, int yMax) {
        for (int i = 0; i < n; i++) {
            int x = (int) (Math.random() * xMax);
            int y = (int) (Math.random() * yMax);
//            System.out.println(i + ": " + x + " | " + y);
            addTelKnoten(x, y);
        }
    }

    TelKnoten getTelKnoten(int idx) {
        for (var map : telMap.entrySet()) {
            if (map.getValue() == idx) return map.getKey();
        }
        return null;
    }

    //    Liefemrt ein optimales Telefonnetz als Liste von Telefonverbindungen zurück.
    List<TelVerbindung> getOptTelNet() {
        return minSpanTree;
    }

    int cost(TelKnoten a, TelKnoten b) {

        int e1 = Math.abs(a.x - b.x);
        int e2 = Math.abs(a.y - b.y);
        int result = e1 + e2;
        if (result <= lbg) return result;
        else return Integer.MAX_VALUE;
    }

    //    Liefert die Gesamtkosten eines optimalen Telefonnetzes zurück.
    int getOptTelNetKosten() {
        int sum = 0;
        if (getOptTelNet() != null) for (var minList : getOptTelNet()) sum += minList.c;
        return sum;
    }

    //    Anwendung.
    public static void main(java.lang.String[] args) {
//        test1();
        test2();
    }

    private static void test2() {
        TelNet telNetRandom = new TelNet(100);
        telNetRandom.generateRandomTelNet(1000, 1000, 1000);

        telNetRandom.computeOptTelNet();

        //System.out.println("optTelNet = " + telNet.getOptTelNet());
        System.out.println("Size = " + telNetRandom.size);
        System.out.println("optCost = " + telNetRandom.getOptTelNetKosten());

        telNetRandom.drawOptTelNet(700, 700);
    }

    private static void test1() {
        TelNet telNet = new TelNet(7);

        telNet.addTelKnoten(1, 1);
        telNet.addTelKnoten(3, 1);
        telNet.addTelKnoten(4, 2);
        telNet.addTelKnoten(3, 4);
        telNet.addTelKnoten(2, 6);
        telNet.addTelKnoten(4, 7);
        telNet.addTelKnoten(7, 5);
        telNet.computeOptTelNet();

        System.out.println("optTelNet = " + telNet.getOptTelNet());
        System.out.println("Size = " + telNet.size);
        System.out.println("optCost = " + telNet.getOptTelNetKosten());
    }

    //    Liefert die Anzahl der Knoten des Telefonnetzes zurück.
    int size() {
        return size;
    }

}




