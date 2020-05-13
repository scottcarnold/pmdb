package org.xandercat.pmdb.form.movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.util.CIString;
import org.xandercat.pmdb.util.ReflectionUtil;

@Validated
public class MovieForm {

	private int id;
	
	@NotBlank
	@Length(max=200)
	private String title;
	
	private int collectionId;
	
	private List<String> keyValueIndicies = new ArrayList<String>();
	
	@Length(max=50)
	private String attrKey0;
	@Length(max=200)
	private String attrValue0;
	@Length(max=50)
	private String attrKey1;
	@Length(max=200)
	private String attrValue1;
	@Length(max=50)
	private String attrKey2;
	@Length(max=200)
	private String attrValue2;
	@Length(max=50)
	private String attrKey3;
	@Length(max=200)
	private String attrValue3;
	@Length(max=50)
	private String attrKey4;
	@Length(max=200)
	private String attrValue4;
	@Length(max=50)
	private String attrKey5;
	@Length(max=200)
	private String attrValue5;
	@Length(max=50)
	private String attrKey6;
	@Length(max=200)
	private String attrValue6;
	@Length(max=50)
	private String attrKey7;
	@Length(max=200)
	private String attrValue7;
	@Length(max=50)
	private String attrKey8;
	@Length(max=200)
	private String attrValue8;
	@Length(max=50)
	private String attrKey9;
	@Length(max=200)
	private String attrValue9;
	@Length(max=50)
	private String attrKey10;
	@Length(max=200)
	private String attrValue10;
	@Length(max=50)
	private String attrKey11;
	@Length(max=200)
	private String attrValue11;
	@Length(max=50)
	private String attrKey12;
	@Length(max=200)
	private String attrValue12;
	@Length(max=50)
	private String attrKey13;
	@Length(max=200)
	private String attrValue13;
	@Length(max=50)
	private String attrKey14;
	@Length(max=200)
	private String attrValue14;
	@Length(max=50)
	private String attrKey15;
	@Length(max=200)
	private String attrValue15;
	@Length(max=50)
	private String attrKey16;
	@Length(max=200)
	private String attrValue16;
	@Length(max=50)
	private String attrKey17;
	@Length(max=200)
	private String attrValue17;
	@Length(max=50)
	private String attrKey18;
	@Length(max=200)
	private String attrValue18;
	@Length(max=50)
	private String attrKey19;
	@Length(max=200)
	private String attrValue19;
	@Length(max=50)
	private String attrKey20;
	@Length(max=200)
	private String attrValue20;
	@Length(max=50)
	private String attrKey21;
	@Length(max=200)
	private String attrValue21;
	@Length(max=50)
	private String attrKey22;
	@Length(max=200)
	private String attrValue22;
	@Length(max=50)
	private String attrKey23;
	@Length(max=200)
	private String attrValue23;
	@Length(max=50)
	private String attrKey24;
	@Length(max=200)
	private String attrValue24;
	@Length(max=50)
	private String attrKey25;
	@Length(max=200)
	private String attrValue25;
	@Length(max=50)
	private String attrKey26;
	@Length(max=200)
	private String attrValue26;
	@Length(max=50)
	private String attrKey27;
	@Length(max=200)
	private String attrValue27;
	@Length(max=50)
	private String attrKey28;
	@Length(max=200)
	private String attrValue28;
	@Length(max=50)
	private String attrKey29;
	@Length(max=200)
	private String attrValue29;
	
	public MovieForm() {
		for (int i=0; i<30; i++) {
			keyValueIndicies.add(String.valueOf(i));
		}
	}
	
	public MovieForm(Movie movie) {
		this();
		this.id = movie.getId();
		this.title = movie.getTitle();
		this.collectionId = movie.getCollectionId();
		int index = 0;
		for (Map.Entry<CIString, String> entry : movie.getAttributes().entrySet()) {
			setAttrKey(index, entry.getKey().toString());
			setAttrValue(index++, entry.getValue());
		}
	}

	public Movie toMovie() {
		Movie movie = new Movie();
		movie.setTitle(title);
		for (int i=0; i<30; i++) {
			String key = getAttrKey(i);
			String value = getAttrValue(i);
			if (!StringUtils.isEmptyOrWhitespace(key) && !StringUtils.isEmptyOrWhitespace(value)) {
				movie.getAttributes().put(new CIString(key), value);
			}
		}
		return movie;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}

	public List<String> getKeyValueIndicies() {
		return keyValueIndicies;
	}

	public void setKeyValueIndicies(List<String> keyValueIndicies) {
		this.keyValueIndicies = keyValueIndicies;
	}

	public String getAttrKey0() {
		return attrKey0;
	}

	public void setAttrKey0(String attrKey0) {
		this.attrKey0 = attrKey0;
	}

	public String getAttrValue0() {
		return attrValue0;
	}

	public void setAttrValue0(String attrValue0) {
		this.attrValue0 = attrValue0;
	}

	public String getAttrKey1() {
		return attrKey1;
	}

	public void setAttrKey1(String attrKey1) {
		this.attrKey1 = attrKey1;
	}

	public String getAttrValue1() {
		return attrValue1;
	}

	public void setAttrValue1(String attrValue1) {
		this.attrValue1 = attrValue1;
	}

	public String getAttrKey2() {
		return attrKey2;
	}

	public void setAttrKey2(String attrKey2) {
		this.attrKey2 = attrKey2;
	}

	public String getAttrValue2() {
		return attrValue2;
	}

	public void setAttrValue2(String attrValue2) {
		this.attrValue2 = attrValue2;
	}

	public String getAttrKey3() {
		return attrKey3;
	}

	public void setAttrKey3(String attrKey3) {
		this.attrKey3 = attrKey3;
	}

	public String getAttrValue3() {
		return attrValue3;
	}

	public void setAttrValue3(String attrValue3) {
		this.attrValue3 = attrValue3;
	}

	public String getAttrKey4() {
		return attrKey4;
	}

	public void setAttrKey4(String attrKey4) {
		this.attrKey4 = attrKey4;
	}

	public String getAttrValue4() {
		return attrValue4;
	}

	public void setAttrValue4(String attrValue4) {
		this.attrValue4 = attrValue4;
	}

	public String getAttrKey5() {
		return attrKey5;
	}

	public void setAttrKey5(String attrKey5) {
		this.attrKey5 = attrKey5;
	}

	public String getAttrValue5() {
		return attrValue5;
	}

	public void setAttrValue5(String attrValue5) {
		this.attrValue5 = attrValue5;
	}

	public String getAttrKey6() {
		return attrKey6;
	}

	public void setAttrKey6(String attrKey6) {
		this.attrKey6 = attrKey6;
	}

	public String getAttrValue6() {
		return attrValue6;
	}

	public void setAttrValue6(String attrValue6) {
		this.attrValue6 = attrValue6;
	}

	public String getAttrKey7() {
		return attrKey7;
	}

	public void setAttrKey7(String attrKey7) {
		this.attrKey7 = attrKey7;
	}

	public String getAttrValue7() {
		return attrValue7;
	}

	public void setAttrValue7(String attrValue7) {
		this.attrValue7 = attrValue7;
	}

	public String getAttrKey8() {
		return attrKey8;
	}

	public void setAttrKey8(String attrKey8) {
		this.attrKey8 = attrKey8;
	}

	public String getAttrValue8() {
		return attrValue8;
	}

	public void setAttrValue8(String attrValue8) {
		this.attrValue8 = attrValue8;
	}

	public String getAttrKey9() {
		return attrKey9;
	}

	public void setAttrKey9(String attrKey9) {
		this.attrKey9 = attrKey9;
	}

	public String getAttrValue9() {
		return attrValue9;
	}

	public void setAttrValue9(String attrValue9) {
		this.attrValue9 = attrValue9;
	}

	public String getAttrKey10() {
		return attrKey10;
	}

	public void setAttrKey10(String attrKey10) {
		this.attrKey10 = attrKey10;
	}

	public String getAttrValue10() {
		return attrValue10;
	}

	public void setAttrValue10(String attrValue10) {
		this.attrValue10 = attrValue10;
	}

	public String getAttrKey11() {
		return attrKey11;
	}

	public void setAttrKey11(String attrKey11) {
		this.attrKey11 = attrKey11;
	}

	public String getAttrValue11() {
		return attrValue11;
	}

	public void setAttrValue11(String attrValue11) {
		this.attrValue11 = attrValue11;
	}

	public String getAttrKey12() {
		return attrKey12;
	}

	public void setAttrKey12(String attrKey12) {
		this.attrKey12 = attrKey12;
	}

	public String getAttrValue12() {
		return attrValue12;
	}

	public void setAttrValue12(String attrValue12) {
		this.attrValue12 = attrValue12;
	}

	public String getAttrKey13() {
		return attrKey13;
	}

	public void setAttrKey13(String attrKey13) {
		this.attrKey13 = attrKey13;
	}

	public String getAttrValue13() {
		return attrValue13;
	}

	public void setAttrValue13(String attrValue13) {
		this.attrValue13 = attrValue13;
	}

	public String getAttrKey14() {
		return attrKey14;
	}

	public void setAttrKey14(String attrKey14) {
		this.attrKey14 = attrKey14;
	}

	public String getAttrValue14() {
		return attrValue14;
	}

	public void setAttrValue14(String attrValue14) {
		this.attrValue14 = attrValue14;
	}

	public String getAttrKey15() {
		return attrKey15;
	}

	public void setAttrKey15(String attrKey15) {
		this.attrKey15 = attrKey15;
	}

	public String getAttrValue15() {
		return attrValue15;
	}

	public void setAttrValue15(String attrValue15) {
		this.attrValue15 = attrValue15;
	}

	public String getAttrKey16() {
		return attrKey16;
	}

	public void setAttrKey16(String attrKey16) {
		this.attrKey16 = attrKey16;
	}

	public String getAttrValue16() {
		return attrValue16;
	}

	public void setAttrValue16(String attrValue16) {
		this.attrValue16 = attrValue16;
	}

	public String getAttrKey17() {
		return attrKey17;
	}

	public void setAttrKey17(String attrKey17) {
		this.attrKey17 = attrKey17;
	}

	public String getAttrValue17() {
		return attrValue17;
	}

	public void setAttrValue17(String attrValue17) {
		this.attrValue17 = attrValue17;
	}

	public String getAttrKey18() {
		return attrKey18;
	}

	public void setAttrKey18(String attrKey18) {
		this.attrKey18 = attrKey18;
	}

	public String getAttrValue18() {
		return attrValue18;
	}

	public void setAttrValue18(String attrValue18) {
		this.attrValue18 = attrValue18;
	}

	public String getAttrKey19() {
		return attrKey19;
	}

	public void setAttrKey19(String attrKey19) {
		this.attrKey19 = attrKey19;
	}

	public String getAttrValue19() {
		return attrValue19;
	}

	public void setAttrValue19(String attrValue19) {
		this.attrValue19 = attrValue19;
	}

	public String getAttrKey20() {
		return attrKey20;
	}

	public void setAttrKey20(String attrKey20) {
		this.attrKey20 = attrKey20;
	}

	public String getAttrValue20() {
		return attrValue20;
	}

	public void setAttrValue20(String attrValue20) {
		this.attrValue20 = attrValue20;
	}

	public String getAttrKey21() {
		return attrKey21;
	}

	public void setAttrKey21(String attrKey21) {
		this.attrKey21 = attrKey21;
	}

	public String getAttrValue21() {
		return attrValue21;
	}

	public void setAttrValue21(String attrValue21) {
		this.attrValue21 = attrValue21;
	}

	public String getAttrKey22() {
		return attrKey22;
	}

	public void setAttrKey22(String attrKey22) {
		this.attrKey22 = attrKey22;
	}

	public String getAttrValue22() {
		return attrValue22;
	}

	public void setAttrValue22(String attrValue22) {
		this.attrValue22 = attrValue22;
	}

	public String getAttrKey23() {
		return attrKey23;
	}

	public void setAttrKey23(String attrKey23) {
		this.attrKey23 = attrKey23;
	}

	public String getAttrValue23() {
		return attrValue23;
	}

	public void setAttrValue23(String attrValue23) {
		this.attrValue23 = attrValue23;
	}

	public String getAttrKey24() {
		return attrKey24;
	}

	public void setAttrKey24(String attrKey24) {
		this.attrKey24 = attrKey24;
	}

	public String getAttrValue24() {
		return attrValue24;
	}

	public void setAttrValue24(String attrValue24) {
		this.attrValue24 = attrValue24;
	}

	public String getAttrKey25() {
		return attrKey25;
	}

	public void setAttrKey25(String attrKey25) {
		this.attrKey25 = attrKey25;
	}

	public String getAttrValue25() {
		return attrValue25;
	}

	public void setAttrValue25(String attrValue25) {
		this.attrValue25 = attrValue25;
	}

	public String getAttrKey26() {
		return attrKey26;
	}

	public void setAttrKey26(String attrKey26) {
		this.attrKey26 = attrKey26;
	}

	public String getAttrValue26() {
		return attrValue26;
	}

	public void setAttrValue26(String attrValue26) {
		this.attrValue26 = attrValue26;
	}

	public String getAttrKey27() {
		return attrKey27;
	}

	public void setAttrKey27(String attrKey27) {
		this.attrKey27 = attrKey27;
	}

	public String getAttrValue27() {
		return attrValue27;
	}

	public void setAttrValue27(String attrValue27) {
		this.attrValue27 = attrValue27;
	}

	public String getAttrKey28() {
		return attrKey28;
	}

	public void setAttrKey28(String attrKey28) {
		this.attrKey28 = attrKey28;
	}

	public String getAttrValue28() {
		return attrValue28;
	}

	public void setAttrValue28(String attrValue28) {
		this.attrValue28 = attrValue28;
	}

	public String getAttrKey29() {
		return attrKey29;
	}

	public void setAttrKey29(String attrKey29) {
		this.attrKey29 = attrKey29;
	}

	public String getAttrValue29() {
		return attrValue29;
	}

	public void setAttrValue29(String attrValue29) {
		this.attrValue29 = attrValue29;
	}

	public String getAttrKey(int index) {
		try {
			return ReflectionUtil.invokeGetter(this, "attrKey" + index, String.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getAttrValue(int index) {
		try {
			return ReflectionUtil.invokeGetter(this, "attrValue" + index, String.class);
		} catch (Exception e) {
			return null;
		}		
	}
	
	public void setAttrKey(int index, String key) {
		try {
			ReflectionUtil.invokeSetter(this, "attrKey" + index, String.class, key);
		} catch (Exception e) {
		}
	}
	
	public void setAttrValue(int index, String value) {
		try {
			ReflectionUtil.invokeSetter(this, "attrValue" + index, String.class, value);
		} catch (Exception e) {
		}		
	}
	
	public boolean isAttrPairEmpty(int index) {
		return StringUtils.isEmptyOrWhitespace(getAttrKey(index)) && StringUtils.isEmptyOrWhitespace(getAttrValue(index));
	}
}
