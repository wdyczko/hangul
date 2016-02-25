/**
 * Created by wdyczko on 9/28/2015.
 */
public class Question {
    String hangul;
    String pronunciation;
    boolean vowel;
    boolean consonant;

    public String getHangul() {
        return hangul;
    }

    public void setHangul(String hangul) {
        this.hangul = hangul;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public boolean isVowel() {
        return vowel;
    }

    public void setVowel(boolean vowel) {
        this.vowel = vowel;
    }

    public boolean isConsonant() {
        return consonant;
    }

    public void setConsonant(boolean consonant) {
        this.consonant = consonant;
    }
}
