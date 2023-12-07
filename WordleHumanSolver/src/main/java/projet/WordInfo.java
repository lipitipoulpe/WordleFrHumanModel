package projet;

public record WordInfo(String word, String cgram, float freqfilms2, int islem, int puorth){
	public String toString() {
		return word + "," + cgram + "," + freqfilms2 + "," + islem + "," + puorth;
	}
}
