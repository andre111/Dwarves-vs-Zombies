package me.andre111.dvz;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Language {
	public static Collection<String> getPossibleLanguages() {
		Collection<String> col = new ArrayList<String>();
		
		col.add("af_ZA");
		col.add("ar_SA");
		col.add("ca_ES");
		col.add("cs_CZ");
		col.add("da_DK");
		col.add("de_DE");
		col.add("en_EN");
		col.add("fi_FI");
		col.add("fr_FR");
		col.add("hr_HR");
		col.add("hu_HU");
		col.add("it_IT");
		col.add("ja_JP");
		col.add("ko_KR");
		col.add("nl_NL");
		col.add("no_NO");
		col.add("pl_PL");
		col.add("pt_BR");
		col.add("pt_PT");
		col.add("ro_RO");
		col.add("ru_RU");
		col.add("sp_SP");
		col.add("sv_SE");
		col.add("tr_TR");
		col.add("uk_UA");
		col.add("vi_VN");
		
		return col;
	}
}
