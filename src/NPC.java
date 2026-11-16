/* Tyler C Smith */

import java.io.Serializable;
import javax.swing.JOptionPane;

/** Lab 13: a non-player character. Specific characters extend this class and override talk(). */
public class NPC implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;

	public NPC(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** Prints the character's name in all caps, then what they say. */
	public void say(String dialog) {
		Game.print(name.toUpperCase());
		Game.print(dialog);
		Game.print("");
	}

	/** Generic version. Each character overrides this with their own dialog. */
	public void talk() {
		Game.print("You can't talk to " + name + ".");
	}

	/** Prints the numbered choices, asks the player to pick one, and returns their choice. */
	public int getResponse(String[] options) {
		for (int i = 0; i < options.length; i++) {
			Game.print((i + 1) + ") " + options[i]);
		}
		Game.print("");

		String input = JOptionPane.showInputDialog(null, "Choose an option:");
		try {
			return Integer.parseInt(input.trim());
		} catch (Exception e) {
			return 0;   // Cancel, a blank box, or not a number: the character won't understand
		}
	}
}
