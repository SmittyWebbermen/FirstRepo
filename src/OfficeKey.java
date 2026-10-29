
/* Tyler C Smith */


public class OfficeKey extends Item {
	private static final long serialVersionUID = 1L;

	private boolean usedKey;

	public OfficeKey() {
		super("officekey");
		usedKey = false;
	}

	
	public void use() {
		if (Game.getCurrentRoom().getName().equals("Living Room")) {
			if (usedKey) {
				Game.print("You already unlocked the office door!");
			} else {
				Game.getCurrentRoom().getExit(1).setLocked(false);   // 1 = west (the Office)
				usedKey = true;
				Game.print("You slide the key into the office door and turn it. "
						+ "Click. The office is unlocked.");
			}
		} else {
			Game.print("You don't see any way to use the office key here.");
		}
	}
}