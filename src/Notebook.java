/* Tyler C Smith */

public class Notebook extends Item {
	private static final long serialVersionUID = 1L;

	private boolean foundPassword;

	public Notebook() {
		super("notebook");
		foundPassword = false;
	}

	public void read() {
		if (!Game.hasItem(this)) {
			Game.print("You need to pick up the notebook first.");
		} else if (!foundPassword) {
			foundPassword = true;
			Game.addInventoryItem(new Item("password"));
			Game.print("You read the notebook and find the password!");
		} else {
			Game.print("You read the notebook again. Nothing new.");
		}
	}
}