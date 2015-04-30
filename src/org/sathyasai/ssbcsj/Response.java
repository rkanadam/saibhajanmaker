package org.sathyasai.ssbcsj;

import java.util.ArrayList;
import java.util.List;

public class Response {
	public final List<Bhajan> getBhajans() {
		return bhajans;
	}

	public final void setBhajans(List<Bhajan> bhajans) {
		this.bhajans = bhajans;
	}

	public final String getThoughtForTheDay() {
		return thoughtForTheDay;
	}

	public final void setThoughtForTheDay(String thoughtForTheDay) {
		this.thoughtForTheDay = thoughtForTheDay;
	}

	public final String getDivineCodeOfConduct() {
		return divineCodeOfConduct;
	}

	public final void setDivineCodeOfConduct(String divineCodeOfConduct) {
		this.divineCodeOfConduct = divineCodeOfConduct;
	}

	private List<Bhajan> bhajans = new ArrayList<Bhajan>();
	private String thoughtForTheDay = "", divineCodeOfConduct = "";
}
