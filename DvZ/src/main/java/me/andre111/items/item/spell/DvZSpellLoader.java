package me.andre111.items.item.spell;

import me.andre111.items.item.ItemSpell;

public class DvZSpellLoader {
	public static void addSpells() {
		ItemSpell.addSpellToLUA(new ItemClassCheck(), "ItemClassCheck");
		ItemSpell.addSpellToLUA(new ItemCrystalStorage(), "ItemCrystalStorage");
		ItemSpell.addSpellToLUA(new ItemDvZClassPoint(), "ItemDvZClassPoint");
		ItemSpell.addSpellToLUA(new ItemDVZTeleport(), "ItemDVZTeleport");
		ItemSpell.addSpellToLUA(new ItemPortal(), "ItemPortal");
		ItemSpell.addSpellToLUA(new ItemPortalTeleport(), "ItemPortalTeleport");
		ItemSpell.addSpellToLUA(new ItemPotions(), "ItemPotions");
		ItemSpell.addSpellToLUA(new ItemReinforcePortal(), "ItemReinforcePortal");
		ItemSpell.addSpellToLUA(new ItemVariableSetDvZ(), "ItemVariableSetDvZ");
	}
}
