package me.andre111.dvz.iface;

public interface IUpCounter {
	public int countUPgetMax();
	public int countUPperSecond();

	public boolean countUPOverridable();

	public boolean countUPinterruptMove();
	public boolean countUPinterruptDamage();
	public boolean countUPinterruptItemChange();

	public void countUPincrease(String vars);
	public void countUPinterrupt(String vars);

	public void countUPfinish(String vars);
}
