import com.sun.deploy.util.StringUtils;

import java.util.Arrays;

/**
 * Created by Mel on 5/13/2019.
 */
public class crypt {
    private static final String MSG = "LTJAQ LBQAX YGVXZ HOCMZ IFMWX LZVPE DFVJS IGKBD LJWNH LTJPQNBFWD TUJNX IBIER IZEVE PKMZS LWJUQ TUQUX LTJML GFMPT DZFZELNMOE DFUPE UZEAM LGKGE WSMZS IZNPL JOZIV UWPNS JBIJX PNIERIUJHZ YWABH GUMRE BXEJM IUMOQ HGZBJ VSIEX UFFER IUJNS IZEJDBJWNH LTJWD LGZRZ QGIME PKMOE UFECQ YZEUZ HRXBQ BBIJQ BMJRUUZMZS LWJUR UIJSE IFCGL LJJXX NGVYZ HOMIE URWIH VGXND EMXWHDFRML JLHEE WRQXE";
    static String msgF;

    public static void main(String[] args){
        setMsg();
        //OVERALL IC: 0.04517237893034071
        System.out.println("msg IC: "+ic(msgF.toCharArray()));
        //SPLIT INTO SETS FIND IC (LENGTH 4)
        double subset4 = splitIC(4, 0);
        System.out.println("IC4: "+subset4);
        //SPLIT INTO SETS FIND IC (LENGTH 5)
        double subset5 = splitIC(5,0);
        System.out.println("IC5: "+subset5);
        //SPLIT INTO SETS FIND IC (LENGTH 6)
        double subset6 = splitIC(6,0);
        System.out.println("IC6: "+subset6);
        //CHOOSE HIGHER IC TO DETERMINE LENGTH OF KEYWORD (this case it's 5 letters)
        int keywordLength = 5;
        System.out.println("\n-------2nd Keyword Length: "+keywordLength+"-------\n");
        //FREQ TABLES
        FreqTableElement [][] alphabets = new FreqTableElement[keywordLength][26];
        for(int i = 0; i < keywordLength; i++)
            alphabets[i] = freqTable(keywordLength, i);
        //PRINT OUT FREQ TABLES
        System.out.println("\n-------FREQ TABLES-------\n");
        printFreqTables(alphabets);
        //SHIFT ALPHABETS
//        shiftAlphabets(alphabets);
//        System.out.println("\n-------SHIFTED FREQ TABLES-------\n");
//        printFreqTables(alphabets);
        System.out.println("\n-------BEST IC SHIFTS-------\n");

        int subsetI = 1;
        for(; subsetI < alphabets.length; subsetI++){
            double[][] best1 = checkICMatches(alphabets, subsetI);
            for(int i = 0; i <= best1[0][subsetI+1]; i++)
                shift(alphabets, subsetI);
        }
        printFreqTables(alphabets);
        System.out.println("VOIDS: "+countVoids(alphabets));
        System.out.println("New IC: "+icTable(alphabets));
        for(int i = 0; i < alphabets.length; i++){
            for(int j = 0; j < 7; j++)
                shift(alphabets, i);
        }
        System.out.println("\n-------SHIFTED ALPHABET TABLES-------\n");
        for(char i = 'A'; i <= 'Z'; i++){
            System.out.print(i+" ");
        }
        System.out.println();
        printFreqTables(alphabets);
        System.out.println("\nVOIDS: "+countVoids(alphabets));
        System.out.println("New IC: "+icTable(alphabets));
        System.out.println("\n-------REWRITTEN SUBSETS-------\n");

        String[] subsets = new String[keywordLength];
        for(int i = 0; i < alphabets.length; i++){
            subsets[i] = rewriteSubset(String.valueOf(createSubset(5, i)), alphabets, i);
            System.out.println(subsets[i]);
        }
        System.out.println("\n-------ONCE DECODED MSG-------\n");
        rewriteMsg(subsets);
        System.out.println(msgF);

        System.out.println("\n-------FIND E-------\n");
        FreqTableElement E = findE();
        System.out.println();
        System.out.println("E is "+E.letter+" : "+E.freq);
        System.out.println("\n-------FIND R-------\n");
        char R = findR();
        System.out.println("R is "+R);
    }

    public static char findR(){
        String[][] trigraph = new String[26][47];
        char letter = 'A';
        for(int i = 0; i < trigraph.length; i++)
           trigraph[i][0] = (char)(letter+i)+"";
        for(int j = 0; j < trigraph.length; j++){
            int triI = 1;
            for(int i = 0; i < msgF.length()-1; i++){
                String t = "";
                if(msgF.charAt(i) == trigraph[j][0].charAt(0)){
                    if(i == 0)
                        t += "."+msgF.charAt(i+1);
                    else{
                        t += msgF.charAt(i-1)+""+msgF.charAt(i+1);
                    }

                    trigraph[j][triI] = t;
                    triI++;

                }
            }
        }
        printMostFreqTri(trigraph);
        String[] maxOccur = trigraph[0];
        long maxCount = 0;
//        String[] currOccurP;
        long currCount;

        for(String[] e : trigraph){
//            currOccurP = e;
            currCount = 0;
            for(int i = 1; i < e.length; i++){
                if(e[i] != null)
                    currCount += e[i].chars().filter(ch -> ch =='P').count();
            }
            if(currCount > maxCount){
                maxCount = currCount;
                maxOccur = e;
            }
        }

        return maxOccur[0].charAt(0);
    }

    public static FreqTableElement findE(){
        FreqTableElement[] msgFreq = new FreqTableElement[26];
        char letter = 'A';
        for(int i = 0; i < msgFreq.length; i++)
            msgFreq[i] = new FreqTableElement(0, (char)(letter+i));
        for(int i = 0; i < msgFreq.length; i++){
            for(char c : msgF.toCharArray())
                if(c == letter+i)
                    msgFreq[i].freq++;
        }
        for(FreqTableElement e : msgFreq){
            System.out.print(e.letter+" : "+e.freq+" ");
        }

        FreqTableElement maxOccur = msgFreq[0];
        FreqTableElement currOccur;

        for(FreqTableElement e : msgFreq){
            currOccur = e;
            if(currOccur.freq > maxOccur.freq)
                maxOccur = currOccur;
        }

        return maxOccur;

    }

    public static void printMostFreqTri(String[][] trigraph){
        String maxOccur = trigraph[0][0];
        long maxCount = 0;
        long currCount;
        String currOccur;

        for(String[] e : trigraph){
//            currOccurP = e;
            currCount = 0;
            maxOccur = e[0];
            maxCount = 0;
            int i = 1;
            while(i < trigraph.length && e[i] != null){
                for(String t : e){
                    if(e[i].equals(t))
                        currCount ++;
                }
                if(currCount > maxCount){
                    maxCount = currCount;
                    maxOccur = e[i];
                }
                currCount = 0;
                i++;
                }
            System.out.println(e[0]+" : "+maxOccur+" ("+maxCount+")");
        }
    }

    public static void printTrigraph( String[][] trigraph ){
        for(String[] letter : trigraph){
            for(String e : letter){
                if(e != null)
                    System.out.println(e);
            }
            System.out.println();
        }
    }

    public static void rewriteMsg(String[] subsets){
        String msgF = "";
        for(int i = 0; i < subsets[0].length(); i++){
            msgF += subsets[0].charAt(i);
            msgF += subsets[1].charAt(i);
            msgF += subsets[2].charAt(i);
            msgF += subsets[3].charAt(i);
            msgF += subsets[4].charAt(i);
        }
        crypt.msgF = msgF;
    }

    public static String rewriteSubset(String subset, FreqTableElement [][] freqTable, int row){
        char letter = 'a';
        for(int i = 0; i < freqTable[0].length; i++){
            subset = subset.replace(freqTable[row][i].letter, (char)(letter+i));
        }

        return subset.toUpperCase();
    }

    public static void printFreqTables(FreqTableElement [][] alphabets){
        for(FreqTableElement[] arr : alphabets) {
            for (FreqTableElement e : arr)
                System.out.print(e + " ");
            System.out.println();
        }
    }

    public static void setMsg(){
        String [] msgArr = MSG.split(" ");
        msgF = "";
        for(String s : msgArr)
            msgF += s;
    }
    public static double ic(char[] set){
        double N = set.length;
        char letter = 'A';
        double sum = 0;
        char occur = 0;
        for(int i = 0; i < 26; i++){
            for(char curr : set){
                if(curr == letter)
                    occur++;
            }
            sum += occur*(occur-1);
            occur = 0;
            letter++;
        }
        return 1/(N*(N-1))*sum;
    }

    public static double splitIC(int len, int offset){
        return ic(createSubset(len, offset));
    }
    public static FreqTableElement[] freqTable(int len, int offset){
        char [] temp = createSubset(len, offset);
        System.out.println("IC"+offset+" : "+ic(temp));
        String subset = String.valueOf(temp);
        FreqTableElement[] freqTable = new FreqTableElement[26];
        char letter = 'A';
        for(int i = 0; i < freqTable.length; i++)
            freqTable[i] = new FreqTableElement(0, (char)(letter+i));
        for(int i = 0; i < freqTable.length; i++){
            for(char c : subset.toCharArray())
                if(c == letter+i)
                    freqTable[i].freq++;
        }
        //TEST
        int voids = 0;
        for(FreqTableElement e : freqTable)
            if(e.freq == 0)
                voids++;
        System.out.println(voids);
        //
        return freqTable;
    }
    private static char[] createSubset(int len, int offset){
        String subset = "";
        for(int i = offset; i < msgF.length(); i+=len)
            subset += msgF.charAt(i);
        int x = subset.length();
        int y = msgF.length();
        return subset.toCharArray();
    }

//    public static void shiftAlphabets(FreqTableElement[][] freqTable){
//        int len = freqTable[0].length;
//
//        if(countVoids(freqTable))
//            return;
//        for(int i0 = 0; i0 < len; i0++){
//            if(countVoids(freqTable))
//                return;
//            shift(freqTable, 1);
//            for(int i1 = 0; i1 < len; i1++){
//                if(countVoids(freqTable))
//                    return;
//                shift(freqTable, 2);
//                for(int i2 = 0; i2 < len; i2++){
//                    if(countVoids(freqTable))
//                        return;
//                    shift(freqTable, 3);
//                    for(int i3 = 0; i3 < len; i3++){
//                        if(countVoids(freqTable))
//                            return;
//                        shift(freqTable, 4);
////                        System.out.println("---------");
////                        printFreqTables(freqTable);
//                    }
//                }
//            }
//        }
//    }
    private static void shift(FreqTableElement[][] freqTable, int row){
        FreqTableElement[] old = new FreqTableElement[freqTable[0].length];
        for(int i = 0; i < freqTable[0].length;i++){
            old[i]= new FreqTableElement(freqTable[row][i].freq, freqTable[row][i].letter);
        }

        for(int i = 0; i < freqTable[0].length; i++){
            if(i == freqTable[0].length-1)
                freqTable[row][0] = old[i];
            else
                freqTable[row][i+1] = old[i];
        }
    }
    private static int countVoids(FreqTableElement[][] freqTable){
        int voids = 0;
        for(int i = 0; i < freqTable[0].length; i++){
            if(freqTable[0][i].freq==0 && freqTable[1][i].freq==0 && freqTable[2][i].freq==0 && freqTable[3][i].freq==0 && freqTable[4][i].freq == 0)
                voids++;
        }
        return voids;
    }

    public static double[][] checkICMatches(FreqTableElement[][] freqTable,  int subsetRow) {
        int len = freqTable[0].length;
        double[][] best = new double[3][6];
        double currIC = 0;
        int row = subsetRow;
        FreqTableElement [] combined = new FreqTableElement[freqTable[0].length];
        for(int i = 1; i < freqTable[0].length; i++){
            shift(freqTable, row);
            for(int j = 0; j < freqTable[0].length; j++){
                try{
                combined[j] = new FreqTableElement(freqTable[0][j].freq+freqTable[row][j].freq, freqTable[0][j].letter);}
                catch(Exception e){
                    int pooo = 10;
                }
            }
            currIC = icComb(combined);
            for(int j = 0; j < best.length; j++){
                if(currIC > best[j][0]){
                    changeBest(j, row, i, best, currIC);
                    break;
                }
            }
        }
        return best;
    }

    public static double icComb(FreqTableElement[] combined){
        double sum = 0;
        for(int j = 0; j < combined.length; j++){
            sum += combined[j].freq*(combined[j].freq - 1);
        }
        return (1/(63.0*(63.0-1)))*sum;
    }
    public static double icTable(FreqTableElement[][] combined){
        double sum = 0;
        for(int j = 0; j < combined[0].length; j++){
            int sumOfSub = 0;
            for(int i = 0; i < combined.length; i++)
                sumOfSub += combined[i][j].freq;
            sum += sumOfSub*(sumOfSub - 1);
        }
        return (1/((double)msgF.length()*(msgF.length()-1)))*sum;
    }

    public static void changeBest(int bestI, int subsetI, int shift,double[][] best, double currIC){
        //int store = best[bestI][]
        for(int i = 2; i > bestI; i--){
            for(int j = 0; j < best[0].length; j++)
                best[i][j] = best[i-1][j];
        }
        //FIDDLE WITH THIS IF YOU USE MORE THAN ONE LIST SHIFTING AT A TIME
        best[bestI][0] = currIC;
        best[bestI][subsetI+1] = shift;
//        for(int i = 0; i < best[0].length; i++){
//
//        }
    }
}
