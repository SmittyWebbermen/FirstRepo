/* Tyler C Smith */

public class Safe extends Item {
	private static final long serialVersionUID = 1L;

	private boolean isOpen;
	private boolean foundNotebook;

	public Safe() {
		super("safe", true);   // heavy
		isOpen = false;
		foundNotebook = false;
	}

	public void open() {
		if (isOpen) {
			Game.print("The safe is already open.");
		} else if (!foundNotebook) {
			isOpen = true;
			foundNotebook = true;
			Game.getCurrentRoom().addItem(new Notebook());
			Game.print("You open the safe. There's a notebook inside.");
		} else {
			isOpen = true;
			Game.print("You open the safe.");
		}
	}

	public void close() {
		if (!isOpen) {
			Game.print("The safe is already closed.");
		} else {
			isOpen = false;
			Game.print("You close the safe.");
		}
	}
}