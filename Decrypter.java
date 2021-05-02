import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Decrypter {
    private String encodedMsg;
    private int keyLength;
    private ArrayList<PotentialKey> possibleKeys;

    public Decrypter(){
        encodedMsg = null;
        keyLength = 0;
        possibleKeys = new ArrayList<>();
    }

    public Decrypter(int length, String msg){
        this.keyLength = length;
        this.encodedMsg = msg;
        this.possibleKeys = new ArrayList<>();
    }

    public void decriptKey(int numKeysToTry){
        if(encodedMsg == null){
            throw new NullPointerException("cannot decrypt without an encoded message. Got null.");
        }
        //fill list with possible keys
        for (int i = 0; i < numKeysToTry * 4; i++){
            PotentialKey key = new PotentialKey(keyLength, encodedMsg);
            possibleKeys.add(key);
        }
        dropWorstSolutions(numKeysToTry);
        improveSolution();
    }

    //make a mutation of each key and add it into the list, then remove the worst solutions
    public void improveSolution(){
        ArrayList<PotentialKey> mutations = new ArrayList<>();//holds the new keys we will add
        for(PotentialKey key : possibleKeys){
            mutations.add(key.getSimilarKey());
        }
        int oldSize = possibleKeys.size();
        possibleKeys.addAll(mutations);
        dropWorstSolutions(oldSize);
    }

    public void recombine(){
        ArrayList<PotentialKey> recombs = new ArrayList<>();
        for (int i = 0; i < possibleKeys.size()-1; i++){
            recombs.add(PotentialKey.recombine(possibleKeys.get(i), possibleKeys.get(i+1)));
        }
        int oldSize = possibleKeys.size();
        possibleKeys.addAll(recombs);
        dropWorstSolutions(oldSize);
    }

    public PotentialKey solve(){
        return solve(10, 500_000, 12);
    }
    public PotentialKey solve(int numKeysToTry){
        return solve(numKeysToTry, 500_000, 12);
    }
    public PotentialKey solve(int numKeysToTry, int failSafe){
        return solve(numKeysToTry, failSafe, 12);
    }

    public PotentialKey solve(int numKeysToTry, int failSafe, int delta){
        decriptKey(numKeysToTry);
        int i = 0;
        PotentialKey best = findBestKey();
        while(i < failSafe && best.getValue() > delta){//while solution is not good enough
            if(i % 3 == 0){
                recombine();
            }
            else{
                improveSolution();
            }
            i++;
            best = findBestKey();
        }
        return new PotentialKey(best);//deep copy
    }

    private PotentialKey findBestKey(){
        PotentialKey best = possibleKeys.get(0);
        for(int i = 1; i < possibleKeys.size(); i++){
            if(possibleKeys.get(i).getValue() < best.getValue()){
                best = possibleKeys.get(i);
            }
        }
        return best;
    }

    //This method looks at the list of possible solutions and drops the worse half of them
    private void dropWorstSolutions(int desiredSize){
        Collections.sort(possibleKeys);
        while (possibleKeys.size() > desiredSize){
            possibleKeys.remove(possibleKeys.size()-1);
        }
    }

    public String getEncodedMsg() {
        return encodedMsg;
    }

    public void setEncodedMsg(String encodedMsg) {
        this.encodedMsg = encodedMsg;
        //need to pass this change on to all the potential keys.
        for (PotentialKey key : possibleKeys){
            key.setEncodedMsg(encodedMsg);
        }
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public List<PotentialKey> getPossibleKeys(){
        return new ArrayList<>(possibleKeys);
    }

}
