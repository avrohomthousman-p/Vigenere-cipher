import java.util.*;

public class PotentialKey implements Comparable<PotentialKey> {
    protected static double[] frequency =
                {8.2, 1.5, 2.8, 4.3, 13, 2.2, 2, 6.1, 7, 0.15, 0.77, 4, 2.4, 6.7,
                 7.5, 1.9, 0.095, 6, 6.3, 9.1, 2.8, 0.98, 2.4, 0.15, 2, 0.074};

    private String key;
    private String decodedMsg;
    private double value;

    public PotentialKey(){}//default constructor shouldnt really be used.

    public PotentialKey(int length){
        generateString(length);//sets this.key
        value = -1;
    }

    public PotentialKey(int length, String encodedMsg){
        generateString(length);//sets this.key
        decodedMsg = Vigenere.decode(key, encodedMsg);
        calculateValue();//sets this.value
    }

    public PotentialKey(String key){
        this.key = key;
        this.value = -1;
    }

    public PotentialKey(String key, String encodedMsg){
        this.key = key;
        decodedMsg = Vigenere.decode(key, encodedMsg);
        calculateValue();//sets this.value
    }

    public PotentialKey(PotentialKey template){
        this.key = template.key;
        this.decodedMsg = template.decodedMsg;
        this.value = template.value;
    }

    @Override
    public String toString(){
        return key;
    }

    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(this.getClass() == o.getClass()){
            PotentialKey other = (PotentialKey) o;
            if(this.key.equals(other.key)){
                if(this.value == other.value)
                    return true;
            }
        }
        return false;
    }

    private void calculateValue(){
        if(decodedMsg == null){//if you used the default constructor and never called setEncodedMsg()
            throw new NullPointerException("A potential key cannot be evaluated without an encoded message to analyze. got null");
        }
        value = 0;
        double[] actualFrequ = new double[26];//frequency of each letter in the message as decoded by this.key
        for (int i = 0; i < decodedMsg.length(); i++) {
            char current = decodedMsg.charAt(i);
            Character.toUpperCase(current);
            actualFrequ[Character.toUpperCase(current) - 65]++;
        }
        for (int i = 0; i < actualFrequ.length; i++) {
            actualFrequ[i] /= decodedMsg.length();
            actualFrequ[i] *= 100;//convert to percent
            value += Math.abs(actualFrequ[i] - frequency[i]);//notice: high value is bad
            //alternitavely, only increment value if sameFrequency()
        }
    }

    private boolean sameFrequency(double expected, double actual, double delta){
        return (actual <= expected + delta && actual >= expected - delta);
    }

    private void generateString(int length){
        Random generator = new Random();
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < length; i++){
            char current = ((char)(generator.nextInt(26) + 65));
            string.append(current);
        }
        key = string.toString();
    }

    public void mutateKey(int numMutations){
        String encodedMsg = null;//this is really only needed if this.decodedMsg != null (see next comment)
        boolean haveEncodedMsg = (this.decodedMsg != null);
        if(haveEncodedMsg) {
            //encode it again, so we can decoded it using he new key
            encodedMsg = Vigenere.cipherMaker(key, decodedMsg);
        }


        Random generator = new Random();
        int[] indexesToMutate = new int[numMutations];
        for (int i = 0; i < numMutations; i++){
            indexesToMutate[i] = generator.nextInt(key.length());
        }
        StringBuilder newKey = new StringBuilder(key);
        for (int index : indexesToMutate){
            newKey.setCharAt(index, (char) (generator.nextInt(26) + 65));
        }
        key = newKey.toString();


        if(haveEncodedMsg){
            decodedMsg = Vigenere.decode(key, encodedMsg);
            calculateValue();
        }
    }

    public PotentialKey getSimilarKey(){
        PotentialKey copy = new PotentialKey(this);
        copy.mutateKey(3);//if you dont specify a number, 3 mutations are done
        return copy;
    }

    public PotentialKey getSimilarKey(int numMutations){
        PotentialKey copy = new PotentialKey(this);
        copy.mutateKey(numMutations);
        return copy;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
        calculateValue();
    }

    public double getValue() {
        return value;
    }

    public void setEncodedMsg(String msg){
        this.decodedMsg = Vigenere.decode(key, msg);
        calculateValue();
    }

    //this is ineffiecient and shouldnt really be used, but its here just in case.
    public String getEncodedMsg(){
        return Vigenere.cipherMaker(key, decodedMsg);
    }

    public String getDecodedMsg(){
        return decodedMsg;
    }

    @Override
    public int compareTo(PotentialKey o) {
        //couldve done this in 1 line, but I feel like this is more read-able
        if(this.value - o.value > 0){
            return 1;
        }
        else if(this.value - o.value < 0){
            return -1;
        }
        else{
            return 0;
        }
    }



    private static HashMap<Character, Double> setUpFrequency(){
        HashMap<Character, Double> characters = new HashMap<>();
        double[] frequ = {8.2, 1.5, 2.8, 4.3, 13, 2.2, 2, 6.1, 7, 0.15, 0.77, 4, 2.4, 6.7, 7.5, 1.9, 0.095, 6,
                6.3, 9.1, 2.8, 0.98, 2.4, 0.15, 2, 0.074};
        for (int i = 65; i < 91;i++){
            characters.put(((char) i), frequ[i-65]);
        }
        return characters;
    }

    public static PotentialKey recombine(PotentialKey parent1, PotentialKey parent2) throws IllegalStateException{
        if(parent1.key.length() != parent2.key.length())
            throw new IllegalStateException("for recombination, both parents need to have keys of the same length");

        StringBuilder newKey = new StringBuilder();
        int halfString = parent1.key.length() / 2;
        newKey.append(parent1.key.substring(0, halfString));
        newKey.append(parent2.key.substring(halfString, parent2.key.length()));
        if(parent1.getDecodedMsg() == null) {//because the parent keys never had their encoded messages initialized
            return new PotentialKey(newKey.toString());
        }
        else{
            return new PotentialKey(newKey.toString(), parent1.getEncodedMsg());
        }
    }

}
