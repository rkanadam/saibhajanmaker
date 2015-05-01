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

	public final String getDivineCodeOfConduct() {
		return divineCodeOfConduct;
	}

	public final void setDivineCodeOfConduct(String divineCodeOfConduct) {
		this.divineCodeOfConduct = divineCodeOfConduct;
	}

	private List<Bhajan> bhajans = new ArrayList<Bhajan>();
	private String thoughtForTheWeek = "", divineCodeOfConduct = "";

	public String getThoughtForTheWeek() {
		return thoughtForTheWeek;
	}

	public void setThoughtForTheWeek(String thoughtForTheWeek) {
		this.thoughtForTheWeek = thoughtForTheWeek;
	}
}
