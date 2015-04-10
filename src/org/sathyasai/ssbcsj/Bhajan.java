package org.sathyasai.ssbcsj;

public class Bhajan {

	public Bhajan() {
	}

	public Bhajan(String lyrics, String meaning, String scale) {
		super();
		this.lyrics = lyrics;
		this.meaning = meaning;
		this.scale = scale;
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}

	public String getMeaning() {
		return meaning;
	}

	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}

	private String lyrics, meaning, scale;

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

}
