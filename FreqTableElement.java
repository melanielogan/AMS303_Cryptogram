/**
 * Created by Mel on 5/13/2019.
 */
public class FreqTableElement {
    int freq;
    char letter;

    public FreqTableElement(int freq, char letter){
        this.freq = freq;
        this.letter = letter;
    }
    @Override
    public String toString(){
        return /*(this.freq < 10)?*/ ""+this.letter/*+" : "+*//*("0"+this.freq) : ""+this.freq*/;
    }
}
