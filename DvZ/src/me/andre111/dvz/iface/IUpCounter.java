package me.andre111.dvz.iface;

public interface IUpCounter {
	public int countUPgetMax();
	public int countUPperSecond();

	public boolean countUPOverridable();

	public boolean countUPinterruptMove();
	public boolean countUPinterruptDamage();
	public boolean countUPinterruptItemChange();

	public void countUPinterrupt();

	public void countUPfinish(String vars);
}
